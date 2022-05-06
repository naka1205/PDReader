package com.oyoula.data;
public class Column {
    public String name;
    public String type;
    public String length;
    public String comment;
    public boolean primary;
    public boolean notNull;

    public Column(String name, String type, String length, String comment, String text, boolean primary, boolean notNull){
        this.name = name;
        this.type = type;
        this.length = length;
        this.comment = comment;
        this.primary = primary;
        this.notNull = notNull;
    }
}