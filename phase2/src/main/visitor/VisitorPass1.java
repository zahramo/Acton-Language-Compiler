package main.visitor;
import main.ast.node.*;
import main.ast.node.Program;
import main.ast.node.declaration.*;
import main.ast.node.declaration.handler.*;
import main.ast.node.declaration.VarDeclaration;
import main.ast.node.expression.*;
import main.ast.node.expression.values.BooleanValue;
import main.ast.node.expression.values.IntValue;
import main.ast.node.expression.values.StringValue;
import main.ast.node.statement.*;
import main.symbolTable.SymbolTable;
import main.symbolTable.SymbolTableActorItem;
import main.symbolTable.SymbolTableHandlerItem;
import main.symbolTable.SymbolTableMainItem;
import main.symbolTable.itemException.ItemAlreadyExistsException;
import main.symbolTable.symbolTableVariableItem.SymbolTableActorVariableItem;
import main.symbolTable.symbolTableVariableItem.SymbolTableHandlerArgumentItem;
import main.symbolTable.symbolTableVariableItem.SymbolTableKnownActorItem;
import main.symbolTable.symbolTableVariableItem.SymbolTableLocalVariableItem;

import java.util.ArrayList;


public class VisitorPass1 implements Visitor {

    private ArrayList<ErrorItem> errors = new ArrayList<>();
    @Override
    public void visit(Program program) {
        SymbolTable.root = new SymbolTable();
        SymbolTable.push(SymbolTable.root);
        if(program.getActors() != null) {
            for (int i = 0; i < program.getActors().size(); i++)
            {
                program.getActors().get(i).accept(this);
            }
        }
        if (program.getMain() != null)
        {
            program.getMain().accept(this);
        }
    }

    @Override
    public void visit(ActorDeclaration actorDeclaration) {

        SymbolTableActorItem actorItem = new SymbolTableActorItem(actorDeclaration);
        try
        {
            SymbolTable.root.put(actorItem);
        }
        catch(ItemAlreadyExistsException e1)
        {
            errors.add(new ErrorItem(actorDeclaration.getLine(),"Redefinition of actor " + actorDeclaration.getName().getName()));
            actorItem = new SymbolTableActorItem(actorDeclaration);
            actorItem.setName("temp_" + actorDeclaration.getName().getName());
            try {
                SymbolTable.top.put(actorItem);
            } catch (ItemAlreadyExistsException e) {

            }
        }
        SymbolTable curSymbolTable = new SymbolTable(SymbolTable.top, actorDeclaration.getName().getName());
        SymbolTable.push(curSymbolTable);
        //?
        actorItem.setActorSymbolTable(SymbolTable.top);

        if (actorDeclaration.getName() != null) {
            actorDeclaration.getName().accept(this);
        }

        if (actorDeclaration.getParentName() != null)
        {
        }
        if (actorDeclaration.getKnownActors() != null) {
            for (int i = 0; i < actorDeclaration.getKnownActors().size(); i++)
            {
                actorDeclaration.getKnownActors().get(i).accept(this);
                try
                {
                    SymbolTableKnownActorItem knownActorItem = new SymbolTableKnownActorItem(actorDeclaration.getKnownActors().get(i));
                    curSymbolTable.put(knownActorItem);
                }
                catch(ItemAlreadyExistsException e2)
                {

//                    Identifier id = new Identifier("temp_" + actorDeclaration.getKnownActors().get(i).getIdentifier().getName());
//                    actorDeclaration.getKnownActors().get(i).setIdentifier(id);
//                    SymbolTableKnownActorItem knownActorItem = new SymbolTableKnownActorItem(actorDeclaration.getKnownActors().get(i));
//                    try {
//                        curSymbolTable.put(knownActorItem);
//                    } catch (ItemAlreadyExistsException e) {
//                        e.printStackTrace();
//                    }

                }

            }
        }

        if (actorDeclaration.getActorVars() != null) {
            for (int i = 0; i < actorDeclaration.getActorVars().size(); i++)
            {
                actorDeclaration.getActorVars().get(i).accept(this);
                try
                {
                    SymbolTableActorVariableItem actorVariableItem = new SymbolTableActorVariableItem(actorDeclaration.getActorVars().get(i));
                    curSymbolTable.put(actorVariableItem);
                }
                catch(ItemAlreadyExistsException e3)
                {

//                    Identifier id = new Identifier("temp_" + actorDeclaration.getActorVars().get(i).getIdentifier().getName());
//                    actorDeclaration.getActorVars().get(i).setIdentifier(id);
//                    SymbolTableActorVariableItem actorVariableItem = new SymbolTableActorVariableItem(actorDeclaration.getActorVars().get(i));
//                    try {
//                        curSymbolTable.put(actorVariableItem);
//                    } catch (ItemAlreadyExistsException e) {
//                        e.printStackTrace();
//                    }
                }
            }
        }
        if(actorDeclaration.getInitHandler() != null)
        {
            try
            {
                SymbolTableHandlerItem actorHandlerItem = new SymbolTableHandlerItem(actorDeclaration.getInitHandler());
                curSymbolTable.put(actorHandlerItem);
            }
            catch(ItemAlreadyExistsException e3)
            {
            }
        }
        if (actorDeclaration.getMsgHandlers() != null) {
            for (int i = 0; i < actorDeclaration.getMsgHandlers().size(); i++)
            {
                actorDeclaration.getMsgHandlers().get(i).accept(this);
                try
                {
                    SymbolTableHandlerItem actorHandlerItem = new SymbolTableHandlerItem(actorDeclaration.getMsgHandlers().get(i));
                    curSymbolTable.put(actorHandlerItem);
                }
                catch(ItemAlreadyExistsException e3)
                {
                }
            }
        }

        SymbolTable.pop();

    }

