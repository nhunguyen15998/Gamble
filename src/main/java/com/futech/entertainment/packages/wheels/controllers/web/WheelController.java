package com.futech.entertainment.packages.wheels.controllers.web;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WheelController {
    
    @GetMapping("/games/wheels")
    public String wheelIndex(Model model, HttpSession session){
        var userId = session.getAttribute("user_id");
        if(userId == null){
            return "redirect:/";
        }
        return "wheels/index";
    }
}
