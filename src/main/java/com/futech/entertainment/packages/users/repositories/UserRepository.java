package com.futech.entertainment.packages.users.repositories;

import com.futech.entertainment.packages.users.repositories.interfaces.UserRepositoryInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.futech.entertainment.packages.core.repositories.BaseRepository;
import com.futech.entertainment.packages.core.utils.DataMapper;
import com.futech.entertainment.packages.core.utils.JoinCondition;
import com.futech.entertainment.packages.users.models.User;

@Repository
public class UserRepository extends BaseRepository<User> implements UserRepositoryInterface{

    public UserRepository(){
        super();
        this.setModel(new User());
    }

    @Override
    public List<Map<String,Object>> getListUser(String[] selects, List<DataMapper> conditions, List<JoinCondition> joins, String[] groupBys, String orderBy, String[] limit) {
        return this.getAll(selects, conditions, joins, groupBys, orderBy, limit);
    }



}