package com.futech.entertainment.packages.wallets.modelMappers;

import javax.validation.constraints.NotNull;

public class DepositMapper {

    @NotNull(message = "Amount is required")
    private String amount;
    private String code;

    private String language;
    private String bankcode;

    @NotNull(message = "Type is required")
    private Integer type;
    @NotNull(message = "Method is required")
    private Integer method;
    private String title;
    private String note;
    private Integer sender;
    
    public DepositMapper() {}

    public String getAmount() {
        return amount;
    }
    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
    
    public String getLanguage() {
        return language;
    }
    public void setLanguage(String language) {
        this.language = language;
    }

    public String getBankcode() {
        return bankcode;
    }
    public void setBankcode(String bankcode) {
        this.bankcode = bankcode;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getNote() {
        return note;
    }
    public void setNote(String note) {
        this.note = note;
    }
    
    public Integer getType() {
        return type;
    }
    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getMethod() {
        return method;
    }
    public void setMethod(Integer method) {
        this.method = method;
    }

    public Integer getSender() {
        return sender;
    }
    public void setSender(Integer sender) {
        this.sender = sender;
    }
}
