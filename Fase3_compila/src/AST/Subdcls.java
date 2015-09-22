package AST;
import Lexer.*;
import java.util.*;

public class Subdcls{
	private ArrayList<Subdcl> subdcls = new ArrayList<Subdcl>();

	public Subdcl(ArrayList<Subdcl> subdcls){
		
		this.subdcls = subdcls;

	}

	public void genC(PW pw){

		if(this.dcls != null){
			pw.println("{");
			dcls.genC(pw);
			pw.println("{");
		}
	
	}

}