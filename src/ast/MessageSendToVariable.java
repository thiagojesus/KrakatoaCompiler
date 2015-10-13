/*
 * @author Thiago Martins de Jesus 380385
 * @author Vinnícius Ferreira da Silva 380032
 * */
package ast;


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