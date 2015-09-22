package AST;
import Lexer.*;
import java.util.*;

public class Vbl{

	private Var var;
	private Expr expr;
	
	public Vbl(Var var, Expr expr){
		this.var = var;
		this.expr = expr;
	}

	public String getType(){
		return var.getType();
	}

	public String getName(){
		return var.getName();
	}

	public Expr getExpr(){
		return expr;
	}
	
	public void genC(PW pw){
		pw.print(var.getName());

		if(expr != null){
			pw.print("[");
			expr.genC(pw);
			pw.print("]");
		}
	}

}
