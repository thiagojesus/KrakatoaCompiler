package AST;
import Lexer.*;

public class Var {
    private String name;
    private Type type;
    
    public Var( String name, Type type ) {
        this.name = name;
        this.type = type;
    }
    
    public Var( String name ) {
        this.name = name;
    }
    
    public void setType( Type type ) {
        this.type = type;
    }

    public void setType( String stdtype ){
        this.type = new Type(stdtype, null);
    }
    
    public String getName() { 
        return name; 
    }
    
    public String getType() {
        return type.getTypename();
    }
}