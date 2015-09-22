/*
    Grammar:
        prog ::= PROGRAM pid ’;’ body ’.’
        body ::= [dclpart] compstmt

        dclpart ::= VAR dcls [subdcls] | subdcls

        dcls ::= dcl {dcl}
        dcl ::= idlist ’:’ type ’;’
        idlist ::= id {’,’ id}
        type ::= stdtype | arraytype
        stdtype ::= INTEGER | REAL | CHAR | STRING
        arraytype ::= ARRAY ’[’ intnum ’..’ intnum ’]’ OF stdtype

        subdcls ::= subdcl {subdcl}
        subdcl ::= subhead ’;’ body ’;’
        subhead ::= FUNCTION pid args ’:’ stdtype | PROCEDURE pid args
        args ::= ’(’ [dcls] ’)’

        compstmt ::= BEGIN stmts END
        stmts ::= stmt {’;’ stmt} ’;’
        stmt ::= ifstmt | whilestmt | assignstmt | compstmt | readstmt | writestmt | writelnstmt | returnstmt | procfuncstmt
        ifstmt ::= IF expr THEN stmts [ELSE stmts] ENDIF
        whilestmt ::= WHILE expr DO stmts ENDWHILE
        assignstmt ::= vbl ’:=’ expr
        readstmt ::= READ ’(’ vblist ’)’
        writestmt ::= WRITE ’(’ exprlist ’)’
        writelnstmt ::= WRITELN ’(’ [exprlist] ’)’

        returnstmt ::= RETURN [expr]
        procfuncstmt ::= pid ’(’ [exprlist] ’)’

        vblist ::= vbl {’,’ vbl}
        vbl ::= id [’[’ expr ’]’]
        exprlist ::= expr {’,’ expr}
        expr ::= simexp [relop expr]
        simexp ::= [unary] term {addop term}
        term ::= factor {mulop factor}
        factor ::= vbl | num | ’(’ expr ’)’ | ’"’.’"’ | procfuncstmt
        id ::= letter {letter | digit}
        pid ::= letter {letter | digit}

        num ::= intnum [’.’ intnum]
        
        intnum ::= digit {digit}
        relop ::= ’=’ | ’<’ | ’>’ | ’<=’ | ’>=’ | ’<>’
        addop ::= ’+’ | ’-’ | OR
        mulop ::= ’*’ | ’/’ | AND | MOD | DIV
        unary ::= ’+’ | ’-’ | NOT
*/
 
import AST.*;
import java.util.*;
import Lexer.*;
import java.io.*;

public class Compiler {

    public Prog compile( char []input, PrintWriter outError) {
        symbolTable = new Hashtable<String, Var>();
        localSymbolTable = new Hashtable<String, Var>();
        functionTable = new Hashtable<String, Subhead>();
        error = new CompilerError( outError );
        lexer = new Lexer(input, error);
        error.setLexer(lexer);
        
        lexer.nextToken();

        return prog();
    }


    //prog ::= PROGRAM pid ’;’ body ’.’
    private Prog prog(){
        Body b = null;

        if(lexer.token == Symbol.PROGRAM){
            lexer.nextToken();
            pid();

            if(lexer.token == Symbol.SEMICOLON){
                b = body();

                if(lexer.token != Symbol.DOT){
                    error.signal("Error: Expected '.'.");
                }

            }else{
                error.signal("Error: Expected ';'.");
            }

        }else{
            error.signal("Error: incorrect start simbol. Try 'PROGRAM'.");
        }

        return new Prog(b);
    }


    // body ::= [dclpart] compstmt
    private Body body(){
        Dclpart dclpart = null;

        lexer.nextToken();
        
        if(lexer.token == Symbol.VAR){
            dclpart = dclpart();
        }

        Compstmt compstmt = compstmt();

        return new Body(dclpart, compstmt);
    }

