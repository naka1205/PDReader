package com.oyoula.data;

import java.util.List;

public class Model {
    public String name;
    public String comment;
    public List<Table> tables;
    public Model(String name, String comment, List<Table> tables){
        this.name = name;
        this.comment = comment;
        this.tables = tables;
    }
}
