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
}