    // dclpart ::= VAR dcls [subdcls] | subdcls
    private Dclpart dclpart(){
        Dcls dclpart = null;
        ArrayList<Subdcl> subdcls = new ArrayList<Subdcl>();

        //redundancia na verificação da chamada para melhorar o reuso da função
        if(lexer.token == Symbol.VAR){
            lexer.nextToken();
            //armazena a lista de variaveis declaradas
            dclpart = dcls();

            for( Dcl dcl : dclpart.getDcl()){
                for ( Var v : dcl.getIdList()) {
                    // semantic analysis
                    // if the name is in the symbol table, the variable is been declared twice.
                    if ( symbolTable.get(v.getName()) != null )
                        error.signal("Variable " + v.getName() + " has already been declared");
                    // inserts the variable in the symbol table. The name is the key and an
                    // object of class Variable is the value. Hash tables store a pair (key, value)
                    // retrieved by the key.
                    symbolTable.put( v.getName(), v ); 
                }
            }
        }else{
            error.signal("Error: incorrect variable section identifier. Try 'VAR'.");
        }
        if(lexer.token == Symbol.FUNCTION || lexer.token == Symbol.PROCEDURE){
            subdcls = subdcls();
        }

        return new Dclpart(dclpart, subdcls);
    }

    // dcls ::= dcl {dcl}
    //constroi a lista das variaveis declaradas
    private Dcls dcls(){
        ArrayList<Dcl> dcls = new ArrayList<Dcl>();
        dcls.add(dcl());

        while(lexer.token == Symbol.ID){
            dcls.add(dcl());
        }


        return new Dcls(dcls);
    }
    
    // dcl ::= idlist ’:’ type ’;’
    //verifica a declaração das variaveis
    private Dcl dcl(){

        Type type = null;
        ArrayList<Var> idlist = idlist();

        if(lexer.token == Symbol.COLON){
            lexer.nextToken();
            type = type();

            for ( Var v : idlist ) {
                // add type to the variable
                v.setType(type);
            }

            if(lexer.token != Symbol.SEMICOLON){
                error.signal("dError: Expected ';'.");
            }else{
                lexer.nextToken();
            }

        }else{
            error.signal("Error: Expected ':'.");
        }

        return new Dcl(type, idlist);
    }

    // idlist ::= id {’,’ id}
    private ArrayList<Var> idlist(){
        String name = new String();
        ArrayList<Var> idlist = new ArrayList<Var>();

        name = id();
        // variable does not have a type yet
        Var v = new Var(name);
        // list of the last variables declared. They don't have types yet
        idlist.add(v); //idlist

        while(lexer.token == Symbol.COMMA){
            lexer.nextToken();
            
            name = id();
            // variable does not have a type yet
            v = new Var(name);
            // list of the last variables declared. They don't have types yet
            idlist.add(v); //idlist
        }


        return idlist;
    }

    //type ::= stdtype | arraytype
    private Type type(){
        String typename = new String();
        Array array = null;

        if(lexer.token == Symbol.INTEGER || lexer.token == Symbol.REAL || lexer.token == Symbol.CHAR || lexer.token == Symbol.STRING){
            
            typename = stdtype();

        }else if(lexer.token == Symbol.ARRAY){
            
            array = arraytype();
            typename = array.getArrayType();
        }

        return new Type(typename, array);
    }

    //stdtype ::= INTEGER | REAL | CHAR | STRING
    private String stdtype(){
        String stdtype = new String();

        switch ( lexer.token ) {
            case INTEGER :
              stdtype = "int";
              break;
            case REAL :
              stdtype = "real";
              break;
            case CHAR :
              stdtype = "char";
              break;
            case STRING :
              stdtype = "char*";
              break;
            default :
              error.signal("Type expected");
              stdtype = null;
        }

        lexer.nextToken();

        return stdtype;
    }

