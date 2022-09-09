package com.futech.entertainment.packages.todos.repositories.interfaces;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.futech.entertainment.packages.core.repositories.interfaces.BaseRepositoryInterface;
import com.futech.entertainment.packages.core.utils.DataMapper;
import com.futech.entertainment.packages.core.utils.JoinCondition;
import com.futech.entertainment.packages.todos.models.Todo;

@Component
public interface TodoRepositoryInterface extends BaseRepositoryInterface<Todo> {
    public List<Map<String,Object>> getListTodo(String[] selects, List<DataMapper> conditions, List<JoinCondition> joins, String[] groupBys, String orderBy, String[] limit);
}
