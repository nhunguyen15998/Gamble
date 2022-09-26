package com.futech.entertainment.packages.users.modelMappers;

import javax.validation.constraints.NotBlank;

import com.futech.entertainment.packages.core.middlewares.validations.interfaces.NotExisted;
import com.futech.entertainment.packages.core.middlewares.validations.interfaces.Phone;

public class SignInMapper {
    @NotExisted(message = "Invalid phone number")
    @NotBlank(message = "Phone is required")
    @Phone(message = "Invalid phone format")
    private String phone;

    @NotBlank(message = "Password is required")
    private String plain_password;

    public SignInMapper() {}

    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getplain_password() {
        return plain_password;
    }
    public void setplain_password(String plain_password) {
        this.plain_password = plain_password;
    }

}
