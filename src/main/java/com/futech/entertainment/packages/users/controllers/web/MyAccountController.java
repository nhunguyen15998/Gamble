package com.futech.entertainment.packages.users.controllers.web;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.futech.entertainment.packages.users.modelMappers.UserProfileMapper;
import com.futech.entertainment.packages.users.services.interfaces.UserServiceInterface;

@Controller
public class MyAccountController {
    
    @Autowired
    private UserServiceInterface userServiceInterface;
    
    @GetMapping("user/my-account/information")
    public String getMyAccountOverview(Model model, HttpSession session, HttpServletRequest request, RedirectAttributes redirAttrs){
        if(session.getAttribute("phone") == null){
            redirAttrs.addFlashAttribute("errorMsg", "Sign in first");
            return "redirect:/user/sign-in";
        }
        String phone = session.getAttribute("phone").toString();
        try {
            String[] selects = {"users.id as user_id, users.phone, users.email, users.status, "+
            "user_profiles.id as user_profile_id, user_profiles.first_name, user_profiles.last_name, user_profiles.thumbnail, "+
            "user_profiles.birth, user_profiles.gender"};
            var user = this.userServiceInterface.getUserByPhone(selects, phone);
            UserProfileMapper userProfileMapper = new UserProfileMapper();
            userProfileMapper.setId(Integer.parseInt(user.get("user_profile_id").toString()));
            userProfileMapper.setuser_id(Integer.parseInt(user.get("user_id").toString()));
            userProfileMapper.setfirst_name(user.get("first_name").toString());
            userProfileMapper.setlast_name(user.get("last_name").toString());
            userProfileMapper.setPhone(user.get("phone").toString());
            userProfileMapper.setEmail(user.get("email").toString());
            userProfileMapper.setThumbnail(user.get("thumbnail") != null ? user.get("thumbnail").toString() : "");
            userProfileMapper.setBirth(user.get("birth").toString().substring(0, 10));
            userProfileMapper.setGender(Integer.parseInt(user.get("gender").toString()));
            model.addAttribute("userProfileMapper", userProfileMapper);
            Map<Integer, String> genders = new HashMap<Integer, String>();
            genders.put(0, "male");
            genders.put(1, "female");
            genders.put(2, "other");
            model.addAttribute("genders", genders);
        } catch (IllegalStateException e){
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "users/profile/information";
    }

    @GetMapping("user/my-account/friend")
    public String getMyAccountFriends(Model model, HttpSession session, RedirectAttributes redirAttrs){
        if(session.getAttribute("phone") == null){
            redirAttrs.addFlashAttribute("errorMsg", "Sign in first");
            return "redirect:/user/sign-in";
        }
        return "users/profile/friends";
    }

    @GetMapping("user/my-account/statistic")
    public String getMyAccountStatistics(Model model, HttpSession session, RedirectAttributes redirAttrs){
        if(session.getAttribute("phone") == null){
            redirAttrs.addFlashAttribute("errorMsg", "Sign in first");
            return "redirect:/user/sign-in";
        }
        return "users/profile/statistics";
    }

    @GetMapping("user/my-account/play-history")
    public String getMyAccountPlayHistory(Model model, HttpSession session, RedirectAttributes redirAttrs){
        if(session.getAttribute("phone") == null){
            redirAttrs.addFlashAttribute("errorMsg", "Sign in first");
            return "redirect:/user/sign-in";
        }
        return "users/profile/play_history";
    }

    @GetMapping("user/my-account/achievement")
    public String getMyAccountAchievement(Model model, HttpSession session, RedirectAttributes redirAttrs){
        if(session.getAttribute("phone") == null){
            redirAttrs.addFlashAttribute("errorMsg", "Sign in first");
            return "redirect:/user/sign-in";
        }
        return "users/profile/achievements";
    }


}
