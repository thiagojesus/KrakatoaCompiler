/*
 * @author Thiago Martins de Jesus 380385
 * @author Vinnícius Ferreira da Silva 380032
 * */
package ast;

import java.util.ArrayList;

public class ReadStatement extends Statement {

	private ArrayList<Variable> variables;
	private ArrayList<Boolean> self;
	private ArrayList<String> sName;
	@Override
	public void genC(PW pw) {
		int i = 0;
		// TODO Auto-generated method stub
		pw.printlnIdent("{");
		pw.add();
		for(Variable v: variables){
			String staticName;
			if(self.get(i)){
				Type t = v.getType();
				if(t.getName().equals(Type.stringType.getName())){
					pw.printlnIdent("char __s[512];");
					pw.printlnIdent("gets(__s);");
					pw.printlnIdent("_static_"+v.getName()+" = malloc(strlen(__s) + 1);");
					pw.printlnIdent("strcpy(this->_"+v.getType().getName()+"_"+v.getName()+", __s);");
				}else{
					pw.printlnIdent("char __s[512];");
					pw.printlnIdent("gets(__s);");
					pw.printlnIdent("sscanf(__s, \"%d\", &this->_"+v.getType().getName()+"_"+v.getName()+");");
				}
			}
			else if((staticName = sName.get(i)) != null){
				Type t = v.getType();
				if(t.getName().equals(Type.stringType.getName())){
					pw.printlnIdent("char __s[512];");
					pw.printlnIdent("gets(__s);");
					pw.printlnIdent("_static_"+v.getName()+" = malloc(strlen(__s) + 1);");
					pw.printlnIdent("strcpy(_static_"+v.getName()+", __s);");
				}else{
					pw.printlnIdent("char __s[512];");
					pw.printlnIdent("gets(__s);");
					pw.printlnIdent("sscanf(__s, \"%d\", &_static_"+v.getName()+");");
				}
			}else{
				Type t = v.getType();
				if(t.getName().equals(Type.stringType.getName())){
					pw.printlnIdent("char __s[512];");
					pw.printlnIdent("gets(__s);");
					pw.printlnIdent("_"+v.getName()+" = malloc(strlen(__s) + 1);");
					pw.printlnIdent("strcpy(_"+v.getName()+", __s);");
				}else{
					pw.printlnIdent("char __s[512];");
					pw.printlnIdent("gets(__s);");
					pw.printlnIdent("sscanf(__s, \"%d\", &_"+v.getName()+");");
				}
			}
			i++;
		}
		pw.sub();
		pw.printlnIdent("}");
	}
	
	public ReadStatement(ArrayList<Variable> v, ArrayList<Boolean> _self, ArrayList<String> _sName){
		this.variables = v;
		this.self = _self;
		this.sName = _sName;
	}

	@Override
	public void genKra(PW pw, boolean putParenthesis) {
		pw.add();
        pw.printIdent("read");
        pw.print("(");         
        variables.remove(0).genKra(pw, putParenthesis);   
        for(Variable lv : variables){
            pw.print(",");
            lv.genKra(pw,putParenthesis);
        }
        pw.print(")");
        pw.sub();
		
	}

}
