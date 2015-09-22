package AST;
import Lexer.*;
import java.util.*;

public class Array{
	public int tam;
	public String stdtype;

	public Array(int tam, String stdtype){
		
		this.tam = tam;
		this.stdtype = stdtype;

	}

	public String getArrayType(){
		return stdtype;
	}

	public int getTam(){
		return tam;
	}

}