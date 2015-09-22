package AST;
import Lexer.*;
import java.util.*;

public class Args{

	private Dcls dcls = null;

	public Args(Dcls dcls){
		
		this.dcls = dcls;

	}

	public void genC(PW pw){

		if(this.dcls != null){
			pw.println("(");
			dcls.genC(pw);
			pw.println("(");

		}
	
	}

}