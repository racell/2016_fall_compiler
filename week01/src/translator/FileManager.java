package translator;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by SeongJung on 2016-09-10.
 */
public class FileManager {

    private String listId;
    private String resultId;

    public FileManager() {
        this.listId = "";
        this.resultId = "";
    }

    /*
     * Read file test.ad
     * use BufferReader for line by line reading
     */
    public ArrayList<String> readFile() throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(new File("week01/src/translator/test.ad")));
        ArrayList<String> statementSet = new ArrayList<>();
        while(true) {
            String line = bufferedReader.readLine();
            if(line == null) break;
            statementSet.add(line.substring(1,line.length()-1));
        }
        bufferedReader.close();

        return statementSet;
    }

    /*
     * initialize output file header
     */
    public void outputfileInitializer() throws IOException {
        FileOutputStream output = new FileOutputStream(new File("week01/src/translator/test.c"));
        output.close();
        FileWriter fileWriter = new FileWriter(new File("week01/src/translator/test.c"));
        fileWriter.write("#include <stdio.h>\r\n\r\n" +
                "int main() {\r\n");
        fileWriter.close();
    }

    /*
     * initialize output file footer
     */
    public void outputfileEndInitializer() throws IOException {
        FileWriter fileWriter = new FileWriter(new File("week01/src/translator/test.c"),true);
        fileWriter.write("\treturn 0;\r\n}");
        fileWriter.close();
    }

    /*
     * write file according to keyword
     * keyword def - define list with input elements
     *         reduce - calculate result with list elements by input operator
     *         print - list : printing all elements
     *                 result : printing result value
     */
    public void writeFile(ArrayList<String> parsedStatement) throws IOException {
        FileWriter fileWriter = new FileWriter(new File("week01/src/translator/test.c"), true);
        StringBuilder stringBuilder = new StringBuilder();
        switch(parsedStatement.remove(0)) {
            case "def":
                listId = parsedStatement.remove(0);
                fileWriter.write("\tlong " + listId + "[] = {");
                int listSize = parsedStatement.size();
                while(!parsedStatement.isEmpty()) {
                    stringBuilder.append(parsedStatement.remove(0));
                    stringBuilder.append(" ");
                }
                fileWriter.write(stringBuilder.substring(1,stringBuilder.length()-2) + "};\r\n" +
                        "\tint _AD_size = " + listSize + ";\r\n\r\n");
                break;
            case "reduce":
                resultId = parsedStatement.remove(3);
                fileWriter.write("\tint _AD_i = 0;\r\n" +
                        "\tlong _AD_result = " + parsedStatement.remove(2) + ";\r\n" +
                        "\tfor(_AD_i = 0; _AD_i < _AD_size; _AD_i++) {\r\n" +
                        "\t\t_AD_result " + parsedStatement.remove(1) +"= " + listId + "[_AD_i];\r\n" +
                        "\t}\r\n\r\n");
                break;
            case "print":
                if (listId.equals(parsedStatement.get(0))) {
                    fileWriter.write("\tfor(_AD_i = 0; _AD_i < _AD_size-1; _AD_i++) {\r\n" +
                            "\t\tprintf(\"%d, \", " + listId + "[_AD_i]);\r\n\t}\r\n" +
                            "\tprintf(\"%d\\n\"," + listId + "[_AD_i]);\r\n");
                }
                else if(resultId.equals(parsedStatement.get(0))) {
                    fileWriter.write("\tprintf(\"%d\\n\", _AD_result);\r\n");
                }
                break;
        }
        fileWriter.close();
    }
}
