package ast;

import java.util.*;

public class ParamList {

    public ParamList() {
       paramList = new ArrayList<Parameter>();
    }

    public void addElement(Parameter v) {
       paramList.add(v);
    }

    public Iterator<Parameter> elements() {
        return paramList.iterator();
    }

    public int getSize() {
        return paramList.size();
    }
    
    public void genKra(PW pw, boolean putParenthesis){
    	for(Parameter p: paramList){
    		p.genKra(pw, putParenthesis);
    	}
    }

    private ArrayList<Parameter> paramList;

}
