package com.futech.entertainment.packages.core.middlewares.validations;

import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import com.futech.entertainment.packages.core.middlewares.validations.interfaces.IsExisted;
import com.futech.entertainment.packages.core.utils.DataMapper;
import com.futech.entertainment.packages.users.services.interfaces.UserServiceInterface;

public class IsUserExisted implements ConstraintValidator<IsExisted, String>  {

    @Autowired
    private UserServiceInterface userService;

    public IsUserExisted(UserServiceInterface itemsService) {
        this.userService = itemsService;
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        try {
            if(value.trim().length() > 0){
                List<DataMapper> conditions = new ArrayList<DataMapper>();
                conditions.add(DataMapper.getInstance("", "phone", "=", value, ""));
                var dbUser = this.userService.getFirstBy(null, conditions, null);
                if(dbUser != null){
                    System.out.println(dbUser);
                    return false;
                }
                return true;
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
}