    //arraytype ::= ARRAY ’[’ intnum ’..’ intnum ’]’ OF stdtype
    private Array arraytype(){
        int end = 0;
        String stdtype = new String();

        lexer.nextToken();

        if(lexer.token == Symbol.OB){
            lexer.nextToken();

            if(lexer.token == Symbol.NUMBER){
                lexer.nextToken();

                if(lexer.token == Symbol.DOT){
                    lexer.nextToken();

                    if(lexer.token == Symbol.DOT){
                        lexer.nextToken();

                        if(lexer.token == Symbol.NUMBER){
                            end = intnum();
                            lexer.nextToken();

                            if(lexer.token == Symbol.CB){
                                lexer.nextToken();

                                if(lexer.token == Symbol.OF){
                                    lexer.nextToken();
                                    stdtype = stdtype();
                                }else {
                                    error.signal("Error: lexical error.signal. Expected 'OR'.");
                                }

                            }else{
                                error.signal("Error: unexpected symbol. Try ']'.");
                            }
                        }else{
                            error.signal("Error: a number was expected!");
                        }
                    }else{
                        error.signal("Error: unexpected symbol. Try '.'.");
                    }
                }else{
                    error.signal("Error: unexpected symbol. Try '.'.");
                }
            }else{
                error.signal("Error: a number was expected!");
            }
        }else{
             error.signal("Error: unexpected symbol. Try '['.");
        }

        return new Array(end, stdtype);
    }

    // subdcls ::= subdcl {subdcl}
    private ArrayList<Subdcl> subdcls(){
        ArrayList<Subdcl> subdcls = new ArrayList<Subdcl>();

        subdcls.add(subdcl());

        while(lexer.token == Symbol.FUNCTION || lexer.token == Symbol.PROCEDURE){
            subdcls.add(subdcl());
        }

        return subdcls;
    }


    // subdcl ::= subhead ’;’ body ’;’
    private Subdcl subdcl(){
        Subhead subhead = null;
        Body body = null;

        subhead = subhead();

        //verifica se já foi declarada uma função com esse nome
        if ( functionTable.get(subhead.getName()) != null )
            error.signal("Function " + subhead.getName() + " has already been declared");
        //armazena as FUNCTIONS e PROCEDURES
        functionTable.put(subhead.getName(), subhead);

        if(lexer.token == Symbol.SEMICOLON){

            body = body();

            if(lexer.token == Symbol.SEMICOLON){
                lexer.nextToken();

                //limpa a tabela com as variaveis locais para liberalas para uso em outras funções
                localSymbolTable.clear();

            }else{
                error.signal("Error: Expected ';'.");
            }

        }else{
            error.signal("Error: Expected ';'.");
        }

        return new Subdcl(subhead, body);
    }

    // subhead ::= FUNCTION pid args ’:’ stdtype | PROCEDURE pid args
    private Subhead subhead(){
        String func = null;
        Args args = null;
        String stdtype = null;

        Var aux = null;
        if(lexer.token == Symbol.FUNCTION){

            lexer.nextToken();
            func = pid();
            args = args();
            if(lexer.token == Symbol.COLON){
                lexer.nextToken();
                stdtype = stdtype();
            }else{
                error.signal("Error: Expected ':'.");
            }

        }else if(lexer.token == Symbol.PROCEDURE){
            lexer.nextToken();

            func = pid();
            args = args();
        }

        return new Subhead(func, args, stdtype);
    }

    // args ::= ’(’ [dcls] ’)’
    private Args args(){
        Dcls dcls = null;

        if(lexer.token == Symbol.LEFTPAR){
            lexer.nextToken();
            dcls = dcls();
            if(lexer.token == Symbol.RIGHTPAR){
                lexer.nextToken();
            }else{
                error.signal("dError: unexpected symbol. Try ')'.");
            }
        }else{
            error.signal("Error: unexpected symbol. Try '('.");
        }

        if(dcls != null){
            for( Dcl dcl : dcls.getDcl()){
                for ( Var v : dcl.getIdList()) {
                    // semantic analysis
                    // if the name is in the symbol table, the variable is been declared twice.
                    if ( localSymbolTable.get(v.getName()) != null )
                        error.signal("Variable " + v.getName() + " has already been declared");
                    // inserts the variable in the symbol table. The name is the key and an
                    // object of class Variable is the value. Hash tables store a pair (key, value)
                    // retrieved by the key.
                    localSymbolTable.put( v.getName(), v ); 
                }
            }
        }

        return new Args(dcls);
    }

    //compstmt ::= BEGIN stmts END
    private Compstmt compstmt(){
        Stmts stmts = null;
// System.out.println(lexer.token);
        if(lexer.token == Symbol.BEGIN){
            lexer.nextToken();
            stmts = stmts();

            if(lexer.token != Symbol.END){
                error.signal("Error: unexpected symbol. Try 'END'.");
            }else{
                lexer.nextToken();
            }
        }else{
            error.signal("Error: unexpected symbol. Try 'BEGIN'.");
        }

        return new Compstmt(stmts);
    }

