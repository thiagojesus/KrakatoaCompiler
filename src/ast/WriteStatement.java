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

}
