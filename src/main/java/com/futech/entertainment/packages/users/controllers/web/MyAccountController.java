package com.futech.entertainment.packages.users.controllers.web;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.futech.entertainment.packages.users.modelMappers.ChangePasswordMapper;
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
            model.addAttribute("profileURL", request.getRequestURL());
            model.addAttribute("overviewSection", request.getRequestURI());
        } catch (IllegalStateException e){
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "users/profile/information";
    }

    @GetMapping("user/my-account/friend")
    public String getMyAccountFriends(Model model, HttpSession session, RedirectAttributes redirAttrs, HttpServletRequest request){
        if(session.getAttribute("phone") == null){
            redirAttrs.addFlashAttribute("errorMsg", "Sign in first");
            return "redirect:/user/sign-in";
        }
        model.addAttribute("profileURL", request.getRequestURL());
        model.addAttribute("friendSection", request.getRequestURI());
        return "users/profile/friends";
    }

    @GetMapping("user/my-account/statistic")
    public String getMyAccountStatistics(Model model, HttpSession session, RedirectAttributes redirAttrs, HttpServletRequest request){
        if(session.getAttribute("phone") == null){
            redirAttrs.addFlashAttribute("errorMsg", "Sign in first");
            return "redirect:/user/sign-in";
        }
        model.addAttribute("profileURL", request.getRequestURL());
        model.addAttribute("statisticSection", request.getRequestURI());
        return "users/profile/statistics";
    }

    @GetMapping("user/my-account/play-history")
    public String getMyAccountPlayHistory(Model model, HttpSession session, RedirectAttributes redirAttrs, HttpServletRequest request){
        if(session.getAttribute("phone") == null){
            redirAttrs.addFlashAttribute("errorMsg", "Sign in first");
            return "redirect:/user/sign-in";
        }
        model.addAttribute("profileURL", request.getRequestURL());
        model.addAttribute("historySection", request.getRequestURI());
        return "users/profile/play_history";
    }

    @GetMapping("user/my-account/achievement")
    public String getMyAccountAchievement(Model model, HttpSession session, RedirectAttributes redirAttrs, HttpServletRequest request){
        if(session.getAttribute("phone") == null){
            redirAttrs.addFlashAttribute("errorMsg", "Sign in first");
            return "redirect:/user/sign-in";
        }
        model.addAttribute("profileURL", request.getRequestURL());
        model.addAttribute("achievementSection", request.getRequestURI());
        return "users/profile/achievements";
    }

    //change pass
    @PostMapping("user/change-password")
    public ResponseEntity<Map<String, Object>> changePassword(
                @Valid @RequestBody ChangePasswordMapper changePasswordMapper, HttpSession session){
        Map<String, Object> items = new HashMap<String,Object>();
        try {
            var dbUserId = Integer.parseInt(session.getAttribute("user_id").toString());
            changePasswordMapper.setUser_id(dbUserId);
            items = this.userServiceInterface.changePassword(changePasswordMapper);
            if(Integer.parseInt(items.get("code").toString()) == 200){
                items.put("page", "/user/sign-in");
                this.userServiceInterface.sessionClear(session);
            }
            return new ResponseEntity<Map<String, Object>>(items, HttpStatus.OK);
        } catch (Exception e) {
            items.put("code", 500);
            items.put("message", e.getMessage());
            return new ResponseEntity<Map<String, Object>>(items, HttpStatus.BAD_REQUEST);
        }
    }

}
