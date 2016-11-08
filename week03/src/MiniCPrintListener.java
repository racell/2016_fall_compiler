import org.antlr.v4.runtime.tree.ParseTreeProperty;

import java.util.StringTokenizer;

/**
 * Created by SeongJung on 2016-11-03.
 */
public class MiniCPrintListener extends MiniCBaseListener {
    private ParseTreeProperty<String> newTexts = new ParseTreeProperty<>();

    @Override
    public void exitProgram(MiniCParser.ProgramContext ctx) {
        for (int i = 0; i < ctx.decl().size(); i++) {
            System.out.println(newTexts.get(ctx.decl(i)));
        }
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
        String type, s1, eq, colon, s2;
        type = newTexts.get(ctx.type_spec());
        s1 = ctx.getChild(1).getText();
        if (isDeclaration(ctx)) {
            colon = ctx.getChild(2).getText();
            newTexts.put(ctx, type + " " + s1 + colon);
        } else if (isDeclarationAndDefine(ctx)) {
            eq = ctx.getChild(2).getText();
            s2 = ctx.getChild(3).getText();
            colon = ctx.getChild(4).getText();
            newTexts.put(ctx, type + " " + s1 + " " + eq + " " + s2 + colon);
        } else if (isArrayDeclaration(ctx)) {
            eq = ctx.getChild(2).getText();
            s2 = ctx.getChild(3).getText() + ctx.getChild(4).getText();
            colon = ctx.getChild(5).getText();
            newTexts.put(ctx, type + " " + s1 + eq + s2 + colon);
        }
    }

    @Override
    public void exitType_spec(MiniCParser.Type_specContext ctx) {
        newTexts.put(ctx, ctx.getChild(0).getText());
    }

    @Override
    public void exitFun_decl(MiniCParser.Fun_declContext ctx) {
        String type, s1, left, s2, right, s3;
        type = newTexts.get(ctx.type_spec());
        s1 = ctx.getChild(1).getText();
        left = ctx.getChild(2).getText();
        s2 = newTexts.get(ctx.params());
        right = ctx.getChild(4).getText();
        s3 = newTexts.get(ctx.compound_stmt());
        newTexts.put(ctx, type + " " + s1 + left + s2 + right + "\n" + s3);
    }

    @Override
    public void exitParams(MiniCParser.ParamsContext ctx) {
        String s1, type;
        if (isEmptyParams(ctx)) {
            newTexts.put(ctx, "");
        } else if (isVoidParams(ctx)) {
            type = ctx.getChild(0).getText();
            newTexts.put(ctx, type);
        } else if (isOneParams(ctx)) {
            s1 = newTexts.get(ctx.param(0));
            newTexts.put(ctx, s1);
        } else if (isMultipleParams(ctx)) {
            s1 = newTexts.get(ctx.param(0));
            for (int i = 1; i < ctx.param().size(); i++) {
                s1 = s1 + ", " + newTexts.get(ctx.param(i));
            }
            newTexts.put(ctx, s1);
        }
    }

