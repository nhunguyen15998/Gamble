package com.futech.entertainment.packages.todos.repositories;

import com.futech.entertainment.packages.todos.repositories.interfaces.TodoRepositoryInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.futech.entertainment.packages.core.repositories.BaseRepository;
import com.futech.entertainment.packages.core.utils.DataMapper;
import com.futech.entertainment.packages.core.utils.JoinCondition;
import com.futech.entertainment.packages.todos.models.Todo;

@Repository
public class TodoRepository extends BaseRepository<Todo> implements TodoRepositoryInterface{

    @Autowired
    public TodoRepository(){
        super();
        this.setModel(new Todo());
    }

    @Override
    public List<Map<String,Object>> getListTodo(String[] selects, List<DataMapper> conditions, List<JoinCondition> joins, String[] groupBys, String orderBy, String[] limit) {
        return this.getAll(selects, conditions, joins, groupBys, orderBy, limit);
    }



}