package com.futech.entertainment.packages.core.migrations;

public class Migration {
    //1. loop qua tat ca cac model trg package tru pkg core
    //2. dung table name de xd table co ton tai hay chua
    //2a. neu ton tai, check if all properties name/types match db table's
        //if match, 
        //if not, alter table modify type/name properties in db table to match with model's prop
        //if table not exist in db, create new col
    //2b. neu ko ton tai, create new tbl match with model's prop
}
