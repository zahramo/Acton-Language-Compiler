grammar acton;

@header 
{
    package main.parsers;
    import main.*;
    import main.ast.node.* ;
    import main.ast.node.declaration.* ;
    import main.ast.node.declaration.handler.* ;
    import main.ast.node.expression.* ;
    import main.ast.node.expression.operators.* ;
    import main.ast.node.expression.values.* ;
    import main.ast.node.statement.* ;
    import main.ast.type.* ;
    import main.ast.type.actorType.* ;
    import main.ast.type.arrayType.* ;
    import main.ast.type.primitiveType.* ;
    import java.util.ArrayList;

}

program returns [Program p]
    :   
    {
        $p = new Program();}
    (actorDeclaration 
    {
        $p.addActor($actorDeclaration.sub_tree);
    }
    )+ mainDeclaration 
    {
        $p.setMain($mainDeclaration.sub_tree);
    }
    ;

actorDeclaration returns [ActorDeclaration sub_tree] 
    :
    name1 = ACTOR name = identifier
    {
        $sub_tree = new ActorDeclaration($name.sub_tree);
        $sub_tree.setLine($name1.getLine() );

    }
    (EXTENDS parent_name = identifier
    {
        $sub_tree.setParentName($parent_name.sub_tree);
    }
    )? LPAREN INTVAL
    {
        $sub_tree.setQueueSize(Integer.parseInt($INTVAL.getText()));
    }
    RPAREN
    LBRACE

    (KNOWNACTORS
    LBRACE (type = identifier name = identifier SEMICOLON
    {
        ActorType actor_type = new ActorType($type.sub_tree);
        actor_type.setLine($type.sub_tree.getLine());
        VarDeclaration var_declaration = new VarDeclaration($name.sub_tree, actor_type);
        var_declaration.setLine($name.sub_tree.getLine());
        $sub_tree.addKnownActor(var_declaration);
    }
    )*
    RBRACE)

    (ACTORVARS
    LBRACE
    varDeclarations
    {
        $sub_tree.setActorVars($varDeclarations.var_declarations);
    }
    RBRACE)

    (initHandlerDeclaration
    {
        $sub_tree.setInitHandler($initHandlerDeclaration.sub_tree);
    }
    )?
    (msgHandlerDeclaration
    {
        $sub_tree.addMsgHandler($msgHandlerDeclaration.sub_tree);
    }
    )*
    RBRACE
    ;

mainDeclaration returns [Main sub_tree]
    :
    name = MAIN
    {
        $sub_tree = new Main();
        $sub_tree.setLine($name.getLine());
    }

    LBRACE
    (actorInstantiation
    {
        $sub_tree.addActorInstantiation($actorInstantiation.sub_tree);
    }
    )*
    RBRACE
    ;

actorInstantiation returns [ActorInstantiation sub_tree]
    :
    type = identifier name = identifier
    {
        ActorType actor_type = new ActorType($type.sub_tree);
        actor_type.setLine($type.sub_tree.getLine());
        $sub_tree = new ActorInstantiation(actor_type, $name.sub_tree);
        $sub_tree.setLine($name.sub_tree.getLine());
    }
    LPAREN (actor = identifier
    {
         $sub_tree.addKnownActor($actor.sub_tree);
    }
    (COMMA actor = identifier
    {
        $sub_tree.addKnownActor($actor.sub_tree);
    }
    )* | ) RPAREN
    COLON LPAREN expressionList RPAREN SEMICOLON
    {
        $sub_tree.setInitArgs($expressionList.expression_list);
    }
    ;

initHandlerDeclaration returns [InitHandlerDeclaration sub_tree]
    :
    name = MSGHANDLER INITIAL LPAREN argDeclarations RPAREN
    LBRACE
    varDeclarations
    {
        Identifier Id;
        Id = new Identifier("initial");
        Id.setLine($name.getLine());
        $sub_tree = new InitHandlerDeclaration(Id);
        $sub_tree.setLine($name.getLine());
        $sub_tree.setArgs($argDeclarations.arg_declarations);
        $sub_tree.setLocalVars($varDeclarations.var_declarations);
    }
    (statement {$sub_tree.addStatement($statement.sub_tree);})*
    RBRACE
    ;

