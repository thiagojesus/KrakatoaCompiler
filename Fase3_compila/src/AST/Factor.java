package AST;
import Lexer.*;
import java.util.*;

public class Factor{
	private Vbl vbl = null;
    private Num num = null;
    private Expr expr = null;
    private Symbol dot = Symbol.NULL;
    private Stmt procfunc = null;

	public Factor(Vbl vbl, Num num, Expr expr, Symbol dot, Stmt procfunc){
		
		this.vbl = vbl;
		this.num = num;
		this.expr = expr;
		this.dot = dot;
		this.procfunc = procfunc;

	}

	public void genC(PW pw){

		if (vbl != null){
			vbl.genC(pw);
		}
		if (num != null){
			num.genC(pw);
		}
		if (expr != null){
			pw.print(" ( ");
			expr.genC(pw);
			pw.print(" ) ");
		}
		// if (dot != Symbol.NULL){
		// 	pw.print(" . ");
		// }

	}

}