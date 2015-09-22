package AST;
import Lexer.*;
import java.util.*;

// writestmt ::= WRITE ’(’ exprlist ’)’
// writelnstmt ::= WRITELN ’(’ [exprlist] ’)’  

public class Writelnstmt extends Stmt{
    private ArrayList<Expr> exprlist;

    public Writelnstmt(ArrayList<Expr> exprlist){

    	this.exprlist = exprlist;

    }

    public void genC(PW pw){

    	pw.print("prinf(\"");
        if(exprlist != null){
            for (Expr e : exprlist){
            e.genC(pw);
        }
        }

        pw.println("\");");
    }

}