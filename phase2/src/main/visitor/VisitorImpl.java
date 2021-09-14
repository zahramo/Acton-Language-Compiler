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

public class VisitorImpl implements Visitor {

    @Override
    public void visit(Program program) {
        System.out.println(program.toString());
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
        System.out.println(actorDeclaration.toString());
        if (actorDeclaration.getName() != null) {
            actorDeclaration.getName().accept(this);
        }
        
        if (actorDeclaration.getParentName() != null)
        {
            System.out.println(actorDeclaration.getParentName().toString());
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
            actorDeclaration.getInitHandler().accept(this);
        }
        if (actorDeclaration.getMsgHandlers() != null) {
            for (int i = 0; i < actorDeclaration.getMsgHandlers().size(); i++)
            {
                actorDeclaration.getMsgHandlers().get(i).accept(this);
            }
        }

    }

    @Override
    public void visit(HandlerDeclaration handlerDeclaration) {
        System.out.println(handlerDeclaration.toString());

        handlerDeclaration.getName().accept(this);

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
                handlerDeclaration.getBody().get(i).accept(this);
            }
        }

    }

    @Override
    public void visit(VarDeclaration varDeclaration) {
        System.out.println(varDeclaration.toString());
        if (varDeclaration.getIdentifier() != null) {
            varDeclaration.getIdentifier().accept(this);
        }
        if (varDeclaration.getType() != null) {
            varDeclaration.getType().accept(this);
        }
    }

    @Override
    public void visit(Main mainActors) {
        System.out.println(mainActors.toString());
        if (mainActors.getMainActors() != null) {
            for (int i = 0; i < mainActors.getMainActors().size(); i++)
            {
                mainActors.getMainActors().get(i).accept(this);
            }
        }
    }

    @Override
    public void visit(ActorInstantiation actorInstantiation) {
        System.out.println(actorInstantiation.toString());
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
        System.out.println(unaryExpression.toString());
        if (unaryExpression.getOperand() != null) {
            unaryExpression.getOperand().accept(this);
        }

    }

    @Override
    public void visit(BinaryExpression binaryExpression) {
        System.out.println(binaryExpression.toString());
        if (binaryExpression.getLeft() != null) {
            binaryExpression.getLeft().accept(this);
        }
        if (binaryExpression.getRight() != null) {
            binaryExpression.getRight().accept(this);
        }
    }

    @Override
    public void visit(ArrayCall arrayCall) {
        System.out.println(arrayCall.toString());
        if (arrayCall.getArrayInstance() != null) {
            arrayCall.getArrayInstance().accept(this);
        }
        if ( arrayCall.getIndex() != null) {
            arrayCall.getIndex().accept(this);
        }
    }

    @Override
    public void visit(ActorVarAccess actorVarAccess) {
        System.out.println(actorVarAccess.toString());
        if (actorVarAccess.getSelf() != null) {
            actorVarAccess.getSelf().accept(this);
        }
        if (actorVarAccess.getVariable() != null) {
            actorVarAccess.getVariable().accept(this);
        }

    }

    @Override
    public void visit(Identifier identifier) {
        System.out.println(identifier.toString());
    }

    @Override
    public void visit(Self self) {
        System.out.println(self.toString());
    }

    @Override
    public void visit(Sender sender) {
        System.out.println(sender.toString());
    }

    @Override
    public void visit(BooleanValue value) {
        System.out.println(value.toString());
    }

    @Override
    public void visit(IntValue value) {
        System.out.println(value.toString());
    }

    @Override
    public void visit(StringValue value) {
        System.out.println(value.toString());
    }

    @Override
    public void visit(Block block) {
        System.out.println(block.toString());
        if (block.getStatements() != null) {
            for (int i = 0; i < block.getStatements().size(); i++)
            {
                block.getStatements().get(i).accept(this);
            }
        }

    }

    @Override
    public void visit(Conditional conditional) {
        System.out.println(conditional.toString());
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
        System.out.println(loop.toString());
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
        System.out.println(breakLoop.toString());
    }

    @Override
    public void visit(Continue continueLoop) {
        System.out.println(continueLoop.toString());
    }

    @Override
    public void visit(MsgHandlerCall msgHandlerCall) {
        System.out.println(msgHandlerCall.toString());
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
        System.out.println(print.toString());
        if(print.getArg() != null)
        {
            print.getArg().accept(this);
        }
    }

    @Override
    public void visit(Assign assign) {
        System.out.println(assign.toString());
        if (assign.getlValue() != null && assign.getrValue() != null)
        {
            assign.getlValue().accept(this);
            assign.getrValue().accept(this);
        }
    }
}
