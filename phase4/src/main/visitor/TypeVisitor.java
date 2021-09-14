package main.visitor;
import main.ast.node.*;
import main.ast.node.Program;
import main.ast.node.declaration.*;
import main.ast.node.declaration.handler.*;
import main.ast.node.declaration.VarDeclaration;
import main.ast.node.expression.*;
import main.ast.node.expression.operators.BinaryOperator;
import main.ast.node.expression.values.BooleanValue;
import main.ast.node.expression.values.IntValue;
import main.ast.node.expression.values.StringValue;
import main.ast.node.expression.values.Value;
import main.ast.node.statement.*;
import main.ast.type.Type;
import main.ast.type.actorType.ActorType;
import main.ast.type.arrayType.ArrayType;
import main.ast.type.noType.NoType;
import main.ast.type.primitiveType.*;
import main.symbolTable.SymbolTable;
import main.symbolTable.SymbolTableActorItem;
import main.symbolTable.SymbolTableHandlerItem;
import main.symbolTable.SymbolTableMainItem;
import main.symbolTable.itemException.ItemAlreadyExistsException;
import main.symbolTable.itemException.ItemNotFoundException;
import main.symbolTable.symbolTableVariableItem.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;

public class TypeVisitor implements Visitor {

    SymbolTable actorSymbolTable;
    SymbolTable curSymbolTable;
    boolean inInitial = false;
    boolean inFor = false;
    boolean inMain = false;
    boolean validNoType = false;

    protected void visitStatement( Statement stat )
    {
        if( stat == null )
            return;
        else if( stat instanceof MsgHandlerCall )
            this.visit( ( MsgHandlerCall ) stat );
        else if( stat instanceof Block )
            this.visit( ( Block ) stat );
        else if( stat instanceof Conditional )
            this.visit( ( Conditional ) stat );
        else if( stat instanceof For )
            this.visit( ( For ) stat );
        else if( stat instanceof Break )
            this.visit( ( Break ) stat );
        else if( stat instanceof Continue )
            this.visit( ( Continue ) stat );
        else if( stat instanceof Print )
            this.visit( ( Print ) stat );
        else if( stat instanceof Assign )
            this.visit( ( Assign ) stat );
    }

    protected void visitExpr( Expression expr )
    {
        if( expr == null )
            return;
        else if( expr instanceof UnaryExpression )
            this.visit( ( UnaryExpression ) expr );
        else if( expr instanceof BinaryExpression )
            this.visit( ( BinaryExpression ) expr );
        else if( expr instanceof ArrayCall )
            this.visit( ( ArrayCall ) expr );
        else if( expr instanceof ActorVarAccess )
            this.visit( ( ActorVarAccess ) expr );
        else if( expr instanceof Identifier )
            this.visit( ( Identifier ) expr );
        else if( expr instanceof Self )
            this.visit( ( Self ) expr );
        else if( expr instanceof Sender )
            this.visit( ( Sender ) expr );
        else if( expr instanceof BooleanValue )
            this.visit( ( BooleanValue ) expr );
        else if( expr instanceof IntValue )
            this.visit( ( IntValue ) expr );
        else if( expr instanceof StringValue )
            this.visit( ( StringValue ) expr );
    }

    private ArrayList<VarDeclaration> findArgsOfHandler(Identifier handlerName, Identifier actorName)
    {
        ArrayList<VarDeclaration> args = new ArrayList<>();
        SymbolTable curActorSymbolTable = null;
        try
        {
            curActorSymbolTable = ((SymbolTableActorItem)SymbolTable.root.get("Actor_" + actorName.getName())).getActorSymbolTable();
            SymbolTableHandlerItem tempHandlerItem = null;

            try
            {
                tempHandlerItem = (SymbolTableHandlerItem) curActorSymbolTable.get("Handler_" + handlerName.getName());
                args = tempHandlerItem.getHandlerDeclaration().getArgs();

            }
            catch(ItemNotFoundException e)
            {
                args = null;
            }

            return args;

        }catch(ItemNotFoundException e)
        {
            return args;
        }

    }

    private ArrayList<VarDeclaration> findAKnownActorsOfActor(Identifier actorName)
    {
        SymbolTableActorItem curActorItem = null;
        try
        {
            curActorItem = ((SymbolTableActorItem)SymbolTable.root.get("Actor_" + actorName.getName()));

        }catch(ItemNotFoundException e)
        { }

        ArrayList<VarDeclaration> knownActors = null;
        knownActors = curActorItem.getActorDeclaration().getKnownActors();
        return knownActors;
    }


