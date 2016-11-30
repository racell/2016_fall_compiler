import org.antlr.v4.runtime.tree.ParseTreeProperty;

/**
 * Created by SeongJung on 2016-11-29.
 */
public class UCodeGeneratorListener extends MiniCBaseListener {
    private ParseTreeProperty<String> newTexts = new ParseTreeProperty<>();

    @Override
    public void exitArgs(MiniCParser.ArgsContext ctx) {
        String s1, op, s2;
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
