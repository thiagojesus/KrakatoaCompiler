/*
 * @author Thiago Martins de Jesus 380385
 * @author Vinnícius Ferreira da Silva 380032
 * */
package ast;

import java.util.Iterator;

public class MessageSendToVariable extends MessageSend { 

	public MessageSendToVariable(KraClass k, Variable v){
		this.classIdent = k;
		this.variable = v;
		methodCall = null;
		exprList = null;
	}
	
	public MessageSendToVariable(KraClass k, Method methodCall, ExprList e){
		this.classIdent = k;
		this.methodCall = methodCall;
		this.variable = null;
		this.exprList = e;
	}
	
	public MessageSendToVariable(KraClass k, Variable v, Method methodCall, ExprList e){
		this.classIdent = k;
		this.variable = v;
		this.methodCall = methodCall;
		this.exprList = e;
	}
	
	public MessageSendToVariable(Variable v, Method methodCall, ExprList e){
		this.variable = v;
		this.methodCall = methodCall;
		this.exprList = e;
		classIdent = null;
	}
	
    public Type getType() { 
    	//é variavel? 
    	if(methodCall == null){
    		return variable.getType();
    		//é método
    	}else{
    		return methodCall.getType();
    	}
    }
    
    public void genC( PW pw, boolean putParenthesis ) {
    	if(putParenthesis)
        	pw.print("(");
        //caso em que ha chamada estatica de variavel
        if(methodCall == null && exprList == null){
        	pw.print("_static_"+classIdent.getName()+"_"+variable.getName());
        //caso em que a chamada eh do tipo a.call(a) com a sendo objeto
        }else if(classIdent == null){
        	
        	//primeiro adquirimos a classe que o metodo pertence com getThisClass()
        	KraClass methodClass = methodCall.getThisClass();
        	
        	//obtemos o indice em que se encontra o metodo em questao dentro da tabela virtual da classe do metodo
        	int virtualTableIndex = methodClass.getMethodIndex(methodCall.getId());
        	
        	//guardamos o tipo do metodo
        	Type t = methodCall.getType();
        	
        	
        	//verificamos se o tipo eh um dos tipos basicos de C ou se eh classe, apenas adiciona-se * pra classe e comecamos a construcao da mensagem
        	// tipo de chamada eh a primeira coisa a ser colocada no formato
        	if(t.getCname().compareTo("int") == 0 || t.getCname().compareTo("char *") == 0 || t.getCname().compareTo("void") == 0)
        		pw.print("( ("+methodCall.getType().getCname()+" (*)(");
        	else
        		pw.print("( ("+methodCall.getType().getCname()+"* (*)(");
        	
        	//o primeiro argumento da funcao sempre eh this, entao colocamos a definicao do primeiro argumento como a classe
        	pw.print(methodClass.getCname()+" *");
        	//se exprList nao eh null, entao percorremos colocando o resto dos tipos dos argumentos
        	if(exprList != null){
        		Iterator<Expr> exprIt = exprList.elements();
        		while(exprIt.hasNext()){
        			Type exprType = exprIt.next().getType();
        			pw.print(", ");
        			if(exprType.getCname().compareTo("int") == 0 || exprType.getCname().compareTo("char *") == 0 || exprType.getCname().compareTo("void") == 0)
        				pw.print(exprType.getCname());
        			else
        				pw.print(exprType.getCname()+"*");
        		}
        	}
        	//fechamos a declaracao dos tipos dos argumentos e passamos para a chamada em si
        	//que consistira de nome da variavel passando o numero do metodo a se pegar na tabela virtual e os argumentos
        	pw.print(")) ");
        	pw.print("_"+variable.getName()+"->vt["+virtualTableIndex+"])(_"+variable.getName());
        	if(exprList != null){
        		pw.print(", ");
        		exprList.genC(pw);
        	}
        	pw.print(")");
        //caso em que ha chamada estatica de metodo
        }else if(variable == null){
        	pw.print(methodCall.getCname()+"(");
        	if(exprList != null)
        		exprList.genC(pw);
        	pw.print(")");
        //caso em que ha chamada de variavel estatica que chama um metodo
        }else{
        	//primeiro adquirimos a classe que o metodo pertence com getThisClass()
        	KraClass methodClass = methodCall.getThisClass();
        	
        	//obtemos o indice em que se encontra o metodo em questao dentro da tabela virtual da classe do metodo
        	int virtualTableIndex = methodClass.getMethodIndex(methodCall.getId());
        	
        	//guardamos o tipo do metodo
        	Type t = methodCall.getType();
        	
        	
        	//verificamos se o tipo eh um dos tipos basicos de C ou se eh classe, apenas adiciona-se * pra classe e comecamos a construcao da mensagem
        	// tipo de chamada eh a primeira coisa a ser colocada no formato
        	if(t.getCname().compareTo("int") == 0 || t.getCname().compareTo("char *") == 0 || t.getCname().compareTo("void") == 0)
        		pw.print("( ("+methodCall.getType().getCname()+" (*)(");
        	else
        		pw.print("( ("+methodCall.getType().getCname()+"* (*)(");
        	
        	//o primeiro argumento da funcao sempre eh this, entao colocamos a definicao do primeiro argumento como a classe
        	pw.print(methodClass.getCname()+" *");
        	//se exprList nao eh null, entao percorremos colocando o resto dos tipos dos argumentos
        	if(exprList != null){
        		Iterator<Expr> exprIt = exprList.elements();
        		while(exprIt.hasNext()){
        			Type exprType = exprIt.next().getType();
        			pw.print(", ");
        			if(exprType.getCname().compareTo("int") == 0 || exprType.getCname().compareTo("char *") == 0 || exprType.getCname().compareTo("void") == 0)
        				pw.print(exprType.getCname());
        			else
        				pw.print(exprType.getCname()+"*");
        		}
        	}
        	//fechamos a declaracao dos tipos dos argumentos e passamos para a chamada em si
        	//que consistira de nome da variavel passando o numero do metodo a se pegar na tabela virtual e os argumentos
        	pw.print(")) ");
        	pw.print("_static_"+classIdent.getName()+"_"+variable.getName()+"->vt["+virtualTableIndex+"])(_static_"+ classIdent.getName() +"_"+variable.getName());
        	if(exprList != null){
        		pw.print(", ");
        		exprList.genC(pw);
        	}
        	pw.print(") )");
        }
        
        if(putParenthesis)
        	pw.print(")");
    }

	@Override
	public void genKra(PW pw, boolean putParenthesis) {
		// TODO Auto-generated method stub
		//não estatico
    	if(classIdent == null && methodCall != null){
    		pw.printIdent(variable.getName()+".");
    		pw.print(methodCall.getId()+"(");
    		if(exprList != null)
    			exprList.genKra(pw, putParenthesis);
    		pw.println(");");
    		//é estatico
    	}else{
    		if(methodCall != null){
    			pw.printIdent(classIdent.getName()+".");
        		pw.print(methodCall.getId()+"(");
        		if(exprList != null)
        			exprList.genKra(pw, putParenthesis);
        		pw.println(");");
    		}else{
    			pw.printIdent(classIdent.getName()+".");
    			pw.println(variable.getName()+";");
    		}
    	}
		
	}
	
	public Variable getVar(){
		return this.variable;
	}
	
    private KraClass classIdent;
    private Variable variable;
    private Method methodCall;
    private ExprList exprList;
}    