    @Override
    public void visit(HandlerDeclaration handlerDeclaration) {
        SymbolTableHandlerItem handlerItem = new SymbolTableHandlerItem(handlerDeclaration);
        try
        {
            SymbolTable.top.put(handlerItem);
        }
        catch(ItemAlreadyExistsException e4)
        {
//            handlerItem.setName("temp_" + handlerItem.getName());
////            try {
////                SymbolTable.top.put(handlerItem);
////            } catch (ItemAlreadyExistsException e) {
////                e.printStackTrace();
////            }
        }
        SymbolTable curSymbolTable = new SymbolTable(SymbolTable.top, handlerItem.getName());
        SymbolTable.push(curSymbolTable);
        handlerItem.setHandlerSymbolTable(curSymbolTable);

        handlerDeclaration.getName().accept(this);

        if (handlerDeclaration.getArgs() != null) {
            for (int i = 0; i < handlerDeclaration.getArgs().size(); i++)
            {
                handlerDeclaration.getArgs().get(i).accept(this);
                try
                {
                    SymbolTableHandlerArgumentItem handlerArgItem = new SymbolTableHandlerArgumentItem(handlerDeclaration.getArgs().get(i));
                    curSymbolTable.put(handlerArgItem);
                }
                catch(ItemAlreadyExistsException e5)
                {
//                    Identifier id = new Identifier("temp_" + handlerDeclaration.getArgs().get(i).getIdentifier().getName());
//                    handlerDeclaration.getArgs().get(i).setIdentifier(id);
//                    SymbolTableHandlerArgumentItem handlerArgItem = new SymbolTableHandlerArgumentItem(handlerDeclaration.getArgs().get(i));
//                    try {
//                        curSymbolTable.put(handlerArgItem);
//                    } catch (ItemAlreadyExistsException e) {
//                        e.printStackTrace();
//                    }
                }
            }
        }
        if (handlerDeclaration.getLocalVars() != null) {
            for (int i = 0; i < handlerDeclaration.getLocalVars().size(); i++) {
                handlerDeclaration.getLocalVars().get(i).accept(this);
                try
                {
                    SymbolTableLocalVariableItem localVarItem = new SymbolTableLocalVariableItem(handlerDeclaration.getLocalVars().get(i));
                    curSymbolTable.put(localVarItem);
                }
                catch(ItemAlreadyExistsException e6)
                {
//                    Identifier id = new Identifier("temp_" + handlerDeclaration.getLocalVars().get(i).getIdentifier().getName());
//                    handlerDeclaration.getLocalVars().get(i).setIdentifier(id);
//                    SymbolTableLocalVariableItem localVarItem = new SymbolTableLocalVariableItem(handlerDeclaration.getLocalVars().get(i));
//                    try {
//                        curSymbolTable.put(localVarItem);
//                    } catch (ItemAlreadyExistsException e) {
//                        e.printStackTrace();
//                    }
                }
            }
        }
        if (handlerDeclaration.getBody() != null) {
            for (int i = 0; i < handlerDeclaration.getBody().size(); i++)
            {
                handlerDeclaration.getBody().get(i).accept(this);
            }
        }
        SymbolTable.pop();

    }

    @Override
    public void visit(VarDeclaration varDeclaration) {
        if (varDeclaration.getIdentifier() != null) {
            varDeclaration.getIdentifier().accept(this);
        }
        if (varDeclaration.getType() != null) {
            varDeclaration.getType().accept(this);
        }
    }

