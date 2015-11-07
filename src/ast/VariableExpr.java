/*
 * @author Thiago Martins de Jesus 380385
 * @author Vinnícius Ferreira da Silva 380032
 * */
package ast;

public class VariableExpr extends Expr {
    
    public VariableExpr( Variable v ) {
        this.v = v;
    }
    
    public void genC( PW pw, boolean putParenthesis ) {
    	pw.print( "_"+v.getName() );
    }
    
    public Type getType() {
        return v.getType();
    }
    
    private Variable v;

	@Override
	public void genKra(PW pw, boolean putParenthesis) {
		pw.printIdent( v.getName() );
		
	}
}