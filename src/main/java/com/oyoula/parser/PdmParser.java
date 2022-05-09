package com.oyoula.parser;

import com.oyoula.Parser;
import com.oyoula.data.Column;
import com.oyoula.data.Model;
import com.oyoula.data.Relation;
import com.oyoula.data.Table;
import java.util.List;
import org.dom4j.*;

import java.util.ArrayList;

public class PdmParser extends Parser {

    @Override
    public Model getModel() {

        Element name = model.element(new QName("Name", aNamespace));
        Element code = model.element(new QName("Code", aNamespace));

        List<Table> tables = getTables();
        List<Relation> relations = getReference(tables);

        Model dataModel = new Model(name.getText(),code.getText(),tables, relations);
        return dataModel;
    }

    public List<Table> getTables() {
        List<Element> tableEles = new ArrayList<>();

        Element tablesEle = model.element(new QName("Tables", cNamespace));
        if (tablesEle != null) {
            tableEles.addAll(tablesEle.elements(new QName("Table", oNamespace)));
        }

        List<Table> dataTables = new ArrayList<>();

        for (Element tableElement : tableEles) {

            String tableId = tableElement.attribute("Id").getValue();
            Element name = tableElement.element(new QName("Name", aNamespace));
            Element code = tableElement.element(new QName("Code", aNamespace));

            //解析主键
            Element primaryKeyEle = tableElement.element(new QName("PrimaryKey", cNamespace));
            List<String> pkIds = new ArrayList<>();
            if (primaryKeyEle != null) {
                List<Element> pks = primaryKeyEle.elements(new QName("Key", oNamespace));
                for (Element pk1 : pks) {
                    pkIds.add(pk1.attribute("Ref").getValue());
                }
            }

            Element keysEle = tableElement.element(new QName("Keys", cNamespace));
            List<String> pkColumnIds = new ArrayList<>();
            if (keysEle != null) {
                List<Element> keyEleList = keysEle.elements(new QName("Key", oNamespace));
                for (Element keyEle : keyEleList) {
                    Attribute id = keyEle.attribute("Id");
                    if (pkIds.contains(id.getValue())) {
                        List<Element> list = keyEle.element(new QName("Key.Columns", cNamespace)).elements(new QName("Column", oNamespace));
                        for (Element element : list) {
                            pkColumnIds.add(element.attribute("Ref").getValue());
                        }
                    }
                }
            }

            List<Column> dataColumns = new ArrayList<>();
            List<Element> columns = tableElement.element(new QName("Columns", cNamespace)).elements(new QName("Column", oNamespace));
            for (Element columnEle : columns) {
                String columnId = columnEle.attribute("Id").getValue();
                Element cname = columnEle.element(new QName("Name", aNamespace));
                Element ccode = columnEle.element(new QName("Code", aNamespace));
                Element cDataType = columnEle.element(new QName("DataType", aNamespace));
                Element cLength = columnEle.element(new QName("Length", aNamespace));
                Element cComment = columnEle.element(new QName("Comment", aNamespace));
                Element mandatory = columnEle.element(new QName("Column.Mandatory", aNamespace));

                Column dataColumn = new Column(columnId, getTextFromEle(cname), getTextFromEle(ccode), getTextFromEle(cDataType), getTextFromEle(cLength), getTextFromEle(cComment), pkColumnIds.contains(columnId),mandatory != null);

                dataColumns.add(dataColumn);
            }

            Table dataTable = new Table(tableId, name.getText(),code.getText(),dataColumns);

            dataTables.add(dataTable);
        }

        return dataTables;
    }

    public List<Relation> getReference(List<Table> tables){

        List<Element> relationEles = new ArrayList<>();
        Element referencesEle = model.element(new QName("References", cNamespace));
        if (referencesEle != null) {
            relationEles.addAll(referencesEle.elements(new QName("Reference", oNamespace)));
        }

        List<Relation> dataRelations = new ArrayList<>();
        for (Element relationElement : relationEles) {

            Element rname = relationElement.element(new QName("Name", aNamespace));
            Element rcode = relationElement.element(new QName("Code", aNamespace));

            List<Element> parents = relationElement.element(new QName("ParentTable", cNamespace)).elements(new QName("Table", oNamespace));
            List<Element> childs = relationElement.element(new QName("ChildTable", cNamespace)).elements(new QName("Table", oNamespace));

            String parentId = parents.get(0).attribute("Ref").getValue();
            String childId = childs.get(0).attribute("Ref").getValue();

            Table pTable = tables.stream().filter(u -> u.id.equals(parentId)).findAny().orElse(null);
            Table cTable = tables.stream().filter(u -> u.id.equals(childId)).findAny().orElse(null);

            List<Element> joins = relationElement.element(new QName("Joins", cNamespace)).elements(new QName("ReferenceJoin", oNamespace));

            Element parentElement = joins.get(0).element(new QName("Object1", cNamespace));
            Element childElement = joins.get(0).element(new QName("Object2", cNamespace));

            List<Element> parentJoins = parentElement.elements(new QName("Column", oNamespace));
            String parentColumnId = parentJoins.get(0).attribute("Ref").getValue();

            Column pColumn = pTable.columns.stream().filter(u -> u.id.equals(parentColumnId)).findAny().orElse(null);

            String childColumn = "";
            String childTable = "";

            if (childElement != null) {
                List<Element> childJoins = childElement.elements(new QName("Column", oNamespace));
                String childColumnId = childJoins.get(0).attribute("Ref").getValue();
                Column cColumn = cTable.columns.stream().filter(u -> u.id.equals(childColumnId)).findAny().orElse(null);
                childColumn = cColumn.name;
                childTable = cTable.name;
            }

            Relation dataRelation = new Relation(getTextFromEle(rname),getTextFromEle(rcode),pTable.name,pColumn.name,childTable,childColumn);

            dataRelations.add(dataRelation);
        }

        return dataRelations;
    }

}
