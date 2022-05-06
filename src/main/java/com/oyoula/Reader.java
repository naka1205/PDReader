package com.oyoula;

import com.oyoula.data.Column;
import com.oyoula.data.Model;
import com.oyoula.data.Table;
import com.oyoula.parser.CdmParser;
import com.oyoula.parser.PdmParser;
import org.dom4j.DocumentException;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;

import java.util.*;

import static org.fusesource.jansi.Ansi.Color.*;

public class Reader {

    public static Map<String, Object> parsers = new HashMap<String, Object>();

    public static void init(String name, String filename) {


        AnsiConsole.systemInstall();
        System.out.println();

        if (!name.equals("pdm") && !name.equals("cdm") ) {
//            throw new IllegalArgumentException("第一个参数必须文件类型，支持pdm与cdm");
        }

        parsers.put("pdm",new PdmParser());
        parsers.put("cdm",new CdmParser());

        Parser parser = (Parser) parsers.get(name);

        Model model = null;

        try {
            parser.init(filename);
            model = parser.getModel();
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        if (model == null){
            return;
        }

        System.out.println(Ansi.ansi().fg(YELLOW).a("Model name:") + Ansi.ansi().fg(Ansi.Color.GREEN).a(model.name).toString());
        System.out.println(Ansi.ansi().fg(YELLOW).a("Table size:") + Ansi.ansi().fg(Ansi.Color.GREEN).a(model.tables.size()).toString());

        int i = 0;
        for (Table table : model.tables) {
            i++;
            System.out.println("------>" + Ansi.ansi().fg(BLUE).a("NO." + i) + Ansi.ansi().fg(RED).a(" " + table.name + " ") +
                    Ansi.ansi().fg(YELLOW).a(table.comment) + Ansi.ansi().fgDefault().a("<-------"));


            for (Column column : table.columns){
                System.out.print(column.name + " ");
                System.out.print(column.type + " ");
                System.out.print(column.length + " ");

                if (column.primary) {
                    System.out.print("PK  ");
                } else {
                    System.out.print("   ");
                }

                if (column.notNull) {
                    System.out.print("NOT NULL ");
                } else {
                    System.out.print("   ");
                }


                System.out.println();
            }
        }

        AnsiConsole.systemUninstall();


    }

}
