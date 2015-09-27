package ast;

public class BreakStatement extends Statement {

	@Override
	public void genC(PW pw) {
		// TODO Auto-generated method stub
		
	}
	
	public void genKra(PW pw, boolean putParethesis){
		pw.add();
        pw.printlnIdent("break;");
        pw.sub();
	}

}
