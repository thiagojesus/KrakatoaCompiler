/*
 * @author Thiago Martins de Jesus 380385
 * @author Vinnícius Ferreira da Silva 380032
 * */
package ast;

public class ParenthesisExpr extends Expr {
    
    public ParenthesisExpr( Expr expr ) {
        this.expr = expr;
    }
    
    public void genC( PW pw, boolean putParenthesis ) {
        pw.print("(");
        expr.genC(pw, false);
        pw.print(")");
    }
    
    public Type getType() {
        return expr.getType();
    }
    
    private Expr expr;

	@Override
	public void genKra(PW pw, boolean putParenthesis) {
		pw.print("(");
        expr.genKra(pw, putParenthesis);
        pw.print(")");
		
	}
}