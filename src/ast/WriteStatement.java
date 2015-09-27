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
		pw.add();
		if(isLn){
			pw.printIdent("writeln");
		}else{
			pw.printIdent("write");
		}
        pw.print("(");
        eL.genK(pw, putParenthesis);
        pw.print(")");
        pw.sub();
		
	}

}
