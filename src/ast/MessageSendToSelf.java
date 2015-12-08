/*
 * @author Thiago Martins de Jesus 380385
 * @author Vinnícius Ferreira da Silva 380032
 * */
package ast;

import java.util.Iterator;

public class MessageSendToSelf extends MessageSend {
    
	public MessageSendToSelf(KraClass thisClass){
		this.thisClass = thisClass;
		instance = null;
		eList = null;
		methodCall = null;
	}
	
	public MessageSendToSelf(KraClass thisClass, Variable instance){
		this.thisClass = thisClass;
		this.instance = instance;
		eList = null;
		methodCall = null;
	}
	
	public MessageSendToSelf(KraClass thisClass, Method methodCall, ExprList eList){
		this.thisClass = thisClass;
		this.methodCall = methodCall;
		this.eList = eList;
		instance = null;
	}
	
	public MessageSendToSelf(KraClass thisClass, Variable instance, Method methodCall, ExprList eList){
		this.thisClass = thisClass;
		this.methodCall = methodCall;
		this.eList = eList;
		this.instance = instance;
	}
	
    public Type getType() { 
    	//é só o "this"
        if(instance == null && eList == null && methodCall == null)
        	return thisClass;
        //this.id
        if(methodCall == null && eList == null)
        	return instance.getType();
        //this.id()
        if(instance == null)
        	return methodCall.getType();
        return methodCall.getType();
    }
    
    public void genC( PW pw, boolean putParenthesis ) {
    	if(putParenthesis)
        	pw.print("(");
        
    	//printando this e soh isso
        if(instance == null && eList == null & methodCall == null){
        	pw.print("this");
        //se nao ha chama para metodo nem argumentos, estamos tentando acessar uma instancia da classe
        }else if(methodCall == null && eList == null){
        	pw.print("this->_"+thisClass.getName()+"_"+instance.getName());
        //caso contrario eh uma chamada do tipo this.m()
        }else if(instance == null){
        	//se eh privado, ligacao estatica eh feita
        	if(methodCall.isPrivate()){
        		pw.print(methodCall.getCname()+"(( "+ methodCall.getThisClass().getCname() +" *) this");
        		if(eList != null){
        			pw.print(", ");
            		eList.genC(pw);
        		}
        		pw.print(")");
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
            	if(eList != null){
            		Iterator<Expr> exprIt = eList.elements();
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
            	pw.print("this->vt["+virtualTableIndex+"])(("+methodClass.getCname()+"*) this");
            	if(eList != null){
            		pw.print(", ");
            		eList.genC(pw);
            	}
            	pw.print(")");
        	}
//        	pw.print("this."+methodCall.getId()+"(");
//        	if(eList != null)
//        		eList.genC(pw);
//        	pw.print(")");
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
        	if(eList != null){
        		Iterator<Expr> exprIt = eList.elements();
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
        	pw.print(") ");
        	pw.print("this->_"+ instance.getType().getName() +"_"+instance.getName()+"->vt["+virtualTableIndex+"])(this->_"+ instance.getType().getName() +"_"+instance.getName());
        	if(eList != null){
        		pw.print(", ");
        		eList.genC(pw);
        	}
        	pw.print(") )");
        	
        }
        
        if(putParenthesis)
        	pw.print(")");
    }

	@Override
	public void genKra(PW pw, boolean putParenthesis) {
		//é só o "this"
        if(instance == null && eList == null && methodCall == null)
        	pw.printlnIdent("this;");
        else{
        	//this.id
            if(methodCall == null && eList == null){
            	pw.printIdent("this.");
            	instance.genKra(pw, putParenthesis);
            }else{
            	//this.id()
                if(instance == null){
                	pw.printIdent("this.");
        			methodCall.genKra(pw, putParenthesis);
                }
            }
        }
	}
	
	
    private KraClass thisClass;
    private Variable instance; 
    private ExprList eList;
    private Method methodCall;
    
    
}