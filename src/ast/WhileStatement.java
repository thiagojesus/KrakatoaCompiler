package ast;

public class WhileStatement extends Statement {

	private Expr condition;
	private CompositeStatement repeat;
	
	
	
	public WhileStatement(Expr condition, CompositeStatement repeat) {
		super();
		this.condition = condition;
		this.repeat = repeat;
	}



	@Override
	public void genC(PW pw) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void genKra(PW pw, boolean putParenthesis) {
		pw.add();
        pw.printIdent("while");
        pw.print("(");
        condition.genKra(pw, putParenthesis);
        pw.print(")");
        pw.println("");
        repeat.genKra(pw, putParenthesis);         
        pw.sub();
		
	}

}
