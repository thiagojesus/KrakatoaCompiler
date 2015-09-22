package ast;

import java.util.ArrayList;

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
	private Type returnType;
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
	private Symbol qualifier;
	private String id;
	private ParamList variables;
	//lista de variaveis locais
	private StatementList statementList;
}
