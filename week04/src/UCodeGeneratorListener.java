import org.antlr.v4.runtime.tree.ParseTreeProperty;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by SeongJung on 2016-11-29.
 */
public class UCodeGeneratorListener extends MiniCBaseListener {
    private ParseTreeProperty<String> newTexts = new ParseTreeProperty<>();
    final private String blank = "           ";
    private int label;
    private HashMap<String, String> localMap = new HashMap<>();
    private HashMap<String, String> externalMap = new HashMap<>();
    private int localVarOffset = 0;
    private int externalVarOffset = 0;
    private List<String> externalVarDecl = new LinkedList<>();

    @Override
    public void exitProgram(MiniCParser.ProgramContext ctx) {
        for (int i = 0; i < ctx.decl().size(); i++) {
            System.out.println(newTexts.get(ctx.decl(i)));
        }
        System.out.println(blank + "bgn " + externalMap.size());
        if (externalMap.size() != 0) {
            for (int i = 0; i < externalVarDecl.size(); i++) {
                System.out.println(externalVarDecl.get(i));
            }
        }
        System.out.println(blank + "ldp");
        System.out.println(blank + "call main");
        System.out.println(blank + "end");
    }

    @Override
    public void exitDecl(MiniCParser.DeclContext ctx) {
        if (isVarDecl(ctx)) {
            newTexts.put(ctx, newTexts.get(ctx.var_decl()));
        } else if (isFunDecl(ctx)) {
            newTexts.put(ctx, newTexts.get(ctx.fun_decl()));
        }
    }

    @Override
    public void exitVar_decl(MiniCParser.Var_declContext ctx) {
        String s1;
        s1 = ctx.getChild(1).getText();
        if (isDeclaration(ctx)) {
            externalMap.put(s1, "1 " + ++externalVarOffset);
            newTexts.put(ctx, blank + "sym " + externalMap.get(s1) + " 1");
        } else if (isDeclarationAndDefine(ctx)) {
            externalMap.put(s1, "1 " + ++externalVarOffset);
            newTexts.put(ctx, blank + "sym " + externalMap.get(s1) + " 1");
            externalVarDecl.add(blank + "ldc " + ctx.getChild(3).getText() + "\n" + blank +"str " + externalMap.get(s1));
        } else if (isArrayDeclaration(ctx)) {
            externalMap.put(s1, "1 " + ++externalVarOffset);
            externalVarOffset += Integer.parseInt(ctx.getChild(3).getText()) - 1;
            newTexts.put(ctx, blank + "sym " + externalMap.get(s1) + " " + ctx.getChild(3).getText());
        }
    }

    @Override
    public void exitType_spec(MiniCParser.Type_specContext ctx) {
        newTexts.put(ctx, "");
    }

    @Override
    public void exitFun_decl(MiniCParser.Fun_declContext ctx) {
        String s1, s2, s3;
        s1 = ctx.getChild(1).getText();
        s2 = newTexts.get(ctx.params());
        s3 = newTexts.get(ctx.compound_stmt());
        newTexts.put(ctx,  s1 + blank.substring(0, blank.length() - s1.length()) + "proc " + localVarOffset + " 2 2" + s2 + "\n" +
                s3 + "\n" + blank + "end");
        localVarOffset = 0;
    }

    @Override
    public void exitParams(MiniCParser.ParamsContext ctx) {
        String s1;
        if (isEmptyParams(ctx)) {
            newTexts.put(ctx, "");
        } else if (isVoidParams(ctx)) {
            newTexts.put(ctx, "");
        } else if (isOneParams(ctx)) {
            s1 = newTexts.get(ctx.param(0));
            newTexts.put(ctx, s1);
        } else if (isMultipleParams(ctx)) {
            s1 = newTexts.get(ctx.param(0));
            for (int i = 1; i < ctx.param().size(); i++) {
                s1 += newTexts.get(ctx.param(i)) + "\n";
            }
            newTexts.put(ctx, s1);
        }
    }

    @Override
    public void exitParam(MiniCParser.ParamContext ctx) {
        String s1;
        s1 = ctx.getChild(1).getText();
        if (isArrayParam(ctx)) {
            localMap.put(s1, "2 " + ++localVarOffset);
            localVarOffset += Integer.parseInt(ctx.getChild(3).getText()) - 1;
            newTexts.put(ctx, blank + "sym " + localMap.get(s1) + ctx.getChild(3).getText());
        } else {
            localMap.put(s1, "2 " + ++localVarOffset);
            newTexts.put(ctx, blank + "sym " + localMap.get(s1) + " 1");
        }
    }

