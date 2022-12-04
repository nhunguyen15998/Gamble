package com.futech.entertainment.packages.users.controllers.admin;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.catalina.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.futech.entertainment.packages.core.utils.DataMapper;
import com.futech.entertainment.packages.core.utils.FileUploadUtil;
import com.futech.entertainment.packages.core.utils.Helpers;
import com.futech.entertainment.packages.core.utils.JoinCondition;
import com.futech.entertainment.packages.games.services.interfaces.GameHistoryUserServiceInterface;
import com.futech.entertainment.packages.users.modelMappers.UserMapper;
import com.futech.entertainment.packages.users.modelMappers.UserProfileMapper;
import com.futech.entertainment.packages.users.modelMappers.UserUpdateMapper;
import com.futech.entertainment.packages.users.models.User;
import com.futech.entertainment.packages.users.models.UserProfile;
import com.futech.entertainment.packages.users.services.UserService;
import com.futech.entertainment.packages.users.services.interfaces.UserServiceInterface;
import com.rabbitmq.client.AMQP.Confirm.Select;

@Controller
public class UserManaController {
    public static int userType;
    @Autowired
    private UserServiceInterface userServiceiInterface;
    @Autowired
    private GameHistoryUserServiceInterface gameHistoryUserServiceInterface;

    @ModelAttribute("statusList")
    public Map<Integer, String> getStatusList() {
        Map<Integer, String> statusList = new HashMap<Integer, String>();
        statusList.put(Helpers.ACTIVATED, "Activated");
        statusList.put(Helpers.DEACTIVATED, "Deactivated");

        return statusList;
    }
    @ModelAttribute("genderList")
    public Map<Integer, String> getGenderList() {
        Map<Integer, String> genderList = new HashMap<Integer, String>();
        genderList.put(Helpers.MALE, "Male");
        genderList.put(Helpers.FEMALE, "Female");
        genderList.put(Helpers.OTHER, "Other");

        return genderList;
    }


    @GetMapping("/admin/users/all")
    public String ViewUser(Model mdl,@RequestParam int type, HttpSession session){
        if(session.getAttribute("user_id")==null ||(session.getAttribute("user_id")!=null&&Integer.parseInt(session.getAttribute("is_admin").toString())==0)) return "redirect:/user/sign-in";
        session.setAttribute("title", "All | Users");
        mdl.addAttribute("users",LoadData(1,"", 10,type) );
        mdl.addAttribute("is_player",true);
        mdl.addAttribute("is_admin",type);
        mdl.addAttribute("paging", RowEvent(GetCount("",type),10));
        return "users/administrator/all-user";
    } 

