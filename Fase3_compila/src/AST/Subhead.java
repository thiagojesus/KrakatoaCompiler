package AST;
import Lexer.*;
import java.util.*;

public class Subhead{
	private String pid = null;
	private Args args;
	private String stdtype = new String();


	public Subhead(String pid, Args args, String stdtype){
		this.pid = pid;
		this.args = args;
		this.stdtype = stdtype;

	}

	public String getName(){
		return this.pid;
	}

	public Args getArgs(){
		return this.args;
	}

	public void genC(PW pw){
		if(stdtype != null){
			pw.println(stdtype+ " ");
			args.genC(pw);

		}else{
			pw.println("void ");
			args.genC(pw);
		}


	}

}