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
    	pw.print("private");
        pw.print(type.getName());
        pw.printIdent(name);
        pw.println(";");
    }

    private String name;
    private Type type;
}