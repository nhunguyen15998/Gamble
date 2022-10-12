package com.futech.entertainment.packages.users.models;

import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Qualifier;

import com.futech.entertainment.packages.core.models.BaseModel;
import com.futech.entertainment.packages.core.utils.Helpers;

@Qualifier("User")
public class User extends BaseModel {

    public User() {}

    private String table = "users";

    private String[] columns = { "id", "phone", "email", "plain_password", "hash_password", "created_at", "status", 
                                 "activate_code", "forgot_password_code", "code_lifespan", "is_admin" };

    public String getTable() {
        return this.table;
    }

    public String[] getColumns() {
        return this.columns;
    }

    private Integer id;
    private String phone;
    private String email;
    private boolean is_admin;
    private String plain_password;
    private String hash_password;
    private LocalDateTime created_at;
    private Integer status;
    private String activate_code;
    private String forgot_password_code;
    private LocalDateTime code_lifespan;

    public Integer getId() {
        return this.id;
    }
    public void setId(Integer id) {
        this.id = id;
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
    public boolean isIs_admin() {
        return is_admin;
    }

    public void setIs_admin(Boolean is_admin) {
        this.is_admin = is_admin;
    }
    public String getplain_password() {
        return plain_password;
    }
    public void setplain_password(String plain_password) {
        this.plain_password = plain_password;
    }

    public String gethash_password() {
        return hash_password;
    }
    public void sethash_password(String hash_password) {
        this.hash_password = hash_password;
    }

    public LocalDateTime getcreated_at() {
        return created_at;
    }
    public void setcreated_at(LocalDateTime created_at) {
        this.created_at = created_at;
    }
    public String getcreated_atToString() {
        return Helpers.formatDateTime(created_at);
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
    public Integer getStatus() {
        return this.status;
    }

    public String getactivate_code() {
        return activate_code;
    }

    public void setactivate_code(String activate_code) {
        this.activate_code = activate_code;
    }

    public String getforgot_password_code() {
        return forgot_password_code;
    }

    public void setforgot_password_code(String forgot_password_code) {
        this.forgot_password_code = forgot_password_code;
    }

    public LocalDateTime getCode_lifespan() {
        return code_lifespan;
    }
    public void setCode_lifespan(LocalDateTime code_lifespan) {
        this.code_lifespan = code_lifespan;
    }

}
