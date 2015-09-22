/*
KeyWorlds:
        PROGRAM - Program
        VAR - Variables
        INTEGER - Int type
        REAL - Real Type
        CHAR - Char Type
        STRING - String Type
        ARRAY - Array Type
        OF - auxiliar do Array Type
        BEGIN - Begin
        END - End
        IF 
        THEN
        ELSE
        ENDIF
        WHILE
        DO
        ENDWHILE
        READ - scanf ('Ler') 
        WRITE
        WRITELN
        OR
        AND
        MOD
        DIV
        NOT

  Symbols:      
        ’=’
        ’<’
        ’>’
        ’<=’ 
        ’>=’ 
        ’<>’
        ’*’
        ’/’
        ’+’
        ’-’
*/

package Lexer;

public enum Symbol {

      ASSIGN(":="),
      LT("<"),
      GT(">"),
      LE("<="),
      GE(">="),
      NEQ("!="),
      MULT("*"),
      DIV("/"),
      EOF("eof"),
      ID("id"),
      PLUS("+"),
      MINUS("-"),
      EQ("=="),
      LEFTPAR("("),
      RIGHTPAR(")"),
      SEMICOLON(";"),
      COLON(":"),
      COMMA(","),
      DOT("."),
      OR   ("||"),
      AND  ("&&"),
      REMAINDER("%"),
      NOT("!"),
      OB("["),
      CB("]"),
      NUMBER("num"),
      PROGRAM("program"),
      VAR("var"),
      INTEGER("integer"),
      REAL("real"),
      CHAR("char"),
      STRING("string"),
      ARRAY("array"),
      OF("of"),
      BEGIN("begin"),
      END("end"),
      IF("if"),
      THEN("then"),
      ELSE("else"),
      ENDIF("endif"),
      WHILE("while"),
      DO("do"),
      ENDWHILE("endwhile"),
      READ("read"),
      WRITE("write"),
      WRITELN("writeln"),
      MOD("mod"),
      FUNCTION("function"),
      PROCEDURE("procedure"),
      RETURN("return"),
      NULL("null");

      Symbol(String name) {
          this.name = name;
      }

      public String toString() {
          return name;
      }

      private String name;

}