    @Override
    public void exitStmt(MiniCParser.StmtContext ctx) {
        String s1;
        if (isExprStmt(ctx)) {
            s1 = newTexts.get(ctx.expr_stmt());
            newTexts.put(ctx, s1);
        } else if (isCompoundStmt(ctx)) {
            s1 = newTexts.get(ctx.compound_stmt());
            newTexts.put(ctx, s1);
        } else if (isIfStmt(ctx)) {
            s1 = newTexts.get(ctx.if_stmt());
            newTexts.put(ctx, s1);
        } else if (isWhileStmt(ctx)) {
            s1 = newTexts.get(ctx.while_stmt());
            newTexts.put(ctx, s1);
        } else if (isReturnStmt(ctx)) {
            s1 = newTexts.get(ctx.return_stmt());
            newTexts.put(ctx, s1);
        }
    }

    @Override
    public void exitExpr_stmt(MiniCParser.Expr_stmtContext ctx) {
        String s1;
        s1 = newTexts.get(ctx.expr());
        newTexts.put(ctx, s1);
    }

    @Override
    public void exitWhile_stmt(MiniCParser.While_stmtContext ctx) {
        String word, s1, s2;
        s1 = newTexts.get(ctx.expr());
        s2 = newTexts.get(ctx.stmt());
        newTexts.put(ctx, "$$" + label + blank.substring(0, blank.length() - 2 - Integer.toString(label).length()) + "nop" + "\n" +
                s1 + "\n" +
                blank + "fjp $$" + ++label + "\n" +
                s2  + "\n" +
                blank + "ujp $$" + --label + "\n" +
                "$$" + ++label + blank.substring(0, blank.length() - 2 - Integer.toString(label).length()) + "nop");
        label++;
    }

    @Override
    public void exitCompound_stmt(MiniCParser.Compound_stmtContext ctx) {
        String s1 = "", s2 = "";
        for (int i = 0; i < ctx.local_decl().size(); i++) {
            s1 += newTexts.get(ctx.local_decl(i)) + "\n";
        }
        for (int i = 0; i < ctx.stmt().size(); i++) {
            s2 += newTexts.get(ctx.stmt(i)) + "\n";
        }
        newTexts.put(ctx, s1 + s2.substring(0, s2.length()-1));
    }

    @Override
    public void exitLocal_decl(MiniCParser.Local_declContext ctx) {
        String s1;
        s1 = ctx.getChild(1).getText();
        if (isDeclaration(ctx)) {
            localMap.put(s1, "2 " + ++localVarOffset);
            newTexts.put(ctx, blank + "sym " + localMap.get(s1) + " 1");
        } else if (isDeclarationAndDefine(ctx)) {
            localMap.put(s1, "2 " + ++localVarOffset);
            newTexts.put(ctx, blank + "sym " + localMap.get(s1) + " 1\nldc " + ctx.getChild(3).getText() + "\nstr " + localMap.get(s1));
        } else if (isArrayDeclaration(ctx)) {
            localMap.put(s1, "2 " + ++localVarOffset);
            localVarOffset += Integer.parseInt(ctx.getChild(3).getText()) - 1;
            newTexts.put(ctx, blank + "sym " + localMap.get(s1) + ctx.getChild(3).getText());
        }
    }

    @Override
    public void exitIf_stmt(MiniCParser.If_stmtContext ctx) {
        String s1, s2, s3;
        s1 = newTexts.get(ctx.expr());
        s2 = newTexts.get(ctx.stmt(0));
        if (ctx.getChildCount() == 7) {
            s3 = newTexts.get(ctx.stmt(1));
            newTexts.put(ctx, s1 + "\n" + blank + "fjp $$" + label + "\n" + s2 + "\n$$" + label + blank.substring(0, blank.length() - 2 - Integer.toString(label).length()) + "nop\n" + s3);
            label++;
        } else {
            newTexts.put(ctx, s1 + "\n" + blank + "fjp $$" + label + "\n" + s2 + "\n$$" + label + blank.substring(0, blank.length() - 2 - Integer.toString(label).length()) + "nop");
            label++;
        }
    }