    @Override
    public void exitParam(MiniCParser.ParamContext ctx) {
        String type, s1, left, right;
        type = newTexts.get(ctx.type_spec());
        s1 = ctx.getChild(1).getText();
        if (isArrayParam(ctx)) {
            left = ctx.getChild(2).getText();
            right = ctx.getChild(3).getText();
            newTexts.put(ctx, type + " " + s1 + left + right);
        } else {
            newTexts.put(ctx, type + " " + s1);
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
        String s1, colon;
        s1 = newTexts.get(ctx.expr());
        colon = ctx.getChild(1).getText();
        newTexts.put(ctx, s1 + colon);
    }

    @Override
    public void exitWhile_stmt(MiniCParser.While_stmtContext ctx) {
        String word, left, right, s1, s2;
        word = ctx.getChild(0).getText();
        left = ctx.getChild(1).getText();
        s1 = newTexts.get(ctx.expr());
        right = ctx.getChild(3).getText();
        s2 = newTexts.get(ctx.stmt());
        if (newTexts.get(ctx.stmt().compound_stmt()) == null) {
            s2 = "{\n...." + s2 + "\n}";
        }
        newTexts.put(ctx, word + " " + left + s1 + right + "\n" + s2);
    }

    @Override
    public void exitCompound_stmt(MiniCParser.Compound_stmtContext ctx) {
        String open, close, s1 = "", s2 = "";
        open = ctx.getChild(0).getText();
        for (int i = 0; i < ctx.local_decl().size(); i++) {
            s1 = s1 + "...." + newTexts.get(ctx.local_decl(i)) + "\n";
        }
        if (ctx.local_decl().size() != 0) {
            s1 = s1 + "\n";
        }
        for (int i = 0; i < ctx.stmt().size(); i++) {
            StringTokenizer stringTokenizer = new StringTokenizer(newTexts.get(ctx.stmt(i)), "\n");
            while (stringTokenizer.hasMoreTokens()) {
                s2 = s2 + "...." + stringTokenizer.nextToken() + "\n";
            }
        }
        close = ctx.getChild(ctx.local_decl().size() + ctx.stmt().size() + 1).getText();
        newTexts.put(ctx, open + "\n" + s1 + s2 + close);
    }

    @Override
    public void exitLocal_decl(MiniCParser.Local_declContext ctx) {
        String type, s1, eq, colon, s2;
        type = newTexts.get(ctx.type_spec());
        s1 = ctx.getChild(1).getText();
        if (isDeclaration(ctx)) {
            colon = ctx.getChild(2).getText();
            newTexts.put(ctx, type + " " + s1 + colon);
        } else if (isDeclarationAndDefine(ctx)) {
            eq = ctx.getChild(2).getText();
            s2 = ctx.getChild(3).getText();
            colon = ctx.getChild(4).getText();
            newTexts.put(ctx, type + " " + s1 + " " + eq + " " + s2 + colon);
        } else if (isArrayDeclaration(ctx)) {
            eq = ctx.getChild(2).getText();
            s2 = ctx.getChild(3).getText() + ctx.getChild(4).getText();
            colon = ctx.getChild(5).getText();
            newTexts.put(ctx, type + " " + s1 + eq + s2 + colon);
        }
    }

    @Override
    public void exitIf_stmt(MiniCParser.If_stmtContext ctx) {
        String word1, word2, s1, s2, s3;
        word1 = ctx.getChild(0).getText() + " " + ctx.getChild(1).getText();
        s1 = newTexts.get(ctx.expr()) + ctx.getChild(3).getText();
        s2 = newTexts.get(ctx.stmt(0));
        if (newTexts.get(ctx.stmt(0).compound_stmt()) == null) {
            s2 = "{\n...." + s2 + "\n}";
        }
        if (ctx.getChildCount() == 7) {
            word2 = ctx.getChild(5).getText();
            s3 = newTexts.get(ctx.stmt(1));
            if (newTexts.get(ctx.stmt(1).compound_stmt()) == null) {
                s3 = "{\n...." + s3 + "\n}";
            }
            newTexts.put(ctx, word1 + s1 + "\n" + s2 + "\n" + word2 + "\n" + s3);
        } else {
            newTexts.put(ctx, word1 + s1 + "\n" + s2);
        }
    }

    @Override
    public void exitReturn_stmt(MiniCParser.Return_stmtContext ctx) {
        String s1, s2, colon;
        s1 = ctx.getChild(0).getText();
        colon = ctx.getChild(2).getText();
        if (hasReturnExpr(ctx)) {
            s2 = newTexts.get(ctx.expr());
            newTexts.put(ctx, s1 + " " + s2 + colon);
        } else {
            newTexts.put(ctx, s1 + colon);
        }
    }

    @Override
    public void exitExpr(MiniCParser.ExprContext ctx) {
        String s1, s2, s3, op1, op2;
        if (isBinaryOperation(ctx)) {
            if (ctx.getChild(0) != ctx.expr(0)) {
                s1 = ctx.getChild(0).getText();
                s2 = newTexts.get(ctx.expr(0));
                op1 = ctx.getChild(1).getText();
                newTexts.put(ctx, s1 + " " + op1 + " " + s2);
            } else {
                s1 = newTexts.get(ctx.expr(0));
                s2 = newTexts.get(ctx.expr(1));
                op1 = ctx.getChild(1).getText();
                newTexts.put(ctx, s1 + " " + op1 + " " + s2);
            }
        } else if (isPrefixOperation(ctx)) {
            op1 = ctx.getChild(0).getText();
            s1 = newTexts.get(ctx.expr(0));
            newTexts.put(ctx, op1 + s1);
        } else if (isParentheses(ctx)) {
            op1 = ctx.getChild(0).getText();
            op2 = ctx.getChild(2).getText();
            s1 = newTexts.get(ctx.expr(1));
            newTexts.put(ctx, op1 + s1 + op2);
        } else if (ctx.getChildCount() == 1) {
            newTexts.put(ctx, ctx.getText());
        } else if (isArray(ctx)) {
            s1 = ctx.getChild(0).getText();
            s2 = newTexts.get(ctx.expr(0));
            op1 = ctx.getChild(1).getText();
            op2 = ctx.getChild(3).getText();
            newTexts.put(ctx, s1 + op1 + s2 + op2);
        } else if (isFunction(ctx)) {
            s1 = ctx.getChild(0).getText();
            s2 = newTexts.get(ctx.args());
            op1 = ctx.getChild(1).getText();
            op2 = ctx.getChild(3).getText();
            newTexts.put(ctx, s1 + op1 + s2 + op2);
        } else if (ctx.getChildCount() == 6) {
            s1 = ctx.getChild(0).getText();
            s2 = newTexts.get(ctx.expr(0));
            s3 = newTexts.get(ctx.expr(1));
            op1 = ctx.getChild(1).getText();
            op2 = ctx.getChild(3).getText() + " " + ctx.getChild(4).getText();
            newTexts.put(ctx, s1 + op1 + s2 + op2 + " " + s3);
        }
    }

    @Override
    public void exitArgs(MiniCParser.ArgsContext ctx) {
        String s1, op, s2;
        if (isEmptyArgs(ctx)) {
            newTexts.put(ctx, "");
        } else {
            s1 = newTexts.get(ctx.expr(0));
            if (isMultipleExpr(ctx)) {
                for (int i = 1; i < ctx.expr().size(); i++) {
                    op = ctx.getChild(1).getText();
                    s2 = newTexts.get(ctx.expr(i));
                    s1 = s1 + op + " " + s2;
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

    private boolean isBinaryOperation(MiniCParser.ExprContext ctx) {
        return ctx.getChildCount() == 3 && ctx.getChild(1) != ctx.expr();
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
