package translator;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by SeongJung on 2016-09-10.
 */
public class AdCompiler {
    public static void main(String[] args) throws IOException{
        FileManager fileManager = new FileManager();
        fileManager.outputfileInitializer();
        ArrayList<String> statementSet = fileManager.readFile();
        StatementParser statementParser = new StatementParser(statementSet);
        statementParser.parser();
        fileManager.outputfileEndInitializer();
    }
}
