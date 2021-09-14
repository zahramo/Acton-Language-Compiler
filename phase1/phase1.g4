grammar Acton;
//--------------------->Tokens come below here!--------------------<

COMMENT
    :   '//' ~[\r\n]* -> skip
    ;

WS
    :   [ \t\r\n] -> skip
    ;

COLON
    :   ':'
    ;

SEMICOLON
    :   ';'
    ;

DOT
    :   '.'
    ;

QUESTION
    :   '?'
    ;

INT
    :   'int'
    ;

POSNUM
    :   [1-9][0-9]*
    |   [0]
    ;

STRING
    : 'string'
    ;

LITERAL
    : '"'~["]*'"'
    ;


MSGHANDLER
    :   'msghandler'
    ;

INITIAL
    :   'initial'
    ;

EXTENDS
    :   'extends'
    ;

ACTORVARS
    :   'actorvars'
    ;

KNOWNACTORS
    :   'knownactors'
    ;

ACTOR
    :   'actor'
    ;

PRINT
    :   'print'
    ;

FOR
    :   'for'
    ;

ELSE
    :   'else'
    ;

IF
    :   'if'
    ;

SENDER
    :   'sender'
    ;

SELF
    :   'self'
    ;

MAIN
    :   'main'
    ;

BOOLEAN
    : 'boolean'
    ;

TRUE
    :   'true'
    ;

FALSE
    :   'false'
    ;

CONTINUE
    :   'continue'
    ;

BREAK
    :   'break'
    ;

IDENTIFIER
    :   [A-Za-z_][A-Za-z0-9_]*
    ;

NOT
    :   '!'
    ;

//operators come below here!

//Level 1 ops
RPARANT
    :   ')'
    ;

LPARANT
    :   '('
    ;

//level 2&3 ops
CRBRACE
    :   '}'
    ;

CLBRACE
    :   '{'
    ;

RBRACE
    :   ']'
    ;

LBRACE
    :   '['
    ;

PLUSPLUS
    :   '++'
    ;

MINUSMINUS
    :   '--'
    ;

//level 4 ops
MULTIPLY
    :   '*'
    ;

DIVIDE
    :   '/'
    ;

REMAINING
    :   '%'
    ;

//level 5 ops
ADD
    :   '+'
    ;

SUBTRACT
    :   '-'
    ;

//level 6 ops
LESSTHAN
    :   '<'
    ;

GREATERTHAN
    :   '>'
    ;

//level 7 ops
EQUAL
    :   '=='
    ;

NOTEQUAL
    :   '!='
    ;

//level 8 ops
LOGICALAND
    :   '&&'
    ;

//level 9 ops
LOGICALOR
    :   '||'
    ;

//shart 3 taii!!!

//level 11 ops
ASSIGN
    :   '='
    ;

//level 12 ops
COMMA
    :   ','
    ;

//--------------------->Grammar come below here!--------------------<

program
    :   (actor)+ main
    ;

//variable definition comes below!

variable_declarator
    :   primitive {System.out.print("VarDec:" + $primitive.text);} IDENTIFIER {System.out.println("," + $IDENTIFIER.getText());} | array_declarator
    ;

primitive
    :   BOOLEAN | STRING | INT
    ;

boolean_type
    :   TRUE
    | FALSE
    ;

array_declarator
    :   INT {System.out.print("VarDec:int");} IDENTIFIER  LBRACE POSNUM RBRACE {System.out.println($IDENTIFIER.getText()+"["+ $POSNUM.getText() +"]");}
    ;

array
    :   IDENTIFIER LBRACE POSNUM RBRACE
    ;

///////////////////////////////

//expression handling comes below!

statement
    :   CLBRACE statement* CRBRACE
    |   if_statement
    |   for_statement
    |   BREAK
    |   CONTINUE
    |   print_statement
    |   SEMICOLON
    |   expression SEMICOLON
    ;

if_statement
    :   IF {System.out.println("Conditional:if");} expression_with_parant statement (ELSE {System.out.println("Conditional:else");} statement)?
    ;

for_statement
    :   FOR {System.out.println("Loop:for");} forexpression_with_parant statement
    ;

print_statement
    :   PRINT {System.out.println("Built-in:Print");} printexpression_with_parant SEMICOLON
    ;

expression_with_parant
    :   LPARANT expression RPARANT
    ;

forexpression_with_parant
    :   LPARANT (expression)? SEMICOLON (expression)? SEMICOLON (expression)? RPARANT
    ;

printexpression_with_parant
    :   LPARANT expression RPARANT
    |   LPARANT LITERAL RPARANT
    ;

expression
    : {System.out.println("Operator:=");}  ternary_expression  ASSIGN  ternary_expression | ternary_expression
    ;

ternary_expression
    :  {System.out.println("Operator:?:");} or_operand QUESTION (or_operand | ternary_expression) COLON (or_operand | ternary_expression) | or_operand
    ;

or_operand
    :  {System.out.println("Operator:||");} and_operand  LOGICALOR  and_operand | and_operand
    ;

and_operand
    :  {System.out.println("Operator:&&");} eq_neq_operand  LOGICALAND  eq_neq_operand | eq_neq_operand
    ;

eq_neq_operand
    :  {System.out.println("Operator:==");} greater_less_operand EQUAL greater_less_operand | {System.out.println("Operator:!=");} greater_less_operand NOTEQUAL greater_less_operand  | greater_less_operand
    ;

greater_less_operand
    :  {System.out.println("Operator:>");} add_sub_operand GREATERTHAN add_sub_operand | {System.out.println("Operator:<");} add_sub_operand LESSTHAN add_sub_operand | add_sub_operand
    ;

add_sub_operand
    : {System.out.println("Operator:+");}  mult_div_operand ADD mult_div_operand  | {System.out.println("Operator:-");}  mult_div_operand SUBTRACT mult_div_operand | mult_div_operand
    ;

mult_div_operand
    :  {System.out.println("Operator:*");} unary_pre_operand  MULTIPLY  unary_pre_operand | {System.out.println("Operator:/");} unary_pre_operand  DIVIDE  unary_pre_operand | unary_pre_operand
    ;

unary_pre_operand
    :   {System.out.println("Operator:++");} PLUSPLUS  unary_post_operand | {System.out.println("Operator:--");} MINUSMINUS unary_post_operand | {System.out.println("Operator:!");} NOT unary_post_operand |{System.out.println("Operator:-");} SUBTRACT  unary_post_operand | unary_post_operand
    ;

unary_post_operand
    : {System.out.println("Operator:++");}  worth_operand  PLUSPLUS | {System.out.println("Operator:--");} worth_operand  MINUSMINUS | worth_operand
    ;

worth_operand
    :   expression_with_parant
    |   IDENTIFIER LBRACE expression RBRACE
    |   SELF DOT IDENTIFIER
    |   (IDENTIFIER {System.out.print("MsgHandlerCall:" + $IDENTIFIER.getText());} | SELF {System.out.print("MsgHandlerCall:" + $SELF.getText());} | SENDER {System.out.print("MsgHandlerCall:" + $SENDER.getText());} ) DOT IDENTIFIER {System.out.println("," + $IDENTIFIER.getText());} LPARANT inputs RPARANT
    |   POSNUM
    |   LITERAL
    |   array
    |   SENDER
    |   IDENTIFIER
    |   boolean_type
    ;
//actor definition comes below!

actor
    :   actor_init CLBRACE actor_inside CRBRACE
    ;

actor_init
    :   ACTOR IDENTIFIER {System.out.print("ActorDec:" + $IDENTIFIER.getText());} (EXTENDS IDENTIFIER {System.out.print("," + $IDENTIFIER.getText());} )? {System.out.println("");} LPARANT (POSNUM)? RPARANT
    ;

actor_inside
    :   known_actors
        actor_vars
        msg_handler*
    ;

known_actors
    :   KNOWNACTORS CLBRACE friend_actor* CRBRACE
    ;

friend_actor
    :   IDENTIFIER {System.out.print("KnownActor:" + $IDENTIFIER.getText());} IDENTIFIER {System.out.println("," + $IDENTIFIER.getText());} SEMICOLON
    ;

actor_vars
    :   ACTORVARS CLBRACE (variable_declarator SEMICOLON)* CRBRACE
    ;

msg_handler
    :   msg_init CLBRACE msg_inside CRBRACE
    ;

msg_init
    :   MSGHANDLER (IDENTIFIER {System.out.println("MsgHandlerDec:" + $IDENTIFIER.getText());}| INITIAL {System.out.println("MsgHandlerDec:" + $INITIAL.getText());} ) LPARANT arguments? RPARANT
    ;

arguments
    :   (variable_declarator (COMMA variable_declarator)*)?
    ;


msg_inside
    :   (variable_declarator SEMICOLON)* (statement)*
    ;

inputs
    :   (expression (COMMA  expression)*)?
    ;

main
    :   MAIN CLBRACE (IDENTIFIER {System.out.print("ActorInstantiation:" +  $IDENTIFIER.getText());} IDENTIFIER {System.out.print("," + $IDENTIFIER.getText());} LPARANT (IDENTIFIER {System.out.print("," + $IDENTIFIER.getText());} (COMMA IDENTIFIER {System.out.print("," + $IDENTIFIER.getText());} )*)* {System.out.println("");} RPARANT COLON LPARANT inputs RPARANT SEMICOLON)* CRBRACE
    ;




