package AST;
import Lexer.*;
import java.util.*;

public class Compstmt extends Stmt{
	private Stmts stmts;

	public Compstmt(Stmts stmts){
		
		this.stmts = stmts;

	}

	public void genC(PW pw){
		stmts.genC(pw);
	}

}