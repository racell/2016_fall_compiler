package translator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Created by SeongJung on 2016-09-11.
 */
public class StatementParser {
    private ArrayList<String> statementSet;
    private FileManager fileManager = new FileManager();

    public StatementParser(ArrayList<String> inputSet) {
        this.statementSet = inputSet;
    }

    public void parser() throws IOException {
        for(int i=0; i < statementSet.size(); i++) {
            StringTokenizer stringTokenizer = new StringTokenizer(statementSet.get(i));
            ArrayList<String> tempString = new ArrayList<>();
            while(stringTokenizer.hasMoreTokens()) {
                tempString.add(stringTokenizer.nextToken());
            }
            fileManager.writeFile(tempString);
        }
    }
}
