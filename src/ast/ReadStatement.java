/*
 * @author Thiago Martins de Jesus 380385
 * @author Vinnícius Ferreira da Silva 380032
 * */
package ast;

import java.util.ArrayList;

public class ReadStatement extends Statement {

	private ArrayList<Variable> variables;
	@Override
	public void genC(PW pw) {
		// TODO Auto-generated method stub
		
	}
	
	public ReadStatement(ArrayList<Variable> v){
		this.variables = v;
	}

	@Override
	public void genKra(PW pw, boolean putParenthesis) {
		pw.add();
        pw.printIdent("read");
        pw.print("(");         
        variables.remove(0).genKra(pw, putParenthesis);   
        for(Variable lv : variables){
            pw.print(",");
            lv.genKra(pw,putParenthesis);
        }
        pw.print(")");
        pw.sub();
		
	}

}