msgHandlerDeclaration returns [MsgHandlerDeclaration sub_tree]
    :
    MSGHANDLER name = identifier
    {
        $sub_tree = new MsgHandlerDeclaration($name.sub_tree);
        $sub_tree.setLine($name.sub_tree.getLine());
    }
    LPAREN argDeclarations
    {
        $sub_tree.setArgs($argDeclarations.arg_declarations);
    }
    RPAREN
    LBRACE
    varDeclarations
    {
        $sub_tree.setLocalVars($varDeclarations.var_declarations);
    }
    (statement
    {
        $sub_tree.addStatement($statement.sub_tree);
    }
    )*
    RBRACE
    ;

argDeclarations returns [ArrayList<VarDeclaration> arg_declarations]
    :
    {
        $arg_declarations = new ArrayList<>();
    }
    varDeclaration
    {
        $arg_declarations.add($varDeclaration.sub_tree);
    }
    (COMMA varDeclaration
    {
        $arg_declarations.add($varDeclaration.sub_tree);
    }
    ) * |
    {
        $arg_declarations = new ArrayList<>();
    }
    ;

varDeclarations returns [ArrayList<VarDeclaration> var_declarations]
    :
    {
        $var_declarations = new ArrayList<>();
    }
    (varDeclaration
    {
        $var_declarations.add($varDeclaration.sub_tree);
    }
    SEMICOLON)*
    ;

varDeclaration returns [VarDeclaration sub_tree]
    :
    INT identifier
    {
        $sub_tree = new VarDeclaration($identifier.sub_tree,new IntType());
        $sub_tree.setLine($identifier.sub_tree.getLine());
    }
    |   STRING identifier
    {
        $sub_tree = new VarDeclaration($identifier.sub_tree,new StringType());
        $sub_tree.setLine($identifier.sub_tree.getLine());
    }
    |   BOOLEAN identifier
    {
        $sub_tree = new VarDeclaration($identifier.sub_tree,new BooleanType());
        $sub_tree.setLine($identifier.sub_tree.getLine());
    }
    |   INT identifier LBRACKET INTVAL RBRACKET
    {
        $sub_tree = new VarDeclaration($identifier.sub_tree,new ArrayType(Integer.parseInt($INTVAL.getText())));
        $sub_tree.setLine($identifier.sub_tree.getLine());
    }
    ;

statement returns [Statement sub_tree]
    :
    blockStmt
    {
        $sub_tree = $blockStmt.sub_tree;
    }
    | 	printStmt
    {
        $sub_tree = $printStmt.sub_tree;
    }
    |  	assignStmt
    {
        $sub_tree = $assignStmt.sub_tree;
    }
    |  	forStmt
    {
        $sub_tree = $forStmt.sub_tree;
    }
    |  	ifStmt
    {
        $sub_tree = $ifStmt.sub_tree;
    }
    |  	continueStmt
    {
        $sub_tree = $continueStmt.sub_tree;
    }
    |  	breakStmt
    {
        $sub_tree = $breakStmt.sub_tree;
    }
    |  	msgHandlerCall
    {
        $sub_tree = $msgHandlerCall.sub_tree;
    }
    ;

blockStmt returns [Block sub_tree]
    :
    name = LBRACE
    {
        $sub_tree = new Block();
        $sub_tree.setLine($name.getLine());
    }
    (statement
    {
        $sub_tree.addStatement($statement.sub_tree);
    }
    )* RBRACE
    ;

printStmt returns [Print sub_tree]
    :
    name = PRINT LPAREN expression
    {
        $sub_tree = new Print($expression.sub_tree);
        $sub_tree.setLine($name.getLine());
    }
    RPAREN SEMICOLON
    ;