    @Override
    public void exitReturn_stmt(MiniCParser.Return_stmtContext ctx) {
        String s1;
        if (hasReturnExpr(ctx)) {
            s1 = newTexts.get(ctx.expr());
            newTexts.put(ctx, blank + s1 + "\n" + blank + "retv");
        } else {
            newTexts.put(ctx, blank + "ret");
        }
    }

    @Override
    public void exitExpr(MiniCParser.ExprContext ctx) {
        String s1, s2, s3, op1;
        if (isBinaryOperation(ctx)) {
            if (ctx.getChild(0) != ctx.expr(0)) {
                s1 = (localMap.get(ctx.getChild(0).getText()) != null) ? localMap.get(ctx.getChild(0).getText()) : externalMap.get(ctx.getChild(0).getText());
                s2 = newTexts.get(ctx.expr(0));
                newTexts.put(ctx, s2 + "\n" + blank + "str " + s1);
            } else {
                s1 = newTexts.get(ctx.expr(0));
                s2 = newTexts.get(ctx.expr(1));
                op1 = forBinaryOperation(ctx.getChild(1).getText());
                newTexts.put(ctx, s1 + "\n" + s2 + "\n" + blank + op1);
            }
        } else if (isPrefixOperation(ctx)) {
            op1 = forPrefixOperation(ctx.getChild(0).getText());
            s1 = newTexts.get(ctx.expr(0));
            s3 = (localMap.get(ctx.getChild(1).getText()) != null) ? localMap.get(ctx.getChild(1).getText()) : externalMap.get(ctx.getChild(1).getText());
            if (ctx.getChild(0).getText().length() == 2) {
                s2 = "\n" + blank + "str " + s3;
            } else {
                s2 = "";
            }
            newTexts.put(ctx, s1 + "\n" + blank + op1 + s2);
        } else if (isParentheses(ctx)) {
            s1 = newTexts.get(ctx.expr(1));
            newTexts.put(ctx, s1);
        } else if (isIdentOrLiteral(ctx)) {
            if (ctx.getChild(0) == ctx.IDENT()) {
                s1 = (localMap.get(ctx.getChild(0).getText()) != null) ? localMap.get(ctx.getChild(0).getText()) : externalMap.get(ctx.getChild(0).getText());
                newTexts.put(ctx, blank + "lod " + s1);
            } else {
                s1 = ctx.getChild(0).getText();
                newTexts.put(ctx, blank + "ldc " + s1);
            }
        } else if (isArray(ctx)) {
            s1 = (localMap.get(ctx.getChild(0).getText()) != null) ? localMap.get(ctx.getChild(0).getText()) : externalMap.get(ctx.getChild(0).getText());
            s2 = newTexts.get(ctx.expr(0));
            newTexts.put(ctx, s2 + "\n" + blank + "lda " + s1);
        } else if (isFunction(ctx)) {
            s1 = ctx.getChild(0).getText();
            s2 = newTexts.get(ctx.args());
            newTexts.put(ctx, s2 + "\n" + blank + "call " + s1);
        } else if (ctx.getChildCount() == 6) {
            s1 = (localMap.get(ctx.getChild(0).getText()) != null) ? localMap.get(ctx.getChild(0).getText()) : externalMap.get(ctx.getChild(0).getText());
            s2 = newTexts.get(ctx.expr(0));
            s3 = newTexts.get(ctx.expr(1));
            newTexts.put(ctx, s2 + "\n" + blank + "lda " + s1 + "\n" + blank + "add\n" + s3 + "\n" + blank + "sti");
        }
    }

    @Override
    public void exitArgs(MiniCParser.ArgsContext ctx) {
        String s1;
        if (isEmptyArgs(ctx)) {
            newTexts.put(ctx, "");
        } else {
            s1 = newTexts.get(ctx.expr(0));
            if (isMultipleExpr(ctx)) {
                for (int i = 1; i < ctx.expr().size(); i++) {
                    s1 += newTexts.get(ctx.expr(i)) + "\n";
                }
            }
            newTexts.put(ctx, s1);
        }
    }

    private boolean isVarDecl(MiniCParser.DeclContext ctx) {
        return ctx.var_decl() == ctx.getChild(0);
    }

    private boolean isFunDecl(MiniCParser.DeclContext ctx) {
        return ctx.fun_decl() == ctx.getChild(0);
    }