    private Type findIdType(String id)
    {
        SymbolTableVariableItem tempVarItem = null;
        Type idType;


        try
        {
            tempVarItem = (SymbolTableVariableItem) curSymbolTable.get("Variable_" + id);
            idType = tempVarItem.getType();

        }
        catch(ItemNotFoundException e)
        {
            idType = new NoType();
        }
        return idType;

    }

    private boolean doesVarExist(Identifier varName)
    {
        SymbolTableVariableItem tempVarItem = null;

        try
        {
            tempVarItem = (SymbolTableVariableItem) curSymbolTable.get("Variable_" + varName.getName());

        }
        catch(ItemNotFoundException e)
        {
            return false;
        }
        return true;
    }

    private boolean doesHandlerExist(Identifier handlerName, Identifier actorName)
    {
        SymbolTable curActorSymbolTable = null;
        try
        {
            curActorSymbolTable = ((SymbolTableActorItem)SymbolTable.root.get("Actor_" + actorName.getName())).getActorSymbolTable();

        }catch(ItemNotFoundException e)
        {
            System.out.println("Line:" + actorName.getLine() + ":" + "actor " +  actorName.getName() + " is not declared");
            return false;
        }

        SymbolTableHandlerItem tempHandlerItem = null;

        try
        {
            tempHandlerItem = (SymbolTableHandlerItem) curActorSymbolTable.get("Handler_" + handlerName.getName());

        }
        catch(ItemNotFoundException e)
        {
            return false;
        }
        return true;
    }

    private  boolean doesActorExist(Identifier actorName)
    {
        SymbolTableActorItem tempActorItem = null;
        try
        {
            tempActorItem = (SymbolTableActorItem) SymbolTable.root.get("Actor_" + actorName.getName());

        }
        catch(ItemNotFoundException e)
        {
            return false;
        }
        return  true;
    }

    private String findFather(String child) {

        SymbolTableActorItem tempActorItem = null;
        try
        {
            tempActorItem = (SymbolTableActorItem) SymbolTable.root.get("Actor_" + child);
        }
        catch(ItemNotFoundException e)
        {
            return "";
        }
        return tempActorItem.getParentName();
    }

    private boolean isSubType(Type child, Type parent)
    {
        String fatherName;
        if(child instanceof ActorType && parent instanceof ActorType)
        {
            if(child.toString().equals("sender") || parent.toString().equals("sender"))
            {
                return true;
            }
            if (((ActorType) child).getName().getName().equals(((ActorType) parent).getName().getName()))
            {
                return true;
            }
            fatherName = ((ActorType) child).getName().getName();
            if(fatherName == null)
            {
                return false;
            }
            while(true)
            {
                fatherName = findFather(fatherName);
                if(fatherName == null)
                {
                    return false;
                }
                if(fatherName.equals(""))
                {
                    return false;
                }
                if(fatherName.equals(((ActorType) parent).getName().getName()))
                {
                    return true;
                }
            }


        }

        if((child instanceof IntType && parent instanceof IntType) || (child instanceof StringType && parent instanceof StringType)
            || (child instanceof BooleanType && parent instanceof BooleanType) || (child instanceof ArrayType && parent instanceof ArrayType))
        {
            return true;
        }

        if(child instanceof NoType)
        {
            return true;
        }
        if(parent instanceof NoType)
        {
            return true;
        }
        return false;
    }

