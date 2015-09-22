package AST;
import Lexer.*;
import java.util.*;

public class Returnstmt extends Stmt{

	private Expr expr = null;

	public Returnstmt(Expr expr){
		
		this.expr = expr;

	}

	public void genC(PW pw){
		pw.("return ");
		expr.genC(pw);

	
	}

}