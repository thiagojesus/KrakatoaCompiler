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
	
	public void genKra(PW pw, boolean putParenthesis){
        pw.println("{");
        s.genKra(pw, putParenthesis);
        pw.printlnIdent("}");
    }

}
