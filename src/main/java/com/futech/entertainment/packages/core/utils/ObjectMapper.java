package com.futech.entertainment.packages.core.utils;

public class ObjectMapper {
    private String key;
    private String value;

    public ObjectMapper(String key, String value) {
        this.key = key;
        this.value = value;
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
    public String setValue(String value){
        return this.value = value;
    }


}
