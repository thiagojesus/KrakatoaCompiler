/*
 * @author Thiago Martins de Jesus 380385
 * @author Vinnícius Ferreira da Silva 380032
 * */
package ast;

public class InstanceVariable extends Variable {

    public InstanceVariable( String name, Type type, KraClass _mother ) {
        super(name, type);
        this.staticFlag = false;
        this.motherClass = _mother;
    }
    
    public void genKra(PW pw, boolean putParenthesis){
        if(staticFlag){
            pw.print("static ");
        }
        pw.print("private ");
        super.genKra(pw, putParenthesis);
        pw.println(";");
        
    }
    
    public void setStatic(boolean isStatic){
    	this.staticFlag = isStatic;
    }
    
    public boolean isStatic(){
    	return this.staticFlag;
    }
    
     private boolean staticFlag;
     private KraClass motherClass;
}