/*
 * @author Thiago Martins de Jesus 380385
 * @author Vinnícius Ferreira da Silva 380032
 * */
package ast;

public class MessageSendToSuper extends MessageSend { 
	
	public MessageSendToSuper(Method _message, ExprList _exprList, KraClass _sC){
		this.message = _message;
		this.parameters = _exprList;
		this.superClass = _sC;
	}
	
    public Type getType() { 
        return message.getType();
    }

    public void genC( PW pw, boolean putParenthesis ) {
    	if(putParenthesis)
    		pw.print("(");
        pw.print("_"+superClass.getName()+"_"+message.getId()+"(( "+superClass.getCname()+"*) this" );
        if(parameters != null){
        	pw.print(", ");
        	parameters.genC(pw);
        }
        pw.print(")");
        if(putParenthesis)
    		pw.print(")");   
    }

	@Override
	public void genKra(PW pw, boolean putParenthesis) {
		// TODO Auto-generated method stub
		pw.printIdent("super."+message.getId()+"(");
		if(parameters != null)
			parameters.genKra(pw, putParenthesis);
		pw.println(");");
		
	}
	
    private Method message;
    private ExprList parameters;
    private KraClass superClass;
    
}