    private boolean isDeclaration(MiniCParser.Var_declContext ctx) {
        return ctx.getChildCount() == 3;
    }

    private boolean isDeclarationAndDefine(MiniCParser.Var_declContext ctx) {
        return ctx.getChildCount() == 5;
    }

    private boolean isArrayDeclaration(MiniCParser.Var_declContext ctx) {
        return ctx.getChildCount() == 6;
    }

    private boolean isEmptyParams(MiniCParser.ParamsContext ctx) {
        return ctx.getChildCount() == 0;
    }

    private boolean isVoidParams(MiniCParser.ParamsContext ctx) {
        return ctx.getChildCount() == 1 && ctx.param() == null;
    }

    private boolean isMultipleParams(MiniCParser.ParamsContext ctx) {
        return ctx.getChildCount() > 1;
    }

    private boolean isOneParams(MiniCParser.ParamsContext ctx) {
        return ctx.getChildCount() == 1 && ctx.param() != null;
    }

    private boolean isArrayParam(MiniCParser.ParamContext ctx) {
        return ctx.getChildCount() == 4;
    }

    private boolean isReturnStmt(MiniCParser.StmtContext ctx) {
        return ctx.return_stmt() == ctx.getChild(0);
    }

    private boolean isWhileStmt(MiniCParser.StmtContext ctx) {
        return ctx.while_stmt() == ctx.getChild(0);
    }

    private boolean isIfStmt(MiniCParser.StmtContext ctx) {
        return ctx.if_stmt() == ctx.getChild(0);
    }

    private boolean isCompoundStmt(MiniCParser.StmtContext ctx) {
        return ctx.compound_stmt() == ctx.getChild(0);
    }

    private boolean isExprStmt(MiniCParser.StmtContext ctx) {
        return ctx.expr_stmt() == ctx.getChild(0);
    }

    private boolean isDeclaration(MiniCParser.Local_declContext ctx) {
        return ctx.getChildCount() == 3;
    }

    private boolean isDeclarationAndDefine(MiniCParser.Local_declContext ctx) {
        return ctx.getChildCount() == 5;
    }

    private boolean isArrayDeclaration(MiniCParser.Local_declContext ctx) {
        return ctx.getChildCount() == 6;
    }

    private boolean hasReturnExpr(MiniCParser.Return_stmtContext ctx) {
        return ctx.getChildCount() == 3;
    }

    private boolean isIdentOrLiteral(MiniCParser.ExprContext ctx) {
        return ctx.getChildCount() == 1;
    }

    private boolean isBinaryOperation(MiniCParser.ExprContext ctx) {
        return ctx.getChildCount() == 3 && ctx.getChild(1) != ctx.expr();
    }

    private String forBinaryOperation(String operation) {
        switch (operation) {
            case "+":
                return "add";
            case "-":
                return "sub";
            case "*":
                return "mult";
            case "/":
                return "div";
            case "%":
                return "mod";
            case "==":
                return "eq";
            case "!=":
                return "ne";
            case "<=":
                return "le";
            case "<":
                return "lt";
            case ">=":
                return "ge";
            case ">":
                return "gt";
            case "and":
                return "and";
            case "or":
                return "or";
        }
        return "";
    }

    private String forPrefixOperation(String operation) {
        switch (operation) {
            case "-":
                return "neg";
            case "+":
                return "";
            case "--":
                return "dec";
            case "++":
                return "inc";
            case "!":
                return "notop";
        }
        return "";
    }

    private boolean isPrefixOperation(MiniCParser.ExprContext ctx) {
        return ctx.getChildCount() == 2;
    }

    private boolean isParentheses(MiniCParser.ExprContext ctx) {
        return ctx.getChildCount() == 3 && ctx.getChild(1) == ctx.expr();
    }

    private boolean isFunction(MiniCParser.ExprContext ctx) {
        return ctx.getChildCount() == 4 && ctx.args() == ctx.getChild(2);
    }

    private boolean isArray(MiniCParser.ExprContext ctx) {
        return ctx.getChildCount() == 4 && ctx.expr(0) == ctx.getChild(2);
    }

    private boolean isMultipleExpr(MiniCParser.ArgsContext ctx) {
        return ctx.expr().size() != 1;
    }

    private boolean isEmptyArgs(MiniCParser.ArgsContext ctx) {
        return ctx.getChildCount() == 0;
    }
}
