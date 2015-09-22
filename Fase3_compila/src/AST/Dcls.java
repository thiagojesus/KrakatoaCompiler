package AST;
import Lexer.*;
import java.util.*;

public class Dcls{
	private ArrayList<Dcl> dcl;

	public Dcls(ArrayList<Dcl> dcl){
		
		this.dcl = dcl;

	}

	public ArrayList<Dcl> getDcl(){
		return this.dcl;
	}

	public void genC(PW pw){

		for (Dcl d : dcl){
			d.genC(pw);
			pw.println(";");
		}
		
	}

}