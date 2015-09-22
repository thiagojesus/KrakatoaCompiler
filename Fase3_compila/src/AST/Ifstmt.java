package AST;
import Lexer.*;
import java.util.*;

public class Ifstmt extends Stmt{
	private Expr condition;
    private Stmts stmtThen;
    private Stmts stmtElse;

    public Ifstmt(Expr condition, Stmts stmtThen, Stmts stmtElse){

    	this.condition = condition;
    	this.stmtThen = stmtThen;
    	this.stmtElse = stmtElse;

    }

    public void genC(PW pw){
    	
        pw.print("if(");
         condition.genC(pw);
         pw.println("){");

    	pw.add();
    	stmtThen.genC(pw);
    	pw.sub();

        if(stmtElse != null){
            pw.println("}else{");

            pw.add();
            stmtElse.genC(pw);
            pw.sub();

        }

    	pw.println("}");
    }

}