    // stmts ::= stmt {’;’ stmt} ’;’
    private Stmts  stmts(){
        ArrayList<Stmt> stmts = new ArrayList<Stmt>();

// System.out.println("+-" + lexer.token);

        stmts.add(stmt());
        lexer.nextToken();

// System.out.println("+-" + lexer.token);
        if(lexer.token != Symbol.SEMICOLON){
            error.signal("cError: Expected ';'.");
        }

        while(lexer.token == Symbol.SEMICOLON){
            lexer.nextToken();
            //se o proximo caracter for um L, tem um inicio valido para chamar o proximo stmt(), se não o ';'representa o fim do stmt'
            if(lexer.token == Symbol.IF || lexer.token == Symbol.WHILE || lexer.token == Symbol.ID || lexer.token == Symbol.BEGIN 
                || lexer.token == Symbol.READ || lexer.token == Symbol.WRITE || lexer.token == Symbol.WRITELN || lexer.token == Symbol.RETURN){
                stmts.add(stmt());
            }
// System.out.println("+- " + lexer.token);
        }

        return new Stmts(stmts);
    }

    // stmt ::= ifstmt | whilestmt | assignstmt | compstmt | readstmt | writestmt | writelnstmt | returnstmt | procfuncstmt
    private Stmt stmt(){
        Stmt stmt = null;
        if(lexer.token == Symbol.IF){
            return ifstmt();

        }else if(lexer.token == Symbol.WHILE){
            return whilestmt();

        }else if(lexer.token == Symbol.ID){

            Subhead func = functionTable.get(lexer.getStringValue());
            if(func != null){
                return procfuncstmt();
            }else{
                return assignstmt();
            }

        }else if(lexer.token == Symbol.BEGIN){
            return compstmt();

        }else if(lexer.token == Symbol.READ){
            return readstmt();

        }else if(lexer.token == Symbol.WRITE){
            return writestmt();

        }else if(lexer.token == Symbol.WRITELN){
            return writelnstmt();

        }else if(lexer.token == Symbol.RETURN){
            return returnstmt();
        }else{
             // will never be executed
              error.signal("Statement expected");
        }

        return null;
    }

    //ifstmt ::= IF expr THEN stmts [ELSE stmts] ENDIF
    private Ifstmt ifstmt(){
        Expr condition = null;
        Stmts stmtThen = null;
        Stmts stmtElse = null;

        lexer.nextToken();
        condition = expr();

        if(lexer.token == Symbol.THEN){
            lexer.nextToken();
            stmtThen = stmts();
            

            if(lexer.token == Symbol.ELSE){
                lexer.nextToken();
                stmtElse = stmts();
                
            }

            if(lexer.token == Symbol.ENDIF){
                lexer.nextToken();
            }else{
                error.signal("Error: unexpected symbol. Try 'ENDIF'.");
            }


        }else{
            error.signal("Error: unexpected symbol. Try 'THEN'.");
        }

        return new Ifstmt(condition, stmtThen, stmtElse);
    }

    // whilestmt ::= WHILE expr DO stmts ENDWHILE
    private Whilestmt whilestmt(){
        Expr condition = null;
        Stmts stmtsDo = null;

        lexer.nextToken();

        condition = expr();
        lexer.nextToken();

        if(lexer.token == Symbol.DO){

            stmtsDo = stmts();
            lexer.nextToken();

            if(lexer.token == Symbol.ENDWHILE){
                lexer.nextToken();
            }else{
                error.signal("Error: unexpected symbol. Try 'ENDWHILE'.");
            }
        }else{
            error.signal("Error: unexpected symbol. Try 'DO'.");
        }

        return new Whilestmt(condition, stmtsDo);
    }

    //assignstmt ::= vbl ’:=’ expr
    private Assignstmt assignstmt(){
        Vbl vblname = null;
        Expr expr = null;

        vblname = vbl();
        if(lexer.token == Symbol.ASSIGN){
            lexer.nextToken();

            expr = expr();
            lexer.nextToken();
        }else{
            error.signal("Error: unexpected symbol. Try ':='.");
        }

        return new Assignstmt(vblname, expr);

    }

