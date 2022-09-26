package com.futech.entertainment.packages.core.middlewares.validations;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.futech.entertainment.packages.core.middlewares.validations.interfaces.Size;

public class FormatSize implements ConstraintValidator<Size, String>  {

    int min;
	int max;

    @Override
    public void initialize(Size constraintAnnotation) {
        this.min = constraintAnnotation.min();
        this.max = constraintAnnotation.max();
    }
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        try {
            if(value.trim().length() > 0){
                if(value.trim().length() >= this.min && value.trim().length() <= this.max){
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
