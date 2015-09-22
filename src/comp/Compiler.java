package comp;

import ast.*;
import lexer.*;
import java.io.*;
import java.util.*;

public class Compiler {
	
	/*
	 * AssignExprLocalDe:c:= Expression [ “=” Expression ] | LocalDec
BasicType BasicValue BooleanValue ClassDec CompStatement Digit Expression ExpressionList Factor
FormalParamDec HighOperator
Id
IdList
IfStat InstVarDec
::= “void” | “int” | “boolean” | “String”
::= IntValue | BooleanValue | StringValue
::= “true” | “false”
::= “class” Id [ “extends” Id ] “{” MemberList “}” ::= “{” { Statement } “}”
::=“0”|...|“9”
::= SimpleExpression [ Relation SimpleExpression ] ::= Expression { “,” Expression }
::= BasicValue |
“(” Expression “)” | “!” Factor |
“null” | ObjectCreation | PrimaryExpr
::= ParamDec { “,” ParamDec } ::=“∗”|“/”|“&&” ::=Letter{Letter|Digit|“”} ::=Id{“,”Id}
::= “if ” “(” Expression “)” Statement [ “else” Statement ]
::= Type IdList “;”
￼9
IntValue LeftValue Letter LocalDec LowOperator MemberList Member MethodDec
MOCall MOParam ObjectCreation ParamDec Program Qualifier ReadStat PrimaryExpr
Relation ReturnStat RightValue Signal SignalFactor SimpleExpression Statement
StatementList Term
Type WriteStat WhileStat
::= Digit { Digit }
::= [ (“this” | Id ) “.” ] Id
::=“A”|... |“Z”|“a”|... |“z”
::= Type IdList “;”
::= “+” | “−” | “||”
::= { Qualifier Member }
::= InstVarDec | MethodDec
::= Type Id “(” [ FormalParamDec ] “)”
“{” StatementList “}”
::= “@” Id [ “(” { MOParam } “)” ]
::= IntValue | StringValue | Id
::= “new” Id “(” “)”
::= Type Id
::= { MOCall } ClassDec { ClassDec }
::= [ “final” ] [ “static” ] ( “private” | “public”) ::= “read” “(” LeftValue { “,” LeftValue } “)” ::= “super” “.” Id “(” [ ExpressionList ] “)” |
Id |
Id “.” Id |
Id “.” Id “(” [ ExpressionList ] ”)” |
Id “.” Id “.” Id “(” [ ExpressionList ] “)” | “this” |
“this” “.” Id |
“this” ”.” Id “(” [ ExpressionList ] “)” | “this” ”.” Id “.” Id “(” [ ExpressionList ] “)”
::= “==” | “<” | “>” | “<=” | “>=” | “! =” ::= “return” Expression
::= “this” [ “.” Id ] | Id [ “.” Id ]
::= “+” | “−”
::= [ Signal ] Factor
::= Term { LowOperator Term }
::= AssignExprLocalDec “;” | IfStat | WhileStat | ReturnStat “;” |
ReadStat “;” | WriteStat “;” | “break” “;” | “;” | CompStatement ::= { Statement }
::= SignalFactor { HighOperator SignalFactor }
::= BasicType | Id
::= “write” “(” ExpressionList “)”
::= “while” “(” Expression “)” Statement
	 * */

	// compile must receive an input with an character less than
	// p_input.lenght
	public Program compile(char[] input, PrintWriter outError) {

		ArrayList<CompilationError> compilationErrorList = new ArrayList<>();
		signalError = new SignalError(outError, compilationErrorList);
		symbolTable = new SymbolTable();
		lexer = new Lexer(input, signalError);
		signalError.setLexer(lexer);

		Program program = null;
		lexer.nextToken();
		program = program(compilationErrorList);
		return program;
	}

