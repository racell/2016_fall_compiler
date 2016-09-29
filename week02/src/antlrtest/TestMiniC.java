package antlrtest;

import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

/*
 * Created by SeongJung on 2016-09-29.
 */
public class TestMiniC {
    public static void main(String[] args) throws Exception {
        MiniCLexer miniCLexer = new MiniCLexer(new ANTLRFileStream("week02/src/antlrtest/test.c"));
        CommonTokenStream commonTokenStream = new CommonTokenStream(miniCLexer);
        MiniCParser miniCParser = new MiniCParser(commonTokenStream);
        ParseTree parseTree = miniCParser.program();
    }
}
