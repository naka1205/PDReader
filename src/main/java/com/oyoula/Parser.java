package com.oyoula;

import com.oyoula.data.Model;
import org.dom4j.*;
import org.dom4j.io.SAXReader;

import java.io.File;

public class Parser implements Target{

    public Document document;

    public Namespace oNamespace;
    public Namespace cNamespace;
    public Namespace aNamespace;

    public Element rootElement;
    public Element rootObject;

    public Element children;

    public Element model;

    public Parser(){}

    public void init(String fileName) throws DocumentException {
        SAXReader saxReader = new SAXReader();
        document = saxReader.read(new File(fileName));

        rootElement = document.getRootElement();

        oNamespace = new Namespace("o", "object");
        cNamespace = new Namespace("c", "collection");
        aNamespace = new Namespace("a", "attribute");

        rootObject = rootElement.element(new QName("RootObject", oNamespace));

        children = rootObject.element(new QName("Children", cNamespace));
        model = children.element(new QName("Model", oNamespace));
    }

    public String getTextFromEle(Element element) {
        if (element == null) {
            return "";
        }
        return element.getText();
    }

    public String getPadString(String str, int length) {
        int size = str.length();
        if (size < length) {
            str += getBlank(length - size);
            return str;
        } else
            return str + "  ";
    }


    public String getBlank(int length) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < length; i++) {
            s.append(" ");
        }
        return s.toString();
    }

    @Override
    public Model getModel() {
        return null;
    }
}
