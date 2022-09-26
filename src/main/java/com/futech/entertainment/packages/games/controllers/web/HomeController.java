package com.futech.entertainment.packages.games.controllers.web;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping("/")
    public String homePage(){
        return "home";
    }

    @GetMapping("/games")
    public String gamePage(){
        return "games";
    }

    // @GetMapping("/games")
    // public String gamePage(){
    //     return "games";
    // }

    //get success
    @GetMapping("/results/success")
    public String successResult(Model model){
        return "results/success";
    }

    //get errors
    @GetMapping("/results/error")
    public String errorResult(Model model){
        return "results/error";
    }
}
