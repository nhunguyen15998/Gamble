package com.futech.entertainment.packages.todos.modelMappers;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class TodoMapper {

    private Integer id;
    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Status is required")
    private Integer status;

    public TodoMapper(){}

    public TodoMapper(Integer id, String name, Integer status){
        this.id = id;
        this.name = name;
        this.status = status;
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getStatus() {
        return this.status;
    }
}