    private Type getExpressionType(Expression e) {
        validNoType = false;
        if (e instanceof UnaryExpression) {
            if (((UnaryExpression) e).getOperand().getType() instanceof NoType) {
                return new NoType();
            } else if (((UnaryExpression) e).getUnaryOperator().toString().equals("not")) {

                if (((UnaryExpression) e).getOperand().getType() instanceof BooleanType) {
                    return new BooleanType();
                } else {
                    return new NoType();
                }
            } else if (((UnaryExpression) e).getUnaryOperator().toString().equals("minus")) {
                if (((UnaryExpression) e).getOperand().getType() instanceof IntType) {
                    return new IntType();
                } else {
                    return new NoType();
                }
            } else if (((UnaryExpression) e).getUnaryOperator().toString().equals("preinc")) {
                if (((UnaryExpression) e).getOperand() instanceof Value || ((UnaryExpression) e).getOperand() instanceof BinaryExpression || ((UnaryExpression) e).getOperand() instanceof UnaryExpression ||((UnaryExpression) e).getOperand() instanceof Sender) {
                    System.out.println("Line:" + e.getLine() + ":" + "lvalue required as increment operand");
                }
                if (((UnaryExpression) e).getOperand().getType() instanceof IntType) {
                    return new IntType();
                } else {
                    return new NoType();
                }

            } else if (((UnaryExpression) e).getUnaryOperator().toString().equals("postinc")) {
                if (((UnaryExpression) e).getOperand() instanceof Value || ((UnaryExpression) e).getOperand() instanceof BinaryExpression || ((UnaryExpression) e).getOperand() instanceof UnaryExpression ||((UnaryExpression) e).getOperand() instanceof Sender) {
                    System.out.println("Line:" + e.getLine() + ":" + "lvalue required as increment operand");
                }
                if (((UnaryExpression) e).getOperand().getType() instanceof IntType) {
                    return new IntType();
                } else {
                    return new NoType();
                }
            } else if (((UnaryExpression) e).getUnaryOperator().toString().equals("predec")) {
                if (((UnaryExpression) e).getOperand() instanceof Value || ((UnaryExpression) e).getOperand() instanceof BinaryExpression || ((UnaryExpression) e).getOperand() instanceof UnaryExpression ||((UnaryExpression) e).getOperand() instanceof Sender) {
                    System.out.println("Line:" + e.getLine() + ":" + "lvalue required as decrement operand");
                }
                if (((UnaryExpression) e).getOperand().getType() instanceof IntType) {
                    return new IntType();
                } else {
                    return new NoType();
                }
            } else if (((UnaryExpression) e).getUnaryOperator().toString().equals("postdec")) {
                if (((UnaryExpression) e).getOperand() instanceof Value || ((UnaryExpression) e).getOperand() instanceof BinaryExpression || ((UnaryExpression) e).getOperand() instanceof UnaryExpression ||((UnaryExpression) e).getOperand() instanceof Sender) {
                    System.out.println("Line:" + e.getLine() + ":" + "lvalue required as decrement operand");
                }
                if (((UnaryExpression) e).getOperand().getType() instanceof IntType) {
                    return new IntType();
                } else {
                    return new NoType();
                }
            }
        }
        else if(e instanceof BinaryExpression)
        {
            if(((BinaryExpression) e).getLeft().getType() instanceof NoType && ((BinaryExpression) e).getRight().getType() instanceof NoType )
            {
                validNoType = true;
                return new NoType();
            }
            if(((BinaryExpression) e).getBinaryOperator().toString().equals("eq") || ((BinaryExpression) e).getBinaryOperator().toString().equals("neq"))
            {
                if(((BinaryExpression) e).getLeft().getType() instanceof ActorType && ((BinaryExpression) e).getRight().getType() instanceof ActorType)
                {
                    return new BooleanType();
                }
                if(((BinaryExpression) e).getLeft().getType().toString().equals(((BinaryExpression) e).getRight().getType().toString()))
                {
                    if(((BinaryExpression) e).getLeft().getType() instanceof ArrayType)
                    {
                        if(((ArrayType) ((BinaryExpression) e).getLeft().getType()).getSize() != ((ArrayType)((BinaryExpression) e).getRight().getType()).getSize())
                        {
                            return new NoType();
                        }
                    }
                    return new BooleanType();
                }
                else
                {
                    if(((BinaryExpression) e).getLeft().getType() instanceof NoType)
                    {
                        if(((BinaryExpression) e).getRight().getType() instanceof ActorType
                                || ((BinaryExpression) e).getRight().getType() instanceof IntType
                                || ((BinaryExpression) e).getRight().getType() instanceof BooleanType
                                ||  ((BinaryExpression) e).getRight().getType() instanceof ArrayType
                                ||  ((BinaryExpression) e).getRight().getType() instanceof StringType)
                        {
                            validNoType = true;
                        }
                    }
                    if(((BinaryExpression) e).getRight().getType() instanceof NoType)
                    {
                        if(((BinaryExpression) e).getLeft().getType() instanceof ActorType
                                || ((BinaryExpression) e).getLeft().getType() instanceof IntType
                                || ((BinaryExpression) e).getLeft().getType() instanceof BooleanType
                                ||  ((BinaryExpression) e).getLeft().getType() instanceof ArrayType
                                ||  ((BinaryExpression) e).getLeft().getType() instanceof StringType)
                        {
                            validNoType = true;
                        }
                    }
                    return new NoType();
                }
            }
            else if(((BinaryExpression) e).getBinaryOperator().toString().equals("gt")
                    || ((BinaryExpression) e).getBinaryOperator().toString().equals("lt"))
            {
                if(((BinaryExpression) e).getLeft().getType() instanceof IntType && ((BinaryExpression) e).getRight().getType() instanceof  IntType)
                {
                    if(((BinaryExpression) e).getLeft().getType() instanceof ArrayType)
                    {
                        if(((ArrayType) ((BinaryExpression) e).getLeft().getType()).getSize() != ((ArrayType)((BinaryExpression) e).getRight().getType()).getSize())
                        {
                            return new NoType();
                        }
                    }
                    return new BooleanType();
                }
                else
                {
                    if(((BinaryExpression) e).getLeft().getType() instanceof NoType)
                    {
                        if(((BinaryExpression) e).getRight().getType() instanceof IntType)
                        {
                            validNoType = true;
                        }
                    }
                    if(((BinaryExpression) e).getRight().getType() instanceof NoType)
                    {
                        if(((BinaryExpression) e).getLeft().getType() instanceof IntType)
                        {
                            validNoType = true;
                        }
                    }
                    return new NoType();
                }
            }
            else if(((BinaryExpression) e).getBinaryOperator().toString().equals("add")
                    || ((BinaryExpression) e).getBinaryOperator().toString().equals("sub")
                    || ((BinaryExpression) e).getBinaryOperator().toString().equals("mult")
                    || ((BinaryExpression) e).getBinaryOperator().toString().equals("mod")
                    || ((BinaryExpression) e).getBinaryOperator().toString().equals("div"))
            {
                if(((BinaryExpression) e).getLeft().getType() instanceof IntType && ((BinaryExpression) e).getRight().getType() instanceof  IntType)
                {
                    if(((BinaryExpression) e).getLeft().getType() instanceof ArrayType)
                    {
                        if(((ArrayType) ((BinaryExpression) e).getLeft().getType()).getSize() != ((ArrayType)((BinaryExpression) e).getRight().getType()).getSize())
                        {
                            return new NoType();
                        }
                    }
                    return new IntType();
                }
                else
                {
                    if(((BinaryExpression) e).getLeft().getType() instanceof NoType)
                    {
                        if(((BinaryExpression) e).getRight().getType() instanceof IntType)
                        {
                            validNoType = true;
                        }
                    }
                    if(((BinaryExpression) e).getRight().getType() instanceof NoType)
                    {
                        if(((BinaryExpression) e).getLeft().getType() instanceof IntType)
                        {
                            validNoType = true;
                        }
                    }
                    return new NoType();
                }
            }
            else if(((BinaryExpression) e).getBinaryOperator().toString().equals("and") || ((BinaryExpression) e).getBinaryOperator().toString().equals("or"))
            {
                if(((BinaryExpression) e).getLeft().getType() instanceof BooleanType && ((BinaryExpression) e).getRight().getType() instanceof  BooleanType)
                {
                    if(((BinaryExpression) e).getLeft().getType() instanceof ArrayType)
                    {
                        if(((ArrayType) ((BinaryExpression) e).getLeft().getType()).getSize() != ((ArrayType)((BinaryExpression) e).getRight().getType()).getSize())
                        {
                            return new NoType();
                        }
                    }
                    return new BooleanType();
                }
                else
                {
                    if(((BinaryExpression) e).getLeft().getType() instanceof NoType)
                    {
                        if(((BinaryExpression) e).getRight().getType() instanceof BooleanType)
                        {
                            validNoType = true;
                        }
                    }
                    if(((BinaryExpression) e).getRight().getType() instanceof NoType)
                    {
                        if(((BinaryExpression) e).getLeft().getType() instanceof BooleanType)
                        {
                            validNoType = true;
                        }
                    }
                    return new NoType();
                }
            }
            else if(((BinaryExpression) e).getBinaryOperator().toString().equals("assign"))
            {
                if(((BinaryExpression) e).getLeft() instanceof  Value || ((BinaryExpression) e).getLeft() instanceof BinaryExpression || ((BinaryExpression) e).getLeft() instanceof UnaryExpression || ((BinaryExpression) e).getLeft() instanceof Sender)
                {
                    System.out.println("Line:" + e.getLine() + ":" + "left side of assignment must be a valid lvalue");
                }
                if(isSubType(((BinaryExpression) e).getRight().getType(), ((BinaryExpression) e).getLeft().getType()))
                {
                    if(((BinaryExpression) e).getLeft().getType() instanceof ArrayType && ((BinaryExpression) e).getRight().getType() instanceof ArrayType)
                    {
                        if(((ArrayType) ((BinaryExpression) e).getLeft().getType()).getSize() != ((ArrayType)((BinaryExpression) e).getRight().getType()).getSize())
                        {
                            return new NoType();
                        }
                    }
                    return ((BinaryExpression) e).getLeft().getType();
                }
                else
                {
                    return new NoType();
                }
            }
        }
        return new NoType();
    }