assignStmt returns [Statement sub_tree]
    :    assignment SEMICOLON
    {
        $sub_tree = $assignment.sub_tree;
    }
    ;

assignment returns [Assign sub_tree]
    :  o = orExpression name = ASSIGN e = expression
    {
        $sub_tree = new Assign($o.sub_tree, $e.sub_tree);
        $sub_tree.setLine($name.getLine());
    }
    ;

forStmt returns [For sub_tree]
    :
    name = FOR{ $sub_tree = new For();  $sub_tree.setLine($name.getLine());} LPAREN (a = assignment
    {

        $sub_tree.setInitialize($a.sub_tree);
    }
    )? SEMICOLON (e = expression
    {
        $sub_tree.setCondition($e.sub_tree);
    }
    )? SEMICOLON (a2 = assignment
    {
        $sub_tree.setUpdate($a2.sub_tree);
    }
    )? RPAREN s = statement
    {
        $sub_tree.setBody($s.sub_tree);
    }
    ;

ifStmt returns [Conditional sub_tree]
    :   name = IF LPAREN expression RPAREN statement elseStmt
    {
        $sub_tree = new Conditional($expression.sub_tree, $statement.sub_tree);
        $sub_tree.setLine($name.getLine());
        $sub_tree.setElseBody($elseStmt.sub_tree);
    }
    ;

elseStmt returns [Statement sub_tree]
    : ELSE statement
    {
        $sub_tree = $statement.sub_tree;
    }
    |
    {
        $sub_tree = null;
    }
    ;

continueStmt returns [Continue sub_tree]
    : 	CONTINUE SEMICOLON
    ;

breakStmt returns [Break sub_tree]
    : 	BREAK SEMICOLON
    ;

msgHandlerCall returns [MsgHandlerCall sub_tree]
    :   identifier DOT second = identifier LPAREN expressionList RPAREN SEMICOLON
        {
              $sub_tree = new MsgHandlerCall($identifier.sub_tree, $second.sub_tree);
              $sub_tree.setLine($second.sub_tree.getLine());
              $sub_tree.setArgs($expressionList.expression_list);
        }
        |
        SELF DOT second = identifier LPAREN expressionList RPAREN SEMICOLON
        {
              Self self;
              self = new Self();
              self.setLine($second.sub_tree.getLine());
              $sub_tree = new MsgHandlerCall(self, $second.sub_tree);
              $sub_tree.setLine($second.sub_tree.getLine());
              $sub_tree.setArgs($expressionList.expression_list);
        }
        |
        SENDER DOT second = identifier LPAREN expressionList RPAREN SEMICOLON
        {
              Sender sender;
              sender = new Sender();
              sender.setLine($second.sub_tree.getLine());
              $sub_tree = new MsgHandlerCall(sender, $second.sub_tree);
              $sub_tree.setLine($second.sub_tree.getLine());
              $sub_tree.setArgs($expressionList.expression_list);
        }

    ;

expression returns [Expression sub_tree]
    :
    orExpression name = ASSIGN expression
    {
        $sub_tree = new BinaryExpression($orExpression.sub_tree, $expression.sub_tree, BinaryOperator.assign);
        $sub_tree.setLine($name.getLine());
    }
    | orExpression
    {
        $sub_tree = $orExpression.sub_tree;
    }
    ;

orExpression returns [Expression sub_tree]
    :	andExpression orExpressionTemp[$andExpression.sub_tree]
    {
        $sub_tree = $orExpressionTemp.sub_tree;
    }
    ;

orExpressionTemp [Expression left_tree] returns [Expression sub_tree]
    :   name = OR andExpression orExpressionTemp[$andExpression.sub_tree]
    {
        $sub_tree = new BinaryExpression($left_tree, $orExpressionTemp.sub_tree, BinaryOperator.or);
        $sub_tree.setLine($name.getLine());
    }
    |
    {
        $sub_tree = $left_tree;
    }
    ;

andExpression returns [Expression sub_tree]
    :	equalityExpression andExpressionTemp[$equalityExpression.sub_tree]
    {
        $sub_tree = $andExpressionTemp.sub_tree;
    }
    ;

