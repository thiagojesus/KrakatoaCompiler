/*
 * @author Thiago Martins de Jesus 380385
 * @author Vinnícius Ferreira da Silva 380032
 * */
package ast;

public class NullType extends Type {

	public NullType() {
		super("null");
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getCname() {
		// TODO Auto-generated method stub
		return "null";
	}

}