    //readstmt ::= READ ’(’ vblist ’)’
    private Readstmt readstmt(){
        ArrayList<Vbl> vbls = new ArrayList<Vbl>();

        lexer.nextToken();

        if(lexer.token == Symbol.LEFTPAR){
            lexer.nextToken();

            vbls = vblist();

            if(lexer.token == Symbol.RIGHTPAR){
                lexer.nextToken();
            }else{
                error.signal("eError: unexpected symbol. Try ')'.");
            }

        }else{
            error.signal("Error: unexpected symbol. Try '('.");
        }

        return new Readstmt(vbls);
    }

    //writestmt ::= WRITE ’(’ exprlist ’)’
    private Writestmt writestmt(){
        ArrayList<Expr> exprlist = null;
        lexer.nextToken();

        if(lexer.token == Symbol.LEFTPAR){
            lexer.nextToken();
            exprlist = exprlist();
            lexer.nextToken();

            if(lexer.token == Symbol.RIGHTPAR){
                lexer.nextToken();

            }else{
                error.signal("fError: unexpected symbol. Try ')'.");
            }

        }else{
            error.signal("Error: unexpected symbol. Try '('.");
        }

        return new Writestmt(exprlist);
    }


    //writelnstmt ::= WRITELN ’(’ [exprlist] ’)’
    private Writelnstmt writelnstmt(){
        ArrayList<Expr> exprlist = null;

        lexer.nextToken();

        if(lexer.token == Symbol.LEFTPAR){
            lexer.nextToken();

            if(lexer.token == Symbol.ID){
                exprlist = exprlist();
// System.out.println(" --" + lexer.token);
                //lexer.nextToken();
            }
            lexer.nextToken();
// System.out.println(" +-" + lexer.token);
            if(lexer.token == Symbol.RIGHTPAR){
// System.out.println("---"+lexer.token);  
            }else{
                error.signal("aError: unexpected symbol. Try ')'.");
            }

        }else{
            error.signal("Error: unexpected symbol. Try '('.");
        }

        return new Writelnstmt(exprlist);
    }

    // returnstmt ::= RETURN [expr]
    private Returnstmt returnstmt(){

        lexer.nextToken();
        Expr expr = expr();

        return new Returnstmt(expr);
    }

    // procfuncstmt ::= pid ’(’ [exprlist] ’)’
    private Procfuncstmt procfuncstmt(){
        String pid = null;
        ArrayList<Expr> exprlist = null;

        pid = pid();
        Subhead sub = (Subhead) functionTable.get( pid );
        if ( sub == null ) {
            error.signal("Function " + pid + " was not declared.");
        }

        if(lexer.token == Symbol.LEFTPAR){
            lexer.nextToken();
            exprlist = exprlist();

            if(lexer.token == Symbol.RIGHTPAR){
                
            }else{
                error.signal("bError: unexpected symbol. Try ')'.");
            }
        }else{
            error.signal("Error: unexpected symbol. Try '('.");
        }

        return new Procfuncstmt(pid, exprlist);

    }

    // vblist ::= vbl {’,’ vbl}
    private ArrayList<Vbl> vblist(){
        ArrayList<Vbl> vblist = new ArrayList<Vbl>();
        vblist.add(vbl());
        // nextToken();

        while(lexer.token == Symbol.COMMA){
            lexer.nextToken();
            vblist.add(vbl());
        }

        return vblist;
    }

    // vbl ::= id [’[’ expr ’]’]
    private Vbl vbl(){
        String vblname = new String();
        Expr expr = null;

        vblname = id();

        Var v = (Var) symbolTable.get( vblname );
         // semantic analysis
         // was the variable declared ? 
        if ( v == null ) {
            v = (Var) localSymbolTable.get( vblname );
            if ( v == null ) {
                error.signal("Variable " + vblname + " was not declared");
            }
        }

        if(lexer.token == Symbol.OB){

            lexer.nextToken();
            expr = expr();
            lexer.nextToken();

            if(lexer.token != Symbol.CB){
                error.signal("Error: unexpected symbol. Try ']'.");
            }
        }

        return new Vbl(v, expr);
    }

