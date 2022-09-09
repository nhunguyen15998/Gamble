package com.futech.entertainment.packages.core.validations;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.futech.entertainment.packages.core.validations.interfaces.Email;

public class IsEmail implements ConstraintValidator<Email, String>  {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        try {
            if(value.trim().length() > 0){
                Pattern pattern = Pattern.compile("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
                Matcher matcher = pattern.matcher(value);
                if (!matcher.matches()) {
                    return false;
                } else {
                    return true;
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
}
