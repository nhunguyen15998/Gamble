package com.futech.entertainment.packages.wallets.controllers.admin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.futech.entertainment.packages.core.utils.DataMapper;
import com.futech.entertainment.packages.core.utils.Helpers;
import com.futech.entertainment.packages.users.services.interfaces.UserServiceInterface;
import com.futech.entertainment.packages.wallets.models.Transaction;
import com.futech.entertainment.packages.wallets.services.interfaces.TransactionServiceInterface;

@Controller
public class TransactionManaController {
    @Autowired
    TransactionServiceInterface transactionServiceInterface;
    @Autowired
    UserServiceInterface userServiceInterface;

    @ModelAttribute("statusList")
    public Map<Integer, String> getStatusList() {
        Map<Integer, String> statusList = new HashMap<Integer, String>();
        statusList.put(1, "Completed");
        statusList.put(0, "Pending");
        statusList.put(2, "Failed");

        return statusList;
    }
    
    @GetMapping("/admin/transactions/all")
    public String showAll(Model mdl, HttpSession session){
        if(session.getAttribute("user_id")==null ||(session.getAttribute("user_id")!=null&&Integer.parseInt(session.getAttribute("is_admin").toString())==0)) return "redirect:/user/sign-in";
        session.setAttribute("title", "All | Transactions");
        mdl.addAttribute("transaction",LoadData(1, 10,-1,-1,-1, Helpers.EMPTY, Helpers.EMPTY) );
        mdl.addAttribute("paging", RowEvent(GetCount(-1,-1,-1,Helpers.EMPTY,Helpers.EMPTY),10));
        return "/transactions/administrator/all";
    }
    @PostMapping("/admin/transactions/update")
    public String updateStatus(@RequestParam int id,@RequestParam int method,@RequestParam String bcaddress,@RequestParam String amount, Transaction transaction, RedirectAttributes attrs){
        var update = transactionServiceInterface.updateBitcoinStatus(id, method, bcaddress, amount, transaction.getStatus());
        if(Integer.parseInt(update.get("code").toString()) == 200){
            attrs.addFlashAttribute("successMsg", update.get("message").toString());
            return "redirect:/admin/transactions/all";
        } 
        attrs.addFlashAttribute("errorMsg", update.get("message").toString());
        return "redirect:/admin/transactions/all";
    }
    @GetMapping("/admin/transactions/view")
    public String viewTransaction(Model mdl, @RequestParam String code, HttpSession session){
        if(session.getAttribute("user_id")==null ||(session.getAttribute("user_id")!=null&&Integer.parseInt(session.getAttribute("is_admin").toString())==0)) return "redirect:/user/sign-in";

        String[] selects = {"first_name, last_name, is_admin"};
        var transation = transactionServiceInterface.getTransactionByCode(code);
        System.out.println(transation.get("received_amount"));
        var sender = transation.get("sender")!=null ? userServiceInterface.getUserById(selects, Integer.parseInt(transation.get("sender").toString())):null;
        var receiver = transation.get("receiver")!=null ? userServiceInterface.getUserById(selects, Integer.parseInt(transation.get("receiver").toString())):null;
        String typeTrans = Helpers.EMPTY, methodTrans = Helpers.EMPTY;
        switch(Integer.parseInt(transation.get("type").toString())) {
            case 0:
               typeTrans = "Deposit";
               methodTrans = Integer.parseInt(transation.get("method").toString()) == 0 ? "VNPAY" : (Integer.parseInt(transation.get("method").toString()) == 1 ? "MOMO" : "BTC");
               break;
            case 1: 
                typeTrans = "Withdraw";
                methodTrans = Integer.parseInt(transation.get("method").toString()) == 1 ? "Bank" : "BTC";
                break;
            case 2:
                typeTrans = "Transfer";
                methodTrans = "None";
                break;
        }
        session.setAttribute("title", typeTrans == "Withdraw"?"Update | Transactions":"View | Transactions");

        mdl.addAttribute("transaction", transation);
        mdl.addAttribute("method", methodTrans);
        mdl.addAttribute("sender", sender);
        mdl.addAttribute("receiver", receiver);
        mdl.addAttribute("type", typeTrans);
        // mdl.addAttribute("bcaddress", transation.get("bcaddress").toString());
        return "/transactions/administrator/view";
    }
     //Pagination
 @PostMapping("/admin/transactions/all/LoadDataTransaction")
 public @ResponseBody ResponseEntity<List<Map<String,Object>>> LoadDataTransaction(int p,  int take, int type, int method, int status, String from, String to )
 {
     List<Map<String,Object>> objs = LoadData(p, take,type,method,status, from,to);
     return new ResponseEntity<List<Map<String,Object>>>(objs, HttpStatus.OK);
 }

