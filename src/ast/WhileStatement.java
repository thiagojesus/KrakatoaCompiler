/*
 * @author Thiago Martins de Jesus 380385
 * @author Vinnícius Ferreira da Silva 380032
 * */
package ast;

public class WhileStatement extends Statement {

	private Expr condition;
	private CompositeStatement repeat;
	private Statement s;
	
	
	public WhileStatement(Expr condition, CompositeStatement repeat) {
		super();
		this.condition = condition;
		this.repeat = repeat;
	}
	
	public WhileStatement(Expr condition, Statement repeat) {
		super();
		this.condition = condition;
		this.s = repeat;
	}


	@Override
	public void genC(PW pw) {
		// TODO Auto-generated method stub
		pw.printIdent("while(");
		condition.genC(pw, false);
		pw.print("){");
		pw.add();
		pw.println("");
		if(repeat != null)
			repeat.genC(pw);
		else
			s.genC(pw);
		pw.sub();
		pw.printlnIdent("}");
	}



	@Override
	public void genKra(PW pw, boolean putParenthesis) {
		pw.add();
        pw.printIdent("while");
        pw.print("(");
        condition.genKra(pw, putParenthesis);
        pw.print(")");
        pw.println("{");
        if(repeat != null)
        	repeat.genKra(pw, putParenthesis);         
        pw.sub();
		
	}

}
