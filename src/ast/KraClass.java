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
   
   public boolean getInstanceVariable(String name){
	   return this.getInstanceVariable(name);
   }
   
   public Method getMethod(String name){
	   return methodHash.get(name);
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

   public KraClass(String name, KraClass superclass, InstanceVariableList instanceVariableList,
		boolean isFinal, HashMap<String, Method> methodHash) {
	super(name);
	this.superclass = superclass;
	this.instanceVariableList = instanceVariableList;
	this.isFinal = isFinal;
	this.methodHash = methodHash;
}
   private KraClass superclass;
   private InstanceVariableList instanceVariableList;
   private boolean isFinal;
   private HashMap<String,Method> methodHash;
   // m�todos p�blicos get e set para obter e iniciar as vari�veis acima,
   // entre outros m�todos
}
