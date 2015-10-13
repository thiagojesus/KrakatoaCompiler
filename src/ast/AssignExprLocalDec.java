/*
 * @author Thiago Martins de Jesus 380385
 * @author Vinnícius Ferreira da Silva 380032
 * */


package ast;

import java.util.Iterator;

public class AssignExprLocalDec extends Expr{   
	public Type getType() {
		if(firstExpr != null)
			return firstExpr.getType();
		if(localList != null){
			Iterator<Variable> i = localList.elements();
			return i.next().getType();
		}
		return null;
	}
	
	public AssignExprLocalDec(Expr first, Expr second){
		firstExpr = first;
		secondExpr = second;
		localList = null;
	}
	
	/*public AssignExprLocalDec(LocalVarList localList, Expr first){
		firstExpr = first;
		this.localList = localList;
		secondExpr = null;
	}*/
	
	public AssignExprLocalDec(Expr first){
		firstExpr = first;
		localList = null;
		secondExpr = null;
	}
	
	public AssignExprLocalDec(VariableList localList){
		this.localList = localList;
		firstExpr = null;
		secondExpr = null;
	}
	
	private Expr firstExpr;
	private Expr secondExpr;
	private VariableList localList;
	@Override
	public void genC(PW pw, boolean putParenthesis) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void genKra(PW pw, boolean putParenthesis) {
		// TODO Auto-generated method stub
		if(localList != null){
			localList.genKra(pw, putParenthesis);
		}else{
			firstExpr.genKra(pw, putParenthesis);
			if(secondExpr != null){
				pw.print(" = ");
				secondExpr.genKra(pw, putParenthesis);
			}
			
		}
	}
}
