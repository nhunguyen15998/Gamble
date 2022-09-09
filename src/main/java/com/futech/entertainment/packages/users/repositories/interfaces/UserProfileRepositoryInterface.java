package com.futech.entertainment.packages.users.repositories.interfaces;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.futech.entertainment.packages.core.repositories.interfaces.BaseRepositoryInterface;
import com.futech.entertainment.packages.core.utils.DataMapper;
import com.futech.entertainment.packages.core.utils.JoinCondition;
import com.futech.entertainment.packages.users.models.UserProfile;

@Component
public interface UserProfileRepositoryInterface extends BaseRepositoryInterface<UserProfile> {
    public List<Map<String,Object>> getListUserProfile(String[] selects, List<DataMapper> conditions, List<JoinCondition> joins, String[] groupBys, String orderBy, String[] limit);
}