andExpressionTemp [Expression left_tree] returns [Expression sub_tree]
    :   name = AND equalityExpression andExpressionTemp[$equalityExpression.sub_tree]
    {
        $sub_tree = new BinaryExpression($left_tree, $andExpressionTemp.sub_tree, BinaryOperator.and);
        $sub_tree.setLine($name.getLine());
    }
    |
    {
        $sub_tree = $left_tree;
    }
    ;

equalityExpression returns [Expression sub_tree]
    :	relationalExpression equalityExpressionTemp[$relationalExpression.sub_tree]
    {
        $sub_tree = $equalityExpressionTemp.sub_tree;
    }
    ;

equalityExpressionTemp [Expression left_tree] returns [Expression sub_tree]
                                              locals [BinaryOperator b]
    :   name = EQ {$b = BinaryOperator.eq;} relationalExpression equalityExpressionTemp [$relationalExpression.sub_tree]
    {
        $sub_tree = new BinaryExpression($left_tree, $equalityExpressionTemp.sub_tree, $b);
        $sub_tree.setLine($name.getLine());
    }
    |   name = NEQ {$b = BinaryOperator.neq;} relationalExpression equalityExpressionTemp [$relationalExpression.sub_tree]
    {
        $sub_tree = new BinaryExpression($left_tree, $equalityExpressionTemp.sub_tree, $b);
        $sub_tree.setLine($name.getLine());
    }
    |
    {
        $sub_tree = $left_tree;
    }
    ;

relationalExpression returns [Expression sub_tree]
    : additiveExpression relationalExpressionTemp [$additiveExpression.sub_tree]
    {
        $sub_tree = $relationalExpressionTemp.sub_tree;
    }
    ;

relationalExpressionTemp [Expression left_tree] returns [Expression sub_tree]
                                                locals [BinaryOperator b]
    :   name = LT {$b = BinaryOperator.lt;} additiveExpression relationalExpressionTemp [$additiveExpression.sub_tree]
    {
        $sub_tree = new BinaryExpression($left_tree, $relationalExpressionTemp.sub_tree, $b);
        $sub_tree.setLine($name.getLine());
    }
    |   name = GT {$b = BinaryOperator.gt;} additiveExpression relationalExpressionTemp [$additiveExpression.sub_tree]
    {
        $sub_tree = new BinaryExpression($left_tree, $relationalExpressionTemp.sub_tree, $b);
        $sub_tree.setLine($name.getLine());
    }
    |
    {
        $sub_tree = $left_tree;
    }
    ;

additiveExpression returns [Expression sub_tree]
    : multiplicativeExpression  additiveExpressionTemp [$multiplicativeExpression.sub_tree]
    {
        $sub_tree = $additiveExpressionTemp.sub_tree;
    }
    ;

additiveExpressionTemp [Expression left_tree] returns [Expression sub_tree]
                                              locals [BinaryOperator b]
    :   name = PLUS {$b = BinaryOperator.add;} multiplicativeExpression additiveExpressionTemp [$multiplicativeExpression.sub_tree]
    {
        $sub_tree = new BinaryExpression($left_tree, $additiveExpressionTemp.sub_tree, $b);
        $sub_tree.setLine($name.getLine());
    }
    |   name = MINUS {$b = BinaryOperator.sub;} multiplicativeExpression additiveExpressionTemp [$multiplicativeExpression.sub_tree]
    {
        $sub_tree = new BinaryExpression($left_tree, $additiveExpressionTemp.sub_tree, $b);
        $sub_tree.setLine($name.getLine());
    }
    |
    {
        $sub_tree = $left_tree;
    }
    ;

multiplicativeExpression returns [Expression sub_tree]
    : preUnaryExpression multiplicativeExpressionTemp [$preUnaryExpression.sub_tree]
    {
        $sub_tree = $multiplicativeExpressionTemp.sub_tree;
    }
    ;

