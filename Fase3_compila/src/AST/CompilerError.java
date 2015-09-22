package AST;


import Lexer.*;
import java.io.*;

public class CompilerError {
    
    public CompilerError( PrintWriter out ) {
          // output of an error is done in out
        this.out = out;
    }
    
    public void setLexer( Lexer lexer ) {
        this.lexer = lexer;
    }
    
    public void signal( String strMessage ) {
        out.println("Error at line " + lexer.getLineNumber() + ": ");
        out.println(lexer.getCurrentLine());
        out.println( "bababa"+strMessage );
        if ( out.checkError() )
          System.out.println("Error in signaling an error");
        throw new RuntimeException(strMessage);
    }
    
    private Lexer lexer;
    PrintWriter out;
}
