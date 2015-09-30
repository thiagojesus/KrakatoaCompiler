package ast;

public class AssignStatement extends Statement {

	@Override
	public void genC(PW pw) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void genKra(PW pw, boolean putParenthesis) {
		// TODO Auto-generated method stub
		
	}
	
	public AssignStatement(Expr e){
		this.assign = e;
	}
	
	private Expr assign;

}
