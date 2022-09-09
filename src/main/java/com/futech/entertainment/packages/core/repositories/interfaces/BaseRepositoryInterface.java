package com.futech.entertainment.packages.core.repositories.interfaces;

import java.util.List;
import java.util.Map;

import com.futech.entertainment.packages.core.utils.DataMapper;
import com.futech.entertainment.packages.core.utils.JoinCondition;

public interface BaseRepositoryInterface<T> {

    public T create(T model);

    public Boolean update(T model);
    
    public Map<String,Object> find(int id);

    public Map<String,Object> getFirstBy(String[] selects, List<DataMapper> conditions, List<JoinCondition> joins);

    public T getFirst(String[] selects, List<DataMapper> conditions, List<JoinCondition> joins);

    public List<Map<String,Object>> getAll(String[] selects, List<DataMapper> conditions, List<JoinCondition> joins, String[] groupBys, String orderBy, String[] limit);

    public Boolean delete(int id);

}
