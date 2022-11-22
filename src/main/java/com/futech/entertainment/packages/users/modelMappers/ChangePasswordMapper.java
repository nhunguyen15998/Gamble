package com.futech.entertainment.packages.users.modelMappers;

import javax.validation.constraints.NotBlank;

public class ChangePasswordMapper {
    private int user_id;
    @NotBlank(message = "Current password is required")
    private String old_password;
    @NotBlank(message = "New password is required")
    private String new_password;
    @NotBlank(message = "New confirmation password is required")
    private String confirm_new_password;

    public ChangePasswordMapper() {}

    public int getUser_id() {
        return user_id;
    }
    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getOld_password() {
        return old_password;
    }
    public void setOld_password(String old_password) {
        this.old_password = old_password;
    }

    public String getNew_password() {
        return new_password;
    }
    public void setNew_password(String new_password) {
        this.new_password = new_password;
    }

    public String getConfirm_new_password() {
        return confirm_new_password;
    }
    public void setConfirm_new_password(String confirm_new_password) {
        this.confirm_new_password = confirm_new_password;
    }
    
}

