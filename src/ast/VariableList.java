package ast;

import java.util.ArrayList;
import java.util.Iterator;

public class VariableList {
	private ArrayList<Variable> v;

	public VariableList(ArrayList<Variable> v) {
		super();
		this.v = v;
	}

	public Iterator<Variable> elements() {
		return v.iterator();
	}
	
	//falta implementar os metodos de procurar uma variavel nessa lista pra
	//ver se ela existe, mas to com preguiça de fazer
}
