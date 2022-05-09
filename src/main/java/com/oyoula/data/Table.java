package com.oyoula.data;

import java.util.List;

public class Table {
    public String id;
    public String name;
    public String comment;
    public List<Column> columns;
    public Table(String id, String name, String comment, List<Column> columns){
        this.id = id;
        this.name = name;
        this.comment = comment;
        this.columns = columns;
    }
}
