/*
 * @author Thiago Martins de Jesus 380385
 * @author Vinnícius Ferreira da Silva 380032
 * */
package ast;

import java.util.ArrayList;
import java.util.Iterator;

public class VariableList {
	private ArrayList<Variable> v;

	public VariableList(ArrayList<Variable> v) {
		super();
		this.v = v;
	}

	public Iterator<Variable> elements() {
		return v.iterator();
	}
	
	public void genKra(PW pw, boolean putParenthesis){
		for(Variable var: v){
			var.genKra(pw, putParenthesis);
		}
	}
}
