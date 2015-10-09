package ast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/*
 * Krakatoa Class
 */
public class KraClass extends Type {
	
   public KraClass( String name, boolean _isFinal ) {
      super(name);
      this.isFinal = _isFinal;
      this.methodList = new ArrayList<Method>();
      this.instanceVariableList = new InstanceVariableList();
   }
   
   public String getCname() {
      return getName();
   }
   
   public boolean isFinal(){
	   if(this.isFinal == true){
		   return true;
	   }else{
		   return false;
	   }
   }
   
   public Variable getInstanceVariable(String name, boolean isStatic){
	   if(instanceVariableList.getSize() !=0)
		 return instanceVariableList.isThere(name, isStatic);
	   else
		   return null;
   }
   
   public Method getMethod(String name, boolean isStatic){
	   for(Method m: methodList){
		   if(m.getId().compareTo(name)==0 && m.isStatic()==isStatic)
			   return m;
	   }
	   return null;
   }
   
   public Method addMethod(Method m){
	   methodList.add(m);
	   return m;
   }
   
   public boolean hasSuper(){
	   if(this.superclass != null){
		   return true;
	   }
	   return false;
   }
   
   public KraClass getSuper(){
	   return this.superclass;
   }
   
   public void genKra(PW pw, boolean putParenthesis){
	   if(isFinal){
		   pw.print("final ");
	   }
	   pw.print("class "+this.getCname());       
       if(superclass != null){
           pw.print(" extends "+superclass.getCname());
       }
       pw.printlnIdent("{");
       instanceVariableList.genKra(pw, putParenthesis);
       for(Method m: methodList){
    	   m.genKra(pw, putParenthesis);
       }
       pw.printlnIdent("}");
   }
   
   public KraClass getSuperClass(){
	   return this.superclass;
   }

   public KraClass(String name, KraClass superclass, InstanceVariableList instanceVariableList,
		boolean isFinal, ArrayList<Method> methodList) {
	super(name);
	this.superclass = superclass;
	this.instanceVariableList = instanceVariableList;
	this.isFinal = isFinal;
	this.methodList = methodList;
}
   
   public KraClass(String name, boolean isFinal, boolean isStatic){
	   super(name);
	   this.isFinal = isFinal;
	   this.isStatic = isStatic;
	   this.methodList = new ArrayList<Method>();
	   this.instanceVariableList = new InstanceVariableList();
   }
   
   public void setSuperclass(KraClass superclass) {
	this.superclass = superclass;
}
   
   public void addInstances(InstanceVariableList vList, boolean isStatic){
	   Iterator<InstanceVariable> i;
	   i = vList.elements();
	   while(i.hasNext()){
		   instanceVariableList.addElement(i.next(),isStatic);
	   }
   }

private KraClass superclass;
   private InstanceVariableList instanceVariableList;
   private boolean isFinal;
   private boolean isStatic;
   private ArrayList<Method> methodList;
   // m�todos p�blicos get e set para obter e iniciar as vari�veis acima,
   // entre outros m�todos
}
