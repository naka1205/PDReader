package com.oyoula.parser;

import com.oyoula.Parser;
import com.oyoula.data.Column;
import com.oyoula.data.Model;
import com.oyoula.data.Relation;
import com.oyoula.data.Table;
import java.util.List;
import org.dom4j.*;

import java.util.ArrayList;

public class LdmParser extends Parser {

    @Override
    public Model getModel() {

        Element name = model.element(new QName("Name", aNamespace));
        Element code = model.element(new QName("Code", aNamespace));

        List<Table> tables = getTables();
        List<Relation> relations = getReference(tables);

        Model dataModel = new Model(name.getText(),code.getText(),tables,relations);
        return dataModel;
    }

    public List<Table> getTables() {

        List<Element> tableElements = new ArrayList<>();

        Element tablesEle = model.element(new QName("Entities", cNamespace));
        if (tablesEle != null) {
            tableElements.addAll(tablesEle.elements(new QName("Entity", oNamespace)));
        }

        List<Table> dataTables = new ArrayList<>();

        for (Element tableElement : tableElements) {

            String tableId = tableElement.attribute("Id").getValue();
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

            List<Element> columnElements = tableElement.element(new QName("Attributes", cNamespace)).elements(new QName("EntityAttribute", oNamespace));
            List<Column> dataColumns = new ArrayList<>();

            for (Element columnEle : columnElements) {
                String columnId = columnEle.attribute("Id").getValue();
                Element cname = columnEle.element(new QName("Name", aNamespace));
                Element ccode = columnEle.element(new QName("Code", aNamespace));
                Element cDataType = columnEle.element(new QName("DataType", aNamespace));
                Element cLength = columnEle.element(new QName("Length", aNamespace));

                boolean primary = columnId.equals(pkColumnIds.get(0));
                Element mandatory = columnEle.element(new QName("LogicalAttribute.Mandatory", aNamespace));

                Column dataColumn = new Column(columnId, getTextFromEle(cname), getTextFromEle(ccode), getTextFromEle(cDataType), getTextFromEle(cLength), "", primary,mandatory != null);

                dataColumns.add(dataColumn);
            }

            Table dataTable = new Table(tableId, name.getText(),code.getText(),dataColumns);

            dataTables.add(dataTable);
        }

        return dataTables;
    }

    public List<Relation> getReference(List<Table> tables){
        List<Element> relationEles = new ArrayList<>();
        Element referencesEle = model.element(new QName("Relationships", cNamespace));
        if (referencesEle != null) {
            relationEles.addAll(referencesEle.elements(new QName("Relationship", oNamespace)));
        }

        List<Relation> dataRelations = new ArrayList<>();
        for (Element relationElement : relationEles) {
            Element rname = relationElement.element(new QName("Name", aNamespace));
            Element rcode = relationElement.element(new QName("Code", aNamespace));

            List<Element> parents = relationElement.element(new QName("Object1", cNamespace)).elements(new QName("Entity", oNamespace));
            List<Element> childs = relationElement.element(new QName("Object2", cNamespace)).elements(new QName("Entity", oNamespace));

            Element entity1ToEntity = relationElement.element(new QName("Entity1ToEntity2RoleCardinality", aNamespace));
            Element entity2ToEntity = relationElement.element(new QName("Entity2ToEntity1RoleCardinality", aNamespace));

            String parentId = parents.get(0).attribute("Ref").getValue();
            String childId = childs.get(0).attribute("Ref").getValue();

            String text = entity1ToEntity.getText().replace(",", "_").toUpperCase();
            String toText = entity2ToEntity.getText().replace(",", "_").toUpperCase();

            Table pTable = tables.stream().filter(u -> u.id.equals(parentId)).findAny().orElse(null);
            Table cTable = tables.stream().filter(u -> u.id.equals(childId)).findAny().orElse(null);

            Relation dataRelation = new Relation(getTextFromEle(rname),getTextFromEle(rcode),pTable.name,"",cTable.name,"",text,toText);

            dataRelations.add(dataRelation);

        }


        return dataRelations;
    }

}
