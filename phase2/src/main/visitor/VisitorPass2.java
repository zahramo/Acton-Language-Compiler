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
import main.ast.type.arrayType.ArrayType;
import main.symbolTable.SymbolTable;
import main.symbolTable.SymbolTableActorItem;
import main.symbolTable.SymbolTableHandlerItem;
import main.symbolTable.SymbolTableItem;
import main.symbolTable.itemException.ItemAlreadyExistsException;
import main.symbolTable.itemException.ItemNotFoundException;
import main.symbolTable.symbolTableVariableItem.SymbolTableHandlerArgumentItem;
import main.symbolTable.symbolTableVariableItem.SymbolTableKnownActorItem;
import main.symbolTable.symbolTableVariableItem.SymbolTableLocalVariableItem;
import main.symbolTable.symbolTableVariableItem.SymbolTableVariableItem;

import java.util.ArrayList;
import java.util.SimpleTimeZone;


public class VisitorPass2 implements Visitor {

    private ArrayList<ErrorItem> errors = new ArrayList<>();
    private ArrayList<String> cycle = new ArrayList<>();
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
            program.getMain().accept(this);
        }
    }

    public void checkWithParentsForVariables(VarDeclaration v, SymbolTableActorItem actorItem, String actorName)  {

        SymbolTable curSymbolTable = SymbolTable.top;
        SymbolTableActorItem tempActorItem = null;
        try
        {
            tempActorItem = (SymbolTableActorItem) SymbolTable.root.get("Actor_" + actorItem.getParentName());

        }
        catch(ItemNotFoundException e){}

        int findInChild = 0;

        try
        {
            SymbolTableVariableItem varItem = new SymbolTableVariableItem(v);
            curSymbolTable.put(varItem);
        }
        catch(ItemAlreadyExistsException e3)
        {

            errors.add(new ErrorItem(v.getLine(), "Redefinition of variable " + v.getIdentifier().getName()));
            while(true) {

                findInChild = 1;


                Identifier id = new Identifier("temp_" + v.getIdentifier().getName());
                v.setIdentifier(id);
                SymbolTableVariableItem varItem = new SymbolTableVariableItem(v);
                try {
                    curSymbolTable.put(varItem);
                    break;
                } catch (ItemAlreadyExistsException e1) {
                    continue;
                }
            }
        }
        int findInParent = 1;
        if(findInChild == 0) {
            while (tempActorItem != null && (tempActorItem.getActorDeclaration().getName() != actorItem.getActorDeclaration().getName())) {
                SymbolTable parentTable = tempActorItem.getActorSymbolTable();
                try {
                    parentTable.get("Variable_" + v.getIdentifier().getName());
                    findInParent = 1;
                } catch (ItemNotFoundException e) {
                    findInParent = 0;
                }

                if (findInParent == 1) {
                    errors.add(new ErrorItem(v.getLine(), "Redefinition of variable " + v.getIdentifier().getName()));
                    while(true) {

                        Identifier id = new Identifier("temp_" + v.getIdentifier().getName());
                        v.setIdentifier(id);
                        SymbolTableVariableItem varItem = new SymbolTableVariableItem(v);

                        try {
                            curSymbolTable.put(varItem);
                            return;
                        } catch (ItemAlreadyExistsException e1) {
                            continue;
                        }
                    }

                }

                try {
                    tempActorItem = (SymbolTableActorItem) SymbolTable.root.get("Actor_" + tempActorItem.getParentName());
                } catch (ItemNotFoundException e) {
                    break;
                }
            }
        }


        actorItem.setActorSymbolTable(curSymbolTable);
    }


    public void checkWithParentsForKnownActors(VarDeclaration v, SymbolTableActorItem actorItem, String actorName)  {

        SymbolTable curSymbolTable = SymbolTable.top;
        SymbolTableActorItem tempActorItem = null;
        try
        {
            tempActorItem = (SymbolTableActorItem) SymbolTable.root.get("Actor_" + actorItem.getParentName());

        }
        catch(ItemNotFoundException e){}

        int findInChild = 0;

        try
        {
            SymbolTableKnownActorItem knownActorItem = new SymbolTableKnownActorItem(v);
            curSymbolTable.put(knownActorItem);
        }
        catch(ItemAlreadyExistsException e3)
        {

            errors.add(new ErrorItem(v.getLine(), "Redefinition of variable " + v.getIdentifier().getName()));
            while(true) {

                findInChild = 1;


                Identifier id = new Identifier("temp_" + v.getIdentifier().getName());
                v.setIdentifier(id);
                SymbolTableKnownActorItem knownActorItem = new SymbolTableKnownActorItem(v);
                try {
                    curSymbolTable.put(knownActorItem);
                    break;
                } catch (ItemAlreadyExistsException e1) {
                    continue;
                }
            }
        }
        int findInParent = 1;
        if(findInChild == 0) {
            while (tempActorItem != null && (tempActorItem.getActorDeclaration().getName() != actorItem.getActorDeclaration().getName())) {
                SymbolTable parentTable = tempActorItem.getActorSymbolTable();
                try {
                    parentTable.get("Variable_" + v.getIdentifier().getName());
                    findInParent = 1;
                } catch (ItemNotFoundException e) {
                    findInParent = 0;
                }

                if (findInParent == 1) {
                    errors.add(new ErrorItem(v.getLine(), "Redefinition of variable " + v.getIdentifier().getName()));
                    while(true) {

                        Identifier id = new Identifier("temp_" + v.getIdentifier().getName());
                        v.setIdentifier(id);
                        SymbolTableKnownActorItem knownActorItem = new SymbolTableKnownActorItem(v);

                        try {
                            curSymbolTable.put(knownActorItem);
                            return;
                        } catch (ItemAlreadyExistsException e1) {
                            continue;
                        }
                    }

                }

                try {
                    tempActorItem = (SymbolTableActorItem) SymbolTable.root.get("Actor_" + tempActorItem.getParentName());
                } catch (ItemNotFoundException e) {
                    break;
                }
            }
        }


    }

    public void checkWithParentsForHandler(HandlerDeclaration v)  {

        SymbolTable curSymbolTable = SymbolTable.top;
        SymbolTableActorItem tempActorItem = null;
        try
        {

            String parentName = ((SymbolTableActorItem)(SymbolTable.root.get("Actor_" + curSymbolTable.getName()))).getParentName();

            tempActorItem = ((SymbolTableActorItem)(SymbolTable.root.get("Actor_" + parentName)));
        }
        catch(ItemNotFoundException e){}
        if(tempActorItem == null) {
            return;
        }

        int findInParent = 1;
        while (tempActorItem != null && !(tempActorItem.getActorDeclaration().getName().getName().equals(SymbolTable.top.getName()))) {
            SymbolTable parentTable = tempActorItem.getActorSymbolTable();
            try {
                parentTable.get("Handler_" + v.getName().getName());
                findInParent = 1;
            } catch (ItemNotFoundException e) {
                findInParent = 0;
            }

            if (findInParent == 1) {
                errors.add(new ErrorItem(v.getLine(), "Redefinition of msghandler " + v.getName().getName()));
                while(true) {

                    Identifier id = new Identifier("temp_" + v.getName().getName());
                    v.setName(id);
                    SymbolTableHandlerItem handlerItem = new SymbolTableHandlerItem(v);

                    try {
                        curSymbolTable.put(handlerItem);
                        return;
                    } catch (ItemAlreadyExistsException e1) {
                        continue;
                    }
                }

            }

            try {
                tempActorItem = (SymbolTableActorItem) SymbolTable.root.get("Actor_" + tempActorItem.getParentName());
            } catch (ItemNotFoundException e) {
                break;
            }
        }

    }

    public void checkWithParentsForArg(VarDeclaration v, SymbolTableHandlerItem handlerItem, String actorName)  {

        SymbolTable curSymbolTable = SymbolTable.top;
        SymbolTableActorItem tempActorItem = null;

        int findInChild = 0;


        try
        {
            SymbolTableHandlerArgumentItem argItem = new SymbolTableHandlerArgumentItem(v);
            curSymbolTable.put(argItem);
        }
        catch(ItemAlreadyExistsException e3)
        {

            errors.add(new ErrorItem(v.getLine(), "Redefinition of variable " + v.getIdentifier().getName()));
            while(true) {

                findInChild = 1;


                Identifier id = new Identifier("temp_" + v.getIdentifier().getName());
                v.setIdentifier(id);
                SymbolTableHandlerArgumentItem argItem = new SymbolTableHandlerArgumentItem(v);
                try {
                    curSymbolTable.put(argItem);
                    break;
                } catch (ItemAlreadyExistsException e1) {
                    continue;
                }
            }
        }
        handlerItem.setHandlerSymbolTable(curSymbolTable);
    }

    public void checkWithParentsForLocalVars(VarDeclaration v, SymbolTableHandlerItem handlerItem, String actorName)  {

        SymbolTable curSymbolTable = SymbolTable.top;
        SymbolTableActorItem tempActorItem = null;

        int findInChild = 0;


        try
        {
            SymbolTableLocalVariableItem varItem = new SymbolTableLocalVariableItem(v);
            curSymbolTable.put(varItem);
        }
        catch(ItemAlreadyExistsException e3)
        {

            errors.add(new ErrorItem(v.getLine(), "Redefinition of variable " + v.getIdentifier().getName()));
            while(true) {

                findInChild = 1;


                Identifier id = new Identifier("temp_" + v.getIdentifier().getName());
                v.setIdentifier(id);
                SymbolTableLocalVariableItem varItem = new SymbolTableLocalVariableItem(v);
                try {
                    curSymbolTable.put(varItem);
                    break;
                } catch (ItemAlreadyExistsException e1) {
                    continue;
                }
            }
        }
        handlerItem.setHandlerSymbolTable(curSymbolTable);
    }


    @Override
    public void visit(ActorDeclaration actorDeclaration) throws ItemNotFoundException {
        if (actorDeclaration.getName() != null) {
            actorDeclaration.getName().accept(this);
        }
        SymbolTableActorItem actorItem = null;
        try {
            actorItem = (SymbolTableActorItem) SymbolTable.root.get("Actor_" + actorDeclaration.getName().getName());
        } catch (ItemNotFoundException e) {}
        SymbolTable curSymbolTable = new SymbolTable(SymbolTable.top,actorDeclaration.getName().getName());
        SymbolTable.push(curSymbolTable);
        actorItem.setActorSymbolTable(curSymbolTable);

        ArrayList<String> cycleTemp = new ArrayList<>();
        SymbolTableActorItem tempActorItem = actorItem;


        boolean isCycle = false;

        while (tempActorItem != null)
        {

            if(this.cycle.contains(tempActorItem.getActorDeclaration().getName().getName()))
            {
                break;
            }
            if(cycleTemp.contains(tempActorItem.getActorDeclaration().getName().getName()))
            {
                errors.add(new ErrorItem(tempActorItem.getActorDeclaration().getLine(),"Cyclic inheritance involving actor "+ tempActorItem.getActorDeclaration().getName().getName()));
                isCycle = true;
                break;
            }
            else
            {
                cycleTemp.add(tempActorItem.getActorDeclaration().getName().getName());
            }
            try {
                tempActorItem = (SymbolTableActorItem) SymbolTable.root.get("Actor_" + tempActorItem.getParentName());
            }catch(ItemNotFoundException e){break;}
        }

        if(isCycle)
        {
            for(int i=0; i<cycleTemp.size(); i++)
            {
                this.cycle.add(cycleTemp.get(i));
            }
        }

        cycleTemp.clear();

        if (actorDeclaration.getKnownActors() != null) {


            for (int i = 0; i < actorDeclaration.getKnownActors().size(); i++)
            {
                actorDeclaration.getKnownActors().get(i).accept(this);
                checkWithParentsForKnownActors(actorDeclaration.getKnownActors().get(i), actorItem, actorDeclaration.getName().getName());
            }
        }

        if (actorDeclaration.getActorVars() != null) {

            for (int i = 0; i < actorDeclaration.getActorVars().size(); i++)
            {

                checkWithParentsForVariables(actorDeclaration.getActorVars().get(i), actorItem, actorDeclaration.getName().getName());
                actorDeclaration.getActorVars().get(i).accept(this);
            }
        }
        if(actorDeclaration.getInitHandler() != null)
        {
            actorDeclaration.getInitHandler().accept(this);
        }
        if (actorDeclaration.getMsgHandlers() != null) {
            for (int i = 0; i < actorDeclaration.getMsgHandlers().size(); i++)
            {
                actorDeclaration.getMsgHandlers().get(i).accept(this);
            }
        }

        if(actorDeclaration.getQueueSize() <= 0)
        {
            errors.add(new ErrorItem(actorDeclaration.getLine(), "Queue size must be positive"));
        }
        SymbolTable.pop();
    }

    @Override
    public void visit(HandlerDeclaration handlerDeclaration) {

        handlerDeclaration.getName().accept(this);
        boolean flagMsg = true;
        SymbolTableHandlerItem handlerItem = new SymbolTableHandlerItem(handlerDeclaration);
        try {
            SymbolTable.top.put(handlerItem);
        }catch (ItemAlreadyExistsException e) {

            errors.add(new ErrorItem(handlerDeclaration.getLine(), "Redefinition of msghandler " + handlerDeclaration.getName().getName()));
            while(true) {

                Identifier id = new Identifier("temp_" + handlerDeclaration.getName().getName());
                handlerDeclaration.setName(id);
                SymbolTableHandlerItem handlerItemTemp = new SymbolTableHandlerItem(handlerDeclaration);
                try {
                    SymbolTable.top.put(handlerItemTemp);
                    break;
                } catch (ItemAlreadyExistsException e1) {
                    continue;
                }
            }
            flagMsg = false;
        }
        if (flagMsg) {
            if(handlerDeclaration.getName().getName() != "initial")
                checkWithParentsForHandler(handlerDeclaration);
        }

        SymbolTable curSymbolTable = new SymbolTable(SymbolTable.top,handlerDeclaration.getName().getName());
        SymbolTable.push(curSymbolTable);
        handlerItem.setHandlerSymbolTable(SymbolTable.top);

        if (handlerDeclaration.getArgs() != null) {
            for (int i = 0; i < handlerDeclaration.getArgs().size(); i++)
            {

                checkWithParentsForArg(handlerDeclaration.getArgs().get(i), handlerItem,  handlerDeclaration.getName().getName());
                handlerDeclaration.getArgs().get(i).accept(this);
            }
        }

        if (handlerDeclaration.getLocalVars() != null) {
            for (int i = 0; i < handlerDeclaration.getLocalVars().size(); i++) {

                checkWithParentsForLocalVars(handlerDeclaration.getLocalVars().get(i), handlerItem,  handlerDeclaration.getName().getName());
                handlerDeclaration.getLocalVars().get(i).accept(this);
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
            if (varDeclaration.getType() instanceof ArrayType)
            {
                if(((ArrayType) varDeclaration.getType()).getSize() <= 0)
                {
                    errors.add(new ErrorItem(varDeclaration.getLine(), "Array size must be positive"));
                }
            }
        }
    }

    @Override
    public void visit(Main mainActors) {
        if (mainActors.getMainActors() != null) {
            for (int i = 0; i < mainActors.getMainActors().size(); i++)
            {
                mainActors.getMainActors().get(i).accept(this);
            }
        }
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
