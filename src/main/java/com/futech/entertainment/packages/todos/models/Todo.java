package com.futech.entertainment.packages.todos.models;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.futech.entertainment.packages.core.models.BaseModel;

@Qualifier("Todo")
public class Todo extends BaseModel {

  @Autowired
  public Todo(){

  }

  public Todo(int id, String name, int status){
    this.id = id;
    this.name = name;
    this.status = status;
  }

  public Todo(String name, int status){
    this.name = name;
    this.status = status;
  }

  private String table = "todos";

  private String[] columns = { "id", "name", "status" };

  public String getTable() {
    return this.table;
  }

  public String[] getColumns() {
    return this.columns;
  }

  private Integer id;

  private String name;

  private Integer status;

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
