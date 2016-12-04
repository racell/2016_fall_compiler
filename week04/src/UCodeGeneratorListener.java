import org.antlr.v4.runtime.tree.ParseTreeProperty;

/**
 * Created by SeongJung on 2016-11-29.
 */
public class UCodeGeneratorListener extends MiniCBaseListener {
    private ParseTreeProperty<String> newTexts = new ParseTreeProperty<>();
    final private String blank = "           ";

    @Override
    public void exitProgram(MiniCParser.ProgramContext ctx) {
        for (int i = 0; i < ctx.decl().size(); i++) {
            System.out.println(newTexts.get(ctx.decl(i)));
        }
        System.out.println("bgn 0");
        System.out.println("ldp");
        System.out.println("call main");
        System.out.println("end");
    }

    @Override
    public void exitExpr(MiniCParser.ExprContext ctx) {
        String s1, s2, s3, op1, op2;
        if (isBinaryOperation(ctx)) {
            if (ctx.getChild(0) != ctx.expr(0)) {
                s1 = newTexts.get(ctx.IDENT());
                s2 = newTexts.get(ctx.expr(0));
                newTexts.put(ctx, s2 + "\n" + "str " + s1 + "\n");
            } else {
                s1 = newTexts.get(ctx.expr(0));
                s2 = newTexts.get(ctx.expr(1));
                op1 = forBinaryOperation(ctx.getChild(1).getText());
                newTexts.put(ctx, s1 + "\n" + s2 + "\n" + op1 + "\n");
            }
        } else if (isPrefixOperation(ctx)) {
            op1 = forPrefixOperation(ctx.getChild(0).getText());
            s1 = newTexts.get(ctx.expr(0));
            newTexts.put(ctx, s1 + "\n" + op1 + "\n");
        } else if (isParentheses(ctx)) {
            s1 = newTexts.get(ctx.expr(1));
            newTexts.put(ctx, s1 + "\n");
        } else if (isIdentOrLiteral(ctx)) {
            if (ctx.getChild(0) == ctx.IDENT()) {
                s1 = newTexts.get(ctx.IDENT());
                newTexts.put(ctx, "lod " + s1 + "\n");
            } else {
                s1 = ctx.getChild(0).getText();
                newTexts.put(ctx, "ldc " + s1 + "\n");
            }
        } else if (isArray(ctx)) {
            s1 = newTexts.get(ctx.IDENT());
            s2 = newTexts.get(ctx.expr(0));
            newTexts.put(ctx, s2 + "\n" + "lda " + s1 + "\n");
        } else if (isFunction(ctx)) {
            s1 = newTexts.get(ctx.IDENT());
            s2 = newTexts.get(ctx.args());
            newTexts.put(ctx, s2 + "\n" + "call " + s1 + "\n");
        } else if (ctx.getChildCount() == 6) {
            s1 = newTexts.get(ctx.IDENT());
            s2 = newTexts.get(ctx.expr(0));
            s3 = newTexts.get(ctx.expr(1));
            newTexts.put(ctx, s2 + "lda " + s1 + "\nadd\n" + s3 + "sti\n");
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
                    s1 += newTexts.get(ctx.expr(i));
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
