/*
 * @author Thiago Martins de Jesus 380385
 * @author Vinnícius Ferreira da Silva 380032
 * */
package ast;


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