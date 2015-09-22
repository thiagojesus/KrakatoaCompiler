/*
KeyWorlds:
        PROGRAM - Program
        VAR - Variables
        INTEGER - Int type
        REAL - Real Type
        CHAR - Char Type
        STRING - String Type
        ARRAY - Array Type
        OF - auxiliar do Array Type
        BEGIN - Begin
        END - End
        IF 
        THEN
        ELSE
        ENDIF
        WHILE
        DO
        ENDWHILE
        READ - scanf ('Ler') 
        WRITE
        WRITELN
        OR
        AND
        MOD
        DIV
        NOT
*/
package Lexer;

import java.util.*;
import AST.*;

public class Lexer {

    public Lexer( char []input, CompilerError error ) {
        this.input = input;
      // add an end-of-file label to make it easy to do the lexer
        input[input.length - 1] = '\0';
      // number of the current line
        lineNumber = 1;
        tokenPos = 0;
        this.error = error;
    }

    // contains the keywords
    static private Hashtable<String, Symbol> keywordsTable;

    // this code will be executed only once for each program execution
    static {
        keywordsTable = new Hashtable<String, Symbol>();
        keywordsTable.put( "program", Symbol.PROGRAM );
        keywordsTable.put( "var", Symbol.VAR );
        keywordsTable.put( "integer", Symbol.INTEGER );
        keywordsTable.put( "real", Symbol.REAL );
        keywordsTable.put( "char", Symbol.CHAR );
        keywordsTable.put( "string", Symbol.STRING );
        keywordsTable.put( "array", Symbol.ARRAY );
        keywordsTable.put( "of", Symbol.OF );
        keywordsTable.put( "begin", Symbol.BEGIN );
        keywordsTable.put( "end", Symbol.END );
        keywordsTable.put( "if", Symbol.IF );
        keywordsTable.put( "then", Symbol.THEN );
        keywordsTable.put( "else", Symbol.ELSE );
        keywordsTable.put( "endif", Symbol.ENDIF );
        keywordsTable.put( "while", Symbol.WHILE );
        keywordsTable.put( "do", Symbol.DO );
        keywordsTable.put( "endwhile", Symbol.ENDWHILE );
        keywordsTable.put( "read", Symbol.READ );
        keywordsTable.put( "write", Symbol.WRITE );
        keywordsTable.put( "writeln", Symbol.WRITELN );
        keywordsTable.put( "or", Symbol.OR );
        keywordsTable.put( "and", Symbol.AND );
        keywordsTable.put( "mod", Symbol.MOD );
        keywordsTable.put( "div", Symbol.DIV );
        keywordsTable.put( "not", Symbol.NOT );
        keywordsTable.put( "dot", Symbol.DOT );
        keywordsTable.put( "eof", Symbol.EOF );
        keywordsTable.put( "function", Symbol.FUNCTION );
        keywordsTable.put( "procedure", Symbol.PROCEDURE );
        keywordsTable.put( "return", Symbol.RETURN);
    }