	private Program program(ArrayList<CompilationError> compilationErrorList) {
		// Program ::= KraClass { KraClass }
		ArrayList<MetaobjectCall> metaobjectCallList = new ArrayList<>();
		ArrayList<KraClass> kraClassList = new ArrayList<>();
		try {
			while ( lexer.token == Symbol.MOCall ) {
				metaobjectCallList.add(metaobjectCall());
			}
			kraClassList.add(classDec());
			while ( lexer.token == Symbol.CLASS || lexer.token == Symbol.FINAL)
				kraClassList.add(classDec());
			if ( lexer.token != Symbol.EOF ) {
				signalError.show("End of file expected");
			}
		}
		catch( RuntimeException e) {
			// if there was an exception, there is a compilation signalError
		}
		return new Program(kraClassList, metaobjectCallList, compilationErrorList);
	}

	/**  parses a metaobject call as <code>{@literal @}ce(...)</code> in <br>
     * <code>
     * @ce(5, "'class' expected") <br>
     * clas Program <br>
     *     public void run() { } <br>
     * end <br>
     * </code>
     * 
	   
	 */
	@SuppressWarnings("incomplete-switch")
	private MetaobjectCall metaobjectCall() {
		String name = lexer.getMetaobjectName();
		lexer.nextToken();
		ArrayList<Object> metaobjectParamList = new ArrayList<>();
		if ( lexer.token == Symbol.LEFTPAR ) {
			// metaobject call with parameters
			lexer.nextToken();
			while ( lexer.token == Symbol.LITERALINT || lexer.token == Symbol.LITERALSTRING ||
					lexer.token == Symbol.IDENT ) {
				switch ( lexer.token ) {
				case LITERALINT:
					metaobjectParamList.add(lexer.getNumberValue());
					break;
				case LITERALSTRING:
					metaobjectParamList.add(lexer.getLiteralStringValue());
					break;
				case IDENT:
					metaobjectParamList.add(lexer.getStringValue());
				}
				lexer.nextToken();
				if ( lexer.token == Symbol.COMMA ) 
					lexer.nextToken();
				else
					break;
			}
			if ( lexer.token != Symbol.RIGHTPAR ) 
				signalError.show("')' expected after metaobject call with parameters");
			else
				lexer.nextToken();
		}
		if ( name.equals("nce") ) {
			if ( metaobjectParamList.size() != 0 )
				signalError.show("Metaobject 'nce' does not take parameters");
		}
		else if ( name.equals("ce") ) {
			if ( metaobjectParamList.size() != 3 && metaobjectParamList.size() != 4 )
				signalError.show("Metaobject 'ce' take three or four parameters");
			if ( !( metaobjectParamList.get(0) instanceof Integer)  )
				signalError.show("The first parameter of metaobject 'ce' should be an integer number");
			if ( !( metaobjectParamList.get(1) instanceof String) ||  !( metaobjectParamList.get(2) instanceof String) )
				signalError.show("The second and third parameters of metaobject 'ce' should be literal strings");
			if ( metaobjectParamList.size() >= 4 && !( metaobjectParamList.get(3) instanceof String) )  
				signalError.show("The fourth parameter of metaobject 'ce' should be a literal string");
			
		}
			
		return new MetaobjectCall(name, metaobjectParamList);
	}

