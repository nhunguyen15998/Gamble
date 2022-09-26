package com.futech.entertainment.packages.core.middlewares.validations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import com.futech.entertainment.packages.core.middlewares.validations.interfaces.NotExisted;
import com.futech.entertainment.packages.core.utils.DataMapper;
import com.futech.entertainment.packages.core.utils.Helpers;
import com.futech.entertainment.packages.users.services.interfaces.UserServiceInterface;

public class IsNotExistedUser implements ConstraintValidator<NotExisted, String>  {

    @Autowired
    private UserServiceInterface userService;

    public IsNotExistedUser(UserServiceInterface itemsService) {
        this.userService = itemsService;
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        try {
            if(value.trim().length() > 0 && Pattern.compile("^[0][3|5|7|8|9][0-9]{8}$").matcher(value).matches()){
                List<DataMapper> conditions = new ArrayList<DataMapper>();
                conditions.add(DataMapper.getInstance("", "phone", "=", value, ""));
                conditions.add(DataMapper.getInstance("and", "users.status", "=", String.valueOf(Helpers.ACTIVATED), ""));
                var dbUser = this.userService.getFirstBy(null, conditions, null);
                if(dbUser != null){
                    System.out.println(dbUser);
                    return true;
                }
                return false;
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
}
