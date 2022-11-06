package com.futech.entertainment.packages.core.middlewares.auth.interfaces;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import com.futech.entertainment.packages.core.middlewares.auth.IsAuthenticated;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Constraint(validatedBy = { IsAuthenticated.class })
@Target(PARAMETER)
@Retention(RUNTIME)
public @interface Authentication {
    
    String message() default "";

    boolean required() default true;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
} 
