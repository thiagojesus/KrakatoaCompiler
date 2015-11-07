/*
 * @author Thiago Martins de Jesus 380385
 * @author Vinnícius Ferreira da Silva 380032
 * */
package ast;

import java.util.ArrayList;
import java.util.Iterator;

import lexer.Symbol;

public class Method{
	public Method(Symbol _qualifier, Type type, String id, ParamList variables, StatementList statementList) {
		super();
		this.qualifier = _qualifier;
		this.returnType = type;
		this.id = id;
		this.variables = variables;
		this.statementList = statementList;
	}
	
	public Type getType() {
		return returnType;
	}
	public void setType(Type type) {
		this.returnType = type;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public ParamList getVariables() {
		return variables;
	}
	public void setVariables(ParamList variables) {
		this.variables = variables;
	}
	public StatementList getStatementList() {
		return statementList;
	}
	public void setStatementList(StatementList statementList) {
		this.statementList = statementList;
	}
	
	public void genKra(PW pw, boolean putParenthesis){
		//pw.add();
        pw.printIdent(qualifier.toString()+ " ");
        pw.print(returnType.getCname() + " ");
        pw.print(this.id);
        pw.print("(");
        if(variables != null)
            variables.genKra(pw, putParenthesis);
        pw.print(") ");
        pw.println("{"); 
        pw.add();
        statementList.genKra(pw, putParenthesis);  
        pw.sub();
        pw.printlnIdent("}");
        //pw.sub();
	}
	
	public Method(String _id, Type _returnType, Symbol _qualifier, boolean _isStatic, boolean _isFinal, KraClass _motherClass){
		this.id = _id;
		this.returnType = _returnType;
		this.qualifier = _qualifier;
		this.isStatic = _isStatic;
		this.isFinal = _isFinal;
		this.motherClass = _motherClass;
	}
	
	public boolean isFinal(){
		return this.isFinal;
	}
	
	public ParamList getParamList(){
		return this.variables;
	}
	
	public int getParamListSize(){
		return variables.getSize();
	}
	
	public Iterator<Variable> getParamElements(){
		return variables.elements();
	}
	
	public boolean isStatic(){
		return this.isStatic;
	}
	
	public boolean isPrivate(){
		if(qualifier == Symbol.PRIVATE)
			return true;
		return false;
	}
	
	public String getCname(){
		if(isStatic)
			return "_static_"+motherClass.getName()+"_"+id;
		else
			return "_"+motherClass.getName()+"_"+id;
	}
	
	public KraClass getThisClass(){
		return this.motherClass;
	}
	
	public void genC(PW pw){
		if(isStatic){
			if(returnType.getCname().compareTo("int") == 0 || returnType.getCname().compareTo("char *") == 0 || returnType.getCname().compareTo("void") == 0)
				pw.print(returnType.getCname()+" _static_"+motherClass.getName()+"_"+id+"(");
			else
				pw.print(returnType.getCname()+"* _static_"+motherClass.getName()+"_"+id+"(");
			if(variables != null){
				Iterator<Variable> paramIt = variables.elements();
				while(paramIt.hasNext()){
					paramIt.next().genC(pw);
					if(paramIt.hasNext())
						pw.print(", ");
				}
			}
			pw.print("){");
			pw.println("");
			pw.add();
			//pw.printIdent("");
			statementList.genC(pw);
			
			pw.sub();
			pw.println("");
			pw.printlnIdent("}");
		}else{
			if(returnType.getCname().compareTo("int") == 0 || returnType.getCname().compareTo("char *") == 0 || returnType.getCname().compareTo("void") == 0)
				pw.print(returnType.getCname()+" _"+motherClass.getName()+"_"+id+"( "+motherClass.getCname()+" *this");
			else
				pw.print(returnType.getCname()+"* _"+motherClass.getName()+"_"+id+"( "+motherClass.getCname()+" *this");
			if(variables != null){
				pw.print(", ");
				Iterator<Variable> paramIt = variables.elements();
				while(paramIt.hasNext()){
					paramIt.next().genC(pw);
					if(paramIt.hasNext())
						pw.print(", ");
				}
			}
			pw.print("){");
			pw.println("");
			pw.add();
			statementList.genC(pw);
			
			pw.sub();
			pw.println("");
			pw.printlnIdent("}");
		}
	}
	
	private Symbol qualifier;
	private boolean isStatic;
	private boolean isFinal;
	private KraClass motherClass;
	private Type returnType;
	private String id;
	private ParamList variables;
	private ArrayList<Variable> local;
	private StatementList statementList;
}
