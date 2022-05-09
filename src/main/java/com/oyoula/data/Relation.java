package com.oyoula.data;

public class Relation {
    public String name;
    public String code;
    public String text;
    public String toText;
    public String childColumn;
    public String childTable;
    public String parentColumn;
    public String parentTable;
    public Relation(String name, String code,String parentTable, String parentColumn, String childTable, String childColumn){
        this.name = name;
        this.code = code;
        this.parentTable = parentTable;
        this.parentColumn = parentColumn;
        this.childTable = childTable;
        this.childColumn = childColumn;

        this.text = "";
        this.toText = "";
    }

    public Relation(String name, String code,String parentTable, String parentColumn, String childTable, String childColumn, String text, String toText){
        this.name = name;
        this.code = code;
        this.parentTable = parentTable;
        this.parentColumn = parentColumn;
        this.childTable = childTable;
        this.childColumn = childColumn;

        this.text = text;
        this.toText = toText;
    }
}
