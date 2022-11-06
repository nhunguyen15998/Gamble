package com.futech.entertainment.packages.dashboard.controller;

import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.futech.entertainment.packages.core.utils.DataMapper;
import com.futech.entertainment.packages.games.services.interfaces.GameHistoryServiceInterface;
import com.futech.entertainment.packages.users.services.UserService;
import com.futech.entertainment.packages.users.services.interfaces.UserServiceInterface;
import com.futech.entertainment.packages.wallets.models.Transaction;
import com.futech.entertainment.packages.wallets.services.interfaces.TransactionServiceInterface;
@Controller
public class DashboardController {
    @Autowired
    UserServiceInterface userServiceInterface;
    @Autowired
    TransactionServiceInterface transactionServiceInterface;
    @Autowired
    GameHistoryServiceInterface gameHistoryServiceInterface;
    @GetMapping("/admin/dashboard")
    public String Admin(HttpSession session, Model model){
        if(session.getAttribute("user_id")==null) return "redirect:/user/sign-in";
        //Total player compared to last month
        // List<DataMapper> lsCond = new ArrayList<DataMapper>();
        // lsCond.add(DataMapper.getInstance("", "created_at", ">=",LocalDate.now().minusMonths(1).toString(), null));
        // lsCond.add(DataMapper.getInstance("", "created_at", "<=",LocalDate.now().toString(), null));
        // var lsPlayers = userServiceInterface.getCount(null, lsCond, null);
        List<DataMapper> lsCond = new ArrayList<DataMapper>();
        lsCond.add(DataMapper.getInstance("", "created_at", ">=",LocalDate.now().with(TemporalAdjusters.firstDayOfMonth()).toString(), "and"));
        lsCond.add(DataMapper.getInstance("", "created_at", "<=",LocalDate.now().with(TemporalAdjusters.lastDayOfMonth()).toString(), ""));
        //Plays in a month
        var countPlays = gameHistoryServiceInterface.getCount(null, lsCond, null);
        //Total player compared to last month
        //Total deposited
        String[] select ={"sum(amount) as sumAmount"};
        lsCond.add(DataMapper.getInstance("and", "status", "=","1", ""));
        var countPlayers = userServiceInterface.getCount(null, lsCond, null);

        var totalDeposited = transactionServiceInterface.getFirstBy(select, lsCond, null);
        model.addAttribute("countPlayers", countPlayers);
        model.addAttribute("countPlays", countPlays);
        model.addAttribute("sumAmount", totalDeposited==null?0: totalDeposited.get("sumAmount"));
        return "dashboard/dashboard";
    }

    @GetMapping("/admin/lockscreen")
    public String LockScreen(){
        return "users/administrator/lockscreen";
    }
}
