package com.example.allexey991.ui;

/**
 * Created by allexey991 on 01/06/2016.
 */
public class mail_item {
    String name;
    int size;

    public mail_item(String name,int size) {
        this.name = name;
        this.size = size;
    }
    public String toString(){
        return name+" "+size ;
    }
}
