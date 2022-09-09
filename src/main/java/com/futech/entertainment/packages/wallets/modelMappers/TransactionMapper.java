package com.futech.entertainment.packages.wallets.modelMappers;

import javax.validation.constraints.NotNull;

public class TransactionMapper {

    private Integer id;
    private String code;
    @NotNull(message = "Amount is required")
    private Double amount;
    private Double received_amount;
    @NotNull(message = "Type is required")
    private Integer type;
    @NotNull(message = "Method is required")
    private Integer method;
    private String title;
    private String note;
    @NotNull(message = "Sender is required")
    private Integer sender;
    private Integer receiver;
    private String created_at;
    private Integer status;
    private Double exchange_rate;
    private String from_currency;
    private String txid;
    private String bcaddress;
    
    public TransactionMapper() {}

    public Integer getId() {
        return this.id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }

    public Double getAmount() {
        return amount;
    }
    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getreceived_amount() {
        return received_amount;
    }
    public void setreceived_amount(Double received_amount) {
        this.received_amount = received_amount;
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

    public String getNote() {
        return note;
    }
    public void setNote(String note) {
        this.note = note;
    }

    public Integer getSender() {
        return sender;
    }
    public void setSender(Integer sender) {
        this.sender = sender;
    }

    public Integer getReceiver() {
        return receiver;
    }
    public void setReceiver(Integer receiver) {
        this.receiver = receiver;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getcreated_at() {
        return created_at;
    }
    public void setcreated_at(String created_at) {
        this.created_at = created_at;
    }

    public Integer getStatus() {
        return status;
    }
    public void setStatus(Integer status) {
        this.status = status;
    }

    public Double getexchange_rate() {
        return exchange_rate;
    }
    public void setexchange_rate(Double exchange_rate) {
        this.exchange_rate = exchange_rate;
    }

    public String getfrom_currency() {
        return from_currency;
    }
    public void setfrom_currency(String from_currency) {
        this.from_currency = from_currency;
    }

    public String getTxid() {
        return txid;
    }
    public void setTxid(String txid) {
        this.txid = txid;
    }

    public String getBcaddress() {
        return bcaddress;
    }
    public void setBcaddress(String bcaddress) {
        this.bcaddress = bcaddress;
    }    
}
