package com.oyoula.parser;

import com.oyoula.Parser;
import com.oyoula.data.Column;
import com.oyoula.data.Model;
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

        Model dataModel = new Model(name.getText(),code.getText(),tables);
        return dataModel;
    }

    public List<Table> getTables() {
        List<Element> tableEles = new ArrayList<>();

        Element tablesEle = model.element(new QName("Tables", cNamespace));
        if (tablesEle != null) {
            tableEles.addAll(tablesEle.elements(new QName("Table", oNamespace)));
        }

        List<Table> dataTables = new ArrayList<>();

        int i = 0;
        for (Element tableElement : tableEles) {
            i++;
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

                Column dataColumn = new Column(getTextFromEle(cname), getTextFromEle(ccode), getTextFromEle(cDataType), getTextFromEle(cLength), getTextFromEle(cComment), pkColumnIds.contains(columnId),mandatory != null);

                dataColumns.add(dataColumn);
            }

            Table dataTable = new Table(name.getText(),code.getText(),dataColumns);

            dataTables.add(dataTable);
        }

        return dataTables;
    }

}
