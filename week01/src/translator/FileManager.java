package translator;

import java.io.*;

/**
 * Created by SeongJung on 2016-09-10.
 */
public class FileManager {

    public String[] readFile() throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader("C:/Users/SeongJung/Desktop/STUDY/컴파일러/2016_fall_compiler/week01/src/translator/test.ad"));
        String[] set = new String[100];
        for (int i = 0; true; i++) {
            String line = bufferedReader.readLine();
            if(line == null) break;
            set[i] = line.substring(1,line.length()-1);
        }
        bufferedReader.close();

        return set;
    }

    public void outputfileInitializer() throws IOException {
        FileOutputStream output = new FileOutputStream("C:/Users/SeongJung/Desktop/STUDY/컴파일러/2016_fall_compiler/week01/src/translator/test.c");
        output.close();
        FileWriter fileWriter = new FileWriter("C:/Users/SeongJung/Desktop/STUDY/컴파일러/2016_fall_compiler/week01/src/translator/test.c");
        fileWriter.write("#include <stdio.h>\r\n\r\n" +
                "int main() {\r\n\r\n" +
                "}");
        fileWriter.close();
    }

    public void writeFile() {

    }
}
