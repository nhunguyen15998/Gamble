package com.futech.entertainment.packages.core.models;

public class BaseModel {
    private String table;
    private String[] columns;

    public String getTable(){
        return this.table;
    }

    public String[] getColumns(){
        return this.columns;
    }

    private int id;

    public Integer getId() {
        return id;
      }
    
      public void setId(Integer id) {
        this.id = id;
      }
}
