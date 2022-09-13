package com.futech.entertainment.packages.users.controllers.web;

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
    public String home(Model model, HttpSession session, HttpServletResponse response){
        // if(session.getAttribute("phone") != null){
        //     Cookie firstCookie = new Cookie("ID1", session.getAttribute("phone").toString());
        //     firstCookie.setMaxAge(7*24*60*60);
        //     firstCookie.setSecure(true);
        //     firstCookie.setPath("/");
        //     firstCookie.setHttpOnly(false);
        //     Cookie secondCookie = new Cookie("ID2", session.getAttribute("user_id").toString());
        //     secondCookie.setMaxAge(7*24*60*60);
        //     secondCookie.setSecure(true);
        //     secondCookie.setPath("/");
        //     secondCookie.setHttpOnly(false);
        //     response.addCookie(firstCookie);
        //     response.addCookie(secondCookie);
        // }
        // try {
        //     Clip clip = AudioSystem.getClip();
        //     clip.stop();
        // } catch (LineUnavailableException e) {
        //     e.printStackTrace();
        // }
        return "home";
    }

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