 public List<Map<String,Object>>  LoadData(int p, int take, int type, int method, int status,  String from, String to)
 {
        int currentSkip = take * (p - 1);
        //select
        String[] selects = {"transactions.id,code,type,method,received_amount, DATE_FORMAT(transactions.created_at,'%d-%m-%Y %H:%i') as created_at, transactions.status,amount"};
      
        String[] limit = {String.valueOf(currentSkip),String.valueOf(take==0?1:take)};
       //condition
       List<DataMapper> lsCond = new ArrayList<DataMapper>();
       if(type!=-1)  lsCond.add(DataMapper.getInstance(Helpers.EMPTY,"type","=",""+type+"",Helpers.EMPTY));
       if(method!=-1)  lsCond.add(DataMapper.getInstance(lsCond.size()>0?"and ":"","method","=",""+method+"",Helpers.EMPTY));
       if(status!=-1)  lsCond.add(DataMapper.getInstance(lsCond.size()>0?"and ":"","status","=",""+status+"",Helpers.EMPTY));
       if(!from.isEmpty())  lsCond.add(DataMapper.getInstance(lsCond.size()>0?"and ":"","DATE_FORMAT(created_at,'%Y-%m-%d')",">=",""+from+"",Helpers.EMPTY));
       if(!to.isEmpty())  lsCond.add(DataMapper.getInstance(lsCond.size()>0?"and ":"","DATE_FORMAT(created_at,'%Y-%m-%d')","<=",""+to+"",Helpers.EMPTY));
    
        var u = transactionServiceInterface.getAll(selects, lsCond.size()==0?null:lsCond, null, null, "transactions.id desc", limit);
        return u;
 }

@PostMapping("/admin/transactions/all/GetCount")
 public @ResponseBody int GetCount(int type, int method, int status, String from, String to)
 {
    //select
    String[] selects = {"transactions.id,code,type,method, DATE_FORMAT(transactions.created_at,'%d-%m-%Y %H:%i') as created_at, transactions.status,amount"};
      
     //condition
    List<DataMapper> lsCond = new ArrayList<DataMapper>();
    if(type!=-1)  lsCond.add(DataMapper.getInstance(Helpers.EMPTY,"type","=",""+type+"",Helpers.EMPTY));
    if(method!=-1)  lsCond.add(DataMapper.getInstance(lsCond.size()>0?"and ":"","method","=",""+method+"",Helpers.EMPTY));
    if(status!=-1)  lsCond.add(DataMapper.getInstance(lsCond.size()>0?"and ":"","status","=",""+status+"",Helpers.EMPTY));
    if(!from.isEmpty())  lsCond.add(DataMapper.getInstance(lsCond.size()>0?"and ":"","DATE_FORMAT(created_at,'%Y-%m-%d')",">=",""+from+"",Helpers.EMPTY));
    if(!to.isEmpty())  lsCond.add(DataMapper.getInstance(lsCond.size()>0?"and ":"","DATE_FORMAT(created_at,'%Y-%m-%d')","<=",""+to+"",Helpers.EMPTY));
    
    var u = transactionServiceInterface.getAll(selects, lsCond.size()==0?null:lsCond, null, null, "transactions.id desc", null);
    return u==null?0:u.size();
 }

@PostMapping("/admin/transactions/all/RowEvent")
 public @ResponseBody  int RowEvent(int i, int take)
 {
     double pagi = i / Double.parseDouble(String.valueOf(take==0?1:take));
     double temp = pagi - Math.floor(pagi);
     return (int)(pagi+(temp>0?1:0));
     
 }
}
