package com.futech.entertainment.packages.core.repositories;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.futech.entertainment.packages.core.models.BaseModel;
import com.futech.entertainment.packages.core.repositories.interfaces.BaseRepositoryInterface;
import com.futech.entertainment.packages.core.utils.DataMapper;
import com.futech.entertainment.packages.core.utils.JoinCondition;
import com.futech.entertainment.packages.core.utils.ObjectMapper;

@Repository
public class BaseRepository<T extends BaseModel> implements BaseRepositoryInterface<T> {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private T model; 
    public void setModel(T modelT){
        this.model = modelT;
    }

    @Override
    @Transactional
    public T create(T model){
        try {
            String table = this.model.getTable(); // get table name
            String[] columns = this.model.getColumns(); // get list columns

            List<ObjectMapper> fieldList = new ArrayList<ObjectMapper>(); // contain all field of model with key = field_name => value = field_value

            List<String> keys = new ArrayList<String>(); // contain all keys of model
            List<String> values = new ArrayList<String>(); // contain all values of model
            List<String> paramValues = new ArrayList<String>(); // contain params string: such as: [?,?,?...] --> depend on length of values

            List<Field> fields = Arrays.asList(model.getClass().getDeclaredFields());// contain all fields get from model
            for (Field field : fields) {
                field.setAccessible(true);
                Object fieldObject = field.get(model);
                if(fieldObject == null || !Arrays.asList(columns).contains(field.getName())) continue; // Passed if field is null or field not in list columns of model
                String fieldName = field.getName();
                String fieldValue = fieldObject.toString();

                keys.add(fieldName);
                values.add(fieldValue);
                paramValues.add("?");
                fieldList.add(new ObjectMapper(fieldName, fieldValue));// Put key = field_name and value = field_value in model
            }

            String fieldString = String.join(",", keys);// Join keys with "," => abc,cde used in sql statement
            String paramValueString = String.join(",", paramValues); // Join values with "," => efg,ghi used in sql statement

            String sql= "insert into " + table + "("+fieldString+") values("+paramValueString+")";

            // Insert return id of record
            GeneratedKeyHolder holder = new GeneratedKeyHolder();
            jdbcTemplate.update(new PreparedStatementCreator() {
                @Override
                public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                    PreparedStatement statement = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                    int index = 1;
                    for (ObjectMapper fieldMapper : fieldList) {
                        statement.setString(index, fieldMapper.getValue());
                        index++;
                    }
                    return statement;
                }
            }, holder);

            int id = holder.getKey().intValue();
            model.setId(id); // fill id to model
            return model;
        } catch (Exception e) {
            e.printStackTrace();
            return model;
        }
    }

    @Override
    @Transactional
    public Boolean update(T model){
        try {
            String table = this.model.getTable(); // get table name
            String[] columns = this.model.getColumns(); // get list columns

            List<ObjectMapper> fieldList = new ArrayList<ObjectMapper>(); // contain all field of model with key = field_name => value = field_value

            List<String> keys = new ArrayList<String>(); // contain all keys of model
            List<String> values = new ArrayList<String>(); // contain all values of model
            List<String> paramValues = new ArrayList<String>(); // contain params string: such as: [?,?,?...] --> depend on length of values

            List<Field> fields = Arrays.asList(model.getClass().getDeclaredFields());// contain all fields get from model
            for (Field field : fields) {
                field.setAccessible(true);
                Object fieldObject = field.get(model);
                if(fieldObject == null || !Arrays.asList(columns).contains(field.getName()) || field.getName()=="id") continue; // Passed if field is null or field not in list columns of model || not update id
                String fieldName = field.getName();
                String fieldValue = fieldObject.toString();
                
                keys.add(fieldName);
                values.add(fieldValue);
                System.out.println(fieldValue);
                paramValues.add("?");
                fieldList.add(new ObjectMapper(fieldName, fieldValue));// Put key = field_name and value = field_value in model
            }

            String sql= "update "+table+" set ";
            for (int i = 0; i < keys.size(); i++) {
                sql+= keys.get(i)+" = ?";
                if(i<keys.size()-1){
                    sql+=", ";
                }
            }
            sql+=" where id = ?";
            values.add(model.getId().toString());
            System.out.println("query:"+sql);
        
            int result = jdbcTemplate.update(sql, values.toArray());// index=0 ==> id columns
            if( result == 0 ){
                return false;
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    };

    @Override
    public List<Map<String,Object>> getAll(String[] selects, List<DataMapper> conditions, List<JoinCondition> joins, String[] groupBys, String orderBy, String[] limit) {
        try {
            String table = this.model.getTable(); // get table name
            String[] columns = this.model.getColumns(); // get list columns

            if(selects == null){
                selects = columns;
            }
            //condition
            String conditionStr = " where ";
            if(conditions != null){
                for(DataMapper condition : conditions) {
                    String formatConditionStr = " %s %s %s '%s' %s ";
                    if(condition.getValue().matches("^[0-9]*$")){
                        formatConditionStr = (condition.getValue().startsWith("0") && condition.getValue().length() > 1) ? 
                                             " %s %s %s '%s' %s " : " %s %s %s %s %s ";
                    }
                    conditionStr += String.format(formatConditionStr, condition.getPrefix(), condition.getKey(), condition.getOperator(), condition.getValue(), condition.getSuffix());
                }
            }
            //join
            String joinStr = "";
            if(joins != null){
                for (JoinCondition join : joins) {
                    joinStr += String.format(" %s %s on %s %s %s ", join.getTypeJoin(), join.getTable(), join.getOnEquation().getKey(), join.getOnEquation().getOperator(), join.getOnEquation().getValue());
                }
            }
            //sql
            String sql = "select " + String.join(",", selects) + " from " + table + " " 
                                     + (joins != null ? joinStr + " " : "") 
                                     + (conditions != null ? conditionStr + " " : "");
            //groupby
            if(groupBys != null){
                sql += " group by " + String.join(",", groupBys);
            }
            //orderby
            if(orderBy != null){
                sql += " order by " + orderBy;
            }
            //limit
            if(limit != null){
                sql += " limit " + String.join(",", limit);
            }
            System.out.println("query:"+sql);
            List<Map<String,Object>> list = jdbcTemplate.queryForList(sql);
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    @Transactional
    public Boolean delete(int id) {
        try {
            String table = this.model.getTable(); // get table name

            String sql = String.format("delete from %s where id = %s", table, id);
            System.out.println("query:"+sql);
            int result = jdbcTemplate.update(sql);
            if( result == 0 ){
                return false;
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Map<String,Object> find(int id) {
        try{
            String sql = "select * from "+this.model.getTable()+" where id=? limit 1";
            Map<String,Object> results = this.jdbcTemplate.queryForMap(sql, id);
            return results;
        }catch(EmptyResultDataAccessException err){
            return new HashMap<String, Object>();
        }catch(Exception e){
            return new HashMap<String, Object>();
        }
    }

    @Override
    public Map<String,Object> getFirstBy(String[] selects, List<DataMapper> conditions, List<JoinCondition> joins) {
        try {
            String table = this.model.getTable(); // get table name
            String[] columns = this.model.getColumns(); // get list columns

            if(selects == null){
                selects = columns;
            }
            //condition
            String conditionStr = " where ";
            if(conditions != null){
                for(DataMapper condition : conditions) {
                    String formatConditionStr = " %s %s %s '%s' %s ";
                    if(condition.getValue().matches("^[0-9]*$")){
                        formatConditionStr = (condition.getValue().startsWith("0") && condition.getValue().length() > 1) ? 
                                             " %s %s %s '%s' %s " : " %s %s %s %s %s ";
                    }
                    conditionStr += String.format(formatConditionStr, condition.getPrefix(), condition.getKey(), condition.getOperator(), condition.getValue(), condition.getSuffix());
                }
            }
            //join
            String joinStr = "";
            if(joins != null){
                for (JoinCondition join : joins) {
                    joinStr += String.format(" %s %s on %s %s %s ", join.getTypeJoin(), join.getTable(), join.getOnEquation().getKey(), join.getOnEquation().getOperator(), join.getOnEquation().getValue());
                }
            }
            //sql
            String sql = "select " + String.join(",", selects) + " from " + table + " " 
                                     + (joins != null ? joinStr + " " : "") 
                                     + (conditions != null ? conditionStr + " " : "");
            System.out.println("query:"+sql);
            Map<String,Object> item = jdbcTemplate.queryForMap(sql);
            return item;
        } catch (EmptyResultDataAccessException e) {
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public T getFirst(String[] selects, List<DataMapper> conditions, List<JoinCondition> joins) {
        try {
            String table = this.model.getTable(); // get table name
            String[] columns = this.model.getColumns(); // get list columns

            if(selects == null){
                selects = columns;
            }
            //condition
            String conditionStr = " where ";
            if(conditions != null){
                for(DataMapper condition : conditions) {
                    String formatConditionStr = " %s %s %s '%s' %s ";
                    if(condition.getValue().matches("^[0-9]*$")){
                        formatConditionStr = (condition.getValue().startsWith("0") && condition.getValue().length() > 1) ? 
                                             " %s %s %s '%s' %s " : " %s %s %s %s %s ";
                    }
                    conditionStr += String.format(formatConditionStr, condition.getPrefix(), condition.getKey(), condition.getOperator(), condition.getValue(), condition.getSuffix());
                }
            }
            //join
            String joinStr = "";
            if(joins != null){
                for (JoinCondition join : joins) {
                    joinStr += String.format(" %s %s on %s %s %s ", join.getTypeJoin(), join.getTable(), join.getOnEquation().getKey(), join.getOnEquation().getOperator(), join.getOnEquation().getValue());
                }
            }
            //sql
            String sql = "select " + String.join(",", selects) + " from " + table + " " 
                                     + (joins != null ? joinStr + " " : "") 
                                     + (conditions != null ? conditionStr + " " : "");
            System.out.println("query:"+sql);
            
            T item = jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<T>());
            return item;
        } catch (IllegalStateException e){
            return null;
        } catch (EmptyResultDataAccessException e) {
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    

}
