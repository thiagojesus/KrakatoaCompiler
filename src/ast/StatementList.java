/*
 * @author Thiago Martins de Jesus 380385
 * @author Vinnícius Ferreira da Silva 380032
 * */
package ast;

import java.util.ArrayList;

public class StatementList {

	private ArrayList<Statement> sL;
	
	public StatementList(){
		sL = new ArrayList<Statement>();
	}
	
	public StatementList(ArrayList<Statement> _s){
		this.sL = _s;
	}
	
	public ArrayList<Statement> getList(){
		return this.sL;
	}
	
	public void addElement(Statement s){
		sL.add(s);
	}
	
	public void genKra(PW pw, boolean putParenthesis){
		for(Statement s: sL){
			s.genKra(pw, putParenthesis);
		}
	}
}