	private KraClass classDec() {
		KraClass superClass = new KraClass(null,false);
		KraClass kraClass;
		boolean isFinal = false;
		boolean isPrivate = false;
		boolean isStatic = false;
		Symbol qualifier = null;
		Method method;
		HashMap methodHash = new HashMap<String, Method>();
		InstanceVariableList varList = new InstanceVariableList();
		// Note que os m�todos desta classe n�o correspondem exatamente �s
		// regras
		// da gram�tica. Este m�todo classDec, por exemplo, implementa
		// a produ��o KraClass (veja abaixo) e partes de outras produ��es.

		/*
		 * KraClass ::= [``final'']``class'' Id [ ``extends'' Id ] "{" MemberList "}"
		 * MemberList ::= { Qualifier Member } 
		 * Member ::= InstVarDec | MethodDec
		 * InstVarDec ::= Type IdList ";" 
		 * MethodDec ::= Qualifier Type Id "("[ FormalParamDec ] ")" "{" StatementList "}" 
		 * Qualifier ::= [ "static" ]  ( "private" | "public" )
		 */
		//verifica se é final
		if(lexer.token == Symbol.FINAL){
			isFinal = true;
			lexer.nextToken();
		}
		
		if ( lexer.token != Symbol.CLASS ) signalError.show("'class' expected");
		lexer.nextToken();
		if ( lexer.token != Symbol.IDENT )
			signalError.show(SignalError.ident_expected);
		String className = lexer.getStringValue();
		//colocar na global somente após toda a validação
		lexer.nextToken();
		if ( lexer.token == Symbol.EXTENDS ) {
			//verificar se existe, se não é final e mandar pra kraclass
			//será que a gente copia os métodos e variaveis do pai pra essa tbm?
			lexer.nextToken();
			if ( lexer.token != Symbol.IDENT )
				signalError.show(SignalError.ident_expected);
			String superclassName = lexer.getStringValue();
			superClass = symbolTable.getInGlobal(superclassName); 
			if(superClass == null){
				signalError.show("The inherithed class does not exist");
			}else{
				if(superClass.isFinal() == true){
					signalError.show("final classes can not be inherithed");
				}
			}
			lexer.nextToken();
		}
		if ( lexer.token != Symbol.LEFTCURBRACKET )
			signalError.show("{ expected", true);
		lexer.nextToken();

		while (lexer.token == Symbol.STATIC || lexer.token == Symbol.PRIVATE || lexer.token == Symbol.PUBLIC) {
			
			switch (lexer.token) {
			case PRIVATE:
				lexer.nextToken();
				qualifier = Symbol.PRIVATE;
				isPrivate = true;
				break;
			case PUBLIC:
				lexer.nextToken();
				qualifier = Symbol.PUBLIC;
				break;
			case STATIC:
				lexer.nextToken();
				isStatic = true;
				break;
			default:
				signalError.show("private, or public expected");
				qualifier = Symbol.PUBLIC;
			}
			Type t = type();
			currentReturnType = t;
			if ( lexer.token != Symbol.IDENT )
				signalError.show("Identifier expected");
			String name = lexer.getStringValue();
			lexer.nextToken();
			//se tem ( é método
			if ( lexer.token == Symbol.LEFTPAR ){
				methodHash.put(name, methodDec(t, name, qualifier, isStatic));
			}
			//senão é variável de instância
			else if ( qualifier != Symbol.PRIVATE )
				signalError.show("Attempt to declare a public instance variable");
			else
				varList = instanceVarDec(t, name);
				currentInstanceVariableList = varList;
		}
		if ( lexer.token != Symbol.RIGHTCURBRACKET )
			signalError.show("public/private or \"}\" expected");
		lexer.nextToken();
		kraClass = new KraClass(className,className,superClass,varList,isFinal,methodHash);
		symbolTable.putInGlobal(className, kraClass);
		return kraClass;

	}
	private InstanceVariableList instanceVarDec(Type type, String name) {
		// InstVarDec ::= [ "static" ] "private" Type IdList ";"
		String variableName;
		InstanceVariableList varList = new InstanceVariableList();
		varList.addElement(new InstanceVariable(name, type));
		while (lexer.token == Symbol.COMMA) {
			lexer.nextToken();
			if ( lexer.token != Symbol.IDENT )
				signalError.show("Identifier expected");
			variableName = lexer.getStringValue();
			varList.addElement(new InstanceVariable(variableName, type));
			lexer.nextToken();
		}
		if ( lexer.token != Symbol.SEMICOLON )
			signalError.show(SignalError.semicolon_expected);
		lexer.nextToken();
		return varList;
	}

