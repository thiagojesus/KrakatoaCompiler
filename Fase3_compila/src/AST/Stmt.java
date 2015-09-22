package AST;
import Lexer.*;
import java.util.*;

abstract public class Stmt{
	abstract public void genC(PW pw);
}