package ast;

public class IfStatement extends Statement {
	
	private Expr condition;
	private Statement thenPart;
	private Statement elsePart;
	
	
	public IfStatement(Expr condition, Statement thenPart, Statement elsePart) {
		super();
		this.condition = condition;
		this.thenPart = thenPart;
		this.elsePart = elsePart;
	}


	@Override
	public void genC(PW pw) {
		// TODO Auto-generated method stub
		
	}

}