    @Override
    public void visit(Program program) {
        if(program.getActors() != null) {
            for (int i = 0; i < program.getActors().size(); i++)
            {
                program.getActors().get(i).accept(this);
            }
        }
        if (program.getMain() != null)
        {
            inMain = true;
            program.getMain().accept(this);
            inMain = false;
        }


    }

    @Override
    public void visit(ActorDeclaration actorDeclaration) {
        if (actorDeclaration.getName() != null) {
            try
            {
                actorSymbolTable = ((SymbolTableActorItem)SymbolTable.root.get("Actor_" + actorDeclaration.getName().getName())).getActorSymbolTable();

            }catch(ItemNotFoundException e){ }
        }
        if (actorDeclaration.getParentName() != null)
        {
            if(!doesActorExist(actorDeclaration.getParentName()))
            {
                System.out.println("Line:" + actorDeclaration.getLine() + ":" + "actor " + actorDeclaration.getParentName().getName() + " is not declared");
            }
        }
        if (actorDeclaration.getKnownActors() != null) {
            for (int i = 0; i < actorDeclaration.getKnownActors().size(); i++)
            {
                actorDeclaration.getKnownActors().get(i).accept(this);
            }
        }

        if (actorDeclaration.getActorVars() != null) {
            for (int i = 0; i < actorDeclaration.getActorVars().size(); i++)
            {
                actorDeclaration.getActorVars().get(i).accept(this);
            }
        }

        if(actorDeclaration.getInitHandler() != null)
        {
            inInitial = true;
            try
            {
                curSymbolTable = ((SymbolTableHandlerItem)actorSymbolTable.get("Handler_" + actorDeclaration.getInitHandler().getName().getName())).getHandlerSymbolTable();
            }catch(ItemNotFoundException e){}
            actorDeclaration.getInitHandler().accept(this);
            inInitial = false;
        }


        if (actorDeclaration.getMsgHandlers() != null) {
            for (int i = 0; i < actorDeclaration.getMsgHandlers().size(); i++)
            {
                try
                {
                    curSymbolTable = ((SymbolTableHandlerItem)actorSymbolTable.get("Handler_" + actorDeclaration.getMsgHandlers().get(i).getName().getName())).getHandlerSymbolTable();
                }catch(ItemNotFoundException e){}

                actorDeclaration.getMsgHandlers().get(i).accept(this);
            }
        }

    }

