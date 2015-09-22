package AST;
import Lexer.*;
import java.util.*;

public class Dclpart{
	private Dcls dcls;
	private ArrayList<Subdcl> subdcls = new ArrayList<Subdcl>();

	public Dclpart(Dcls dcls, ArrayList<Subdcl> subdcls){
		
		this.dcls = dcls;
		this.subdcls = subdcls;

	}

	public Dcls getDcls(){
		return this.dcls;
	}

	public ArrayList<Subdcl> getSubdcls(){
		return this.subdcls;
	}

	public void genC(PW pw){

		if(this.dcls != null){
			dcls.genC(pw);
		}
	
	}

}