multiplicativeExpressionTemp [Expression left_tree] returns [Expression sub_tree]
                                                    locals [BinaryOperator b]
    :   name = MULT {$b = BinaryOperator.mult;} preUnaryExpression multiplicativeExpressionTemp [$preUnaryExpression.sub_tree]
    {
        $sub_tree = new BinaryExpression($left_tree, $multiplicativeExpressionTemp.sub_tree, $b);
        $sub_tree.setLine($name.getLine());
    }
    |   name = DIV {$b = BinaryOperator.div;} preUnaryExpression multiplicativeExpressionTemp [$preUnaryExpression.sub_tree]
    {
        $sub_tree = new BinaryExpression($left_tree, $multiplicativeExpressionTemp.sub_tree, $b);
        $sub_tree.setLine($name.getLine());
    }
    |   name = PERCENT {$b = BinaryOperator.mod;} preUnaryExpression multiplicativeExpressionTemp [$preUnaryExpression.sub_tree]
    {
        $sub_tree = new BinaryExpression($left_tree, $multiplicativeExpressionTemp.sub_tree, $b);
        $sub_tree.setLine($name.getLine());
    }
    |
    {
        $sub_tree = $left_tree;
    }
    ;

preUnaryExpression returns [Expression sub_tree]
    :   name = NOT preUnaryExpression
    {
        $sub_tree = new UnaryExpression(UnaryOperator.not ,$preUnaryExpression.sub_tree);
        $sub_tree.setLine($name.getLine());
    }
    |   name = MINUS preUnaryExpression
    {
        $sub_tree = new UnaryExpression(UnaryOperator.minus ,$preUnaryExpression.sub_tree);
        $sub_tree.setLine($name.getLine());
    }
    |   name = PLUSPLUS preUnaryExpression
    {
        $sub_tree = new UnaryExpression(UnaryOperator.preinc ,$preUnaryExpression.sub_tree);
        $sub_tree.setLine($name.getLine());
    }
    |   name = MINUSMINUS preUnaryExpression
    {
        $sub_tree = new UnaryExpression(UnaryOperator.predec ,$preUnaryExpression.sub_tree);
        $sub_tree.setLine($name.getLine());
    }
    |   postUnaryExpression
    {
        $sub_tree = $postUnaryExpression.sub_tree;
    }
    ;

postUnaryExpression returns [Expression sub_tree]
    :   otherExpression postUnaryOp
    {
        $sub_tree = new UnaryExpression($postUnaryOp.sub_tree ,$otherExpression.sub_tree);
        $sub_tree.setLine($postUnaryOp.line);
    }
    |   otherExpression
    {
        $sub_tree = $otherExpression.sub_tree;
    }
    ;

postUnaryOp returns [UnaryOperator sub_tree, int line]
    : name = PLUSPLUS
    {   $sub_tree = UnaryOperator.postinc;
        $line = $name.getLine();
    }
    | mine = MINUSMINUS
    {
        $sub_tree = UnaryOperator.postdec;
        $line = $mine.getLine();
    }
    ;

otherExpression returns [Expression sub_tree]
    :    LPAREN expression RPAREN
    {
        $sub_tree = $expression.sub_tree;

    }
    |    identifier
    {
        $sub_tree = $identifier.sub_tree;
    }
    |    arrayCall
    {
        $sub_tree = $arrayCall.sub_tree;
    }
    |    actorVarAccess
    {
        $sub_tree = $actorVarAccess.sub_tree;
    }
    |    value
    {
        $sub_tree = $value.sub_tree;
    }
    |    name = SENDER
    {
        $sub_tree = new Sender();
        $sub_tree.setLine($name.getLine());
    }
    ;

arrayCall returns [ArrayCall sub_tree]
    :   identifier name = LBRACKET expression RBRACKET
    {
        $sub_tree = new ArrayCall($identifier.sub_tree, $expression.sub_tree);
        $sub_tree.setLine($name.getLine());
    }
    | actorVarAccess name = LBRACKET expression RBRACKET
    {
        $sub_tree = new ArrayCall($actorVarAccess.sub_tree, $expression.sub_tree);
        $sub_tree.setLine($name.getLine());
    }
    ;

