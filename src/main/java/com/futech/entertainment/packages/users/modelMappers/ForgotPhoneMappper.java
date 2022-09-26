package com.futech.entertainment.packages.users.modelMappers;

import javax.validation.constraints.NotBlank;

import com.futech.entertainment.packages.core.middlewares.validations.interfaces.NotExisted;
import com.futech.entertainment.packages.core.middlewares.validations.interfaces.Phone;

public class ForgotPhoneMappper {
    @NotExisted(message = "Invalid phone number")
    @NotBlank(message = "Phone is required")
    @Phone(message = "Invalid phone format")
    private String phone;

    public ForgotPhoneMappper() {}

    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
}