    @Override
    public void visit(Main mainActors) {
        SymbolTableMainItem mainItem = new SymbolTableMainItem(mainActors);
        try {
            SymbolTable.root.put(mainItem);
        } catch (ItemAlreadyExistsException e) {

        }
        SymbolTable curSymbolTable = new SymbolTable(SymbolTable.top, "Main");
        SymbolTable.push(curSymbolTable);
        mainItem.setMainSymbolTable(curSymbolTable);

        if (mainActors.getMainActors() != null) {
            //????
            for (int i = 0; i < mainActors.getMainActors().size(); i++)
            {
                SymbolTableLocalVariableItem symbolTableLocalVariableItem = new SymbolTableLocalVariableItem(mainActors.getMainActors().get(i));
                try {
                    SymbolTable.top.put(symbolTableLocalVariableItem);
                }
                catch (ItemAlreadyExistsException e) {
                    errors.add(new ErrorItem(mainActors.getMainActors().get(i).getLine(), "Redefinition of variable " + mainActors.getMainActors().get(i).getIdentifier().getName()));
                }
                mainActors.getMainActors().get(i).accept(this);
            }
        }
        SymbolTable.pop();
    }

    @Override
    public void visit(ActorInstantiation actorInstantiation) {
        if (actorInstantiation.getIdentifier() != null) {
            actorInstantiation.getIdentifier().accept(this);
        }
        if (actorInstantiation.getKnownActors() != null) {
            for (int i = 0; i < actorInstantiation.getKnownActors().size(); i++)
            {
                actorInstantiation.getKnownActors().get(i).accept(this);
            }
        }
        if (actorInstantiation.getInitArgs() != null) {
            for (int i = 0; i < actorInstantiation.getInitArgs().size(); i++)
            {
                actorInstantiation.getInitArgs().get(i).accept(this);
            }
        }

    }

    @Override
    public void visit(UnaryExpression unaryExpression) {
        if (unaryExpression.getOperand() != null) {
            unaryExpression.getOperand().accept(this);
        }

    }

    @Override
    public void visit(BinaryExpression binaryExpression) {
        if (binaryExpression.getLeft() != null) {
            binaryExpression.getLeft().accept(this);
        }
        if (binaryExpression.getRight() != null) {
            binaryExpression.getRight().accept(this);
        }
    }

    @Override
    public void visit(ArrayCall arrayCall) {
        if (arrayCall.getArrayInstance() != null) {
            arrayCall.getArrayInstance().accept(this);
        }
        if ( arrayCall.getIndex() != null) {
            arrayCall.getIndex().accept(this);
        }
    }

    @Override
    public void visit(ActorVarAccess actorVarAccess) {
        if (actorVarAccess.getSelf() != null) {
            actorVarAccess.getSelf().accept(this);
        }
        if (actorVarAccess.getVariable() != null) {
            actorVarAccess.getVariable().accept(this);
        }

    }

    @Override
    public void visit(Identifier identifier) {
    }

    @Override
    public void visit(Self self) {
    }

    @Override
    public void visit(Sender sender) {
    }

    @Override
    public void visit(BooleanValue value) {
    }

    @Override
    public void visit(IntValue value) {
    }

    @Override
    public void visit(StringValue value) {
    }

    @Override
    public void visit(Block block) {
        if (block.getStatements() != null) {
            for (int i = 0; i < block.getStatements().size(); i++)
            {
                block.getStatements().get(i).accept(this);
            }
        }

    }

    @Override
    public void visit(Conditional conditional) {
        if (conditional.getExpression() != null)
        {
            conditional.getExpression().accept(this);
        }

        if (conditional.getThenBody() != null)
        {
            conditional.getThenBody().accept(this);
        }

        if (conditional.getElseBody() != null)
        {
            conditional.getElseBody().accept(this);
        }
    }

    @Override
    public void visit(For loop) {
        if(loop.getInitialize() != null)
        {
            loop.getInitialize().accept(this);
        }
        if(loop.getCondition() != null)
        {
            loop.getCondition().accept(this);
        }
        if(loop.getUpdate() != null)
        {
            loop.getUpdate().accept(this);
        }
        if(loop.getBody() != null)
        {
            loop.getBody().accept(this);
        }
    }

    @Override
    public void visit(Break breakLoop) {
    }

    @Override
    public void visit(Continue continueLoop) {
    }

    @Override
    public void visit(MsgHandlerCall msgHandlerCall) {
        msgHandlerCall.getInstance().accept(this);
        msgHandlerCall.getMsgHandlerName().accept(this);
        if (msgHandlerCall.getArgs() != null) {
            for (int i = 0; i < msgHandlerCall.getArgs().size(); i++)
            {
                msgHandlerCall.getArgs().get(i).accept(this);
            }
        }
    }

    @Override
    public void visit(Print print) {
        if(print.getArg() != null)
        {
            print.getArg().accept(this);
        }
    }

    @Override
    public void visit(Assign assign) {
        if (assign.getlValue() != null && assign.getrValue() != null)
        {
            assign.getlValue().accept(this);
            assign.getrValue().accept(this);
        }
    }

    public ArrayList<ErrorItem> getErrors() {
        return errors;
    }
}
