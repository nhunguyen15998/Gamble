package com.futech.entertainment.packages.wallets.modelMappers;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.futech.entertainment.packages.core.validations.interfaces.Phone;

public class TransferMapper {
    
    @NotBlank(message = "Phone is required")
    @Phone(message = "Invalid phone format")
    private String phone;

    @NotBlank(message = "Amount is required")
    private String amount;

    private Integer type;
    private String notes;
    private Integer sender;
    
    public TransferMapper() {}

    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getAmount() {
        return amount;
    }
    public void setAmount(String amount) {
        this.amount = amount;
    }
    
    public Integer getType() {
        return type;
    }
    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getSender() {
        return sender;
    }
    public void setSender(Integer sender) {
        this.sender = sender;
    }   

    public String getNotes() {
        return notes;
    }
    public void setNotes(String notes) {
        this.notes = notes;
    }

}
