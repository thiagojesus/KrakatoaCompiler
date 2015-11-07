/*
 * @author Thiago Martins de Jesus 380385
 * @author Vinnícius Ferreira da Silva 380032
 * */

package ast;

public class BreakStatement extends Statement {

	@Override
	public void genC(PW pw) {
		// TODO Auto-generated method stub
		pw.printlnIdent("break;");
	}
	
	public void genKra(PW pw, boolean putParethesis){
		pw.add();
        pw.printlnIdent("break;");
        pw.sub();
	}

}