    public void nextToken() {
        char ch;

        //caracter a ser ignorado
        while ((ch = input[tokenPos]) == ' ' || ch == '\r' || ch == '\t' || ch == '\n')  {
            // count the number of lines
            if ( ch == '\n'){
                lineNumber++;
            }
            tokenPos++;
// System.out.print(input[tokenPos-1]);
        }
        //fim de arquivo
        if ( ch == '\0') {
            token = Symbol.EOF;
        }else{
            //comentario
            if ( input[tokenPos] == '/' && input[tokenPos + 1] == '/' ) {
                while ( input[tokenPos] != '\0'&& input[tokenPos] != '\n' ){
                    tokenPos++;
// System.out.print(input[tokenPos-1]);
                }
                nextToken();

            } else {
                //se for letra/palavra
                if ( Character.isLetter( ch ) ) {
                    StringBuffer ident = new StringBuffer();
                    // get an identifier or keyword
                    ident.append(input[tokenPos]);
                    tokenPos++;
// System.out.print(input[tokenPos-1]);
                    while ( Character.isLetter( input[tokenPos] ) || Character.isDigit( input[tokenPos] )) {
                        ident.append(input[tokenPos]);
                        tokenPos++;
// System.out.print(input[tokenPos-1]);
                    }

                    stringValue = ident.toString();
// System.out.println("  StringValue "+ident.toString());
                      // if identStr is in the list of keywords, it is a keyword !
                    Symbol value = keywordsTable.get(stringValue);
                    if ( value == null ) {
                        token = Symbol.ID;
                    }else {
                        token = value;
                    }
                    if ( Character.isDigit(input[tokenPos]) ){
                        error.signal("Word followed by a number");
                    }
                //se for numero
                } else if ( Character.isDigit( ch ) ) {
                    StringBuffer number = new StringBuffer();
                    while ( Character.isDigit( input[tokenPos] ) ) {
                        number.append(input[tokenPos]);
                        tokenPos++;
// System.out.print(input[tokenPos-1]);
                    }
                    token = Symbol.NUMBER;
                    try {
                        numberValue = Integer.valueOf(number.toString()).intValue();
                    } catch ( NumberFormatException e ) {
                        error.signal("Number out of limits");
                    }
                    if ( numberValue >= MaxValueInteger ){
                        error.signal("Number out of limits");
                    }
                //se for não for palavra nem numero
                } else {

                    tokenPos++;
// System.out.print(input[tokenPos-1]);
                    switch ( ch ) {
                        case '+' :
                            token = Symbol.PLUS;
                            break;
                        case '-' :
                            token = Symbol.MINUS;
                            break;
                        case '*' :
                            token = Symbol.MULT;
                            break;
                        case '/' :
                            token = Symbol.DIV;
                            break;
                        case '%' :
                            token = Symbol.REMAINDER;
                            break;
                        case '<' :
                            if ( input[tokenPos] == '=' ) {
                                tokenPos++;
// System.out.print(input[tokenPos-1]);
                                token = Symbol.LE;
                            } else if ( input[tokenPos] == '>' ) {
                                tokenPos++;
// System.out.print(input[tokenPos-1]);
                                token = Symbol.NEQ;
                            } else {
                                token = Symbol.LT;
                            }
                            break;
                        case '>' :
                            if ( input[tokenPos] == '=' ) {
                                tokenPos++;
// System.out.print(input[tokenPos-1]);
                                token = Symbol.GE;
                            } else {
                                token = Symbol.GT;
                            }
                            break;
                        case '=' :
                            if ( input[tokenPos] == '=' ) {
                                tokenPos++;
// System.out.print(input[tokenPos-1]);
                                token = Symbol.EQ;
                            }
                            break;
                        case '.' :
                            token = Symbol.DOT;
                            break;
                        case '(' :
                            token = Symbol.LEFTPAR;
                            break;
                        case '[' :
                            token = Symbol.OB;
                            break;
                        case ']' :
                            token = Symbol.CB;
                            break;
                        case ')' :
                            token = Symbol.RIGHTPAR;
                            break;
                        case ',' :
                            token = Symbol.COMMA;
                            break;
                        case ';' :
                            token = Symbol.SEMICOLON;
                            break;
                        case ':' :
                            if ( input[tokenPos] == '=' ) {
                                tokenPos++;
// System.out.print(input[tokenPos-1]);
                                token = Symbol.ASSIGN;
                            } else{
                                token = Symbol.COLON;
                            }
                            break;
                        case '"' : 
                            token = Symbol.CHAR;

                            StringBuffer ident = new StringBuffer();
                            ident.append(input[tokenPos]);
                            tokenPos++;
// System.out.print(input[tokenPos-1]);
                            // while ((input[tokenPos]) == ' ' ||Character.isLetter( input[tokenPos] ) || Character.isDigit( input[tokenPos] )) {
                            while ( input[tokenPos] != '"' ) {
                                ident.append(input[tokenPos]);
                                tokenPos++;
// System.out.print(input[tokenPos-1]);
                            }
                            stringValue = ident.toString();

                            if ( input[tokenPos] != '"' ) {
                                tokenPos++;
                                error.signal("Illegal literal character: " + input[tokenPos-1] );
                            }
                            tokenPos++;
// System.out.print(input[tokenPos-1]);
                            break;
                        default :
                            error.signal("Invalid Character: '" + ch + "'");
                            break;
                    }
                }
            }
        }
    lastTokenPos = tokenPos - 1;      
    }

    // return the line number of the last token got with getToken()
    public int getLineNumber() {
        return lineNumber;
    }

    public String getCurrentLine() {
        int i = lastTokenPos;
        if ( i == 0 ) {
            i = 1; 
        }else {
            if ( i >= input.length ){
                i = input.length;
            }
        }
        StringBuffer line = new StringBuffer();
        // go to the beginning of the line
        while ( i >= 1 && input[i] != '\n' ){
            i--;
        }
        if ( input[i] == '\n' ){
            i++;
        }
        // go to the end of the line putting it in variable line
        while ( input[i] != '\0' && input[i] != '\n' && input[i] != '\r' ) {
            line.append( input[i] );
            i++;
        }

    return line.toString();
    }

    public String getStringValue() {
        return stringValue;
    }

    public int getNumberValue() {
        return numberValue;
    }

    public char getCharValue() {
        return charValue;
    }

    public Symbol getProxToken(){
        Symbol proxToken =  null;
        int proxTokenPos = tokenPos;
        char ch;

        while ((ch = input[proxTokenPos]) == ' ' ){
            proxTokenPos++;
        }

        //se for letra/palavra
        if ( Character.isLetter( ch ) ) {
            StringBuffer ident = new StringBuffer();
            // get an identifier or keyword
            ident.append(input[proxTokenPos]);
            proxTokenPos++;
            while ( Character.isLetter( input[proxTokenPos] ) || Character.isDigit( input[proxTokenPos] )) {
                ident.append(input[proxTokenPos]);
                proxTokenPos++;
            }

            // if identStr is in the list of keywords, it is a keyword !
            Symbol value = keywordsTable.get(ident.toString());
            if ( value == null ) {
                proxToken = Symbol.ID;
            }else {
                proxToken = value;
            }
            if ( Character.isDigit(input[proxTokenPos]) ){
                error.signal("Word followed by a number");
            }
        }else{

            if(ch == '*'){
                proxToken = Symbol.MULT;
            }
        }
        return proxToken;
    }
    
    // current token
    public Symbol token;
    private String stringValue;
    private int numberValue;
    private char charValue;
    private int  tokenPos;
    //  input[lastTokenPos] is the last character of the last token
    private int lastTokenPos;
    // program given as input - source code
    private char []input;
    // number of current line. Starts with 1
    private int lineNumber;
    private CompilerError error;
    private static final int MaxValueInteger = 32768;

}
