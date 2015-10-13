/*
 * @author Thiago Martins de Jesus 380385
 * @author Vinnícius Ferreira da Silva 380032
 * */
package ast;

public class Variable {

    public Variable( String name, Type type ) {
        this.name = name;
        this.type = type;
    }

    public String getName() { return name; }

    public Type getType() {
        return type;
    }
    
    public void genKra(PW pw, boolean putParenthesis){
        pw.printIdent(type.getName()+" ");
        pw.print(name);
        pw.println(";");
    }
    
    public boolean isStatic(){
    	return false;
    }
    
    private String name;
    private Type type;
}