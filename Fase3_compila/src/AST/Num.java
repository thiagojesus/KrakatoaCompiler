package AST;
import Lexer.*;
import java.util.*;

public class Num{
    private int intPart = 0;
    private int decPart = 0;


	public Num(int intPart, int decimalPart){
		
		this.intPart = intPart;
		this.decPart = decPart;

	}

	public void genC(PW pw){

		pw.print(Integer.toString(intPart));
		if (decPart != 0){
			pw.print(" . ");
			pw.print(Integer.toString(decPart));
		}
	}
}