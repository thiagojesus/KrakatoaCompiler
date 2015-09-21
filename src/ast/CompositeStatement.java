package ast;

public class CompositeStatement extends Statement {

	private Statement s;
	
	
	
	public CompositeStatement(Statement s) {
		super();
		this.s = s;
	}



	@Override
	public void genC(PW pw) {
		// TODO Auto-generated method stub
		
	}

}
