package AST;
import Lexer.*;
import java.util.*;

public class Whilestmt extends Stmt{
    private Expr condition;
    private Stmts stmtsDo;

    public Whilestmt(Expr condition, Stmts stmtsDO){

    	this.condition = condition;
    	this.stmtsDo = stmtsDo;

    }

    public void genC(PW pw){
    	pw.println("while(");
        condition.genC(pw);
        pw.println("){");

    	pw.add();
    	stmtsDo.genC(pw);
    	pw.sub();

    	pw.println("}");
    }

}