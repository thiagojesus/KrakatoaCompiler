package AST;
import Lexer.*;
import java.util.*;

public class Stmts{
	private ArrayList<Stmt> stmt;

	public Stmts(ArrayList<Stmt> stmt){
		
		this.stmt = stmt;

	}

	public void genC(PW pw){

		for (Stmt s : stmt){
			s.genC(pw);
			// pw.println(";");
		}
		
	}

}