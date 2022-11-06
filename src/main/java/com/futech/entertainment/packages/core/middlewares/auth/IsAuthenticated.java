package com.futech.entertainment.packages.core.middlewares.auth;

import java.util.Map;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import com.futech.entertainment.packages.core.middlewares.auth.interfaces.Authentication;
import com.futech.entertainment.packages.users.services.interfaces.UserServiceInterface;

public class IsAuthenticated implements ConstraintValidator<Authentication, Map<String, String>> {

    @Autowired
    private UserServiceInterface userServiceInterface;

    @Override
    public boolean isValid(Map<String, String> value, ConstraintValidatorContext context) {
        try {
            var verifiedToken = this.userServiceInterface.verifyToken(value);
            if(verifiedToken == null){
                return false;
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
