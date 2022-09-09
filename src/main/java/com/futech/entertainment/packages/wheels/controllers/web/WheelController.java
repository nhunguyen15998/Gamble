package com.futech.entertainment.packages.wheels.controllers.web;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.futech.entertainment.packages.wallets.services.interfaces.UserWalletServiceInterface;

@Controller
public class WheelController {

    @Autowired
    private UserWalletServiceInterface userWalletServiceInterface;
    
    @GetMapping("/games/wheels")
    public String wheelIndex(Model model, HttpSession session){
        var userId = session.getAttribute("user_id");
        if(userId == null){
            return "redirect:/";
        }
        var balance = this.userWalletServiceInterface.getUserBalance(userId.toString());
        model.addAttribute("balance", balance);
        return "wheels/index";
    }
}