	private Method methodDec(Type type, String name, Symbol qualifier, boolean isStatic) {
		/*
		 * MethodDec ::= Qualifier Return Id "("[ FormalParamDec ] ")" "{"
		 *                StatementList "}"
		 */
		StatementList stList;
		ParamList pL = new ParamList();
		lexer.nextToken();
		if ( lexer.token != Symbol.RIGHTPAR ) pL = formalParamDec();
		if ( lexer.token != Symbol.RIGHTPAR ) signalError.show(") expected");

		lexer.nextToken();
		if ( lexer.token != Symbol.LEFTCURBRACKET ) signalError.show("{ expected");

		lexer.nextToken();
		stList = statementList();
		if ( lexer.token != Symbol.RIGHTCURBRACKET ) signalError.show("} expected");

		lexer.nextToken();
		return new Method(qualifier, type, name, pL, stList);

	}

	private VariableList localDec() {
		// LocalDec ::= Type IdList ";"
		ArrayList<Variable>v = new ArrayList<Variable>();
		Type type = type();
		if ( lexer.token != Symbol.IDENT ) signalError.show("Identifier expected");
		v.add(new Variable(lexer.getStringValue(), type));
		lexer.nextToken();
		while (lexer.token == Symbol.COMMA) {
			lexer.nextToken();
			if ( lexer.token != Symbol.IDENT )
				signalError.show("Identifier expected");
			v.add(new Variable(lexer.getStringValue(), type));
			lexer.nextToken();
		}
		return new VariableList(v);
	}

	private ParamList formalParamDec() {
		// FormalParamDec ::= ParamDec { "," ParamDec }
		ParamList p = new ParamList();
		p.addElement(paramDec());
		while (lexer.token == Symbol.COMMA) {
			lexer.nextToken();
			p.addElement(paramDec());
		}
		return p;
	}

	private Parameter paramDec() {
		// ParamDec ::= Type Id
		String name = null;
		lexer.nextToken();
		Type t = type();
		if ( lexer.token != Symbol.IDENT ){
			signalError.show("Identifier expected");
		}else{
			name = lexer.getStringValue();
		}
		lexer.nextToken();
		return new Parameter(name,t);
	}

	private Type type() {
		// Type ::= BasicType | Id
		Type result;

		switch (lexer.token) {
		case VOID:
			result = Type.voidType;
			break;
		case INT:
			result = Type.intType;
			break;
		case BOOLEAN:
			result = Type.booleanType;
			break;
		case STRING:
			result = Type.stringType;
			break;
		case IDENT:
			// # corrija: fa�a uma busca na TS para buscar a classe
			// IDENT deve ser uma classe.
			//AQUI A PORRA FICA MUITO SÉRIA
			result = null;
			break;
		default:
			signalError.show("Type expected");
			result = Type.undefinedType;
		}
		lexer.nextToken();
		return result;
	}

	private CompositeStatement compositeStatement() {
		Statement s;
		lexer.nextToken();
		s = statement();
		if ( lexer.token != Symbol.RIGHTCURBRACKET )
			signalError.show("} expected");
		else
			lexer.nextToken();
		return new CompositeStatement(s);
	}

	private StatementList statementList() {
		ArrayList<Statement> sL = new ArrayList<Statement>();
		// CompStatement ::= "{" { Statement } "}"
		Symbol tk;
		// statements always begin with an identifier, if, read, write, ...
		while ((tk = lexer.token) != Symbol.RIGHTCURBRACKET
				&& tk != Symbol.ELSE)
			sL.add(statement());
		return new StatementList(sL);
	}

	private Statement statement() {
		Statement s;
		/*
		 * Statement ::= Assignment ``;'' | IfStat |WhileStat | MessageSend
		 *                ``;'' | ReturnStat ``;'' | ReadStat ``;'' | WriteStat ``;'' |
		 *               ``break'' ``;'' | ``;'' | CompStatement | LocalDec
		 */

		switch (lexer.token) {
		case THIS:
		case IDENT:
		case SUPER:
		case INT:
		case BOOLEAN:
		case STRING:
			s = assignExprLocalDec(); //esse puto aqui parece ser importante
			break;
		case RETURN:
			s = returnStatement();
			break;
		case READ:
			s = readStatement();
			break;
		case WRITE:
			s = writeStatement();
			break;
		case WRITELN:
			s = writelnStatement();
			break;
		case IF:
			s = ifStatement();
			break;
		case BREAK:
			s = breakStatement();
			break;
		case WHILE:
			s = whileStatement();
			break;
		case SEMICOLON:
			s = nullStatement();
			break;
		case LEFTCURBRACKET:
			s = compositeStatement();
			break;
		default:
			signalError.show("Statement expected");
		}
		return s;
	}

