import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.IOException;

/**
 * Created by SeongJung on 2016-11-29.
 */
public class UcodeGenerator {
    public static void main(String[] args) throws IOException {
        MiniCLexer lexer = new MiniCLexer(new ANTLRFileStream("week04/src/test"));
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        MiniCParser parser = new MiniCParser(tokenStream);
        ParseTree tree = parser.program();

        ParseTreeWalker walker = new ParseTreeWalker();
        walker.walk(new UCodeGeneratorListener(), tree);
    }
}