    @Override
    public void visit(HandlerDeclaration handlerDeclaration) {

        if (handlerDeclaration.getArgs() != null) {
            for (int i = 0; i < handlerDeclaration.getArgs().size(); i++)
            {
                handlerDeclaration.getArgs().get(i).accept(this);
            }
        }
        if (handlerDeclaration.getLocalVars() != null) {
            for (int i = 0; i < handlerDeclaration.getLocalVars().size(); i++) {
                handlerDeclaration.getLocalVars().get(i).accept(this);
            }
        }
        if (handlerDeclaration.getBody() != null) {
            for (int i = 0; i < handlerDeclaration.getBody().size(); i++)
            {
                if (handlerDeclaration.getBody().get(i) != null)
                    handlerDeclaration.getBody().get(i).accept(this);
            }
        }
    }

    @Override
    public void visit(VarDeclaration varDeclaration) {
        if (varDeclaration.getIdentifier() != null) {
        }
        if (varDeclaration.getType() != null) {
            varDeclaration.getType().accept(this);
        }


        if(varDeclaration.getType() instanceof  ActorType)
        {
            if(!doesActorExist(((ActorType) varDeclaration.getType()).getName()))
            {
                System.out.println("Line:" + varDeclaration.getLine() + ":" + "actor " + varDeclaration.getType().toString() + " is not declared");
            }
        }


    }

    @Override
    public void visit(Main mainActors) {

        try
        {
            curSymbolTable = ((SymbolTableMainItem)actorSymbolTable.get("Main_main")).getMainSymbolTable();
        }catch(ItemNotFoundException e){}
        if (mainActors.getMainActors() != null) {
            for (int i = 0; i < mainActors.getMainActors().size(); i++)
            {
                mainActors.getMainActors().get(i).accept(this);
            }
        }
    }

