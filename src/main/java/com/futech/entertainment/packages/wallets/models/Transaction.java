package com.futech.entertainment.packages.wallets.models;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Qualifier;

import com.futech.entertainment.packages.core.models.BaseModel;

@Qualifier("Transaction")
public class Transaction extends BaseModel {
    
    public Transaction() {}
    
    private String table = "transactions";

    private String[] columns = { "id", "code", "amount", "type", "method", "note", "sender", "receiver", "title", 
                                 "status", "created_at", "exchange_rate", "from_currency", "txid", "bcaddress", "received_amount" };

    public String getTable() {
        return this.table;
    }

    public String[] getColumns() {
        return this.columns;
    }

    private Integer id;
    private String code;
    private Double amount;
    private Double received_amount;
    private Integer type;
    private Integer method;
    private String note;
    private Integer sender;
    private Integer receiver;
    private String title;
    private LocalDateTime created_at;
    private Integer status;
    private Double exchange_rate;
    private String from_currency;
    private String txid;
    private String bcaddress;

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

    public LocalDateTime getcreated_at() {
        return created_at;
    }
    public void setcreated_at(LocalDateTime created_at) {
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
