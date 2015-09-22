package AST;
import Lexer.*;
import java.util.*;

//readstmt ::= READ ’(’ vblist ’)’
public class Readstmt extends Stmt{
    private ArrayList<Vbl> vbls;

    public Readstmt(ArrayList<Vbl> vbls){

    	this.vbls = vbls;

    }

    public void genC(PW pw){

        pw.print("scanf(\"");

        for (Vbl v : vbls){

            String type = v.getType();

            switch(type){
                case "int":
                    pw.print("%d ");
                break;
                case "real":
                    pw.print("%i ");
                break;
                case "char":
                    pw.print("%c ");
                break;
                case "char*":
                    pw.print("%s ");
                break;
            }
        }

        pw.print("\"");

        for (Vbl v : vbls){
            pw.print(", &"+v.getName());
        }

        pw.println(");");

    }

}