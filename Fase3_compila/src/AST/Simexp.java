package AST;
import Lexer.*;
import java.util.*;

public class Simexp{
	private Symbol unary = Symbol.NULL;
    private Term term1 = null;
    private ArrayList<Symbol> addop = new ArrayList<Symbol>();
    private ArrayList<Term> term2 = new ArrayList<Term>();

	public Simexp(Symbol unary, Term term1, ArrayList<Symbol> addop, ArrayList<Term> term2){
		
		this.unary = unary;
		this.term1 = term1;
		this.addop = addop;
		this.term2 = term2;

	}

	public void genC(PW pw){
		int cont;

		if (unary != Symbol.NULL){
			if(unary == Symbol.PLUS){
            	pw.print(" + ");
        	}else if(unary == Symbol.MINUS){
            	pw.print(" - ");
        	}else if(unary == Symbol.NOT){
            	pw.print(" ! ");
			}
		}
		term1.genC(pw);

		if (addop != null){
			for (cont = 0; cont < addop.size(); cont++){
				if(addop.get(cont) == Symbol.PLUS){
            		pw.print(" + ");
        		}else if(addop.get(cont) == Symbol.MINUS){
            		pw.print(" - ");
        		}else if(addop.get(cont) == Symbol.OR){
            		pw.print(" || ");
            	}
            	term2.get(cont).genC(pw);
			}
		}
	}

}