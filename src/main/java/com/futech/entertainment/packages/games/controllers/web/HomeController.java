package com.futech.entertainment.packages.games.controllers.web;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping("/")
    public String homePage(HttpServletRequest request, Model model) throws IOException{
        model.addAttribute("homeURL", request.getRequestURL());
        return "games/home";
    }

    @GetMapping("/games")
    public String gamePage(HttpServletRequest request, Model model) throws IOException {
        model.addAttribute("gameURL", request.getRequestURL());
        return "games/games";
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

    //get 404
    @GetMapping("/results/not-found")
    public String pageNotFound(){
        return "results/404";
    }
}
