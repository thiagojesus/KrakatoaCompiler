package AST;
import Lexer.*;
import java.util.*;

public class Assignstmt extends Stmt{
    private Vbl vbl;
    private Expr expr;

    public Assignstmt(Vbl vbl, Expr expr){

    	this.vbl = vbl;
    	this.expr = expr;

    }

    public void genC(PW pw){

        vbl.genC(pw);
    	pw.print(" = ");
        expr.genC(pw);
        pw.println(";");

    }

}