package AST;
import Lexer.*;
import java.util.*;


public class Expr{
	private Simexp simexp = null;
    private Symbol relop;
    private Expr expr = null;

	public Expr(Simexp simexp, Symbol relop, Expr expr){
		
		this.simexp = simexp;
		this.relop = relop;
		this.expr = expr;

	}

	public void genC(PW pw){

		simexp.genC(pw);
		
		if (relop != Symbol.NULL){
			if(relop == Symbol.EQ){
            	pw.print(" == ");
        	}else if(relop == Symbol.LT){
           		pw.print(" < ");
        	}else if(relop == Symbol.GT){
            	pw.print(" > ");
        	}else if(relop == Symbol.LE){
            	pw.print(" <= ");
        	}else if(relop == Symbol.GE){
            	pw.print(" >= ");
        	}else if(relop == Symbol.NEQ){
            	pw.print(" != ");
            }
			expr.genC(pw);
		}

	}

}