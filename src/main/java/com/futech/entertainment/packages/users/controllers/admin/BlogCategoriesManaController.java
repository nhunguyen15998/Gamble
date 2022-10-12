package com.futech.entertainment.packages.users.controllers.admin;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.futech.entertainment.packages.core.utils.DataMapper;
import com.futech.entertainment.packages.core.utils.FileUploadUtil;
import com.futech.entertainment.packages.core.utils.Helpers;
import com.futech.entertainment.packages.core.utils.JoinCondition;
import com.futech.entertainment.packages.users.modelMappers.UserMapper;
import com.futech.entertainment.packages.users.modelMappers.UserProfileMapper;
import com.futech.entertainment.packages.users.models.User;
import com.futech.entertainment.packages.users.models.UserProfile;
import com.futech.entertainment.packages.users.services.UserService;
import com.futech.entertainment.packages.users.services.interfaces.UserServiceInterface;
import com.rabbitmq.client.AMQP.Confirm.Select;

@Controller
public class BlogCategoriesManaController {

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


    @GetMapping("/admin/blog-categories/all")
    public String ViewUser(Model mdl){
        // mdl.addAttribute("users",LoadData(1,"", 10,type) );
        // mdl.addAttribute("paging", RowEvent(GetCount("",type),10));
        return "blogcategories/administrator/all-blog-categories";
    } 

    // @GetMapping("/admin/users/create")
    // public String showCreateForm(Model mdl){
    //     mdl.addAttribute("userMapper", new UserMapper());
    //     mdl.addAttribute("formType", 0);
    //     return "users/administrator/create-update";
    // }

    // @PostMapping("/admin/users/create")
    // public String CreateUser(@Valid @ModelAttribute("userMapper") UserMapper userMapper,BindingResult bindingResult,RedirectAttributes redirAttrs, @RequestParam("pathImg") MultipartFile multipartFile, Model model)  throws IOException
    // {
    //     try {
    //         if(!userMapper.getPlain_password().trim().equals(userMapper.getConfirm_password().trim())){
    //             bindingResult.addError(new FieldError("userMapper", "confirm_password", "Passwords do not match"));
    //         } 
    //         if(userMapper.getBirth().plusYears(18).isAfter(LocalDate.now())){
    //             bindingResult.addError(new FieldError("userMapper", "birth", "not 18"));

    //         }
    //         if(bindingResult.hasErrors()){
    //             model.addAttribute("userMapper", userMapper);
    //             model.addAttribute("formType", 0);
    //             return "users/administrator/create-update";
    //         }
    //         if(!multipartFile.getOriginalFilename().isEmpty()){
    //             String fileName = Helpers.randomForImageName()+"_"+StringUtils.cleanPath(multipartFile.getOriginalFilename());
    //             userMapper.setThumbnail(fileName);
    //             FileUploadUtil.saveFile("src/main/resources/static/images/users/", fileName, multipartFile);
    //         }
    //         userMapper.setIs_admin(true);
    //         boolean sent = this.userServiceiInterface.createUser(userMapper);
    //         if(sent){
    //             redirAttrs.addFlashAttribute("successMsg", String.format("Saved successfully!"));
    //             return "redirect:/admin/users/all?type=1";
    //         }
    //         redirAttrs.addFlashAttribute("errorMsg", "Oops! Something went wrong!");
    //         return "redirect:/admin/users/all?type=1";
    //     } catch (Exception e) {
    //         redirAttrs.addFlashAttribute("errorMsg", e.getMessage());
    //         return "redirect:/admin/users/all?type=1";
    //     }
    // }
    // @GetMapping("/admin/users/update")
    // public String showUpdateForm(@RequestParam int id, Model model, RedirectAttributes atts)
    // {
    //     try{
    //         model.addAttribute("userMapper", getUserMapperByID(id));
    //         model.addAttribute("formType", 1);
    //         return "users/administrator/create-update";
    //     } catch(Exception ex){
    //         atts.addFlashAttribute("error", ex.getMessage());
    //         return "/admin/users/all?type=1";

    //     }
    // }
    // @PostMapping("/admin/users/update")
    // public String updateUser(@Valid @ModelAttribute("userMapper") UserMapper userMapper, BindingResult bindingResult, RedirectAttributes atts,@RequestParam("pathImg") MultipartFile multipartFile)  throws IOException {
    //     try {
    //         if(userMapper.getBirth().plusYears(18).isAfter(LocalDate.now())){
    //             bindingResult.addError(new FieldError("userMapper", "birth", "not 18"));

    //         }
    //         if(bindingResult.hasErrors()&&bindingResult.getErrorCount()!=4){
    //             atts.addAttribute("oldData", userMapper);
    //             atts.addAttribute("formType", 0);
    //             return "users/administrator/create-update";
    //         }
    //         if(!multipartFile.getOriginalFilename().isEmpty()){
    //             FileUploadUtil.deleteFile(getUserMapperByID(userMapper.getUser_id()).getThumbnail());
    //             String fileName = Helpers.randomForImageName()+"_"+StringUtils.cleanPath(multipartFile.getOriginalFilename());
    //             userMapper.setThumbnail(fileName);
    //             FileUploadUtil.saveFile("src/main/resources/static/images/users/", fileName, multipartFile);
    //         }

