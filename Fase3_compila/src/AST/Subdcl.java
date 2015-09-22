package AST;
import Lexer.*;
import java.util.*;

public class Subdcl{
	private Subhead subhead;
	private Body body = null;

	public Subdcl(Subhead subhead, Body body){
		
		this.subhead = subhead;
		this.body = body;

	}

	public Subhead getSubhead(){
		return this.subhead;
	}

	public void genC(PW pw){
		subhead.genC(pw);
		pw.println("{\n");		
		body.genC(pw);
		pw.println("}\n");


	}

}