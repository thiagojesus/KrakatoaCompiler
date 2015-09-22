package AST;
import Lexer.*;
import java.util.*;

public class Procfuncstmt extends Stmt{

	private String pid = null;
	private ArrayList<Expr> exprlist = new ArrayList<Expr>();

	public Procfuncstmt(String pid, ArrayList<Expr> exprlist){
		this.pid = pid;
		this.exprlist = exprlist;

	}

	public void genC(PW pw){
		pw.println(pid+"(");
		expr.genC(pw);
		pw.println(")");


	
	}

}