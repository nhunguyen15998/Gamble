package com.futech.entertainment.packages.core.services;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.futech.entertainment.packages.core.models.BaseModel;
import com.futech.entertainment.packages.core.repositories.interfaces.BaseRepositoryInterface;
import com.futech.entertainment.packages.core.services.interfaces.BaseServiceInterface;
import com.futech.entertainment.packages.core.utils.DataMapper;
import com.futech.entertainment.packages.core.utils.JoinCondition;

@Service
public class BaseService<T extends BaseModel> implements BaseServiceInterface<T> {

    @Autowired
    private BaseRepositoryInterface<T> baseRepository;

    
    @Override
    public T create(T model) {
        return this.baseRepository.create(model);
    }

    @Override
    public Map<String,Object> getFirstBy(String[] selects, List<DataMapper> conditions, List<JoinCondition> joins) {
        return this.baseRepository.getFirstBy(selects, conditions, joins);
    }

    @Override
    public T getFirst(String[] selects, List<DataMapper> conditions, List<JoinCondition> joins) {
        return this.baseRepository.getFirst(selects, conditions, joins);
    }

    @Override
    public List<Map<String,Object>> getAll(String[] selects, List<DataMapper> conditions, List<JoinCondition> joins, String[] groupBys, String orderBy, String[] limit) {
        return this.baseRepository.getAll(selects, conditions, joins, groupBys, orderBy, limit);
    }

    @Override
    public Boolean delete(int id) {
        return this.baseRepository.delete(id);
    }

    @Override
    public Map<String,Object> find(int id) {
        return this.baseRepository.find(id);
    }

    @Override
    public Boolean update(T model) {
        return this.baseRepository.update(model);
    }
    
}
