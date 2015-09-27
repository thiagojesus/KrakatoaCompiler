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
    
    public void genK(PW pw, boolean putParenthesis){
       exprList.remove(0).genKra(pw, putParenthesis);
        for(Expr exp : exprList){
            pw.print(",");
            exp.genKra(pw, putParenthesis);
        }
    }

    private ArrayList<Expr> exprList;

}
