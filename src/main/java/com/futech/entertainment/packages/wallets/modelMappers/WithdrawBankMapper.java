package com.futech.entertainment.packages.wallets.modelMappers;

import javax.validation.constraints.NotBlank;

public class WithdrawBankMapper {

    @NotBlank(message = "Account name is required")
    private String account_name;

    @NotBlank(message = "Account number is required")
    private String account_number;

    @NotBlank(message = "Bank is required")
    private String bank;
    
    @NotBlank(message = "Amount is required")
    private String bank_amount;

    private Integer type;
    private Integer method;

    private String notes;

    private Integer sender;

    public WithdrawBankMapper() {}

    public String getAccount_name() {
        return account_name;
    }
    public void setAccount_name(String account_name) {
        this.account_name = account_name;
    }

    public String getAccount_number() {
        return account_number;
    }
    public void setAccount_number(String account_number) {
        this.account_number = account_number;
    }

    public String getBank() {
        return bank;
    }
    public void setBank(String bank) {
        this.bank = bank;
    }

    public String getBank_amount() {
        return bank_amount;
    }
    public void setBank_amount(String bank_amount) {
        this.bank_amount = bank_amount;
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

    public String getNotes() {
        return notes;
    }
    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Integer getSender() {
        return sender;
    }
    public void setSender(Integer sender) {
        this.sender = sender;
    }
}
