/*
 * @author Thiago Martins de Jesus 380385
 * @author Vinnícius Ferreira da Silva 380032
 * */
package ast;

public class ReturnStatement extends Statement {
	private Expr e;
	
	public ReturnStatement(Expr _e){
		this.e= _e;
	}

	@Override
	public void genC(PW pw) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void genKra(PW pw, boolean putParenthesis) {
		pw.add();
        pw.printIdent("return ");
        e.genKra(pw, putParenthesis);
        pw.sub();
		
	}
}
