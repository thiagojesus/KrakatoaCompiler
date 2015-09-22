package AST;
import java.util.*;

public class SC{
	private Symbol symbol;

	public SC(Symbol symbol){
		this.symbol = symbol;
	}

	public Symbol getSymbol(){
		return symbol;
	}

	public void setSymbol(Symbol symbol){
		this.symbol = symbol;
	}
}