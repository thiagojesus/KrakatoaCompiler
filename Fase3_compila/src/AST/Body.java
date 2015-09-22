package AST;
import Lexer.*;
import java.util.*;

public class Body{
	private Dclpart dclpart;
	private Compstmt compstmt;

	public Body(Dclpart dclpart, Compstmt compstmt){
		
		this.dclpart = dclpart;
		this.compstmt = compstmt;

	}

	public void genC(PW pw){

		pw.add();

		dclpart.genC(pw);
		pw.println("");
		compstmt.genC(pw);

		pw.sub();

	}

}