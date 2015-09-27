package ast;

import java.util.ArrayList;

public class StatementList {

	private ArrayList<Statement> sL;
	
	public StatementList(ArrayList<Statement> _s){
		this.sL = _s;
	}
	
	public ArrayList<Statement> getList(){
		return this.sL;
	}
	
	public void genKra(PW pw, boolean putParenthesis){
		for(Statement s: sL){
			s.genKra(pw, putParenthesis);
		}
	}
}
