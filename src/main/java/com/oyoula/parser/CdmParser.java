package com.oyoula.parser;

import com.oyoula.Parser;
import com.oyoula.data.Column;
import com.oyoula.data.Model;
import com.oyoula.data.Table;
import org.dom4j.*;

import java.util.ArrayList;
import java.util.List;

public class CdmParser extends Parser {

    @Override
    public Model getModel() {

        Element name = model.element(new QName("Name", aNamespace));
        Element code = model.element(new QName("Code", aNamespace));

        List<Table> tables = getTables();

        Model dataModel = new Model(name.getText(),code.getText(),tables);
        return dataModel;
    }

    public List<Table> getTables() {
        List<Element> tableElements = new ArrayList<>();
        List<Element> columnElements = new ArrayList<>();

        Element tablesEle = model.element(new QName("Entities", cNamespace));
        if (tablesEle != null) {
            tableElements.addAll(tablesEle.elements(new QName("Entity", oNamespace)));
        }

        Element DataItems = model.element(new QName("DataItems", cNamespace));
        if (DataItems != null) {
            columnElements.addAll(DataItems.elements(new QName("DataItem", oNamespace)));
        }


        List<Table> dataTables = new ArrayList<>();

        for (Element tableElement : tableElements) {

            Element name = tableElement.element(new QName("Name", aNamespace));
            Element code = tableElement.element(new QName("Code", aNamespace));

            Element primaryKeyEle = tableElement.element(new QName("PrimaryIdentifier", cNamespace));
            List<String> pkIds = new ArrayList<>();
            if (primaryKeyEle != null) {
                List<Element> pks = primaryKeyEle.elements(new QName("Identifier", oNamespace));
                for (Element pk1 : pks) {
                    pkIds.add(pk1.attribute("Ref").getValue());
                }
            }

            Element keysEle = tableElement.element(new QName("Identifiers", cNamespace));
            List<String> pkColumnIds = new ArrayList<>();
            if (keysEle != null) {
                List<Element> keyEleList = keysEle.elements(new QName("Identifier", oNamespace));
                for (Element keyEle : keyEleList) {
                    Attribute id = keyEle.attribute("Id");
                    if (pkIds.contains(id.getValue())) {
                        List<Element> list = keyEle.element(new QName("Identifier.Attributes", cNamespace)).elements(new QName("EntityAttribute", oNamespace));
                        for (Element element : list) {
                            pkColumnIds.add(element.attribute("Ref").getValue());
                        }
                    }
                }
            }

            List<Element> attributes = tableElement.element(new QName("Attributes", cNamespace)).elements(new QName("EntityAttribute", oNamespace));

            List<Column> dataColumns = new ArrayList<>();
            for (Element attribute : attributes) {
                List<Element> dataItems = attribute.element(new QName("DataItem", cNamespace)).elements(new QName("DataItem", oNamespace));

                String columnId = dataItems.get(0).attribute("Ref").getValue();

                Element columnEle = columnElements.stream()
                        .filter(u -> u.attribute("Id").getValue().equals(columnId))
                        .findAny()
                        .orElse(null);

                Element cname = columnEle.element(new QName("Name", aNamespace));
                Element ccode = columnEle.element(new QName("Code", aNamespace));
                Element cDataType = columnEle.element(new QName("DataType", aNamespace));
                Element cLength = columnEle.element(new QName("Length", aNamespace));

                Element mandatory = attribute.element(new QName("BaseAttribute.Mandatory", aNamespace));

                boolean primary = attribute.attribute("Id").getValue().equals(pkColumnIds.get(0));

                Column dataColumn = new Column(getTextFromEle(cname), getTextFromEle(ccode), getTextFromEle(cDataType), getTextFromEle(cLength), "", primary,mandatory != null);

                dataColumns.add(dataColumn);


            }

            Table dataTable = new Table(name.getText(),code.getText(),dataColumns);

            dataTables.add(dataTable);

        }

        return dataTables;
    }
}
