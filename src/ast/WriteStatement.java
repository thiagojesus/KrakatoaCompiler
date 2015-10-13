/*
 * @author Thiago Martins de Jesus 380385
 * @author Vinnícius Ferreira da Silva 380032
 * */
package ast;

public class WriteStatement extends Statement {
	private ExprList eL;
	private boolean isLn;
	
	public WriteStatement(ExprList _eL, boolean _isLn) {
		this.eL = _eL;
		this.isLn = _isLn;
	}
	
	@Override
	public void genC(PW pw) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void genKra(PW pw, boolean putParenthesis) {
		if(isLn){
			pw.printIdent("writeln");
		}else{
			pw.printIdent("write");
		}
        pw.print("(");
        eL.genKra(pw, putParenthesis);
        pw.println(");");
		
	}

}