    @GetMapping("/admin/users/create")
    public String showCreateForm(Model mdl, HttpSession session){
        if(session.getAttribute("user_id")==null ||(session.getAttribute("user_id")!=null&&Integer.parseInt(session.getAttribute("is_admin").toString())==0)) return "redirect:/user/sign-in";
        session.setAttribute("title", "Create | Users");

        mdl.addAttribute("userMapper", new UserMapper());
        mdl.addAttribute("formType", 0);
        return "users/administrator/create-update";
    }
    @GetMapping("/admin/users/my-profile")
    public String showMyProfile( Model model, RedirectAttributes atts, HttpSession session)
    {
        try{
            if(session.getAttribute("user_id")==null ||(session.getAttribute("user_id")!=null&&Integer.parseInt(session.getAttribute("is_admin").toString())==0)) return "redirect:/user/sign-in";
            session.setAttribute("title", "My profile | Users");

            model.addAttribute("userMapper", getUserMapperByID(Integer.parseInt(session.getAttribute("user_id").toString())));
            return "users/administrator/my-profile";
        } catch(Exception ex){
            atts.addFlashAttribute("error", ex.getMessage());
            return "redirect:/";

        }
    }
    @PostMapping("/admin/users/create")
    public String CreateUser(@Valid @ModelAttribute("userMapper") UserMapper userMapper,BindingResult bindingResult,RedirectAttributes redirAttrs, @RequestParam("pathImg") MultipartFile multipartFile, Model model, @RequestParam String imgViewer)  throws IOException
    {
        try {

            if(!userMapper.getPlain_password().trim().equals(userMapper.getConfirm_password().trim())){
                bindingResult.addError(new FieldError("userMapper", "confirm_password", "Passwords do not match"));
            } 
            if(userMapper.getBirth().isEmpty()) 
            bindingResult.addError(new FieldError("userMapper", "birth", "Birth is required."));

            else if(LocalDate.parse(userMapper.getBirth()).getYear()<Year.now().getValue()-100||LocalDate.parse(userMapper.getBirth()).plusYears(18).isAfter(LocalDate.now())){
                bindingResult.addError(new FieldError("userMapper", "birth", "Invalid year need more than 18 years old"));

            }
            if(bindingResult.hasErrors()){
                if(!imgViewer.isBlank()) userMapper.setThumbnail(imgViewer);
                model.addAttribute("userMapper", userMapper);
                model.addAttribute("formType", 0);
                return "users/administrator/create-update";
            }
            if(!multipartFile.getOriginalFilename().isEmpty()){
                String fileName = Helpers.randomStringDate()+"_"+StringUtils.cleanPath(multipartFile.getOriginalFilename());
                userMapper.setThumbnail("/images/users/"+fileName);
                FileUploadUtil.saveFile("src/main/resources/static/images/users/", fileName, multipartFile);
            }else  userMapper.setThumbnail(null);
            //userMapper.setIs_admin(true);
            boolean sent = this.userServiceiInterface.createUser(userMapper);
            if(sent){
                redirAttrs.addFlashAttribute("successMsg", String.format("Saved successfully!"));
                return "redirect:/admin/users/all?type=1";
            }
            redirAttrs.addFlashAttribute("errorMsg", "Oops! Something went wrong!");
            return "redirect:/admin/users/all?type=1";
        } catch (Exception e) {
            redirAttrs.addFlashAttribute("errorMsg", e.getMessage());
            return "redirect:/admin/users/all?type=1";
        }
    }
    @GetMapping("/admin/users/update")
    public String showUpdateForm(@RequestParam int id, @RequestParam int type, Model model, RedirectAttributes atts, HttpSession session)
    {
        try{
            if(session.getAttribute("user_id")==null ||(session.getAttribute("user_id")!=null&&Integer.parseInt(session.getAttribute("is_admin").toString())==0)) return "redirect:/user/sign-in";
            session.setAttribute("title", "Update | Users");

            model.addAttribute("userMapper", getUserMapperByID(id));
            model.addAttribute("formType", 1);
            model.addAttribute("userType", type);
            model.addAttribute("paging", RowEvent(GetCountUserHistory("-1",LocalDate.now().with(TemporalAdjusters.firstDayOfMonth()).toString(),LocalDate.now().with(TemporalAdjusters.lastDayOfMonth()).toString(),String.valueOf(id)),10));
            userType=type;
            return "users/administrator/create-update";
        } catch(Exception ex){
            atts.addFlashAttribute("error", ex.getMessage());
            return "/admin/users/all?type="+type;

        }
    }
    @PostMapping("/admin/users/update")
    public String updateUser(HttpSession session,@RequestParam int type, @Valid @ModelAttribute("userMapper") UserUpdateMapper userMapper, BindingResult bindingResult, Model model, RedirectAttributes atts,@RequestParam("pathImg") MultipartFile multipartFile,@RequestParam String imgViewer)  throws IOException {
        try {
            if(userMapper.getBirth().isEmpty()) 
            bindingResult.addError(new FieldError("userMapper", "birth", "Birth is required."));

            else if(LocalDate.parse(userMapper.getBirth()).getYear()<Year.now().getValue()-100||LocalDate.parse(userMapper.getBirth()).plusYears(18).isAfter(LocalDate.now())){
                bindingResult.addError(new FieldError("userMapper", "birth", "Invalid year need more than 18 years old"));

            }
            if(bindingResult.hasErrors()){
                if(!imgViewer.isBlank()) userMapper.setThumbnail(imgViewer);
                model.addAttribute("oldData", userMapper);
                model.addAttribute("formType", 1);
                model.addAttribute("userType", userType);

                return "users/administrator/create-update";
            }
            boolean check = multipartFile.getOriginalFilename().isEmpty();
                var tn = getUserMapperByID(userMapper.getUser_id()).getThumbnail();
                String fileName = null;
                if(!check){
                    fileName= Helpers.randomStringDate()+"_"+StringUtils.cleanPath(multipartFile.getOriginalFilename());
                    FileUploadUtil.saveFile("src/main/resources/static/images/users/", fileName, multipartFile);
                    userMapper.setThumbnail("/images/users/"+fileName);
                    if(!tn.isEmpty()) FileUploadUtil.deleteFile(tn);
                }
                else if(check && !imgViewer.isEmpty())
                    userMapper.setThumbnail(tn);
                else{ userMapper.setThumbnail(""); if(!tn.isEmpty()) FileUploadUtil.deleteFile(tn);}
                userMapper.setIs_admin(type);
            boolean updated = userServiceiInterface.updateUserUserProfile(userMapper);
            if(updated){
                System.out.println(userMapper.getUser_id().equals(session.getAttribute("user_id")));
                if(userMapper.getUser_id().equals(session.getAttribute("user_id")))
                    session.setAttribute("thumbnail", userMapper.getThumbnail());
                atts.addFlashAttribute("successMsg", "Successfully update user");
            } else {
                atts.addFlashAttribute("errorMsg", "Cannot update user");
            }
        } catch(Exception ex) {
            atts.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/admin/users/all?type="+type;//+(userMapper.isIs_admin()==true?1:0);
    }
    public UserMapper getUserMapperByID(int id){
        String[] selects = {"users.id as user_id, users.status, users.phone, users.email,users.is_admin, "+
            "user_profiles.id as user_profile_id, user_profiles.first_name, user_profiles.last_name, user_profiles.thumbnail, "+
            "user_profiles.birth, user_profiles.gender"};
            var u = userServiceiInterface.getUserById(selects,id);
            UserMapper mapper=new UserMapper();
            mapper.setUser_id(Integer.parseInt(u.get("user_id").toString()));
            mapper.setUser_profile_id(Integer.parseInt(u.get("user_profile_id").toString()));
            mapper.setFirst_name(u.get("first_name").toString());
            mapper.setLast_name(u.get("last_name").toString());
            mapper.setPhone(u.get("phone").toString());
            mapper.setEmail(u.get("email").toString());
            mapper.setBirth(u.get("birth").toString().substring(0, 10));
            mapper.setGender(Integer.parseInt(u.get("gender").toString()));
            mapper.setThumbnail(u.get("thumbnail")==null?"":u.get("thumbnail").toString());
            mapper.setStatus(Integer.parseInt(u.get("status").toString()));
            //mapper.setIs_admin(Boolean.parseBoolean(u.get("is_admin").toString()));
            return mapper;
    }
    @PostMapping("/admin/users/my-profile/update-password")
    public ResponseEntity<Map<String, Object>>  updatePassword(@RequestBody UserUpdateMapper userMapper, HttpSession session){
        Map<String, Object> items = new HashMap<String,Object>();
        userMapper.setUser_id(Integer.parseInt(session.getAttribute("user_id").toString()));
        userMapper.setUser_profile_id(Integer.parseInt(session.getAttribute("user_profile_id").toString()));
           
        if(!userServiceiInterface.checkPassword(userMapper)) items.put("current", "Current password is not correct");
        if(userServiceiInterface.checkPassword(userMapper)&&userMapper.getNew_password().length()>=6&&userMapper.getNew_password().length()<=15&&userMapper.getCurrent_password().equals(userMapper.getNew_password()))items.put("newPass", "The new password must be different from the current password.");
        if(userMapper.getNew_password().length()<6||userMapper.getNew_password().length()>15) items.put("newPass", "Password must range from 6 to 15");
        if(!userMapper.getConfirm_password().equals(userMapper.getNew_password())) items.put("confirm", "Does not match the new password");
        
        if(userMapper.getCurrent_password().isBlank()) items.put("current", "Current password is required.");
        if(userMapper.getNew_password().isBlank()) items.put("newPass", "New password is required.");
        if(items.size()>0) items.put("code",400);
        else{
            userMapper.setIs_admin(Integer.parseInt(session.getAttribute("is_admin").toString())); 
            userServiceiInterface.updateUserUserProfile(userMapper);

        }
        return new ResponseEntity<Map<String, Object>>(items, HttpStatus.OK);
    }
    @PostMapping("/admin/users/my-profile/update-infomation")
    public ResponseEntity<Map<String, Object>> updateInfomation(@Valid @RequestBody UserUpdateMapper userMapper, HttpSession session){
        Map<String, Object> items = new HashMap<String,Object>();
    try{
        if(LocalDate.parse(userMapper.getBirth()).plusYears(18).isAfter(LocalDate.now())){
            items.put("birth", "need more than 18 years old");
            items.put("code",400);
        }
        if(items.size()>0)
        return new ResponseEntity<Map<String, Object>>(items, HttpStatus.OK);

        
        userMapper.setUser_id(Integer.parseInt(session.getAttribute("user_id").toString()));
        userMapper.setUser_profile_id(Integer.parseInt(session.getAttribute("user_profile_id").toString()));
        userMapper.setIs_admin(Integer.parseInt(session.getAttribute("is_admin").toString())); 
       
        boolean updated = userServiceiInterface.updateUserUserProfile(userMapper);
        return new ResponseEntity<Map<String, Object>>(items, HttpStatus.OK);

    } catch(Exception ex) {
        items.put("message", ex.getMessage());
        return new ResponseEntity<Map<String, Object>>(items, HttpStatus.BAD_REQUEST);
    }
  
    }


 //Pagination for user history
 @PostMapping("/admin/users/getDataUserHistory")
 public @ResponseBody ResponseEntity<List<Map<String,Object>>> getDataUserHistory(int p, String gameid, String from, String to, String userid)
 {
     List<Map<String,Object>> emps = LoadDataUserHistory(p, gameid,from,to, userid);
     return new ResponseEntity<List<Map<String,Object>>>(emps, HttpStatus.OK);
 }

 public List<Map<String,Object>>  LoadDataUserHistory(int p, String gameid, String from, String to, String userid)
 {
        int currentSkip = 12 * (p - 1); //limit
        String[] limit = {String.valueOf(currentSkip),String.valueOf(12==0?1:12)};
        var u = gameHistoryUserServiceInterface.getUserHistory(gameid, from, to, userid, limit);
        return u;
 }

@PostMapping("/admin/users/getCountUserHistory")
 public @ResponseBody int GetCountUserHistory(String gameid, String from, String to, String userid)
 {
     var u = gameHistoryUserServiceInterface.getUserHistory(gameid, from, to, userid, null);
      return u==null?0:u.size();
 }
 //Pagination
 @PostMapping("/admin/user/all/LoadDataUser")
 public @ResponseBody ResponseEntity<List<Map<String,Object>>> LoadDataUser(int p, String cond, int take, int is_admin)
 {
     List<Map<String,Object>> emps = LoadData(p, cond, take,is_admin);
     return new ResponseEntity<List<Map<String,Object>>>(emps, HttpStatus.OK);
 }

 public List<Map<String,Object>>  LoadData(int p, String cond, int take,int is_admin)
 {
        int currentSkip = take * (p - 1);
        //select
        String[] selects = {"users.id,users.phone, users.email, DATE_FORMAT(users.created_at,'%d-%m-%Y %H:%i') as created_at, users.status, users.is_admin,p.first_name, p.last_name,p.ranking"};
       //join
        List<JoinCondition> lsJoin = new ArrayList<JoinCondition>();
        lsJoin.add(JoinCondition.getInstance("left join", "user_profiles p", DataMapper.getInstance("", "users.id", "=", "p.user_id", "")));
       //limit
        String[] limit = {String.valueOf(currentSkip),String.valueOf(take==0?1:take)};
       //condition
       List<DataMapper> lsCond = new ArrayList<DataMapper>();
       lsCond.add(DataMapper.getInstance("", "users.is_admin", "=", is_admin==1?"1":"0", ""));
        if(!cond.isEmpty()&&cond!=null){
            lsCond.add(DataMapper.getInstance("and (users.email like '%"+cond+"%' or ", "users.phone", "like", "%"+cond+"%", "or p.first_name like '%"+cond+"%' or p.last_name like '%"+cond+"%')"));
        }
        var u = userServiceiInterface.getAll(selects, lsCond.size()==0?null:lsCond, lsJoin, null, "users.id desc", limit);
        return u;
 }

@PostMapping("/admin/user/all/GetCount")
 public @ResponseBody int GetCount(String cond,int is_admin)
 {
     //select
     String[] selects = {"users.phone, users.email, users.created_at, users.status, users.is_admin,p.first_name, p.last_name,p.ranking"};
     //join
      List<JoinCondition> lsJoin = new ArrayList<JoinCondition>();
      lsJoin.add(JoinCondition.getInstance("left join", "user_profiles p", DataMapper.getInstance("", "users.id", "=", "p.user_id", "")));
     //condition
     List<DataMapper> lsCond = new ArrayList<DataMapper>();
     lsCond.add(DataMapper.getInstance("", "users.is_admin", "=", is_admin==1?"1":"0", ""));
      if(!cond.isEmpty()&&cond!=null){
        lsCond.add(DataMapper.getInstance("and (users.email like '%"+cond+"%' or ", "users.phone", "like", "%"+cond+"%", "or p.first_name like '%"+cond+"%' or p.last_name like '%"+cond+"%')"));
    }
      var u = userServiceiInterface.getAll(selects, lsCond, lsJoin, null, null, null);
      return u==null?0:u.size();
 }
@PostMapping("/admin/user/all/RowEvent")
 public @ResponseBody  int RowEvent(int i, int take)
 {
     double pagi = i / Double.parseDouble(String.valueOf(take==0?1:take));
     double temp = pagi - Math.floor(pagi);
     return (int)(pagi+(temp>0?1:0));
     
 }
 //End pagination

}