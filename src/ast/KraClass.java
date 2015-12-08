/*
 * @author Thiago Martins de Jesus 380385
 * @author Vinnícius Ferreira da Silva 380032
 * */
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
      this.methodList = new ArrayList<Method>();
      this.instanceVariableList = new InstanceVariableList();
   }
   
   public String getCname() {
	   return "_class_"+getName();
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
	   if(isStatic)
		   pw.print(" static ");
	   pw.print("class "+this.getName());       
       if(superclass != null){
           pw.print(" extends "+superclass.getCname());
       }
       pw.println(" {");
       pw.add();
       instanceVariableList.genKra(pw, putParenthesis);
       for(Method m: methodList){
    	   m.genKra(pw, putParenthesis);
       }
       pw.sub();
       pw.println("}");
   }
   
   public Iterator<InstanceVariable> getInstancesFromSuperclass(){
	   InstanceVariableList superInstances = new InstanceVariableList();
	   
	   KraClass superC = superclass;
	   while(superC != null){
		   Iterator<InstanceVariable> superI = superC.getElementsFromInstList();
		   
		   while(superI.hasNext()){
			   InstanceVariable superInstance = superI.next();
			   
			   
			   if(this.searchInstance(superInstance.getName(), false) == null){
				   if(superInstances.isThere(superInstance.getName(),false) == null){
					   superInstances.addElement(superInstance); 
				   }
			   }
			   
			   
		   }
		   
		   superC = superC.getSuperClass();
	   }
	   
	   return superInstances.elements();
   }
   
   public Variable searchInstance(String name, boolean staticFlag){
	   return instanceVariableList.isThere(name, staticFlag);
   }
   
public void genC(PW pw){
	   
	   
	   pw.println("typedef");
	   pw.add();
	   pw.printlnIdent("struct _St_"+this.getName()+"{");
	   pw.add();
	   pw.printlnIdent("Func *vt;");
	   
	   Iterator<InstanceVariable> instIt = getInstancesFromSuperclass();
	   while(instIt.hasNext()){
		   pw.printIdent("");
		   instIt.next().genC(pw, false);
		   pw.print(";");
		   pw.println(""); 
	   }
	   
	   instIt = instanceVariableList.elements();
	   while(instIt.hasNext()){
		   pw.printIdent("");
		   instIt.next().genC(pw, false);
		   pw.print(";");
		   pw.println("");
	   }
	   
	   pw.sub();
	   pw.printlnIdent("} "+getCname()+";");
	   pw.sub();
	   
	   pw.println("");
	   pw.println(getCname()+" *new_"+getName()+"(void);");
	   pw.println("");
	   
	   
	   for(Method m: methodList){
		   pw.println("");
		   m.genC(pw);
	   }
	   
	   pw.println("");
	   pw.println("Func VTclass_"+getName()+"[] = {");
	   pw.add();
	   int cont=0;
	   //metodos da super classe 
	   if(superclass != null){
		   ArrayList<Method> sMethod = superclass.methodList;
		   for(Method m: sMethod){
			   if(!m.isStatic() && !m.isPrivate())
				   pw.printIdent("( void (*)() ) "+m.getCname());
			   pw.print(",");
			   pw.println("");
		   }
	   }
	   
	   for(Method m: methodList){
		   pw.printIdent("( void (*)() ) "+m.getCname());
		   cont++;
		   if(cont != methodList.size() && methodList.size() >1)
			   pw.println(","); 
	   }
	   pw.println("");
	   pw.sub();
	   pw.println("};");
	   
	   pw.println("");
	   pw.println(getCname()+" *new_"+getName()+"(){");
	   pw.add();
	   pw.printlnIdent(getCname()+" *t;");
	   pw.println("");
	   pw.printlnIdent("if( (t = malloc(sizeof("+getCname()+"))) != NULL )");
	   pw.add();
	   pw.printlnIdent("t->vt = VTclass_"+getName()+";");
	   pw.sub();
	   pw.printlnIdent("return t;");
	   pw.sub();
	   pw.println("}");
	   
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
   
   public Iterator<InstanceVariable> getElementsFromInstList(){
	   return this.instanceVariableList.elements();
   }

   private KraClass superclass;
   private InstanceVariableList instanceVariableList;
   private boolean isFinal;
   private boolean isStatic;
   private ArrayList<Method> methodList;
   // m�todos p�blicos get e set para obter e iniciar as vari�veis acima,
   // entre outros m�todos
public int getMethodIndex(String id) {
	// TODO Auto-generated method stub
	int i = 0;
	for(Method m: methodList){
		if(m.getId().compareTo(id)==0)
			return i;
		else
			i++;
	}
	return 0;
}
}
