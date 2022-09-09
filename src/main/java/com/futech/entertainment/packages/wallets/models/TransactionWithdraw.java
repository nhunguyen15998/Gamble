package com.futech.entertainment.packages.wallets.models;

import org.springframework.beans.factory.annotation.Qualifier;

import com.futech.entertainment.packages.core.models.BaseModel;

@Qualifier("TransactionWithdraw")
public class TransactionWithdraw extends BaseModel {
    
    public TransactionWithdraw() {}
    
    private String table = "transaction_withdraws";

    private String[] columns = { "id", "transaction_id", "account_name", "account_number", "amount", "bank", "notes" };

    public String getTable() {
        return this.table;
    }

    public String[] getColumns() {
        return this.columns;
    }

    private Integer id;
    private Integer transaction_id;
    private String account_name;
    private String account_number;
    private String bank;
    private String notes;
    private Double amount;

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTransaction_id() {
        return transaction_id;
    }
    public void setTransaction_id(Integer transaction_id) {
        this.transaction_id = transaction_id;
    }

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

    public String getNotes() {
        return notes;
    }
    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Double getAmount() {
        return amount;
    }
    public void setAmount(Double amount) {
        this.amount = amount;
    }
    
}
