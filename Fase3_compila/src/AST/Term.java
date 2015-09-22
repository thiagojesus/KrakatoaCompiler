package AST;
import Lexer.*;
import java.util.*;

public class Term{
	private Factor factor = null;
    private ArrayList<Symbol> mulops = new ArrayList<Symbol>();
    private ArrayList<Factor> extraFactors = new ArrayList<Factor>();

	public Term(Factor factor, ArrayList<Symbol> mulops, ArrayList<Factor> extraFactors){
		
		this.factor = factor;
		this.mulops = mulops;
		this.extraFactors = extraFactors;

	}

	public void genC(PW pw){
		int cont;
		factor.genC(pw);
		if (mulops != null){
			for (cont = 0; cont < mulops.size(); cont++){
				if(mulops.get(cont) == Symbol.MULT){
            		pw.print(" * ");
        		}else if(mulops.get(cont) == Symbol.AND){
            		pw.print(" && ");
        		}else if(mulops.get(cont) == Symbol.MOD){
            		pw.print(" mod ");
        		}
        		extraFactors.get(cont).genC(pw);

			}

		}
	
	}

}