    @Override
    public void visit(ActorInstantiation actorInstantiation) {
        if(actorInstantiation.getType() != null)
        {
            if(!doesActorExist(((ActorType)actorInstantiation.getType()).getName()))
            {
                System.out.println("Line:" + actorInstantiation.getLine() + ":" + "actor " + (actorInstantiation.getType().toString()) + " is not declared");
            }
            else
            {
                if (actorInstantiation.getKnownActors() != null) {
                    ArrayList<VarDeclaration> knownActors = findAKnownActorsOfActor(((ActorType) actorInstantiation.getType()).getName());
                    for (int i = 0; i < actorInstantiation.getKnownActors().size(); i++) {
                        actorInstantiation.getKnownActors().get(i).accept(this);
                    }
                    if(knownActors != null && actorInstantiation.getKnownActors().size() != knownActors.size())
                    {
                        System.out.println("Line:" + actorInstantiation.getLine() + ":" + "knownactors do not match with definition");
                    }
                    else
                    {
                        for (int i = 0; i < actorInstantiation.getKnownActors().size(); i++)
                        {
                            if(!doesVarExist(actorInstantiation.getKnownActors().get(i)))
                            {
                                System.out.println("Line:" + actorInstantiation.getLine() + ":" + "variable " + actorInstantiation.getIdentifier().getName() + " is not declared");
                            }
                            else
                            {
                                if(!isSubType(actorInstantiation.getKnownActors().get(i).getType(), knownActors.get(i).getType()))
                                {
                                    System.out.println("Line:" + actorInstantiation.getLine() + ":" + "knownactors do not match with definition");
                                }
                            }
                        }

                    }

                }

                if (actorInstantiation.getInitArgs() != null) {
                    ArrayList<VarDeclaration> initArgs = findArgsOfHandler(new Identifier("initial"), ((ActorType) actorInstantiation.getType()).getName());
                    for (int i = 0; i < actorInstantiation.getInitArgs().size(); i++) {
                        actorInstantiation.getInitArgs().get(i).accept(this);
                    }
                    if(initArgs == null && actorInstantiation.getInitArgs().size()!=0)
                    {
                        System.out.println("Line:" + actorInstantiation.getLine() + ":" + "arguments do not match with definition");
                    }
                    else if(initArgs != null && initArgs.size() != actorInstantiation.getInitArgs().size())
                    {
                        System.out.println("Line:" + actorInstantiation.getLine() + ":" + "arguments do not match with definition");

                    }
                    else
                    {
                        for (int i = 0; i < actorInstantiation.getInitArgs().size(); i++)
                        {
                            if(!isSubType(actorInstantiation.getInitArgs().get(i).getType(), initArgs.get(i).getType()))
                            {
                                System.out.println("Line:" + actorInstantiation.getLine() + ":" + "arguments do not match with definition");
                            }
                        }
                    }
                }

            }
        }

    }


    @Override
    public void visit(UnaryExpression unaryExpression) {
        if (unaryExpression.getOperand() != null) {
            unaryExpression.getOperand().accept(this);

            Type expType = getExpressionType(unaryExpression);
            if(unaryExpression.getOperand().getType() instanceof NoType)
            {
                unaryExpression.setType(new NoType());
            }
            else
            {
                if(expType instanceof NoType)
                {
                    System.out.println("Line:" + unaryExpression.getLine() + ":" + "unsupported operand type for " + unaryExpression.getUnaryOperator());
                }
                unaryExpression.setType(expType);

            }
        }
    }



    @Override
    public void visit(BinaryExpression binaryExpression) {
        if (binaryExpression.getLeft() != null && binaryExpression.getRight() != null) {
            binaryExpression.getLeft().accept(this);
            binaryExpression.getRight().accept(this);
            Type binaryExp = getExpressionType(binaryExpression);


            if(binaryExp instanceof NoType && !validNoType)
            {
                System.out.println("Line:" + binaryExpression.getLine() + ":" + "unsupported operand type for " + binaryExpression.getBinaryOperator());
            }
            binaryExpression.setType(binaryExp);

        }
    }

    @Override
    public void visit(ArrayCall arrayCall) {
        if (arrayCall.getArrayInstance() != null && arrayCall.getIndex() != null)
        {
            arrayCall.getArrayInstance().accept(this);
            arrayCall.getIndex().accept(this);

            if (arrayCall.getArrayInstance().getType() instanceof NoType || arrayCall.getIndex().getType() instanceof NoType)
            {
                arrayCall.setType(new NoType());
            }
            else if (!(arrayCall.getArrayInstance().getType() instanceof ArrayType))
            {
                System.out.println("Line:" + arrayCall.getLine() + ":" + "instance type must be ArrayType in ArrayCall");
                arrayCall.setType(new NoType());
                System.out.println("Line:" + arrayCall.getLine() + ":" + "index type must be IntType in ArrayCall");
                arrayCall.setType(new NoType());
            }
            else if (!(arrayCall.getIndex().getType() instanceof IntType))
            {
                System.out.println("Line:" + arrayCall.getLine() + ":" + "index type must be IntType in ArrayCall");
                arrayCall.setType(new NoType());
            }
            else {
                arrayCall.setType(new IntType());
            }
        }
    }

