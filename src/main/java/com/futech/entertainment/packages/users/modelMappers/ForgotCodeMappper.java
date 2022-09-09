package com.futech.entertainment.packages.users.modelMappers;

import javax.validation.constraints.NotBlank;

public class ForgotCodeMappper {
    @NotBlank(message = "Code is required")
    private String code;

    public ForgotCodeMappper() {}

    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
    
}
