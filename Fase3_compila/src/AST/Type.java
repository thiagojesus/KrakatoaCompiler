package AST;
import Lexer.*;
import java.util.*;

public class Type{
	private String typename;
	private Array array;

	public Type(String typename, Array array){
		
		this.typename = typename;
		this.array = array;

	}

	public String getTypename(){
		return typename;
	}

	public Array getArray(){
		return array;
	}

	public void genC(PW pw){
		
		if(array == null){
			pw.print(typename +" ");
		}else{
			pw.print(array.stdtype +" ");
		}
		
	}

}