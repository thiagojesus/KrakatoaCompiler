package AST;
import Lexer.*;
import java.util.*;

public class Writestmt extends Stmt{
    private ArrayList<Expr> exprlist;

    public Writestmt(ArrayList<Expr> exprlist){

    	this.exprlist = exprlist;

    }

    public void genC(PW pw){


    	pw.print("printf(");

    	pw.print("prinf(\"");


        for (Expr e : exprlist){
            e.genC(pw);
        }

        pw.println("\");");
    }

}