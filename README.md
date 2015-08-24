## Krakatoa Compiler
#Gramática

AssignExprLocalDe:c:= Expression [ “=” Expression ] | LocalDec <br />
BasicType ::= “void” | “int” | “boolean” | “String” <br />
BasicValue ::= IntValue | BooleanValue | StringValue <br />
BooleanValue ::= “true” | “false” <br />
ClassDec ::= “class” Id [ “extends” Id ] “{” MemberList “}” <br />
CompStatement ::= “{” { Statement } “}” <br />
Digit ::=“0”|...|“9” <br />
Expression ::= SimpleExpression [ Relation SimpleExpression ] <br />
ExpressionList ::= Expression { “,” Expression } <br />
Factor ::= BasicValue | <br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;“(” Expression “)” | <br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;“!” Factor | <br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;“null” | <br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;ObjectCreation | <br />
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;PrimaryExpr <br />
FormalParamDec ::= ParamDec { “,” ParamDec } <br />
HighOperator ::=“∗”|“/”|“&&” <br />
Id ::=Letter{Letter|Digit|“”} <br />
IdList ::=Id{“,”Id} <br />
IfStat ::= “if ” “(” Expression “)” Statement <br />
           [ “else” Statement ] <br />
InstVarDec ::= Type IdList “;” <br />
IntValue ::= Digit { Digit } <br />
LeftValue ::= [ (“this” | Id ) “.” ] Id <br />
Letter ::=“A”|... |“Z”|“a”|... |“z” <br />
LocalDec ::= Type IdList “;” <br />
LowOperator ::= “+” | “−” | “||” <br />
MemberList ::= { Qualifier Member } <br />
Member ::= InstVarDec | MethodDec <br />
MethodDec ::= Type Id “(” [ FormalParamDec ] “)” <br />
              “{” StatementList “}” <br />
MOCall ::= “@” Id [ “(” { MOParam } “)” ] <br />
MOParam ::= IntValue | StringValue | Id <br />
ObjectCreation ::= “new” Id “(” “)” <br />
ParamDec ::= Type Id <br />
Program ::= { MOCall } ClassDec { ClassDec } <br />
Qualifier ::= [ “final” ] [ “static” ] ( “private” | “public”) <br />
ReadStat ::= “read” “(” LeftValue { “,” LeftValue } “)” <br />
PrimaryExpr ::= “super” “.” Id “(” [ ExpressionList ] “)” | <br />
                Id | <br />
                Id “.” Id | <br />
                Id “.” Id “(” [ ExpressionList ] ”)” | <br />
                Id “.” Id “.” Id “(” [ ExpressionList ] “)” | “this” | <br />
                “this” “.” Id | <br />
                “this” ”.” Id “(” [ ExpressionList ] “)” | <br />
                “this” ”.” Id “.” Id “(” [ ExpressionList ] “)” <br />
Relation ::= “==” | “<” | “>” | “<=” | “>=” | “! =” <br />
ReturnStat ::= “return” Expression <br />
RightValue ::= “this” [ “.” Id ] | Id [ “.” Id ] <br />
Signal ::= “+” | “−” <br />
SignalFactor ::= [ Signal ] Factor <br />
SimpleExpression ::= Term { LowOperator Term } <br />
Statement ::= AssignExprLocalDec “;” | IfStat | WhileStat | ReturnStat “;” | <br />
              ReadStat “;” | WriteStat “;” | “break” “;” | “;” | CompStatement <br />
StatementList ::= { Statement } <br />
Term ::= SignalFactor { HighOperator SignalFactor } <br />
Type ::= BasicType | Id <br />
WriteStat ::= “write” “(” ExpressionList “)” <br />
WhileStat ::= “while” “(” Expression “)” Statement <br />