    @Override
    public void visit(ActorVarAccess actorVarAccess) {
        SymbolTableActorVariableItem tempActorVarItem;
        if (actorVarAccess.getSelf() != null && actorVarAccess.getVariable() != null) {
            actorVarAccess.getSelf().accept(this);
            if(inMain)
            {
                System.out.println("Line:" + actorVarAccess.getLine() + ":" + "self doesn't refer to any actor");
                actorVarAccess.setType(new NoType());
            }
            else
            {
                actorVarAccess.getSelf().setType(new ActorType(new Identifier(actorSymbolTable.getName())));
                try {
                    tempActorVarItem = (SymbolTableActorVariableItem) actorSymbolTable.get("Variable_" + actorVarAccess.getVariable().getName());
                    actorVarAccess.setType(tempActorVarItem.getType());
                } catch (ItemNotFoundException e) {
                    System.out.println("Line:" + actorVarAccess.getLine() + ":" + "variable " +  actorVarAccess.getVariable().getName() + " is not declared");
                    actorVarAccess.setType(new NoType());
                }
            }

        }

    }

    @Override
    public void visit(Identifier identifier) {
        if(findIdType(identifier.getName()) instanceof NoType)
        {
            System.out.println("Line:" + identifier.getLine() + ":" + "variable " +  identifier.getName() + " is not declared");
            SymbolTableVariableItem tempVarItem = new SymbolTableVariableItem(new VarDeclaration(identifier, new NoType()));
            try
            {
                curSymbolTable.put(tempVarItem);
            }
            catch (ItemAlreadyExistsException e){}

        }
        identifier.setType(findIdType(identifier.getName()));
    }

    @Override
    public void visit(Self self) {
        self.setType(new ActorType(new Identifier(actorSymbolTable.getName())));
    }

    @Override
    public void visit(Sender sender) {
        sender.setType(new ActorType(new Identifier("sender")));
        if(inInitial)
        {
            System.out.println("Line:" + sender.getLine() + ":" + "no sender in initial msghandler");
        }
    }

    @Override
    public void visit(BooleanValue value) {
        value.setType(new BooleanType());
    }

    @Override
    public void visit(IntValue value) {
        value.setType(new IntType());
    }

    @Override
    public void visit(StringValue value) {
        value.setType(new StringType());
    }

    @Override
    public void visit(Block block) {
        if (block != null){
            for (int i = 0; i < block.getStatements().size(); i++) {
                block.getStatements().get(i).accept(this);
            }
        }
    }

    @Override
    public void visit(Conditional conditional) {
        if (conditional.getExpression() != null ) {
            conditional.getExpression().accept(this);

            if(!(conditional.getExpression().getType() instanceof BooleanType
                    || conditional.getExpression().getType() instanceof NoType)){
                System.out.println("Line:" + conditional.getLine() + ":" + "condition type must be Boolean");
            }
        }
        if (conditional.getThenBody() != null ) {
            conditional.getThenBody().accept(this);
        }
        if (conditional.getElseBody() != null ) {
            conditional.getElseBody().accept(this);
        }
    }

    @Override
    public void visit(For loop) {
        boolean tempflag = inFor;
        inFor = true;
        if (loop.getInitialize() != null) {
            loop.getInitialize().accept(this);
        }
        if(loop.getCondition() != null) {
            loop.getCondition().accept(this);
            if(!(loop.getCondition().getType() instanceof BooleanType
                    || loop.getCondition().getType() instanceof NoType)){
                System.out.println("Line:" + loop.getLine() + ":" + "condition type must be Boolean");
            }
        }
        if(loop.getUpdate() != null) {
            loop.getUpdate().accept(this);
        }
        if(loop.getBody() != null) {
            loop.getBody().accept(this);
        }


        inFor = tempflag;
    }

    @Override
    public void visit(Break breakLoop) {
        if(!inFor)
        {
            System.out.println("Line:" + breakLoop.getLine() + ":" + "break statement not within loop");
        }
    }

