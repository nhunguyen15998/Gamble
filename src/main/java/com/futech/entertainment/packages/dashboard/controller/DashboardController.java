package com.futech.entertainment.packages.dashboard.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
@Controller
public class DashboardController {
    @GetMapping("/admin/dashboard")
    public String Admin(){
        return "dashboard/dashboard";
    }

    @GetMapping("/admin/lockscreen")
    public String LockScreen(){
        return "users/administrator/lockscreen";
    }
}
