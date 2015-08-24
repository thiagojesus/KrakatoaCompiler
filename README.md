## Krakatoa Compiler
#Gramática

AssignExprLocalDe:c:= Expression [ “=” Expression ] | LocalDec
BasicType ::= “void” | “int” | “boolean” | “String”
BasicValue ::= IntValue | BooleanValue | StringValue
BooleanValue ::= “true” | “false”
ClassDec ::= “class” Id [ “extends” Id ] “{” MemberList “}” 
CompStatement ::= “{” { Statement } “}”
Digit ::=“0”|...|“9”
Expression ::= SimpleExpression [ Relation SimpleExpression ]
ExpressionList ::= Expression { “,” Expression }
Factor ::= BasicValue |
           “(” Expression “)” |
           “!” Factor |
           “null” |
           ObjectCreation |
           PrimaryExpr
FormalParamDec ::= ParamDec { “,” ParamDec }
HighOperator ::=“∗”|“/”|“&&” 
Id ::=Letter{Letter|Digit|“”}
IdList ::=Id{“,”Id}
IfStat ::= “if ” “(” Expression “)” Statement 
           [ “else” Statement ]
InstVarDec ::= Type IdList “;”
IntValue ::= Digit { Digit }
LeftValue ::= [ (“this” | Id ) “.” ] Id
Letter ::=“A”|... |“Z”|“a”|... |“z”
LocalDec ::= Type IdList “;”
LowOperator ::= “+” | “−” | “||”
MemberList ::= { Qualifier Member }
Member ::= InstVarDec | MethodDec
MethodDec ::= Type Id “(” [ FormalParamDec ] “)”
              “{” StatementList “}”
MOCall ::= “@” Id [ “(” { MOParam } “)” ]
MOParam ::= IntValue | StringValue | Id
ObjectCreation ::= “new” Id “(” “)”
ParamDec ::= Type Id
Program ::= { MOCall } ClassDec { ClassDec }
Qualifier ::= [ “final” ] [ “static” ] ( “private” | “public”)
ReadStat ::= “read” “(” LeftValue { “,” LeftValue } “)”
PrimaryExpr ::= “super” “.” Id “(” [ ExpressionList ] “)” |
                Id |
                Id “.” Id |
                Id “.” Id “(” [ ExpressionList ] ”)” |
                Id “.” Id “.” Id “(” [ ExpressionList ] “)” | “this” |
                “this” “.” Id |
                “this” ”.” Id “(” [ ExpressionList ] “)” | 
                “this” ”.” Id “.” Id “(” [ ExpressionList ] “)”
Relation ::= “==” | “<” | “>” | “<=” | “>=” | “! =”
ReturnStat ::= “return” Expression
RightValue ::= “this” [ “.” Id ] | Id [ “.” Id ]
Signal ::= “+” | “−”
SignalFactor ::= [ Signal ] Factor
SimpleExpression ::= Term { LowOperator Term }
Statement ::= AssignExprLocalDec “;” | IfStat | WhileStat | ReturnStat “;” |
              ReadStat “;” | WriteStat “;” | “break” “;” | “;” | CompStatement
StatementList ::= { Statement } 
Term ::= SignalFactor { HighOperator SignalFactor }
Type ::= BasicType | Id
WriteStat ::= “write” “(” ExpressionList “)”
WhileStat ::= “while” “(” Expression “)” Statement