    //         boolean updated = userServiceiInterface.updateUserUserProfile(userMapper);
    //         if(updated){
    //             atts.addFlashAttribute("successMsg", "Successfully update user");
    //         } else {
    //             atts.addFlashAttribute("errorMsg", "Cannot update user");
    //         }
    //     } catch(Exception ex) {
    //         atts.addFlashAttribute("error", ex.getMessage());
    //     }
    //     return "redirect:/admin/users/all?type=1";
    // }
    // public UserMapper getUserMapperByID(int id){
    //     String[] selects = {"users.id as user_id, users.status, users.phone, users.email,users.is_admin, "+
    //         "user_profiles.id as user_profile_id, user_profiles.first_name, user_profiles.last_name, user_profiles.thumbnail, "+
    //         "user_profiles.birth, user_profiles.gender"};
    //         var u = userServiceiInterface.getUserById(selects,id);
    //         UserMapper mapper=new UserMapper(
    //            Integer.parseInt(u.get("user_id").toString()),
    //            Integer.parseInt(u.get("user_profile_id").toString()),
    //             u.get("first_name").toString(),
    //             u.get("last_name").toString(),
    //             u.get("phone").toString(),
    //             u.get("email").toString(),
    //             LocalDate.parse(u.get("birth").toString().subSequence(0, 10)),
    //             Integer.parseInt(u.get("gender").toString()),
    //             u.get("thumbnail")==null?"":u.get("thumbnail").toString(),
    //             Integer.parseInt(u.get("status").toString()),
    //             Boolean.parseBoolean(u.get("is_admin").toString())
    //         );
    //         return mapper;
    // }
 //Pagination
//  @PostMapping("/admin/user/all/LoadDataUser")
//  public @ResponseBody ResponseEntity<List<Map<String,Object>>> LoadDataUser(int p, String cond, int take, int is_admin)
//  {
//      List<Map<String,Object>> emps = LoadData(p, cond, take,is_admin);
//      return new ResponseEntity<List<Map<String,Object>>>(emps, HttpStatus.OK);
//  }

//  public List<Map<String,Object>>  LoadData(int p, String cond, int take,int is_admin)
//  {
//         int currentSkip = take * (p - 1);
//         //select
//         String[] selects = {"users.id,users.phone, users.email, DATE_FORMAT(users.created_at,'%m-%d-%Y %H:%i') as created_at, users.status, users.is_admin,p.first_name, p.last_name,p.ranking"};
//        //join
//         List<JoinCondition> lsJoin = new ArrayList<JoinCondition>();
//         lsJoin.add(JoinCondition.getInstance("left join", "user_profiles p", DataMapper.getInstance("", "users.id", "=", "p.user_id", "")));
//        //limit
//         String[] limit = {String.valueOf(currentSkip),String.valueOf(take==0?1:take)};
//        //condition
//        List<DataMapper> lsCond = new ArrayList<DataMapper>();
//        lsCond.add(DataMapper.getInstance("", "users.is_admin", "=", is_admin==1?"1":"0", ""));
//         if(!cond.isEmpty()&&cond!=null){
//             lsCond.add(DataMapper.getInstance("and (users.email like '%"+cond+"%' or ", "users.phone", "like", "%"+cond+"%", "or p.first_name like '%"+cond+"%' or p.last_name like '%"+cond+"%')"));
//         }
//         var u = userServiceiInterface.getAll(selects, lsCond.size()==0?null:lsCond, lsJoin, null, "users.id desc", limit);
//         return u;
//  }

// @PostMapping("/admin/user/all/GetCount")
//  public @ResponseBody int GetCount(String cond,int is_admin)
//  {
//      //select
//      String[] selects = {"users.phone, users.email, DATE_FORMAT(users.created_at,'%m-%d-%Y %H:%i') as created_at, users.status, users.is_admin,p.first_name, p.last_name,p.ranking"};
//      //join
//       List<JoinCondition> lsJoin = new ArrayList<JoinCondition>();
//       lsJoin.add(JoinCondition.getInstance("left join", "user_profiles p", DataMapper.getInstance("", "users.id", "=", "p.user_id", "")));
//      //condition
//      List<DataMapper> lsCond = new ArrayList<DataMapper>();
//      lsCond.add(DataMapper.getInstance("", "users.is_admin", "=", is_admin==1?"1":"0", ""));
//       if(!cond.isEmpty()&&cond!=null){
//           lsCond.add(DataMapper.getInstance("and (users.email like '%"+cond+"%' or ", "users.phone", "like", "'%"+cond+"%'", "p.first_name like '%"+cond+"%' or p.last_name like '%"+cond+"%')"));
//       }
//       var u = userServiceiInterface.getAll(selects, lsCond, lsJoin, null, null, null);
//       return u.size();
//  }
// @PostMapping("/admin/user/all/RowEvent")
//  public @ResponseBody  int RowEvent(int i, int take)
//  {
//      double pagi = i / Double.parseDouble(String.valueOf(take==0?1:take));
//      double temp = pagi - Math.floor(pagi);
//      return (int)(pagi+(temp>0?1:0));
     
//  }
 //End pagination

}
