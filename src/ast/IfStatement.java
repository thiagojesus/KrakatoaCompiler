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
		pw.printIdent("if(");
		condition.genC(pw, false);
		pw.print("){");
		pw.add();
		pw.println("");
		thenPart.genC(pw);
		pw.println(";");
		pw.sub();
		pw.printlnIdent("}");
		if(elsePart != null){
			pw.printIdent("else{");
			pw.add();
			pw.println("");
			elsePart.genC(pw);
			pw.sub();
			//pw.println("");
			//pw.println("");
			pw.printlnIdent("}");
		}
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