    // exprlist ::= expr {’,’ expr}
    private ArrayList<Expr> exprlist(){
        ArrayList<Expr> exprlist = new ArrayList<Expr>();

        exprlist.add(expr());
// System.out.println(" +-" + lexer.token);

        while(lexer.token == Symbol.COMMA){
            // lexer.nextToken();
            exprlist.add(expr());
            lexer.nextToken();
        }

        return exprlist;
    }


    // expr ::= simexp [relop expr]
    private Expr expr(){
        Simexp simexp = null;
        Symbol relop = Symbol.NULL;
        Expr expr = null;

        simexp = simexp();

        if(lexer.token == Symbol.EQ || lexer.token == Symbol.LT || lexer.token == Symbol.GT || lexer.token == Symbol.LE || lexer.token == Symbol.GE || lexer.token == Symbol.NEQ){
            relop = relop();
            expr = expr();
        }

        return new Expr(simexp, relop, expr);
    }
    

    // simexp ::= [unary] term {addop term}
    private Simexp simexp(){
        Symbol unary = Symbol.NULL;
        Term term1 = null;
        ArrayList<Symbol> addop = new ArrayList<Symbol>();
        ArrayList<Term> term2 = new ArrayList<Term>();

        if(lexer.token == Symbol.PLUS ||lexer.token == Symbol.MINUS || lexer.token == Symbol.NOT){
            unary = unary();
        }
// System.out.println(lexer.getStringValue()+"....");
        term1 = term();
        // lexer.nextToken();
// System.out.println(lexer.getStringValue()+"....oo");
        while(lexer.token == Symbol.PLUS ||lexer.token == Symbol.MINUS || lexer.token == Symbol.OR){
            addop.add(addop());
            term2.add(term());
        }

        return new Simexp(unary, term1, addop, term2);
    }

    // term ::= factor {mulop factor}
    private Term term(){
        Factor factor = null;
        ArrayList<Symbol> mulops = new ArrayList<Symbol>();
        ArrayList<Factor> extraFactors = new ArrayList<Factor>();

        factor = factor();
// System.out.println("+- " + lexer.token);

        Symbol future = lexer.getProxToken();
        if(future == Symbol.MULT || future == Symbol.DIV || future == Symbol.AND || future == Symbol.MOD){
            lexer.nextToken();
        
// System.out.println(" F"+ future);
            while(lexer.token == Symbol.MULT || lexer.token == Symbol.DIV || lexer.token == Symbol.AND || lexer.token == Symbol.MOD){
                mulops.add(mulop());
                extraFactors.add(factor());
                lexer.nextToken();
            }
        }

        return new Term(factor, mulops, extraFactors);
    }
    

    // factor ::= vbl | num | ’(’ expr ’)’ | ”’.”’ | procfuncstmt //nexttoken
    private Factor factor(){
        Vbl vbl = null;
        Num num = null;
        Expr expr = null;
        Symbol dot = Symbol.NULL;
        Stmt procfunc = null;

        if(lexer.token == Symbol.ID){
            Subhead func = functionTable.get(lexer.getStringValue());
            if(func != null){
                procfunc = procfuncstmt();
// System.out.println("+- " + lexer.token);
                // lexer.nextToken();
            }else{
                vbl = vbl();
// System.out.println("  "+lexer.token);    
                // lexer.nextToken();
            }
// System.out.println(lexer.getStringValue()+"....factor");
        }else if(lexer.token == Symbol.NUMBER){
            num = num();
            // lexer.nextToken();
        }else if(lexer.token == Symbol.LEFTPAR){
            lexer.nextToken();

            expr = expr();
            lexer.nextToken();
// System.out.println("  "+lexer.token);
            if(lexer.token == Symbol.RIGHTPAR){
                lexer.nextToken();
            }else{
                error.signal("cError: unexpected symbol. Try ')'.");
            }

        }else if(lexer.token == Symbol.DOT){
            dot = Symbol.DOT;
            lexer.nextToken();

        }

        return new Factor(vbl, num, expr, dot, procfunc);
    }

