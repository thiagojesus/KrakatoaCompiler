package ast;

import java.util.ArrayList;
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
   

   public KraClass(String name, String name2, KraClass superclass, InstanceVariableList instanceVariableList,
		boolean isFinal, ArrayList<Method> MethodList) {
	super(name);
	this.name = name2;
	this.superclass = superclass;
	this.instanceVariableList = instanceVariableList;
	this.isFinal = isFinal;
	this.MethodList = MethodList;
}
   private String name;
   private KraClass superclass;
   private InstanceVariableList instanceVariableList;
   private boolean isFinal;
   private ArrayList<Method> MethodList;
   // m�todos p�blicos get e set para obter e iniciar as vari�veis acima,
   // entre outros m�todos
}
