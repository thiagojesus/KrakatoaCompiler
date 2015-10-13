/*
 * @author Thiago Martins de Jesus 380385
 * @author Vinnícius Ferreira da Silva 380032
 * */
package ast;

import java.util.*;

public class ExprList {

    public ExprList() {
        exprList = new ArrayList<Expr>();
    }

    public void addElement( Expr expr ) {
        exprList.add(expr);
    }

    public void genC( PW pw ) {

        int size = exprList.size();
        for ( Expr e : exprList ) {
        	e.genC(pw, false);
            if ( --size > 0 )
                pw.print(", ");
        }
    }
    
    public void genKra(PW pw, boolean putParenthesis){
    	for(Expr exp : exprList){
    		pw.print(",");
            exp.genKra(pw, putParenthesis);
        }
    }

    private ArrayList<Expr> exprList;

	public Iterator<Expr> elements() {
		return exprList.iterator();
	}
	
	public int getSize(){
    	return exprList.size();
    }

}
