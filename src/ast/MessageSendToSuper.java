package ast;

public class MessageSendToSuper extends MessageSend { 
	
	public MessageSendToSuper(Method _message, ExprList _exprList, KraClass _sC){
		this.message = _message;
		this.parameters = _exprList;
		this.superClass = _sC;
	}
	
    public Type getType() { 
        return null;
    }

    public void genC( PW pw, boolean putParenthesis ) {
        
    }

	@Override
	public void genKra(PW pw, boolean putParenthesis) {
		// TODO Auto-generated method stub
		
	}
	
    private Method message;
    private ExprList parameters;
    private KraClass superClass;
    
}