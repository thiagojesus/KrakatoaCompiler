package ast;

public class WhileStatement extends Statement {

	private Expr condition;
	private Statement repeat;
	
	
	
	public WhileStatement(Expr condition, Statement repeat) {
		super();
		this.condition = condition;
		this.repeat = repeat;
	}



	@Override
	public void genC(PW pw) {
		// TODO Auto-generated method stub
		
	}

}
