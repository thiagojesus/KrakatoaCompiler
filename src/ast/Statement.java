/*
 * @author Thiago Martins de Jesus 380385
 * @author Vinnícius Ferreira da Silva 380032
 * */
package ast;

abstract public class Statement {

	abstract public void genC(PW pw);
	abstract public void genKra(PW pw, boolean putParenthesis);

}
