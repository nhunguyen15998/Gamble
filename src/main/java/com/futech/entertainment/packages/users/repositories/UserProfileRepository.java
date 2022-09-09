package com.futech.entertainment.packages.users.repositories;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.futech.entertainment.packages.core.repositories.BaseRepository;
import com.futech.entertainment.packages.core.utils.DataMapper;
import com.futech.entertainment.packages.core.utils.JoinCondition;
import com.futech.entertainment.packages.users.models.UserProfile;
import com.futech.entertainment.packages.users.repositories.interfaces.UserProfileRepositoryInterface;

@Repository
public class UserProfileRepository extends BaseRepository<UserProfile> implements UserProfileRepositoryInterface{

    public UserProfileRepository(){
        super();
        this.setModel(new UserProfile());
    }

    @Override
    public List<Map<String, Object>> getListUserProfile(String[] selects, List<DataMapper> conditions,
            List<JoinCondition> joins, String[] groupBys, String orderBy, String[] limit) {
        // TODO Auto-generated method stub
        return null;
    }




}