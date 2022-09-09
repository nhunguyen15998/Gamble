package com.futech.entertainment.packages.core.utils;

public class JoinCondition {
    private String typeJoin;
    private String table;
    private DataMapper onEquation;
    public static JoinCondition joinCondition;

    public static JoinCondition getInstance(String typeJoin, String table, DataMapper onEquation){
        if(joinCondition == null){
            JoinCondition item = new JoinCondition();
            item.typeJoin = typeJoin;
            item.table = table;
            item.onEquation = onEquation;
            return item;
        }
        return joinCondition;
    }

    public String getTypeJoin(){
        return this.typeJoin;
    }
    public void setTypeJoin(String typeJoin){
        this.typeJoin = typeJoin;
    }


    public String getTable(){
        return this.table;
    }
    public void setTable(String table){
        this.table = table;
    }

    public DataMapper getOnEquation(){
        return this.onEquation;
    }
    public void setOnEquation(DataMapper onEquation){
        this.onEquation = onEquation;
    }

    
}
