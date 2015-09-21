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
    
    public boolean isThere(String name){
    	for(InstanceVariable v: instanceVariableList){
    		if(v.getName() == name) return true;
    	}
    	return false;
    }

    private ArrayList<InstanceVariable> instanceVariableList;

}
