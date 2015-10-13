/*
 * @author Thiago Martins de Jesus 380385
 * @author Vinnícius Ferreira da Silva 380032
 * */

package ast;

public class AssignStatement extends Statement {

	@Override
	public void genC(PW pw) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void genKra(PW pw, boolean putParenthesis) {
		// TODO Auto-generated method stub
		assign.genKra(pw, putParenthesis);
	}
	
	public AssignStatement(Expr e){
		this.assign = e;
	}
	
	private Expr assign;

}
