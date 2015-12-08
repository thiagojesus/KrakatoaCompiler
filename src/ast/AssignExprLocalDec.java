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
		if(firstExpr == null){
			if(localList != null){
				Iterator<Variable> varIt = localList.elements();
				Variable aux;
				while(varIt.hasNext()){
					aux = varIt.next();
					Type t = aux.getType();
					if(t.getCname().compareTo("int") == 0 || t.getCname().compareTo("char *") == 0 || t.getCname().compareTo("void") == 0 || t.getCname().compareTo("boolean") == 0)
						pw.print(aux.getType().getCname()+" _"+aux.getName());
					else
						pw.print(aux.getType().getCname()+" *_"+aux.getName());
					//pw.println(";");
				}
			}
		}else{
			firstExpr.genC(pw, putParenthesis);
			
			if(secondExpr != null){
				pw.print(" = ");
				Type t = secondExpr.getType();
				if(t.getCname().compareTo("int") == 0 || t.getCname().compareTo("char *") == 0 || t.getCname().compareTo("void") == 0){
					secondExpr.genC(pw, putParenthesis);
					//pw.println(";");
			}else{
					//pw.print("( "+ t.getCname() +"* ); ");
					secondExpr.genC(pw, putParenthesis);
				}
				//pw.println(";");
			}
			
		}
		//pw.println(";");
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
