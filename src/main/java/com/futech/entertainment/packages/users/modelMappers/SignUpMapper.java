package com.futech.entertainment.packages.users.modelMappers;

import javax.validation.constraints.NotBlank;

import com.futech.entertainment.packages.core.middlewares.validations.interfaces.Email;
import com.futech.entertainment.packages.core.middlewares.validations.interfaces.IsExisted;
import com.futech.entertainment.packages.core.middlewares.validations.interfaces.Phone;
import com.futech.entertainment.packages.core.middlewares.validations.interfaces.Size;

public class SignUpMapper {
    @NotBlank(message = "First name is required")
    private String first_name;

    @NotBlank(message = "Last name is required")
    private String last_name;

    @IsExisted(message = "User is already existed")
    @NotBlank(message = "Phone is required")
    @Phone(message = "Invalid phone format")
    private String phone;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 15, message = "Password must range from 6 to 15")
    private String plain_password;

    @NotBlank(message = "Password confirmation is required")
    private String confirm_password;

    public SignUpMapper() {}

    public String getfirst_name() {
        return first_name;
    }
    public void setfirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getlast_name() {
        return last_name;
    }
    public void setlast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

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
