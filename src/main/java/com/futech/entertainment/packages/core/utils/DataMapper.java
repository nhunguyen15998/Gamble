package com.futech.entertainment.packages.core.utils;

public class DataMapper {
    private String key;
    private String value;
    private String operator;
    private String prefix;
    private String suffix;
    public static DataMapper dataMapper;

    public static DataMapper getInstance(String prefix, String key, String operator, String value, String suffix) {
        if(dataMapper == null){
            DataMapper item = new DataMapper();
            item.prefix = prefix;
            item.key = key;
            item.operator = operator;
            item.value = value;
            item.suffix = suffix;
            return item;
        }
        return dataMapper;
    }

    public String getPrefix(){
        return this.prefix;
    }
    public void setPrefix(String prefix){
        this.prefix = prefix;
    }

    public String getKey(){
        return this.key;
    }
    public void setKey(String key){
        this.key = key;
    }

    public String getValue(){
        return this.value;
    }
    public void setValue(String value){
        this.value = value;
    }

    public String getOperator(){
        return this.operator;
    }
    public void setOperator(String operator){
        this.operator = operator;
    }

    public String getSuffix(){
        return this.suffix;
    }
    public void setSuffix(String suffix){
        this.suffix = suffix;
    }
}
