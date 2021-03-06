/*
 * @author Thiago Martins de Jesus 380385
 * @author Vinnícius Ferreira da Silva 380032
 * */
package ast;

import java.util.ArrayList;

public class CompositeStatement extends Statement {

	private StatementList s;
	
	
	
	public CompositeStatement(ArrayList<Statement> _s) {
		super();
		s = new StatementList(_s);
	}



	@Override
	public void genC(PW pw) {
		// TODO Auto-generated method stub
		s.genC(pw);
	}
	
	public void genKra(PW pw, boolean putParenthesis){
        pw.println("{");
        s.genKra(pw, putParenthesis);
        pw.printlnIdent("}");
    }

}
