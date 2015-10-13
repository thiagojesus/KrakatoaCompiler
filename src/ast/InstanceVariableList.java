/*
 * @author Thiago Martins de Jesus 380385
 * @author Vinnícius Ferreira da Silva 380032
 * */
package ast;

import java.util.*;

public class InstanceVariableList {

    public InstanceVariableList() {
       instanceVariableList = new ArrayList<InstanceVariable>();
    }

    public void addElement(InstanceVariable instanceVariable) {
       instanceVariableList.add( instanceVariable );
    }

    public Iterator<InstanceVariable> elements() {
    	return this.instanceVariableList.iterator();
    }

    public int getSize() {
        return instanceVariableList.size();
    }
    
    public Variable isThere(String name, boolean isStatic){
    	for(InstanceVariable v: instanceVariableList){
    		if(v.getName().compareTo(name) == 0 && v.isStatic()==isStatic) return v;
    	}
    	return null;
    }
    
    public void genKra(PW pw, boolean putParenthesis){
    	for(InstanceVariable v: instanceVariableList){
    		v.genKra(pw, putParenthesis);
    	}
    }
    
    public void addElement(InstanceVariable iV, boolean isStatic){
    	iV.setStatic(isStatic);
    	instanceVariableList.add(iV);
    }

    private ArrayList<InstanceVariable> instanceVariableList;

}