	/*
	 * retorne true se 'name' � uma classe declarada anteriormente. � necess�rio
	 * fazer uma busca na tabela de s�mbolos para isto.
	 */
	private boolean isType(String name) {
		return this.symbolTable.getInGlobal(name) != null;
	}

	/*
	 * AssignExprLocalDec ::= Expression [ ``$=$'' Expression ] | LocalDec
	 */
	private Expr assignExprLocalDec() {
		VariableList vL;
		if ( lexer.token == Symbol.INT || lexer.token == Symbol.BOOLEAN
				|| lexer.token == Symbol.STRING ||
				// token � uma classe declarada textualmente antes desta
				// instru��o
				(lexer.token == Symbol.IDENT && isType(lexer.getStringValue())) ) {
			/*
			 * uma declara��o de vari�vel. 'lexer.token' � o tipo da vari�vel
			 * 
			 * AssignExprLocalDec ::= Expression [ ``$=$'' Expression ] | LocalDec 
			 * LocalDec ::= Type IdList ``;''
			 */
			vL = localDec();
		}
		else {
			/*
			 * AssignExprLocalDec ::= Expression [ ``$=$'' Expression ]
			 */
			expr();
			if ( lexer.token == Symbol.ASSIGN ) {
				lexer.nextToken();
				expr();
				if ( lexer.token != Symbol.SEMICOLON )
					signalError.show("';' expected", true);
				else
					lexer.nextToken();
			}
		}
		return null;
	}

	private ExprList realParameters() {
		ExprList anExprList = null;

		if ( lexer.token != Symbol.LEFTPAR ) signalError.show("( expected");
		lexer.nextToken();
		if ( startExpr(lexer.token) ) anExprList = exprList();
		if ( lexer.token != Symbol.RIGHTPAR ) signalError.show(") expected");
		lexer.nextToken();
		return anExprList;
	}

	private WhileStatement whileStatement() {
		Expr condition;
		Statement repeat;
		lexer.nextToken();
		if ( lexer.token != Symbol.LEFTPAR ) signalError.show("( expected");
		lexer.nextToken();
		condition = expr();
		if ( lexer.token != Symbol.RIGHTPAR ) signalError.show(") expected");
		lexer.nextToken();
		repeat = statement();
		return new WhileStatement(condition, repeat);
	}

	private IfStatement ifStatement() {
		Expr e;
		Statement thenPart, elsePart = null;
		lexer.nextToken();
		if ( lexer.token != Symbol.LEFTPAR ) signalError.show("( expected");
		lexer.nextToken();
		e = expr();
		if ( lexer.token != Symbol.RIGHTPAR ) signalError.show(") expected");
		lexer.nextToken();
		thenPart = statement();
		if ( lexer.token == Symbol.ELSE ) {
			lexer.nextToken();
			elsePart = statement();
		}
		return new IfStatement(e, thenPart, elsePart);
	}

	private ReturnStatement returnStatement() {
		Expr expr;
		lexer.nextToken();
		expr = expr();
		if ( lexer.token != Symbol.SEMICOLON )
			signalError.show(SignalError.semicolon_expected);
		lexer.nextToken();
		return new ReturnStatement(expr);
	}

	private ReadStatement readStatement() {
		ArrayList<String> id = new ArrayList<String>();
		lexer.nextToken();
		if ( lexer.token != Symbol.LEFTPAR ) signalError.show("( expected");
		lexer.nextToken();
		while (true) {
			if ( lexer.token == Symbol.THIS ) {
				lexer.nextToken();
				if ( lexer.token != Symbol.DOT ) signalError.show(". expected");
				lexer.nextToken();
			}
			if ( lexer.token != Symbol.IDENT )
				signalError.show(SignalError.ident_expected);

			String name = lexer.getStringValue();
			lexer.nextToken();
			//procuro nas variaveis de instancia
			if(!currentInstanceVariableList.isThere(name)) signalError.show("instance variable not declared");
			id.add(name);
			if ( lexer.token == Symbol.COMMA )
				lexer.nextToken();
			else
				break;
		}

		if ( lexer.token != Symbol.RIGHTPAR ) signalError.show(") expected");
		lexer.nextToken();
		if ( lexer.token != Symbol.SEMICOLON )
			signalError.show(SignalError.semicolon_expected);
		lexer.nextToken();
		return new ReadStatement(id);
	}