    @Override
    public void visit(Continue continueLoop) {
        if(!inFor)
        {
            System.out.println("Line:" + continueLoop.getLine() + ":" + "continue statement not within loop");
        }
    }

    @Override
    public void visit(MsgHandlerCall msgHandlerCall) {

        msgHandlerCall.getInstance().accept(this);

        if(!(msgHandlerCall.getInstance().getType() instanceof NoType)) {
            if (!(msgHandlerCall.getInstance().getType() instanceof ActorType) && !(msgHandlerCall.getInstance().getType() instanceof NoType)) {
                System.out.println("Line:" + msgHandlerCall.getLine() + ":" + "variable " + ((Identifier) msgHandlerCall.getInstance()).getName() + " is not callable");
            } else if (!(msgHandlerCall.getInstance().getType().toString().equals("sender")) && !(msgHandlerCall.getInstance().getType() instanceof NoType) && !doesHandlerExist(msgHandlerCall.getMsgHandlerName(), ((ActorType) (msgHandlerCall.getInstance().getType())).getName())) {
                System.out.println("Line:" + msgHandlerCall.getLine() + ":" + "there is no msghandler name " + msgHandlerCall.getMsgHandlerName().getName() + " in actor " + (msgHandlerCall.getInstance()).getType().toString());
            }
            //msgHandlerCall.getMsgHandlerName().accept(this);
            else if (!(msgHandlerCall.getInstance().getType().toString().equals("sender")) && msgHandlerCall.getArgs() != null) {
                ArrayList<VarDeclaration> args = findArgsOfHandler(msgHandlerCall.getMsgHandlerName(), ((ActorType) (msgHandlerCall.getInstance().getType())).getName());
                if (args != null && args.size() != msgHandlerCall.getArgs().size()) {
                    for (int i = 0; i < msgHandlerCall.getArgs().size(); i++) {
                        msgHandlerCall.getArgs().get(i).accept(this);
                    }
                    System.out.println("Line:" + msgHandlerCall.getLine() + ":" + "arguments do not match with definition");
                } else {

                    for (int i = 0; i < msgHandlerCall.getArgs().size(); i++) {
                        msgHandlerCall.getArgs().get(i).accept(this);
                        if (!isSubType(msgHandlerCall.getArgs().get(i).getType(), args.get(i).getType())) {
                            System.out.println("Line:" + msgHandlerCall.getLine() + ":" + "arguments do not match with definition");
                        } else if (msgHandlerCall.getArgs().get(i).getType() instanceof ArrayType && args.get(i).getType() instanceof ArrayType) {
                            if (((ArrayType) msgHandlerCall.getArgs().get(i).getType()).getSize() != ((ArrayType) args.get(i).getType()).getSize()) {
                                System.out.println("Line:" + msgHandlerCall.getLine() + ":" + "arguments do not match with definition");
                            }
                        }
                    }
                }

            } else if ((msgHandlerCall.getInstance().getType().toString().equals("sender"))) {
                for (int i = 0; i < msgHandlerCall.getArgs().size(); i++) {
                    msgHandlerCall.getArgs().get(i).accept(this);
                }
            }
        }

    }

    @Override
    public void visit(Print print) {
        if (print.getArg() != null) {
            print.getArg().accept(this);
            if (!(print.getArg().getType() instanceof IntType
                    || print.getArg().getType() instanceof StringType
                    || print.getArg().getType() instanceof BooleanType
                    || print.getArg().getType() instanceof NoType)) {
                System.out.println("Line:" + print.getLine() + ":" + "unsupported type for print");
            }
        }
    }

    @Override
    public void visit(Assign assign) {
        visitExpr(assign.getlValue());
        visitExpr(assign.getrValue());

        if(assign.getlValue() instanceof  Value || assign.getlValue() instanceof BinaryExpression || assign.getlValue() instanceof UnaryExpression || assign.getlValue() instanceof Sender)
        {
            System.out.println("Line:" + assign.getLine() + ":" + "left side of assignment must be a valid lvalue");
        }
        if(assign.getlValue().getType() instanceof ArrayType && assign.getrValue().getType() instanceof ArrayType)
        {
            if(((ArrayType) assign.getrValue().getType()).getSize() != ((ArrayType) assign.getlValue().getType()).getSize())
            {
                System.out.println("Line:" + assign.getLine() + ":" + "operation assign requires equal array sizes");
            }
        }
        if(!isSubType(assign.getrValue().getType(), assign.getlValue().getType()))
        {
            System.out.println("Line:" + assign.getLine() + ":" + "unsupported operand type for " + BinaryOperator.assign);
        }

    }
}
