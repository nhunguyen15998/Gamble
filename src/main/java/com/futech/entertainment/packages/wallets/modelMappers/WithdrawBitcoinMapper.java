package com.futech.entertainment.packages.wallets.modelMappers;

import javax.validation.constraints.NotBlank;

public class WithdrawBitcoinMapper {
    
    @NotBlank(message = "Amount is required")
    private String bitcoin_amount;

    @NotBlank(message = "Address is required")
    private String bcaddress;

    private Integer sender;
    private Integer type;
    private Integer method;

    private String transaction_code;

    public WithdrawBitcoinMapper() {}

    public String getBitcoin_amount() {
        return bitcoin_amount;
    }
    public void setBitcoin_amount(String bitcoin_amount) {
        this.bitcoin_amount = bitcoin_amount;
    }

    public String getBcaddress() {
        return bcaddress;
    }
    public void setBcaddress(String bcaddress) {
        this.bcaddress = bcaddress;
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

    public String getTransaction_code() {
        return transaction_code;
    }
    public void setTransaction_code(String transaction_code) {
        this.transaction_code = transaction_code;
    }
}
