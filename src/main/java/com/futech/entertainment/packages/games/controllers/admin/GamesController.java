package com.futech.entertainment.packages.games.controllers.admin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

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
import com.futech.entertainment.packages.core.utils.JoinCondition;
import com.futech.entertainment.packages.games.modelMappers.WheelMapper;
import com.futech.entertainment.packages.games.services.interfaces.GameServiceInterface;
import com.futech.entertainment.packages.wheels.controllers.web.WheelController;
import com.futech.entertainment.packages.wheels.services.interfaces.WheelServiceInterface;
import com.futech.entertainment.packages.wheels.utils.WheelHelpers;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
@Controller
public class GamesController {
    @Autowired 
    GameServiceInterface gameServiceInterface;
    @Autowired 
    WheelServiceInterface wheelServiceInterface;
    @GetMapping("/admin/games/all")
    public String ViewGames(Model mdl, HttpSession session){
        if(session.getAttribute("user_id")==null) return "redirect:/user/sign-in";

        mdl.addAttribute("games",LoadData(1,Helpers.EMPTY, 10) );
        mdl.addAttribute("paging", RowEvent(GetCount(Helpers.EMPTY),10));
        return "games/administrator/all";
    } 
    @ModelAttribute("statusList")
    public Map<Integer, String> getStatusList() {
        Map<Integer, String> statusList = new HashMap<Integer, String>();
        statusList.put(Helpers.ACTIVATED, "Activated");
        statusList.put(Helpers.DEACTIVATED, "Deactivated");

        return statusList;
    }
    @GetMapping("/admin/games/update")
    public String showFormUpdate(@RequestParam String code, Model model, RedirectAttributes atts, HttpSession session)
    {
        try{
            if(session.getAttribute("user_id")==null) return "redirect:/user/sign-in";
            
            model.addAttribute("gameMapper",getWheel(code));
            model.addAttribute("formType", 1);
            return "games/administrator/create-update";
        } catch(Exception ex){
            atts.addFlashAttribute("error", ex.getMessage());
            return "/admin/games/all";

        }
    }
    public WheelMapper getWheel(String code){
        var q = gameServiceInterface.getGameByCode(code);
        WheelMapper temp= new WheelMapper();
        temp.setId(Integer.parseInt(q.get("id").toString()));
        temp.setCode(code);
        temp.setName(q.get("name").toString());
        temp.setConfigs(q.get("configs")!=null?q.get("configs").toString():"");
        temp.setStatus(Integer.parseInt(q.get("status").toString()));
        return temp;
    }
    @PostMapping("/admin/games/update")
    public ResponseEntity<Map<String, Object>> updateConfig(@Valid @RequestBody WheelMapper wheelMapper)
    {
        
        Map<String, Object> items = new HashMap<String,Object>();
        
        boolean updated = wheelServiceInterface.updateWheel(wheelMapper);

        return new ResponseEntity<Map<String, Object>>(items, HttpStatus.OK);

    }

    //Pagination
 @PostMapping("/admin/games/all/LoadDataGame")
 public @ResponseBody ResponseEntity<List<Map<String,Object>>> LoadDataGame(int p, String cond, int take)
 {
     List<Map<String,Object>> objs = LoadData(p, cond, take);
     return new ResponseEntity<List<Map<String,Object>>>(objs, HttpStatus.OK);
 }

 public List<Map<String,Object>>  LoadData(int p, String cond, int take)
 {
        int currentSkip = take * (p - 1);
        //select
        String[] selects = {"games.id,code,name, DATE_FORMAT(games.created_at,'%d-%m-%Y %H:%i') as created_at, games.status,configs"};
      
        String[] limit = {String.valueOf(currentSkip),String.valueOf(take==0?1:take)};
       //condition
       List<DataMapper> lsCond = new ArrayList<DataMapper>();
        if(!cond.isEmpty()&&cond!=null){
            lsCond.add(DataMapper.getInstance(" code like '%"+cond+"%' or ", "name", "like", "%"+cond+"%", ""));
        }
        var u = gameServiceInterface.getAll(selects, lsCond.size()==0?null:lsCond, null, null, "games.id desc", limit);
        return u;
 }

@PostMapping("/admin/games/all/GetCount")
 public @ResponseBody int GetCount(String cond)
 {
    //select
    String[] selects = {"games.id,code,name, DATE_FORMAT(games.created_at,'%d-%m-%Y %H:%i') as created_at, games.status,configs"};
      
   //condition
   List<DataMapper> lsCond = new ArrayList<DataMapper>();
    if(!cond.isEmpty()&&cond!=null){
        lsCond.add(DataMapper.getInstance(" code like '%"+cond+"%' or ", "name", "like", "%"+cond+"%", ""));
    }
    var u = gameServiceInterface.getAll(selects, lsCond.size()==0?null:lsCond, null, null, "games.id desc", null);
    return u==null?0:u.size();
 }

@PostMapping("/admin/games/all/RowEvent")
 public @ResponseBody  int RowEvent(int i, int take)
 {
     double pagi = i / Double.parseDouble(String.valueOf(take==0?1:take));
     double temp = pagi - Math.floor(pagi);
     return (int)(pagi+(temp>0?1:0));
     
 }
 //End pagination
}
