package com.futech.entertainment.packages.users.controllers.admin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.futech.entertainment.packages.core.utils.DataMapper;
import com.futech.entertainment.packages.core.utils.JoinCondition;
import com.futech.entertainment.packages.users.modelMappers.SignUpMapper;
import com.futech.entertainment.packages.users.models.User;
import com.futech.entertainment.packages.users.services.UserService;
import com.futech.entertainment.packages.users.services.interfaces.UserServiceInterface;
import com.rabbitmq.client.AMQP.Confirm.Select;

@Controller
public class UserManaController {
    @Autowired
    private UserServiceInterface userServiceiInterface;
    @GetMapping("/admin/users/all")
    public String ViewUser(Model mdl,@RequestParam int type){
        mdl.addAttribute("users",LoadData(1,"", 10,type) );
        mdl.addAttribute("is_player",true);
        mdl.addAttribute("is_admin",type);
        mdl.addAttribute("paging", RowEvent(GetCount("",type),10));
        return "users/administrator/all-user";
    } 

    @GetMapping("/admin/users/create")
    public String showCreateForm(Model mdl){
        mdl.addAttribute("obj", new SignUpMapper());
        mdl.addAttribute("formType", 0);
        return "users/administrator/create-update";
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
        String[] selects = {"users.phone, users.email, DATE_FORMAT(users.created_at,'%m-%d-%Y %H:%i') as created_at, users.status, users.is_admin,p.first_name, p.last_name,p.ranking"};
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
     String[] selects = {"users.phone, users.email, DATE_FORMAT(users.created_at,'%m-%d-%Y %H:%i') as created_at, users.status, users.is_admin,p.first_name, p.last_name,p.ranking"};
     //join
      List<JoinCondition> lsJoin = new ArrayList<JoinCondition>();
      lsJoin.add(JoinCondition.getInstance("left join", "user_profiles p", DataMapper.getInstance("", "users.id", "=", "p.user_id", "")));
     //condition
     List<DataMapper> lsCond = new ArrayList<DataMapper>();
     lsCond.add(DataMapper.getInstance("", "users.is_admin", "=", is_admin==1?"1":"0", ""));
      if(!cond.isEmpty()&&cond!=null){
          lsCond.add(DataMapper.getInstance("and (users.email like '%"+cond+"%' or ", "users.phone", "like", "'%"+cond+"%'", "p.first_name like '%"+cond+"%' or p.last_name like '%"+cond+"%')"));
      }
      var u = userServiceiInterface.getAll(selects, lsCond, lsJoin, null, null, null);
      return u.size();
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