    // id ::= letter {letter | digit}
    private String id(){
        String id = new String();
        
// System.out.println("Token: " + lexer.token + " id: " + id);
        if(lexer.token == Symbol.ID){
            id = lexer.getStringValue();
// System.out.println("Token:" + lexer.token + " id: " + id);
        }else{
            error.signal("Error: illegal variable name!");
        }

        lexer.nextToken();

        return id;
    }

    // pid ::= letter {letter | digit}
    //percorre o identificador alfanumerico do programa
    private String pid(){

        if(lexer.token != Symbol.ID){
            error.signal("Error: illegal program name!");
        }

        lexer.nextToken();

        return lexer.getStringValue();
    }

    // num ::= [’+’ | ’-’] digit [’.’] {digit}//nexttoken
    //num ::= intnum [’.’ intnum]
    private Num num() {
        int intPart = 0;
        int decimalPart = 0;

        if(lexer.token == Symbol.NUMBER){
            intPart = intnum();
            lexer.nextToken();

            if(lexer.token == Symbol.DOT){
                lexer.nextToken();
                decimalPart = intnum();
            }

        }else{
            error.signal("Error: a number was expected.");
        }

        return new Num(intPart, decimalPart);
    }

    // intnum ::= digit {digit}
    //a validação é feita no proprio nextToken() quando encontramos um Symbol.NUMBER
    private int intnum() {
        if(lexer.token != Symbol.NUMBER){        
            error.signal("Error: a number was expected.");
        }

        return lexer.getNumberValue();
    }

    // relop ::= ’=’ | ’<’ | ’>’ | ’<=’ | ’>=’ | ’<>’ //nexttoken
    private Symbol relop(){
        Symbol symbol = Symbol.NULL;

        if(lexer.token == Symbol.EQ){
            symbol = Symbol.EQ;
        }else if(lexer.token == Symbol.LT){
            symbol = Symbol.LT;
        }else if(lexer.token == Symbol.GT){
            symbol = Symbol.GT;
        }else if(lexer.token == Symbol.LE){
            symbol = Symbol.LE;
        }else if(lexer.token == Symbol.GE){
            symbol = Symbol.GE;
        }else if(lexer.token == Symbol.NEQ){
            symbol = Symbol.NEQ;
        }else{
            error.signal("Error: unexpected symbol. A operator was expected.");
        }

        lexer.nextToken();

        return symbol;
    }

    // addop ::= ’+’ | ’-’ | OR //nexttoken
    private Symbol addop(){
        Symbol symbol = Symbol.NULL;

        if(lexer.token == Symbol.PLUS){
            symbol = Symbol.PLUS;
        }else if(lexer.token == Symbol.MINUS){
            symbol = Symbol.MINUS;
        }else if(lexer.token == Symbol.OR){
            symbol = Symbol.OR;
        }else{
            error.signal("Error: unexpected symbol.");
        }

        lexer.nextToken();

        return symbol;
    }
    
    // mulop ::= ’*’ | ’/’ | AND | MOD | DIV //nexttoken
    private Symbol mulop(){
        Symbol symbol = Symbol.NULL;

        if(lexer.token == Symbol.MULT){
            symbol = Symbol.MULT;
        }else if(lexer.token == Symbol.AND){
            symbol = Symbol.AND;
        }else if(lexer.token == Symbol.MOD){
            symbol = Symbol.MOD;
        }else{
            error.signal("Error: unexpected symbol.");
        }

        lexer.nextToken();

        return symbol;
    }
    
    // unary ::= ’+’ | ’-’ | NOT //nexttoken
    private Symbol unary(){
        Symbol symbol = Symbol.NULL;

        if(lexer.token == Symbol.PLUS){
            symbol = Symbol.PLUS;
        }else if(lexer.token == Symbol.MINUS){
            symbol = Symbol.MINUS;
        }else if(lexer.token == Symbol.NOT){
            symbol = Symbol.NOT;
        }else{
            error.signal("Error: unexpected symbol.");
        }

        lexer.nextToken();

        return symbol;
    }
    
    private Hashtable<String, Var> symbolTable;
    private Hashtable<String, Var> localSymbolTable;
    private Hashtable<String, Subhead> functionTable;
    private Lexer lexer;
    private CompilerError error;
      
}
