/*
 * @author Thiago Martins de Jesus 380385
 * @author Vinnícius Ferreira da Silva 380032
 * */
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


	@Override
	public void genKra(PW pw, boolean putParenthesis) {
		pw.add();
        pw.printIdent("if ");
        pw.print("( ");
        condition.genKra(pw, putParenthesis);
        if(thenPart.getClass().getName().equals("AST.CompStatement"))
            pw.print(" ) ");
        else
            pw.println(" ) ");
        thenPart.genKra(pw, putParenthesis);        
        if(elsePart!= null){
            pw.printlnIdent("else");
            elsePart.genKra(pw, putParenthesis);            
        }
        pw.sub();
		
	}

}
