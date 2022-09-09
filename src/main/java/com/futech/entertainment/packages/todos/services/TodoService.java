package com.futech.entertainment.packages.todos.services;
import org.springframework.stereotype.Service;

import com.futech.entertainment.packages.core.services.BaseService;
import com.futech.entertainment.packages.todos.models.Todo;
import com.futech.entertainment.packages.todos.services.interfaces.TodoServiceInterface;

@Service
public class TodoService extends BaseService<Todo> implements TodoServiceInterface{

}
