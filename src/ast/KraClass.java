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
      this.methodHash = new HashMap<String, Method>();
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
   
   public Variable getInstanceVariable(String name){
	   if(instanceVariableList.getSize() !=0)
		 return instanceVariableList.isThere(name);
	   else
		   return null;
   }
   
   public Method getMethod(String name){
	   return methodHash.get(name);
   }
   
   public Method addMethod(Method m){
	   methodHash.put(m.getId(), m);
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
       for(String key: methodHash.keySet()){
    	   methodHash.get(key).genKra(pw, putParenthesis);
       }
       pw.printlnIdent("}");
   }
   
   public KraClass getSuperClass(){
	   return this.superclass;
   }

   public KraClass(String name, KraClass superclass, InstanceVariableList instanceVariableList,
		boolean isFinal, HashMap<String, Method> methodHash) {
	super(name);
	this.superclass = superclass;
	this.instanceVariableList = instanceVariableList;
	this.isFinal = isFinal;
	this.methodHash = methodHash;
}
   
   public KraClass(String name, boolean isFinal, boolean isStatic){
	   super(name);
	   this.isFinal = isFinal;
	   this.isStatic = isStatic;
	   this.methodHash = new HashMap<String, Method>();
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
   private HashMap<String,Method> methodHash;
   // m�todos p�blicos get e set para obter e iniciar as vari�veis acima,
   // entre outros m�todos
}
