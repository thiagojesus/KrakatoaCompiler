/*
 * @author Thiago Martins de Jesus 380385
 * @author Vinnícius Ferreira da Silva 380032
 * */
package ast;

import java.util.Iterator;

public class WriteStatement extends Statement {
	private ExprList eL;
	private boolean isLn;
	
	public WriteStatement(ExprList _eL, boolean _isLn) {
		this.eL = _eL;
		this.isLn = _isLn;
	}
	
	@Override
	public void genC(PW pw) {
		// TODO Auto-generated method stub
		if(isLn){
			Iterator<Expr> exprIt = eL.elements();
			while(exprIt.hasNext()){
				Expr aux = exprIt.next();
				if(aux.getType().getName().equals(Type.stringType.getName())){
					pw.printIdent("puts( ");
					aux.genC(pw, false);
					pw.print(" );");
				}else{
					pw.printIdent("printf(\"%d \", ");
					aux.genC(pw, false);
					pw.print(" );");
				}
			}
			pw.print("printf(\"\n\");");
		}else{
			Iterator<Expr> exprIt = eL.elements();
			while(exprIt.hasNext()){
				Expr aux = exprIt.next();
				if(aux.getType().getName().equals(Type.stringType.getName())){
					pw.printIdent("puts( ");
					aux.genC(pw, false);
					pw.print(" );");
				}else{
					pw.printIdent("printf(\"%d \", ");
					aux.genC(pw, false);
					pw.print(" );");
				}
			}
		}
	}

	@Override
	public void genKra(PW pw, boolean putParenthesis) {
		if(isLn){
			pw.printIdent("writeln");
		}else{
			pw.printIdent("write");
		}
        pw.print("(");
        eL.genKra(pw, putParenthesis);
        pw.println(");");
		
	}

}
