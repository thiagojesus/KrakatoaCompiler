package AST;
import Lexer.*;
import java.util.*;

public class Prog{

	private Body body;
	
	public Prog(Body body){
		
		this.body = body;
	}
	
	public void genC(PW pw){
	
		pw.println("#include<stdio.h>");
		pw.println("");
		pw.println("void main(){");
		body.genC(pw);
		pw.println("}");
	}

}
