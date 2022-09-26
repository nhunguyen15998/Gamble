package com.futech.entertainment.packages.users.modelMappers;

import javax.validation.constraints.NotBlank;

import com.futech.entertainment.packages.core.middlewares.validations.interfaces.Size;

public class ForgotResetMapper {
    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 15, message = "Password must range from 6 to 15")
    private String plain_password;

    @NotBlank(message = "Password confirmation is required")
    private String confirm_password;

    public ForgotResetMapper() {}

    public String getplain_password() {
        return plain_password;
    }
    public void setplain_password(String plain_password) {
        this.plain_password = plain_password;
    }

    public String getconfirm_password() {
        return confirm_password;
    }
    public void setconfirm_password(String confirm_password) {
        this.confirm_password = confirm_password;
    }

}