actorVarAccess returns [ActorVarAccess sub_tree]
    :   name = SELF DOT identifier
    {
        $sub_tree = new ActorVarAccess($identifier.sub_tree);
        $sub_tree.setLine($name.getLine());
    }
    ;


expressionList returns [ArrayList<Expression> expression_list]
    :
    {
        $expression_list = new ArrayList<>();
    }
    (expression
    {
        $expression_list.add($expression.sub_tree);
    }
    (COMMA expression
    {
        $expression_list.add($expression.sub_tree);
    }
    )* | )
    ;

identifier returns [Identifier sub_tree]
    :   line_help = IDENTIFIER
        {
            $sub_tree = new Identifier($line_help.getText());
            $sub_tree.setLine($line_help.getLine());
        }
        //{$line = $line_help.getLine();}
    ;


value returns [Value sub_tree]

    :   name1 = INTVAL
    {
        $sub_tree = new IntValue($name1.int, new IntType());
        $sub_tree.setLine($name1.getLine());
    }
    | name2 = STRINGVAL
    {
        $sub_tree = new StringValue($name2.text, new StringType());
        $sub_tree.setLine($name2.getLine());
    }
    | name3 = TRUE
    {
        $sub_tree = new BooleanValue(true, new BooleanType());
        $sub_tree.setLine($name3.getLine());
    }
    | name4 = FALSE
    {
        $sub_tree = new BooleanValue(false, new BooleanType());
        $sub_tree.setLine($name4.getLine());
    }
    ;

// values
INTVAL
    : [1-9][0-9]* | [0]
    ;

STRINGVAL
    : '"'~["]*'"'
    ;

TRUE
    :   'true'
    ;

FALSE
    :   'false'
    ;

//types
INT
    : 'int'
    ;

BOOLEAN
    : 'boolean'
    ;

STRING
    : 'string'
    ;

//keywords
ACTOR
	:	'actor'
	;

EXTENDS
	:	'extends'
	;

ACTORVARS
	:	'actorvars'
	;

KNOWNACTORS
	:	'knownactors'
	;

INITIAL
    :   'initial'
    ;

MSGHANDLER
	: 	'msghandler'
	;

SENDER
    :   'sender'
    ;

SELF
    :   'self'
    ;

MAIN
	:	'main'
	;

FOR
    :   'for'
    ;

CONTINUE
    :   'continue'
    ;

BREAK
    :   'break'
    ;

IF
    :   'if'
    ;

ELSE
    :   'else'
    ;

PRINT
    :   'print'
    ;

//symbols
LPAREN
    :   '('
    ;

RPAREN
    :   ')'
    ;

LBRACE
    :   '{'
    ;

RBRACE
    :   '}'
    ;

LBRACKET
    :   '['
    ;

RBRACKET
    :   ']'
    ;

COLON
    :   ':'
    ;

SEMICOLON
    :   ';'
    ;

COMMA
    :   ','
    ;

DOT
    :   '.'
    ;

//operators
ASSIGN
    :   '='
    ;

EQ
    :   '=='
    ;

NEQ
    :   '!='
    ;

GT
    :   '>'
    ;

LT
    :   '<'
    ;

PLUSPLUS
    :   '++'
    ;

MINUSMINUS
    :   '--'
    ;

PLUS
    :   '+'
    ;

MINUS
    :   '-'
    ;

MULT
    :   '*'
    ;

DIV
    :   '/'
    ;

PERCENT
    :   '%'
    ;

NOT
    :   '!'
    ;

AND
    :   '&&'
    ;

OR
    :   '||'
    ;

QUES
    :   '?'
    ;

IDENTIFIER
    :   [a-zA-Z_][a-zA-Z0-9_]*
    ;

COMMENT
    :   '//' ~[\n\r]* -> skip
    ;

WHITESPACE
    :   [ \t\r\n] -> skip
    ;