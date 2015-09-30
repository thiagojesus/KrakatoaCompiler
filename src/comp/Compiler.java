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
			while ( lexer.token == Symbol.CLASS || lexer.token == Symbol.FINAL || lexer.token == Symbol.STATIC)
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
		//verifica se é final ou static
		if(lexer.token == Symbol.FINAL || lexer.token == Symbol.STATIC){
			if(lexer.token == Symbol.FINAL){
				isFinal = true;
				lexer.nextToken();
			}
			if(lexer.token == Symbol.STATIC){
				isStatic = true;
				lexer.nextToken();
			}
		}

		if ( lexer.token != Symbol.CLASS ) signalError.show("'class' expected");
		lexer.nextToken();
		if ( lexer.token != Symbol.IDENT )
			signalError.show(SignalError.ident_expected);
		String className = lexer.getStringValue();
		//colocar na global
		symbolTable.putInGlobal(className, new KraClass(className,isFinal,isStatic));
		//fazendo assim eu consigo alterar a kraClass dentro do hashmap, mais fácil
		KraClass cClass = symbolTable.getInGlobal(className);
		currentClass = cClass;
		lexer.nextToken();
		if ( lexer.token == Symbol.EXTENDS ) {
			//verificar se existe, se não é final e mandar pra kraclass
			lexer.nextToken();
			if ( lexer.token != Symbol.IDENT )
				signalError.show(SignalError.ident_expected);
			//pega o nome da superclasse
			String superclassName = lexer.getStringValue();
			//procuro o nome dela 
			superClass = symbolTable.getInGlobal(superclassName); 
			if(superClass == null){
				signalError.show("The inherithed class does not exist");
			}else{
				if(superClass.isFinal() == true){
					signalError.show("final classes can not be inherithed");
				}
				if(superclassName.compareTo(className)== 0){
					signalError.show("You can't inherit from yourself");
				}
			}
			cClass.setSuperclass(superClass);
			lexer.nextToken();
		}
		if ( lexer.token != Symbol.LEFTCURBRACKET )
			signalError.show("{ expected", true);
		lexer.nextToken();

		while (lexer.token == Symbol.STATIC || lexer.token == Symbol.PRIVATE || lexer.token == Symbol.PUBLIC || lexer.token == Symbol.FINAL) {
			Symbol qualifier = null;
			boolean isStaticMethod = false;
			boolean isFinalMethod = false;

			if(lexer.token == Symbol.FINAL){
				isFinalMethod = true;
			}

			if(lexer.token == Symbol.STATIC){
				isStaticMethod = true;
			}

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
			default:
				signalError.show("private, or public expected");
				qualifier = Symbol.PUBLIC;
			}
			Type t = type();
			currentReturnType = t;
			if ( lexer.token != Symbol.IDENT )
				signalError.show("Identifier expected");
			String name = lexer.getStringValue();
			Method m = null;
			lexer.nextToken();
			//se tem ( é método
			if ( lexer.token == Symbol.LEFTPAR ){
				//aqui faço as verificações caso o metodo tenha o nome run na classe program
				//ele não pode ser static, tem que retornar void e não pode ser privado
				if(name.compareTo("run")==0 && currentClass.getCname().compareTo("Program") == 0){
					if(t.getName().compareTo("void") != 0){
						signalError.show("run method in Program class must return void");
					}
					if(isStaticMethod){
						signalError.show("run method in Program class can't be static");
					}
					if(qualifier == Symbol.PRIVATE){
						signalError.show("run method in Program class must be public");
					}
				}

				//o metodo não pode ter o mesmo nome de uma instância
				Variable inst = currentClass.getInstanceVariable(name);
				if(inst != null){
					signalError.show("Previously declared instance with this name");
				}
				//metodos não podem ter o mesmo nome
				if(currentClass.getMethod(name)!= null){
					signalError.show("Redeclaration of method "+name);
				}
				if(qualifier == Symbol.PRIVATE){
					//metodos finais não podem ser privados
					if(isFinalMethod){
						signalError.show("A final method can't be private");
					}
				}
				//crio o que eu já validei do método
				m = new Method(name, t, qualifier, isStaticMethod, isFinalMethod, currentClass);
				currentMethod = cClass.addMethod(m);
				//boolean para verificar se o metodo tem pelo menos um retorno
				hasReturn = false;
				//chamo o methodDec pra validar o resto
				methodDec(t, name);
			}

			else if ( qualifier != Symbol.PRIVATE )
				signalError.show("Attempt to declare a public instance variable");
			else
				varList = instanceVarDec(t, name, isStaticMethod);
			cClass.addInstances(varList, isStaticMethod);
		}
		if ( lexer.token != Symbol.RIGHTCURBRACKET )
			signalError.show("public/private or \"}\" expected");
		//se a classe atual é Program, procuro um método run
		if(currentClass.getName().compareTo("Program") == 0){
			if(currentClass.getMethod("run") == null){
				signalError.show("Class program must have a run method");
			}
		}
		lexer.nextToken();

		return cClass;

	}
	private InstanceVariableList instanceVarDec(Type type, String name, boolean isStatic) {
		// InstVarDec ::= [ "static" ] "private" Type IdList ";"
		String variableName;
		InstanceVariableList varList = new InstanceVariableList();
		if(currentClass.getInstanceVariable(name)==null){
			varList.addElement(new InstanceVariable(name, type,currentClass));
		}else{
			signalError.show("Duplicate instance variable");
		}
		while (lexer.token == Symbol.COMMA) {
			lexer.nextToken();
			if ( lexer.token != Symbol.IDENT )
				signalError.show("Identifier expected");
			variableName = lexer.getStringValue();
			if(currentClass.getInstanceVariable(variableName)==null){
				varList.addElement(new InstanceVariable(variableName, type,currentClass));
			}else{
				signalError.show("Duplicate instance variable");
			}
			lexer.nextToken();
		}
		if ( lexer.token != Symbol.SEMICOLON )
			signalError.show(SignalError.semicolon_expected);
		lexer.nextToken();
		return varList;
	}

	private void methodDec(Type type, String name) {
		/*
		 * MethodDec ::= Qualifier Return Id "("[ FormalParamDec ] ")" "{"
		 *                StatementList "}"
		 */
		StatementList stList;
		ParamList pL = new ParamList();
		lexer.nextToken();
		if ( lexer.token != Symbol.RIGHTPAR ) pL = formalParamDec();
		if ( lexer.token != Symbol.RIGHTPAR ) signalError.show(") expected");
		//metodo run da classe Program não pode ter parametros
		if(currentMethod.getId().compareTo("run")==0 && currentClass.getName().compareTo("Program")==0){
			if(pL != null){
				signalError.show("run method in Program class must not have parameters");
			}
		}

		//procuro se existe um metodo com o mesmo nome na superclasse
		Method superM = null;
		if(currentClass.hasSuper()){
			superM = findInSuperClass();
		}
		//agora podem acontecer várias coisas
		//se ele não for nulo e final, não pode
		//se o retorno do pai for diferente, também não pode
		//se a quantidade de parametros e/ou seus tipos não baterem, também não pode
		if(superM != null){
			if(superM.isFinal()){
				signalError.show("Can't redefine a final method in super class");
			}
			if(superM.getType().getName().compareTo(type.getName()) != 0){
				signalError.show("trying to redefine a superclass method with different return type");
			}
			boolean paramCheck = compareParamLists(pL, superM.getParamList());
			if(!paramCheck){
				signalError.show("The parameter count or some type does not match the method in superclass");
			}
		}

		lexer.nextToken();
		if ( lexer.token != Symbol.LEFTCURBRACKET ) signalError.show("{ expected");

		lexer.nextToken();
		stList = statementList();
		//se o metodo atual for void e tiver um return, lança erro
		//se o metodo atual não for void e não tiver um return, lança erro tbm
		if(currentMethod.getType().getName().compareTo("void") == 0){
			if(hasReturn){
				signalError.show("can't have a return statement in a void method");
			}
		}else{
			if(!hasReturn){
				signalError.show("The method "+ currentMethod.getId() + " must have a return statement");
			}
		}
		if ( lexer.token != Symbol.RIGHTCURBRACKET ) signalError.show("} expected");

		lexer.nextToken();
		//limpo a tabela local
		symbolTable.removeLocalIdent();
		if(stList != null){
			currentMethod.setStatementList(stList);
		}
		if(pL != null){
			currentMethod.setVariables(pL);
		}

	}

	//compara lista de parametros para a chamada de métodos
	private boolean compareParamLists(ParamList p1, ParamList p2){
		Iterator<Variable> it1, it2;
		Variable v1, v2;
		if(p1 == null && p2 == null){
			return true;
		}else if(p1 == null || p2 == null)
			return false;
		if(p1.getSize() != p2.getSize())
			return false;
		it1 = p1.elements();
		it2 = p2.elements();
		while(it1.hasNext()){
			v1 = it1.next();
			v2 = it2.next();
			if(v1.getType().getName().compareTo(v2.getType().getName()) != 0)
				return false;

		}
		return true;
	}

	private Method findInSuperClass(){
		KraClass superClass = currentClass.getSuper();
		while(superClass != null){
			Method m = superClass.getMethod(currentMethod.getId());
			if(m != null){
				return m;
			}
			superClass = superClass.getSuper();
		}
		return null;
	}
	//atribuicao e declaração são muito parecidos, por isso retorno um assignexprlocaldec
	private Expr localDec() {
		// LocalDec ::= Type IdList ";"
		ArrayList<Variable>v = new ArrayList<Variable>();
		Type type = type();
		if ( lexer.token != Symbol.IDENT ) signalError.show("Identifier expected");
		String name = lexer.getStringValue();
		Variable var = new Variable(name, type);
		v.add(var);
		//procuro na tabela local se tem variaveis com o mesmo nome, se tem lanço erro
		if(symbolTable.getInLocal(name)==null){
			symbolTable.putInLocal(name, var);
		}else{
			signalError.show("Variable already declared");
		}
		lexer.nextToken();
		while (lexer.token == Symbol.COMMA) {
			lexer.nextToken();
			if ( lexer.token != Symbol.IDENT )
				signalError.show("Identifier expected");
			name = lexer.getStringValue();
			var = new Variable(name, type);
			v.add(var);
			//procuro na tabela local se tem variaveis com o mesmo nome, se tem lanço erro
			if(symbolTable.getInLocal(name)==null){
				symbolTable.putInLocal(name, var);
			}else{
				signalError.show("Variable already declared");
			}
			lexer.nextToken();
		}
		if(lexer.token != Symbol.SEMICOLON){
			if(lexer.token == Symbol.DOT){
				signalError.show(". is invalid in this context");
			}
			signalError.show(signalError.semicolon_expected);
		}
		lexer.nextToken();
		VariableList vL = new VariableList(v);
		return new AssignExprLocalDec(vL);
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
		Type t;
		String name;
		Variable v;
		Parameter nP = null;

		t = type();
		if(lexer.token != Symbol.IDENT){
			signalError.show(signalError.ident_expected);
		}
		name = lexer.getStringValue();
		//verifico se tem alguem com o mesmo nome
		if((v = symbolTable.getInLocal(name))==null){
			nP = new Parameter(name, t);
			symbolTable.putInLocal(name, nP);
		}else{
			signalError.show("the parameter "+name+" already exists");
		}
		lexer.nextToken();
		return nP;
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
			if(!isType(lexer.getStringValue())) signalError.show(lexer.getStringValue()+ " is not a type");
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
		// CompStatement ::= "{" { Statement } "}"
		Symbol tk;
		StatementList sList = new StatementList();
		// statements always begin with an identifier, if, read, write, ...
		while ((tk = lexer.token) != Symbol.RIGHTCURBRACKET
				&& tk != Symbol.ELSE)
			sList.addElement(statement());
		return sList;
	}

	private Statement statement() {
		Statement s = null;
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
			s = new AssignStatement(assignExprLocalDec());
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
		Expr vL;
		if ( lexer.token == Symbol.INT || lexer.token == Symbol.BOOLEAN
				|| lexer.token == Symbol.STRING ||
				// token � uma classe declarada textualmente antes desta
				// instru��o
				(lexer.token == Symbol.IDENT && isType(lexer.getStringValue())) && (lexer.lookAheadForDot() != Symbol.DOT && lexer.lookAheadForAttribution() != Symbol.ASSIGN) ) {
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
			Expr left = expr();
			Expr right = null;
			if ( lexer.token == Symbol.ASSIGN ) {
				lexer.nextToken();
				right = expr();
				//se o tipo for uma classe, temos que checar a compatibilidade entre as expressões
				if(isType(left.getType().getName())){
					//a segunda não pode ser null
					if(right.getType().getName().compareTo("null") != 0){
						//checo os tipos
						boolean check = checkClassType(left.getType(), right.getType());
						//se voltar false, não são compatíveis
						if(!check){
							signalError.show("The type of the right expression can't be converted to the first expression's type");
						}

					}

				}else{
					//se os tipos são simples, só comparo mesmo
					if(left.getType().getName().compareTo(right.getType().getName()) != 0){
						signalError.show("Types do not match");
					}
				}
			}
			//tratando o caso dos tipos nulos
			if(left != null && right == null){
				//esquerda não pode ser void, se for inteiro ele ta querendo usar expr como instrução, também não pode
				if(left.getType().getName().compareTo("void") != 0){
					if(left.getType().getName().compareTo("int") == 0){
						signalError.show("cannot use expression as instruction");
					}
				}
			}
			if ( lexer.token != Symbol.SEMICOLON )
				signalError.show("';' expected", true);
			else
				lexer.nextToken();
			if(right != null){
				return new AssignExprLocalDec(left, right);
			}else{
				return new AssignExprLocalDec(left);
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
		//entrei num while, incremento
		nested++;
		lexer.nextToken();
		if ( lexer.token != Symbol.LEFTPAR ) signalError.show("( expected");
		lexer.nextToken();
		condition = expr();
		//condition tem que ser do tipo boolean
		if(condition.getType().getName().compareTo("boolean")!=0){
			signalError.show("expression inside while condition must be of boolean type");
		}
		if ( lexer.token != Symbol.RIGHTPAR ) signalError.show(") expected");
		lexer.nextToken();
		repeat = statement();
		//saindo do while
		nested--;
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
		//se o tipo de retorno é classe, verifica a conversibilidade 
		if(isType(expr.getType().getName())){
			//se a classe não é do tipo do retorno da função, sinaliza erro
			if(!checkClassType(currentMethod.getType(), expr.getType())){
				signalError.show("the return expression is not the same type of the method");
			}
		}else{
			//faço a mesma coisa com os tipos básicos
			if(currentMethod.getType().getName().compareTo(expr.getType().getName())!=0){
				signalError.show("return type mismatch from method's return type");
			}
		}
		if ( lexer.token != Symbol.SEMICOLON )
			signalError.show(SignalError.semicolon_expected);
		lexer.nextToken();
		hasReturn = true;
		return new ReturnStatement(expr);
	}
	//falta verificar nas locais
	private ReadStatement readStatement() {
		ArrayList<Variable> vR = new ArrayList<Variable>();
		lexer.nextToken();
		if ( lexer.token != Symbol.LEFTPAR ) signalError.show("( expected");
		lexer.nextToken();
		//não podemos ter this em metodos estaticos
		boolean self = false, isStatic = false;
		//chamada de classe estatica é diferente, precisamos controlar isso
		String staticName = null;
		Variable v = null;
		while (true) {
			if ( lexer.token == Symbol.THIS ) {
				lexer.nextToken();
				self = true;
				if ( lexer.token != Symbol.DOT ) signalError.show(". expected");
				lexer.nextToken();
			}
			if ( lexer.token != Symbol.IDENT )
				signalError.show(SignalError.ident_expected);

			String name = lexer.getStringValue();
			//se é this, recebo a instancia
			if(self){
				v = currentClass.getInstanceVariable(name);
			}else{
				if(isType(name)){
					//chamada estatica
					staticName = name;
					lexer.nextToken();
					isStatic = true;
					if(lexer.token != Symbol.DOT) signalError.show(". expected");
					lexer.nextToken();
					String sName = lexer.getStringValue();
					v = currentClass.getInstanceVariable(sName);
					if(v==null || !v.isStatic())
						signalError.show("Could not find static variable "+ sName);
				}else{
					if(symbolTable.getInLocal(name)==null){
						v = currentClass.getInstanceVariable(name);
						//não achou
						if(v == null){
							signalError.show("Can't find reference");
						}
					}else{
						v = symbolTable.getInLocal(name);
					}
				}
			}
			lexer.nextToken();
			vR.add(v);
			if ( lexer.token == Symbol.COMMA )
				lexer.nextToken();
			else
				break;
		}

		if ( lexer.token != Symbol.RIGHTPAR ) signalError.show(") expected");
		lexer.nextToken();
		//v não pode ser boolean nem objeto
		if(v.getType().getName().compareTo("boolean") == 0){
			signalError.show("can't read boolean");
		}

		if(isType(v.getType().getName())){
			signalError.show("can't read objects");
		}
		if ( lexer.token != Symbol.SEMICOLON )
			signalError.show(SignalError.semicolon_expected);
		lexer.nextToken();
		return new ReadStatement(vR);
	}

	private WriteStatement writeStatement() {
		ExprList eS = new ExprList();
		lexer.nextToken();
		if ( lexer.token != Symbol.LEFTPAR ) signalError.show("( expected");
		lexer.nextToken();
		eS = exprList();
		if ( lexer.token != Symbol.RIGHTPAR ) signalError.show(") expected");
		lexer.nextToken();
		Iterator<Expr> eA = eS.elements();
		while(eA.hasNext()){
			Expr aux = eA.next();
			//não posso escrever booleanos e objetos
			if(aux != null){
				if(aux.getType().getName().compareTo("boolean") == 0)
					signalError.show("Can't write boolean.");
				if(isType(aux.getType().getName()))
					signalError.show("Can't write a object");
			}
		}
		if ( lexer.token != Symbol.SEMICOLON )
			signalError.show(SignalError.semicolon_expected);
		lexer.nextToken();
		return new WriteStatement(eS, false);
	}

	private WriteStatement writelnStatement() {
		ExprList eS = new ExprList();
		lexer.nextToken();
		if ( lexer.token != Symbol.LEFTPAR ) signalError.show("( expected");
		lexer.nextToken();
		eS = exprList();
		if ( lexer.token != Symbol.RIGHTPAR ) signalError.show(") expected");
		lexer.nextToken();
		Iterator<Expr> eA = eS.elements();
		while(eA.hasNext()){
			Expr aux = eA.next();
			//não posso escrever booleanos e objetos
			if(aux != null){
				if(aux.getType().getName().compareTo("boolean") == 0)
					signalError.show("Can't write boolean.");
				if(isType(aux.getType().getName()))
					signalError.show("Can't write a object");
			}
		}
		if ( lexer.token != Symbol.SEMICOLON )
			signalError.show(SignalError.semicolon_expected);
		lexer.nextToken();
		return new WriteStatement(eS, true);
	}

	private BreakStatement breakStatement() {
		lexer.nextToken();
		if ( lexer.token != Symbol.SEMICOLON )
			signalError.show(SignalError.semicolon_expected);
		//não posso chamar break fora de um while
		if(nested <= 0){
			signalError.show("break outside while");
		}
		lexer.nextToken();
		return new BreakStatement();
	}

	private NullStatement nullStatement() {
		lexer.nextToken();
		return new NullStatement();
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
			if(op == Symbol.EQ || op == Symbol.NEQ){
				//checagem de tipo feita assim como nos outros método que contém verificação de conversibilidade e tipos simples.
				if(isType(left.getType().getName()) && isType(right.getType().getName())){
					boolean check = checkClassType(left.getType(), right.getType());
					if(!check){
						check = checkClassType(right.getType(), left.getType());

						if(!check)
							signalError.show("Trying to compare different types");
					}

				}else{
					if(left.getType().getName().compareTo("null") != 0 && right.getType().getName().compareTo("null") != 0){
						if(left.getType().getName().compareTo(right.getType().getName()) != 0)
							signalError.show("Error trying to compare different types.");
					}
				}
				/*if(left.getType().getName().compareTo(right.getType().getName()) != 0){

					error.show("Comparison between different types.");
				}*/
			}
			left = new CompositeExpr(left, op, right);
		}
		return left;
	}

	private boolean checkClassType(Type t, Type t2){
		if(t.getName().compareTo(t2.getName()) == 0)
			return true;
		else{

			KraClass klass = symbolTable.getInGlobal(t2.getName());
			if(klass != null){
				klass = klass.getSuperClass();
				while(klass != null){
					if(klass.getName().compareTo(t.getName()) == 0)
						return true;
					klass = klass.getSuperClass();
				}
			}
		}
		return false;

	}

	private Expr simpleExpr() {
		Symbol op;

		Expr left = term();
		while ((op = lexer.token) == Symbol.MINUS || op == Symbol.PLUS
				|| op == Symbol.OR) {
			lexer.nextToken();
			Expr right = term();
			//não posso aplicar operações em objetos
			if(isType(left.getType().getName()) || isType(right.getType().getName()))
				signalError.show("invalid operation for this object");
			//booleanos não adicionam nem subtraem
			if(op == Symbol.PLUS || op == Symbol.MINUS){
				if(left.getType().getName().compareTo("boolean")==0 || right.getType().getName().compareTo("boolean")==0)
					signalError.show("You can't add or subtract a boolean type");
			}
			//ints não tem a operação OU definida
			if(op == Symbol.OR){
				if(left.getType().getName().compareTo("int")==0 || right.getType().getName().compareTo("int")==0)
					signalError.show("you can't apply logic operators in int types");
			}
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
			Expr expr = factor();
			//objetos não são compativeis com expressões unárias
			if(isType(expr.getType().getName()))
				signalError.show("objects are not compatible with unary operations");
			//booleanos também não
			if(expr.getType().getName().compareTo("boolean")==0)
				signalError.show("booleans can't have unary operations");
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
			//não posso usar NOT com objetos
			if(isType(e.getType().getName()))
				signalError.show("can't use not in an object");
			if(e.getType().getName().compareTo("int")==0){
				signalError.show("NOT is invalid for int type");
			}
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
			NewExpr nE = null;
			//verifico se a classe existe, senão não tem como dar new
			if(isType(className)){
				KraClass nC = symbolTable.getInGlobal(className);
				nE = new NewExpr(nC);
			}else{
				signalError.show("The class "+className+" does not exist");
			}
			lexer.nextToken();
			if ( lexer.token != Symbol.LEFTPAR ) signalError.show("( expected");
			lexer.nextToken();
			if ( lexer.token != Symbol.RIGHTPAR ) signalError.show(") expected");
			lexer.nextToken();
			/*
			 * return an object representing the creation of an object
			 */
			return nE;
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
			KraClass sC = currentClass.getSuper();
			Method m = null;
			while(sC != null){
				m = sC.getMethod(messageName);
				if(m == null){
					sC = sC.getSuper();
				}else
					break;
			}

			lexer.nextToken();
			exprList = realParameters();
			//se m é null, não achei o metodo
			if(m==null)
				signalError.show("Could not find method "+messageName);
			else{
				if(exprList != null){
					//se o tamanho da lista de parametros da mensagem não bate com o método, sinaliza erro
					if(m.getParamListSize() != exprList.getSize())
						signalError.show("parameter number mismatch");
					Iterator<Variable> iV = m.getParamElements();
					Iterator<Expr> iE = exprList.elements();
					//se bate, verifico se os tipos batem
					while(iV.hasNext() && iE.hasNext()){
						Variable v = iV.next();
						Expr ex = iE.next();
						if(v.getType().getName().compareTo("int") == 0 || v.getType().getName().compareTo("String") == 0 || v.getType().getName().compareTo("boolean") == 0){
							if(v.getType().getName().compareTo(ex.getType().getName()) != 0)
								signalError.show("parameter type mismatch in method "+m.getId());
						}else{
							if(v.getType().getName().compareTo("undefined") == 0 || ex.getType().getName().compareTo("undefined") == 0)
								signalError.show("Undefined type can't apply to expressions or variables");

							if(!checkClassType(v.getType(), ex.getType()))
								signalError.show("Parameter class mismatch");	
						}
					}
				}
			}
			return new MessageSendToSuper(m,exprList,sC);
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
				Variable v = null;
				v = symbolTable.getInLocal(firstId);
				if(v==null){
					v = currentClass.getInstanceVariable(firstId);
					if(v!=null)
						signalError.show("Instance variable must use the keyword this.");
				}
				//não achei nada, deu ruim
				if(v == null)
					signalError.show("Can't find variable");
				return new VariableExpr(v);
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
						KraClass kC = symbolTable.getInGlobal(ident);
						//se não encontrou nada, não pode existir chamada estática
						if(kC == null)
							signalError.show("The class "+ ident + " does not exist");
						Variable var = kC.getInstanceVariable(ident);
						//se não achou a variavel estatica, lança erro
						if(var == null || !var.isStatic())
							signalError.show("can't find static instance");
						lexer.nextToken();
						if ( lexer.token != Symbol.IDENT )
							signalError.show("Identifier expected");
						messageName = lexer.getStringValue();
						Method msg = kC.getMethod(messageName);
						if(msg == null || !msg.isStatic())
							signalError.show("can't find static method");
						lexer.nextToken();
						exprList = this.realParameters();
						return new MessageSendToVariable(kC, var, msg, exprList);

					}
					else if ( lexer.token == Symbol.LEFTPAR ) {
						Variable v = null;
						Variable v1 = null;
						Method mtd = null;
						KraClass KraC = null;
						boolean isStatic = false;
						v = symbolTable.getInLocal(firstId);
						v1 = currentClass.getInstanceVariable(firstId);
						if(v != null || (v1 != null && !v1.isStatic()) ){
							if(v==null)
								v = v1;
							//se o tipo não for uma classe, não posso chamar objeto
							if(!isType(v.getType().getName()))
								signalError.show("The first identifier must be a class type to call methods");

							KraClass c = symbolTable.getInGlobal(v.getType().getName());
							mtd = c.getMethod(ident);
							if(mtd == null || mtd.isStatic() ){
								c = c.getSuper();
								while(c != null){
									mtd = c.getMethod(ident);
									if(mtd == null || !mtd.isStatic()){
										c = c.getSuper();
									}else
										break;
								}
							}
							if(mtd == null)
								signalError.show("can't find method");
							else{
								if(mtd.isPrivate())
									signalError.show("Can't call private method from class "+ c.getName());
							}
						}else{
							isStatic = true;
							KraC = symbolTable.getInGlobal(firstId);
							mtd = KraC.getMethod(ident);
							if(mtd == null || !mtd.isStatic())
								signalError.show("the static method does not exist");
							else{
								if(mtd.isPrivate() && currentClass.getName().compareTo(firstId) != 0 && mtd.isStatic())
									signalError.show("trying to call static private method");
							}
						}
						// Id "." Id "(" [ ExpressionList ] ")"
						exprList = this.realParameters();
						/*
						 * para fazer as confer�ncias sem�nticas, procure por
						 * m�todo 'ident' na classe de 'firstId'
						 */
						if(isStatic)
							return new MessageSendToVariable(KraC,mtd,exprList );
						else
							return new MessageSendToVariable(v, mtd,exprList);
					}
					else {
						// retorne o objeto da ASA que representa Id "." Id
						KraClass kc = symbolTable.getInGlobal(firstId);
						if(kc == null)
							signalError.show("Identifier is not a class");
						Variable v1 = kc.getInstanceVariable(ident);
						if(v1==null || !v1.isStatic())
							signalError.show("Static instance does not belong in class "+ kc.getName());
						return new MessageSendToVariable(kc, v1);
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
				if(currentMethod.isStatic())
					signalError.show("Using this in a static method");
				return new MessageSendToSelf(currentClass);
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
					Method mth = currentClass.getMethod(ident);
					if(mth == null || !mth.isStatic()){
						KraClass find = currentClass.getSuper();
						while(find != null){
							mth = find.getMethod(ident);
							if(mth==null || !mth.isStatic())
								find = find.getSuper();
							else
								break;
						}

					}

					if(mth == null)
						signalError.show("Can't find method");

					if(currentMethod.isStatic())
						signalError.show("can't call instance in static method");

					exprList = this.realParameters();

					if(exprList != null){
						if(mth.getParamListSize() != exprList.getSize())
							signalError.show("Parameter count mismatch");
						Iterator<Variable> iV = mth.getParamElements();
						Iterator<Expr> iE = exprList.elements();
						while(iV.hasNext()){
							Variable v = iV.next();
							Expr ex = iE.next();
							if(isType(ex.getType().getName())){
								boolean check = checkClassType(v.getType(), ex.getType());
								if(!check)
									signalError.show("method has a different type");
							}else{
								if(ex.getType().getName().compareTo(v.getType().getName())!=0)
									signalError.show("method has a different type");
							}
						}
					}
					return new MessageSendToSelf(currentClass,mth,exprList);
				}
				else if ( lexer.token == Symbol.DOT ) {
					// "this" "." Id "." Id "(" [ ExpressionList ] ")"
					lexer.nextToken();
					if ( lexer.token != Symbol.IDENT )
						signalError.show("Identifier expected");
					String msg = lexer.getStringValue();
					Method mto = null;
					Variable v = currentClass.getInstanceVariable(ident);
					if(v != null && !v.isStatic()){
						if(isType(v.getType().getName())){
							KraClass Sc = symbolTable.getInGlobal(v.getType().getName());
							mto = Sc.getMethod(msg);
							if(mto==null || mto.isStatic()){
								Sc = Sc.getSuper();
								while(Sc != null){
									mto = Sc.getMethod(msg);
									if(mto == null || mto.isStatic() || mto.isPrivate())
										Sc = Sc.getSuper();
									else
										break;
								}
							}
						}else{
							signalError.show("this variable is not of an object that can call methods");
						}
					}else{
						signalError.show("No instance found");
					}
					
					if(mto == null)
						signalError.show("Can't find method");
					lexer.nextToken();
					exprList = this.realParameters();
					
					if(exprList != null){
						if(mto.getParamListSize() != exprList.getSize())
							signalError.show("parameter count mismatch");
						Iterator<Variable> iV = mto.getParamElements();
						Iterator<Expr> iE = exprList.elements();
						while(iV.hasNext()){
							Variable var = iV.next();
							Expr ex = iE.next();
							if(isType(ex.getType().getName())){
								boolean check = checkClassType(var.getType(), ex.getType());
								if(!check)
									signalError.show("method type mismatch");
							}else{
								if(ex.getType().getName().compareTo(var.getType().getName())!=0)
									signalError.show("method type mismatch");
							}
						}
					}
					return new MessageSendToSelf(currentClass,v,mto,exprList);
				}
				else {
					// retorne o objeto da ASA que representa "this" "." Id
					/*
					 * confira se a classe corrente realmente possui uma
					 * vari�vel de inst�ncia 'ident'
					 */
					Variable v = currentClass.getInstanceVariable(ident);
					if(v == null || v.isStatic())
						signalError.show("There is no instance "+ident+" in class "+currentClass.getName());
					if(currentMethod.isStatic())
						signalError.show("Can't call instance inside static method.");
					return new MessageSendToSelf(currentClass, v);
				}
			}
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
	private KraClass currentClass;
	private Method currentMethod;
	private Type currentReturnType;
	private int nested;
	private boolean hasReturn;
}
