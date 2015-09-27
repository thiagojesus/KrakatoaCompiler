package ast;

import java.util.ArrayList;

public class ReadStatement extends Statement {

	private ArrayList<String> variables;
	@Override
	public void genC(PW pw) {
		// TODO Auto-generated method stub
		
	}
	
	public ReadStatement(ArrayList<String> v){
		this.variables = v;
	}

	@Override
	public void genKra(PW pw, boolean putParenthesis) {
		pw.add();
        pw.printIdent("read");
        pw.print("(");         
        pw.print(variables.remove(0));   
        for(String lv : variables){
            pw.print(",");
            pw.print(lv);
        }
        pw.print(")");
        pw.sub();
		
	}

}
