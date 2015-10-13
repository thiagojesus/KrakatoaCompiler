/*
 * @author Thiago Martins de Jesus 380385
 * @author Vinnícius Ferreira da Silva 380032
 * */
package ast;

public class NewExpr extends Expr {

	public NewExpr(KraClass _object){
		this.objectClass = _object;
	}
	@Override
	public void genC(PW pw, boolean putParenthesis) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void genKra(PW pw, boolean putParenthesis) {
		// TODO Auto-generated method stub
		pw.println("new "+objectClass.getName()+"();");
	}

	@Override
	public Type getType() {
		return objectClass;
	}
	
	private KraClass objectClass;

}
