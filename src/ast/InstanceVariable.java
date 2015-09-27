package ast;

public class InstanceVariable extends Variable {

    public InstanceVariable( String name, Type type ) {
        super(name, type);
        this.staticFlag = false;
    }
    
    public void genKra(PW pw, boolean putParenthesis){
        if(staticFlag){
            pw.print("static ");
        }
        pw.print("private");
        super.genKra(pw, putParenthesis);
        pw.println(";");
        
    }
    
     private boolean staticFlag;

}