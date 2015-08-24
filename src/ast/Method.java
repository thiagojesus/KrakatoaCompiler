package ast;

import java.util.ArrayList;

public class Method implements Comparable {
	public Method(Type type, String id, ArrayList<Variable> variables, ArrayList<Statement> statementList) {
		super();
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
	public ArrayList<Variable> getVariables() {
		return variables;
	}
	public void setVariables(ArrayList<Variable> variables) {
		this.variables = variables;
	}
	public ArrayList<Statement> getStatementList() {
		return statementList;
	}
	public void setStatementList(ArrayList<Statement> statementList) {
		this.statementList = statementList;
	}
	private String id;
	private ArrayList<Variable> variables;
	private ArrayList<Statement> statementList;
	@Override
	public int compareTo(Method o) {
		if(this.getId().equals(o.id)){
			if(this.getType() == o.returnType){
				if(this.variables.size() == o.variables.size()){
					int i = 0;
					for(Variable v : this.getVariables()){
						if(v.getType() != o.variables.get(i).getType()){
							return -1;
						}
						i++;
					}
					return 0;
				}
			}
		}
		return -1;
	}
}