	private WriteStatement writeStatement() {
		ExprList eS = new ExprList();
		lexer.nextToken();
		if ( lexer.token != Symbol.LEFTPAR ) signalError.show("( expected");
		lexer.nextToken();
		eS = exprList();
		if ( lexer.token != Symbol.RIGHTPAR ) signalError.show(") expected");
		lexer.nextToken();
		if ( lexer.token != Symbol.SEMICOLON )
			signalError.show(SignalError.semicolon_expected);
		lexer.nextToken();
		return new WriteStatement(eS, false);
	}

	private WriteStatement writelnStatement() {
		ExprList eL;
		lexer.nextToken();
		if ( lexer.token != Symbol.LEFTPAR ) signalError.show("( expected");
		lexer.nextToken();
		eL = exprList();
		if ( lexer.token != Symbol.RIGHTPAR ) signalError.show(") expected");
		lexer.nextToken();
		if ( lexer.token != Symbol.SEMICOLON )
			signalError.show(SignalError.semicolon_expected);
		lexer.nextToken();
		return new WriteStatement(eL, true);
	}

	private BreakStatement breakStatement() {
		lexer.nextToken();
		if ( lexer.token != Symbol.SEMICOLON )
			signalError.show(SignalError.semicolon_expected);
		lexer.nextToken();
		return new BreakStatement();
	}

	private Statement nullStatement() {
		lexer.nextToken();
		return null;
	}

	private ExprList exprList() {
		// ExpressionList ::= Expression { "," Expression }

		ExprList anExprList = new ExprList();
		anExprList.addElement(expr());
		while (lexer.token == Symbol.COMMA) {
			lexer.nextToken();
			anExprList.addElement(expr());
		}
		return anExprList;
	}

	private Expr expr() {

		Expr left = simpleExpr();
		Symbol op = lexer.token;
		if ( op == Symbol.EQ || op == Symbol.NEQ || op == Symbol.LE
				|| op == Symbol.LT || op == Symbol.GE || op == Symbol.GT ) {
			lexer.nextToken();
			Expr right = simpleExpr();
			left = new CompositeExpr(left, op, right);
		}
		return left;
	}

	private Expr simpleExpr() {
		Symbol op;

		Expr left = term();
		while ((op = lexer.token) == Symbol.MINUS || op == Symbol.PLUS
				|| op == Symbol.OR) {
			lexer.nextToken();
			Expr right = term();
			left = new CompositeExpr(left, op, right);
		}
		return left;
	}

	private Expr term() {
		Symbol op;

		Expr left = signalFactor();
		while ((op = lexer.token) == Symbol.DIV || op == Symbol.MULT
				|| op == Symbol.AND) {
			lexer.nextToken();
			Expr right = signalFactor();
			left = new CompositeExpr(left, op, right);
		}
		return left;
	}

	private Expr signalFactor() {
		Symbol op;
		if ( (op = lexer.token) == Symbol.PLUS || op == Symbol.MINUS ) {
			lexer.nextToken();
			return new SignalExpr(op, factor());
		}
		else
			return factor();
	}

