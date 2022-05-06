package com.oyoula.data;

import java.util.List;

public class Table {
    public String name;
    public String comment;
    public List<Column> columns;
    public Table(String name, String comment, List<Column> columns){
        this.name = name;
        this.comment = comment;
        this.columns = columns;
    }
}
