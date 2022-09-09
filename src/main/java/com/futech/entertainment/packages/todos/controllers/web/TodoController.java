package com.futech.entertainment.packages.todos.controllers.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.futech.entertainment.packages.core.utils.Helpers;
import com.futech.entertainment.packages.todos.modelMappers.TodoMapper;
import com.futech.entertainment.packages.todos.models.Todo;
import com.futech.entertainment.packages.todos.services.interfaces.TodoServiceInterface;

@Controller
public class TodoController {
    @Autowired
    private TodoServiceInterface todoService;

    public TodoController(TodoServiceInterface itemsService) {
        this.todoService = itemsService;
    }

    @ModelAttribute("statusList")
    public Map<Integer, String> getStatusList() {
        Map<Integer, String> statusList = new HashMap<Integer, String>();
        statusList.put(Helpers.INCOMPLETE, "Incomplete");
        statusList.put(Helpers.COMPLETED, "Completed");
        return statusList;
    }

    @GetMapping("/todos")
    public String list(Model model) {
    //     List<DataMapper> conditions = new ArrayList<DataMapper>();
    //     conditions.add(new DataMapper("", "name", "=", "hahaj", ""));
        List<Map<String, Object>> todos = this.todoService.getAll(null, null, null, null, null, null);
        model.addAttribute("todos", todos);
        return "todos/list";
    }

    @GetMapping("todos/create")
    public String getCreate(Model model) {
        model.addAttribute("todoMapper", new TodoMapper());
        return "todos/create";
    }

    @PostMapping("todos/create")
    public String postCreate(@Valid @ModelAttribute("todoMapper") TodoMapper todoMapper, BindingResult bindingResult,
                              RedirectAttributes atts, Model model) {
        try {
            if (bindingResult.hasErrors()) {
                model.addAttribute("oldData", todoMapper);
                return "todos/create";
            }
            Todo item = new Todo(todoMapper.getName(), todoMapper.getStatus());
            var result = this.todoService.create(item);

            var id = result.getId();
            var name = result.getName();
            var status = result.getStatus();

            return "redirect:/todos";
        } catch (Exception e) {
            return "redirect:/todos";
        }

    }

    @GetMapping("todos/update/{todoId}")
    public String getUpdate(@PathVariable int todoId, Model model, RedirectAttributes redirAttrs) {
        Map<String, Object> result = this.todoService.find(todoId);
        if (result.isEmpty()) {
            redirAttrs.addFlashAttribute("message", "Todo object was not found");
            return "redirect:/todos";
        }
        int id = (int) result.get("id");
        String name = (String) result.get("name");
        int status = (int) result.get("status");
        model.addAttribute("todoMapper", new TodoMapper(id, name, status));
        return "todos/update";
    }

    @PostMapping("todos/update/{todoId}")
    public String update(@PathVariable int todoId, @Valid @ModelAttribute("todoMapper") TodoMapper todoMapper, BindingResult bindingResult,
                          RedirectAttributes redirAttrs, Model model) {
        try {
            if (bindingResult.hasErrors()) {
                model.addAttribute("id", todoId);
                return "todos/update";
            }
            Todo item = new Todo(todoId, todoMapper.getName(), todoMapper.getStatus());
            boolean updated = this.todoService.update(item);
            redirAttrs.addFlashAttribute("message", "Update success!");
            if(!updated){
                redirAttrs.addFlashAttribute("message", "Update failed!");
            }
            return "redirect:/todos";
        } catch (Exception e) {
            redirAttrs.addFlashAttribute("message", "Update failed!");
            return "redirect:/todos/update/"+todoId;
        }
    }

    @PostMapping("todos/delete/{todoId}")
    public String delete(@PathVariable int todoId, RedirectAttributes redirAttrs){
        try {
            Map<String, Object> result = this.todoService.find(todoId);
            if (result.isEmpty()) {
                redirAttrs.addFlashAttribute("message", "Todo object was not found");
            }
            boolean deleted = this.todoService.delete(todoId);
            redirAttrs.addFlashAttribute("message", "Delete success!");
            if(!deleted){
                redirAttrs.addFlashAttribute("message", "Delete failed!");
            }
            return "redirect:/todos";
        } catch (Exception e) {
            redirAttrs.addFlashAttribute("message", "Delete failed!");
            return "redirect:/todos";
        }
    }
}