	/*
	 * Factor ::= BasicValue | "(" Expression ")" | "!" Factor | "null" |
	 *      ObjectCreation | PrimaryExpr
	 * 
	 * BasicValue ::= IntValue | BooleanValue | StringValue 
	 * BooleanValue ::=  "true" | "false" 
	 * ObjectCreation ::= "new" Id "(" ")" 
	 * PrimaryExpr ::= "super" "." Id "(" [ ExpressionList ] ")"  | 
	 *                 Id  |
	 *                 Id "." Id | 
	 *                 Id "." Id "(" [ ExpressionList ] ")" |
	 *                 Id "." Id "." Id "(" [ ExpressionList ] ")" |
	 *                 "this" | 
	 *                 "this" "." Id | 
	 *                 "this" "." Id "(" [ ExpressionList ] ")"  | 
	 *                 "this" "." Id "." Id "(" [ ExpressionList ] ")"
	 */
	private Expr factor() {

		Expr e;
		ExprList exprList;
		String messageName, ident;

		switch (lexer.token) {
		// IntValue
		case LITERALINT:
			return literalInt();
			// BooleanValue
		case FALSE:
			lexer.nextToken();
			return LiteralBoolean.False;
			// BooleanValue
		case TRUE:
			lexer.nextToken();
			return LiteralBoolean.True;
			// StringValue
		case LITERALSTRING:
			String literalString = lexer.getLiteralStringValue();
			lexer.nextToken();
			return new LiteralString(literalString);
			// "(" Expression ")" |
		case LEFTPAR:
			lexer.nextToken();
			e = expr();
			if ( lexer.token != Symbol.RIGHTPAR ) signalError.show(") expected");
			lexer.nextToken();
			return new ParenthesisExpr(e);

			// "null"
		case NULL:
			lexer.nextToken();
			return new NullExpr();
			// "!" Factor
		case NOT:
			lexer.nextToken();
			e = expr();
			return new UnaryExpr(e, Symbol.NOT);
			// ObjectCreation ::= "new" Id "(" ")"
		case NEW:
			lexer.nextToken();
			if ( lexer.token != Symbol.IDENT )
				signalError.show("Identifier expected");

			String className = lexer.getStringValue();
			/*
			 * // encontre a classe className in symbol table KraClass 
			 *      aClass = symbolTable.getInGlobal(className); 
			 *      if ( aClass == null ) ...
			 */

			lexer.nextToken();
			if ( lexer.token != Symbol.LEFTPAR ) signalError.show("( expected");
			lexer.nextToken();
			if ( lexer.token != Symbol.RIGHTPAR ) signalError.show(") expected");
			lexer.nextToken();
			/*
			 * return an object representing the creation of an object
			 */
			return null;
			/*
          	 * PrimaryExpr ::= "super" "." Id "(" [ ExpressionList ] ")"  | 
          	 *                 Id  |
          	 *                 Id "." Id | 
          	 *                 Id "." Id "(" [ ExpressionList ] ")" |
          	 *                 Id "." Id "." Id "(" [ ExpressionList ] ")" |
          	 *                 "this" | 
          	 *                 "this" "." Id | 
          	 *                 "this" "." Id "(" [ ExpressionList ] ")"  | 
          	 *                 "this" "." Id "." Id "(" [ ExpressionList ] ")"
			 */
		case SUPER:
			// "super" "." Id "(" [ ExpressionList ] ")"
			lexer.nextToken();
			if ( lexer.token != Symbol.DOT ) {
				signalError.show("'.' expected");
			}
			else
				lexer.nextToken();
			if ( lexer.token != Symbol.IDENT )
				signalError.show("Identifier expected");
			messageName = lexer.getStringValue();
			/*
			 * para fazer as confer�ncias sem�nticas, procure por 'messageName'
			 * na superclasse/superclasse da superclasse etc
			 */
			lexer.nextToken();
			exprList = realParameters();
			break;
		case IDENT:
			/*
          	 * PrimaryExpr ::=  
          	 *                 Id  |
          	 *                 Id "." Id | 
          	 *                 Id "." Id "(" [ ExpressionList ] ")" |
          	 *                 Id "." Id "." Id "(" [ ExpressionList ] ")" |
			 */

			String firstId = lexer.getStringValue();
			lexer.nextToken();
			if ( lexer.token != Symbol.DOT ) {
				// Id
				// retorne um objeto da ASA que representa um identificador
				return null;
			}
			else { // Id "."
				lexer.nextToken(); // coma o "."
				if ( lexer.token != Symbol.IDENT ) {
					signalError.show("Identifier expected");
				}
				else {
					// Id "." Id
					lexer.nextToken();
					ident = lexer.getStringValue();
					if ( lexer.token == Symbol.DOT ) {
						// Id "." Id "." Id "(" [ ExpressionList ] ")"
						/*
						 * se o compilador permite vari�veis est�ticas, � poss�vel
						 * ter esta op��o, como
						 *     Clock.currentDay.setDay(12);
						 * Contudo, se vari�veis est�ticas n�o estiver nas especifica��es,
						 * sinalize um erro neste ponto.
						 */
						lexer.nextToken();
						if ( lexer.token != Symbol.IDENT )
							signalError.show("Identifier expected");
						messageName = lexer.getStringValue();
						lexer.nextToken();
						exprList = this.realParameters();

					}
					else if ( lexer.token == Symbol.LEFTPAR ) {
						// Id "." Id "(" [ ExpressionList ] ")"
						exprList = this.realParameters();
						/*
						 * para fazer as confer�ncias sem�nticas, procure por
						 * m�todo 'ident' na classe de 'firstId'
						 */
					}
					else {
						// retorne o objeto da ASA que representa Id "." Id
					}
				}
			}
			break;
		case THIS:
			/*
			 * Este 'case THIS:' trata os seguintes casos: 
          	 * PrimaryExpr ::= 
          	 *                 "this" | 
          	 *                 "this" "." Id | 
          	 *                 "this" "." Id "(" [ ExpressionList ] ")"  | 
          	 *                 "this" "." Id "." Id "(" [ ExpressionList ] ")"
			 */
			lexer.nextToken();
			if ( lexer.token != Symbol.DOT ) {
				// only 'this'
				// retorne um objeto da ASA que representa 'this'
				// confira se n�o estamos em um m�todo est�tico
				return null;
			}
			else {
				lexer.nextToken();
				if ( lexer.token != Symbol.IDENT )
					signalError.show("Identifier expected");
				ident = lexer.getStringValue();
				lexer.nextToken();
				// j� analisou "this" "." Id
				if ( lexer.token == Symbol.LEFTPAR ) {
					// "this" "." Id "(" [ ExpressionList ] ")"
					/*
					 * Confira se a classe corrente possui um m�todo cujo nome �
					 * 'ident' e que pode tomar os par�metros de ExpressionList
					 */
					exprList = this.realParameters();
				}
				else if ( lexer.token == Symbol.DOT ) {
					// "this" "." Id "." Id "(" [ ExpressionList ] ")"
					lexer.nextToken();
					if ( lexer.token != Symbol.IDENT )
						signalError.show("Identifier expected");
					lexer.nextToken();
					exprList = this.realParameters();
				}
				else {
					// retorne o objeto da ASA que representa "this" "." Id
					/*
					 * confira se a classe corrente realmente possui uma
					 * vari�vel de inst�ncia 'ident'
					 */
					return null;
				}
			}
			break;
		default:
			signalError.show("Expression expected");
		}
		return null;
	}

	private LiteralInt literalInt() {

		LiteralInt e = null;

		// the number value is stored in lexer.getToken().value as an object of
		// Integer.
		// Method intValue returns that value as an value of type int.
		int value = lexer.getNumberValue();
		lexer.nextToken();
		return new LiteralInt(value);
	}

	private static boolean startExpr(Symbol token) {

		return token == Symbol.FALSE || token == Symbol.TRUE
				|| token == Symbol.NOT || token == Symbol.THIS
				|| token == Symbol.LITERALINT || token == Symbol.SUPER
				|| token == Symbol.LEFTPAR || token == Symbol.NULL
				|| token == Symbol.IDENT || token == Symbol.LITERALSTRING;

	}

	private SymbolTable		symbolTable;
	private Lexer			lexer;
	private SignalError	signalError;
	private InstanceVariableList currentInstanceVariableList;
	private Type currentReturnType;
}
