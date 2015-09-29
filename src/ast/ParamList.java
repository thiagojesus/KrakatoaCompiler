package ast;

import java.util.*;

public class ParamList {

    public ParamList() {
       paramList = new ArrayList<Variable>();
    }

    public void addElement(Parameter v) {
       paramList.add(v);
    }

    public Iterator<Variable> elements() {
        return paramList.iterator();
    }

    public int getSize() {
        return paramList.size();
    }
    
    public void genKra(PW pw, boolean putParenthesis){
    	for(Variable p: paramList){
    		p.genKra(pw, putParenthesis);
    	}
    }

    private ArrayList<Variable> paramList;

}
