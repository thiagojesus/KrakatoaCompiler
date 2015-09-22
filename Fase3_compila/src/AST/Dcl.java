package AST;
import Lexer.*;
import java.util.*;

public class Dcl{
	private Type type;
	private ArrayList<Var> idlist;

	public Dcl(Type type, ArrayList<Var> idlist){
		
		this.type = type;
		this.idlist = idlist;

	}

	public ArrayList<Var>  getIdList(){
		return idlist;
	}

	public void genC(PW pw){
		Var aux;
		Array array = type.getArray();

		if(type.getArray() == null){
			type.genC(pw);

			if(idlist.size() != 0){
				aux = idlist.get(0);
				pw.print(aux.getName());
				idlist.remove(aux);

				for (Var  id : idlist){
					pw.print(" ,"+ id.getName());
				}
			}

		}else{
			type.genC(pw);

			aux = idlist.get(0);
			pw.print(aux + "["+ array.getTam() +"]");
			idlist.remove(aux.getName());

			for (Var  id : idlist){
				pw.print(" ,"+ id.getName() + "["+ array.getTam() +"]");
			}
		}
		
	}

}