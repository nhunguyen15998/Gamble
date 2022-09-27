package com.futech.entertainment.packages.users.controllers.admin;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.futech.entertainment.packages.core.utils.DataMapper;
import com.futech.entertainment.packages.core.utils.JoinCondition;
import com.futech.entertainment.packages.users.models.User;
import com.futech.entertainment.packages.users.services.UserService;
import com.futech.entertainment.packages.users.services.interfaces.UserServiceInterface;
import com.rabbitmq.client.AMQP.Confirm.Select;

@Controller
public class UserManaController {
    @Autowired
    private UserServiceInterface userServiceiInterface;
    @GetMapping("/admin/users/all")
    public String ViewUser(Model mdl,@RequestParam int page , @RequestParam int type){
        String[] selects = {"users.phone, users.email, DATE_FORMAT(users.created_at,'%m-%d-%Y %H:%i') as created_at, users.status, users.is_admin,p.first_name, p.last_name,p.ranking"};
        List<JoinCondition> lsJoin = new ArrayList<JoinCondition>();
        lsJoin.add(JoinCondition.getInstance("left join", "user_profiles p", DataMapper.getInstance("", "users.id", "=", "p.user_id", "")));
        var u = userServiceiInterface.getAll(selects, null, lsJoin, null, "users.id desc", null);
        mdl.addAttribute("users",u );
        return "users/administrator/all-user";
    }
}
