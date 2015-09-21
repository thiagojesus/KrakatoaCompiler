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

}
