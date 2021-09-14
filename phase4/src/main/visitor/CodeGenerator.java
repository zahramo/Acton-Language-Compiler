package main.visitor;

import main.ast.node.Main;
import main.ast.node.Program;
import main.ast.node.declaration.ActorDeclaration;
import main.ast.node.declaration.ActorInstantiation;
import main.ast.node.declaration.VarDeclaration;
import main.ast.node.declaration.handler.HandlerDeclaration;
import main.ast.node.expression.*;
import main.ast.node.expression.values.BooleanValue;
import main.ast.node.expression.values.IntValue;
import main.ast.node.expression.values.StringValue;
import main.ast.node.statement.*;
import main.ast.type.Type;
import main.ast.type.actorType.ActorType;
import main.ast.type.arrayType.ArrayType;
import main.ast.type.noType.NoType;
import main.ast.type.primitiveType.BooleanType;
import main.ast.type.primitiveType.IntType;
import main.ast.type.primitiveType.StringType;
import main.symbolTable.SymbolTable;
import main.symbolTable.SymbolTableActorItem;
import main.symbolTable.SymbolTableHandlerItem;
import main.symbolTable.itemException.ItemNotFoundException;
import main.symbolTable.symbolTableVariableItem.*;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLOutput;
import java.util.ArrayList;

public class CodeGenerator implements Visitor {

    FileWriter curActorFileData;
    FileWriter curHandlerFileData;
    FileWriter mainFileData;
    FileWriter defaultActorFileData;
    FileWriter actorFileData;
    FileWriter messageFileData;

    String curActorName;
    String curHandlerName;

    SymbolTable curActorSymbolTable;
    SymbolTable curHandlerSymbolTable;

    Boolean inInitial = false;
    Boolean isLastActor = false;
    Boolean inMain = false;
    Boolean inInc = false;

    ArrayList<String> curHandlerLocalVars = new ArrayList<>();
    ArrayList<VarDeclaration> curActorKnownActors = new ArrayList<>();

    String forStartLabel = "m";
    String forEndLabel;

    int labelInt = 0;

    private String findTypeCode(Type t) {
        String code = "";
        if (t instanceof IntType) {
            code = "I";
        } else if (t instanceof BooleanType) {
            code = "Z";
        } else if (t instanceof StringType) {
            code = "Ljava/lang/String;";
        } else if (t instanceof ArrayType) {
            code = "[I";
        }
        return code;

    }

    private String getKnownActorType(String name) {
        if (curActorKnownActors != null) {
            for (int i = 0; i < curActorKnownActors.size(); i++) {
                if (curActorKnownActors.get(i).getIdentifier().getName().equals(name)) {
                    return ((ActorType) curActorKnownActors.get(i).getType()).getName().getName();
                }
            }

        }
        return "Actor";
    }

    private boolean isLocal(String id) {
        SymbolTableVariableItem varItem = null;
        try {
            varItem = (SymbolTableVariableItem) curHandlerSymbolTable.get("Variable_" + id);
        } catch (ItemNotFoundException e) {
        }

        if (varItem instanceof SymbolTableLocalVariableItem || varItem instanceof SymbolTableHandlerArgumentItem) {
            return true;
        } else if (varItem instanceof SymbolTableActorVariableItem) {
            return false;
        }
        return false;
    }

    private int indexOf(String varName) {
        for (int i = 0; i < curHandlerLocalVars.size(); i++) {
            if (curHandlerLocalVars.get(i).equals(varName)) {
                if (inInitial) {
                    return i + 1;
                }
                return i + 2;
            }
        }
        return -1;
    }


    private boolean doesHandlerExist(Identifier handlerName, Identifier actorName) {
        SymbolTable curActorSymbolTable = null;
        try {
            curActorSymbolTable = ((SymbolTableActorItem) SymbolTable.root.get("Actor_" + actorName.getName())).getActorSymbolTable();

        } catch (ItemNotFoundException e) {
            return false;
        }

        SymbolTableHandlerItem tempHandlerItem = null;

        try {
            tempHandlerItem = (SymbolTableHandlerItem) curActorSymbolTable.get("Handler_" + handlerName.getName());

        } catch (ItemNotFoundException e) {
            return false;
        }
        return true;
    }

    private int findActorIndex(ArrayList<ActorInstantiation> actors, String name) {
        for (int i = 0; i < actors.size(); i++) {
            if (actors.get(i).getIdentifier().getName().equals(name)) {
                return i;
            }
        }
        return -1;
    }

    private ActorDeclaration findActor(String actorName) {
        SymbolTableActorItem tempActorItem = null;
        try {
            tempActorItem = (SymbolTableActorItem) SymbolTable.root.get("Actor_" + actorName);

        } catch (ItemNotFoundException e) {
            return null;
        }
        return tempActorItem.getActorDeclaration();
    }

    @Override
    public void visit(Program program) {

        try {
            actorFileData = new FileWriter("Actor.j");
            actorFileData.write(".class public Actor\n" +
                    ".super DefaultActor\n" +
                    "\n" +
                    ".field private queue Ljava/util/ArrayList;\n" +
                    ".signature \"Ljava/util/ArrayList<LMessage;>;\"\n" +
                    ".end field\n" +
                    ".field private lock Ljava/util/concurrent/locks/ReentrantLock;\n" +
                    ".end field\n" +
                    ".field queueSize I\n" +
                    ".end field\n" +
                    "\n" +
                    ".method public <init>(I)V\n" +
                    ".limit stack 3\n" +
                    ".limit locals 2\n" +
                    "aload_0\n" +
                    "invokespecial DefaultActor/<init>()V\n" +
                    "aload_0\n" +
                    "new java/util/ArrayList\n" +
                    "dup\n" +
                    "invokespecial java/util/ArrayList/<init>()V\n" +
                    "putfield Actor/queue Ljava/util/ArrayList;\n" +
                    "aload_0\n" +
                    "new java/util/concurrent/locks/ReentrantLock\n" +
                    "dup\n" +
                    "invokespecial java/util/concurrent/locks/ReentrantLock/<init>()V\n" +
                    "putfield Actor/lock Ljava/util/concurrent/locks/ReentrantLock;\n" +
                    "aload_0\n" +
                    "iload_1\n" +
                    "putfield Actor/queueSize I\n" +
                    "return\n" +
                    ".end method\n" +
                    "\n" +
                    ".method public run()V\n" +
                    ".limit stack 2\n" +
                    ".limit locals 2\n" +
                    "Label0:\n" +
                    "aconst_null\n" +
                    "astore_1\n" +
                    "aload_0\n" +
                    "getfield Actor/lock Ljava/util/concurrent/locks/ReentrantLock;\n" +
                    "invokevirtual java/util/concurrent/locks/ReentrantLock/lock()V\n" +
                    "aload_0\n" +
                    "getfield Actor/queue Ljava/util/ArrayList;\n" +
                    "invokevirtual java/util/ArrayList/isEmpty()Z\n" +
                    "ifne Label31\n" +
                    "aload_0\n" +
                    "getfield Actor/queue Ljava/util/ArrayList;\n" +
                    "iconst_0\n" +
                    "invokevirtual java/util/ArrayList/remove(I)Ljava/lang/Object;\n" +
                    "checkcast Message\n" +
                    "astore_1\n" +
                    "Label31:\n" +
                    "aload_0\n" +
                    "getfield Actor/lock Ljava/util/concurrent/locks/ReentrantLock;\n" +
                    "invokevirtual java/util/concurrent/locks/ReentrantLock/unlock()V\n" +
                    "aload_1\n" +
                    "ifnull Label46\n" +
                    "aload_1\n" +
                    "invokevirtual Message/execute()V\n" +
                    "Label46:\n" +
                    "goto Label0\n" +
                    ".end method\n" +
                    "\n" +
                    ".method public send(LMessage;)V\n" +
                    ".limit stack 2\n" +
                    ".limit locals 2\n" +
                    "aload_0\n" +
                    "getfield Actor/lock Ljava/util/concurrent/locks/ReentrantLock;\n" +
                    "invokevirtual java/util/concurrent/locks/ReentrantLock/lock()V\n" +
                    "aload_0\n" +
                    "getfield Actor/queue Ljava/util/ArrayList;\n" +
                    "invokevirtual java/util/ArrayList/size()I\n" +
                    "aload_0\n" +
                    "getfield Actor/queueSize I\n" +
                    "if_icmpge Label30\n" +
                    "aload_0\n" +
                    "getfield Actor/queue Ljava/util/ArrayList;\n" +
                    "aload_1\n" +
                    "invokevirtual java/util/ArrayList/add(Ljava/lang/Object;)Z\n" +
                    "pop\n" +
                    "Label30:\n" +
                    "aload_0\n" +
                    "getfield Actor/lock Ljava/util/concurrent/locks/ReentrantLock;\n" +
                    "invokevirtual java/util/concurrent/locks/ReentrantLock/unlock()V\n" +
                    "return\n" +
                    ".end method\n" +
                    "\n" +
                    "\n");
            messageFileData = new FileWriter("Message.j");
            messageFileData.write(".class public abstract Message\n" +
                    ".super java/lang/Object\n" +
                    "\n" +
                    ".method public <init>()V\n" +
                    ".limit stack 16\n" +
                    ".limit locals 16\n" +
                    "0: aload_0\n" +
                    "1: invokespecial java/lang/Object/<init>()V\n" +
                    "4: return\n" +
                    ".end method\n" +
                    "\n" +
                    ".method public abstract execute()V\n" +
                    ".end method");
            actorFileData.flush();
            actorFileData.close();
            messageFileData.flush();
            messageFileData.close();
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }

        if (program.getActors() != null) {

            try {
                defaultActorFileData = new FileWriter("DefaultActor.j");
                defaultActorFileData.write(".class public DefaultActor\n" +
                        ".super java/lang/Thread\n" +
                        "\n" +
                        ".method public <init>()V\n" +
                        ".limit stack 16\n" +
                        ".limit locals 16\n" +
                        "aload_0\n" +
                        "invokespecial java/lang/Thread/<init>()V\n" +
                        "return\n" +
                        ".end method\n\n");

            } catch (IOException e) {
                System.err.println("Error: " + e.getMessage());
            }
            for (int i = 0; i < program.getActors().size(); i++) {
                if (i == program.getActors().size() - 1) {
                    isLastActor = true;
                }
                program.getActors().get(i).accept(this);

            }
            try {
                defaultActorFileData.flush();
                defaultActorFileData.close();
            } catch (IOException e) {
                System.err.println("Error: " + e.getMessage());
            }
        }
        if (program.getMain() != null) {
            inMain = true;
            program.getMain().accept(this);
            inMain = false;
        }
    }

    @Override
    public void visit(ActorDeclaration actorDeclaration) {

        if (actorDeclaration.getName() != null) {
            curActorName = actorDeclaration.getName().getName();
            try {
                curActorSymbolTable = ((SymbolTableActorItem) SymbolTable.root.get("Actor_" + curActorName)).getActorSymbolTable();

            } catch (ItemNotFoundException e) {
            }
            try {
                curActorFileData = new FileWriter(actorDeclaration.getName().getName() + ".j");
                curActorFileData.write(".class public " + actorDeclaration.getName().getName() +
                        "\n" + ".super Actor\n\n");

            } catch (IOException e) {
                System.err.println("Error: " + e.getMessage());
            }
        }
        if (actorDeclaration.getKnownActors() != null) {
            for (int i = 0; i < actorDeclaration.getKnownActors().size(); i++) {
                curActorKnownActors.add(actorDeclaration.getKnownActors().get(i));
                try {
                    curActorFileData.write(".field " + actorDeclaration.getKnownActors().get(i).getIdentifier().getName() +
                            " L" + actorDeclaration.getKnownActors().get(i).getType().toString() + ';' + '\n');

                } catch (IOException e) {
                    System.err.println("Error: " + e.getMessage());
                }
            }
        }

        if (actorDeclaration.getActorVars() != null) {
            for (int i = 0; i < actorDeclaration.getActorVars().size(); i++) {

                try {
                    curActorFileData.write(".field " + actorDeclaration.getActorVars().get(i).getIdentifier().getName() +
                            ' ' + findTypeCode(actorDeclaration.getActorVars().get(i).getType()) + '\n');
                } catch (IOException e) {
                    System.err.println("Error: " + e.getMessage());
                }
            }
        }


        try {
            curActorFileData.write("\n.method public <init>(I)V\n" +
                    ".limit stack 16\n" +
                    ".limit locals 16\n");

//            for (int i = 0; i < actorDeclaration.getActorVars().size(); i++)
//            {
//                String varName = actorDeclaration.getActorVars().get(i).getIdentifier().getName();
//                String varType = findTypeCode(actorDeclaration.getActorVars().get(i).getType());
//                curActorFileData.write("aload_0\nldc 0\nputfield "+ actorDeclaration.getName().getName() + '/' + varName + ' ' + varType + '\n');
//            }

            for (int i = 0; i < actorDeclaration.getActorVars().size(); i++) {
                String varName = actorDeclaration.getActorVars().get(i).getIdentifier().getName();
                String varType = findTypeCode(actorDeclaration.getActorVars().get(i).getType());
                if (varType.equals("I") || varType.equals("Z")) {
                    curActorFileData.write("aload_0\nldc 0\nputfield " + actorDeclaration.getName().getName() + '/' + varName + ' ' + varType + '\n');
                } else if (varType.equals("[I")) {
                    curActorFileData.write("aload_0\nldc " + String.valueOf(((ArrayType) actorDeclaration.getActorVars().get(i).getType()).getSize()) + '\n' + "newarray int\nputfield " + actorDeclaration.getName().getName() + '/' + varName + ' ' + varType + '\n');
                } else {
                    curActorFileData.write("aload_0\nldc \"\"\nputfield " + actorDeclaration.getName().getName() + '/' + varName + ' ' + varType + '\n');
                }

            }


            curActorFileData.write("aload_0\n" +
                    "iload_1\n" +
                    "invokespecial Actor/<init>(I)V\n" +
                    "return\n" +
                    ".end method\n\n");


        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }

        if (actorDeclaration.getInitHandler() != null) {
            inInitial = true;
            actorDeclaration.getInitHandler().accept(this);
            inInitial = false;
        }

        try {
            curActorFileData.write(".method public setKnownActors" + '(');
            for (int i = 0; i < actorDeclaration.getKnownActors().size(); i++) {
                //check for more args
                curActorFileData.write('L' + actorDeclaration.getKnownActors().get(i).getType().toString() + ';');
            }
            curActorFileData.write(")V\n");
            curActorFileData.write(".limit stack 16\n" +
                    ".limit locals 16\n");
            //  curActorFileData.write(String.valueOf(actorDeclaration.getKnownActors().size()+1) + '\n');
            if (actorDeclaration.getKnownActors().size() != 0) {
//                curActorFileData.write("aload_0\n");
            }
            for (int i = 0; i < actorDeclaration.getKnownActors().size(); i++) {
                curActorFileData.write("aload_0\n");
                curActorFileData.write("aload " + String.valueOf(i + 1) + '\n');
                curActorFileData.write("putfield " + actorDeclaration.getName().getName() + '/'
                        + actorDeclaration.getKnownActors().get(i).getIdentifier().getName() + ' ' + 'L' + actorDeclaration.getKnownActors().get(i).getType().toString() + ";\n");
            }
            curActorFileData.write("return\n" +
                    ".end method\n\n");

        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }

        if (actorDeclaration.getMsgHandlers() != null) {
            for (int i = 0; i < actorDeclaration.getMsgHandlers().size(); i++) {
                actorDeclaration.getMsgHandlers().get(i).accept(this);
                try {
                    defaultActorFileData.write(".method public " + "send_" + actorDeclaration.getMsgHandlers().get(i).getName().getName() + '(' + "LActor;");
                    for (int j = 0; j < actorDeclaration.getMsgHandlers().get(i).getArgs().size(); j++) {
                        defaultActorFileData.write(findTypeCode(actorDeclaration.getMsgHandlers().get(i).getArgs().get(j).getType()));
                        if (!(actorDeclaration.getMsgHandlers().get(i).getLocalVars().size() == 0 && j == actorDeclaration.getMsgHandlers().get(i).getArgs().size() - 1)) {
//                            defaultActorFileData.write(';');
                        }
                    }
                    for (int j = 0; j < actorDeclaration.getMsgHandlers().get(i).getLocalVars().size(); j++) {
//                        defaultActorFileData.write(findTypeCode(actorDeclaration.getMsgHandlers().get(i).getLocalVars().get(j).getType()));
//                        if(!(j == actorDeclaration.getMsgHandlers().get(i).getLocalVars().size()-1))
//                        {
//                            defaultActorFileData.write(';');
//                        }

                    }
                    defaultActorFileData.write(")V\n");
                    defaultActorFileData.write(".limit stack 16\n");
                    defaultActorFileData.write(".limit locals 16" + '\n');
                    defaultActorFileData.write("getstatic java/lang/System/out Ljava/io/PrintStream;\n" +
                            "ldc \"there is no msghandler named " + actorDeclaration.getMsgHandlers().get(i).getName().getName() + " in sender\"\n" +
                            "invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V\n" +
                            "return\n" +
                            ".end method\n\n");
                    if (isLastActor == false) {
                        defaultActorFileData.write("\n");
                    }
                } catch (IOException e) {
                    System.err.println("Error: " + e.getMessage());
                }
            }
        }

        try {
            curActorFileData.flush();
            curActorFileData.close();

        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }

        curActorKnownActors.clear();

    }

    @Override
    public void visit(HandlerDeclaration handlerDeclaration) {
        if (handlerDeclaration.getName() != null && handlerDeclaration.getArgs() != null && handlerDeclaration.getLocalVars() != null) {
            try {
                curHandlerSymbolTable = ((SymbolTableHandlerItem) curActorSymbolTable.get("Handler_" + handlerDeclaration.getName().getName())).getHandlerSymbolTable();

            } catch (ItemNotFoundException e) {
            }

            for (int i = 0; i < handlerDeclaration.getArgs().size(); i++) {
                curHandlerLocalVars.add(handlerDeclaration.getArgs().get(i).getIdentifier().getName());
            }
            for (int i = 0; i < handlerDeclaration.getLocalVars().size(); i++) {
                curHandlerLocalVars.add(handlerDeclaration.getLocalVars().get(i).getIdentifier().getName());
            }
        }
        if (inInitial) {
            if (handlerDeclaration.getName() != null) {
                try {
                    curActorFileData.write(".method public initial(");
                    if (handlerDeclaration.getArgs() != null) {
                        for (int i = 0; i < handlerDeclaration.getArgs().size(); i++) {
                            curActorFileData.write(findTypeCode(handlerDeclaration.getArgs().get(i).getType()));
                        }

                    }
                    curActorFileData.write(")V\n.limit stack 16\n.limit locals 16\n");
                } catch (IOException e) {
                    System.err.println("Error: " + e.getMessage());
                }
            }
//            if (handlerDeclaration.getArgs() != null && handlerDeclaration.getLocalVars() != null) {
//                try {
//                    curActorFileData.write(String.valueOf(handlerDeclaration.getLocalVars().size() + handlerDeclaration.getArgs().size() + 1) + '\n');
//                } catch (IOException e) {
//                    System.err.println("Error: " + e.getMessage());
//                }
//            }
            for (int i = 0; i < handlerDeclaration.getLocalVars().size(); i++) {
                try {
                    String type = findTypeCode(handlerDeclaration.getLocalVars().get(i).getType());
                    if (type.equals("I") || type.equals("Z")) {
                        curActorFileData.write("ldc 0\nistore " + String.valueOf(indexOf(handlerDeclaration.getLocalVars().get(i).getIdentifier().getName())) + '\n');
                    } else if (type.equals("[I")) {
                        curActorFileData.write("ldc " + String.valueOf(((ArrayType) handlerDeclaration.getLocalVars().get(i).getType()).getSize()) + '\n' + "newarray int\n" + "astore " + String.valueOf(indexOf(handlerDeclaration.getLocalVars().get(i).getIdentifier().getName())) + '\n');
                    } else {
                        curActorFileData.write("ldc \"\"\nastore " + String.valueOf(indexOf(handlerDeclaration.getLocalVars().get(i).getIdentifier().getName())) + '\n');
                    }

                } catch (IOException e) {
                    System.err.println("Error: " + e.getMessage());
                }

            }

            if (handlerDeclaration.getBody() != null) {
                for (int i = 0; i < handlerDeclaration.getBody().size(); i++) {
                    handlerDeclaration.getBody().get(i).accept(this);
                }
            }
            try {
                curActorFileData.write("return\n" + ".end method\n\n");
            } catch (IOException e) {
                System.err.println("Error: " + e.getMessage());
            }

        } else {
            //for actor.j
            if (handlerDeclaration.getName() != null) {
                curHandlerName = handlerDeclaration.getName().getName();
                try {
                    curActorFileData.write(".method public send_" + curHandlerName + "(LActor;");
                } catch (IOException e) {
                    System.err.println("Error: " + e.getMessage());
                }
            }
            if (handlerDeclaration.getArgs() != null) {
                for (int i = 0; i < handlerDeclaration.getArgs().size(); i++) {
                    //should we include local vars also?
                    try {
                        curActorFileData.write(findTypeCode(handlerDeclaration.getArgs().get(i).getType()));
                        if ((i != handlerDeclaration.getArgs().size() - 1)) {
//                            curActorFileData.write(';');
                        }
                    } catch (IOException e) {
                        System.err.println("Error: " + e.getMessage());
                    }
                }
                try {
                    curActorFileData.write(")V\n.limit stack 16\n.limit locals 16\naload_0\n");
                    curActorFileData.write("new " + curActorName + '_' + curHandlerName + "\ndup\n" + "aload_0\n" + "aload_1\n");
                    for (int i = 0; i < handlerDeclaration.getArgs().size(); i++) {
                        String type = findTypeCode(handlerDeclaration.getArgs().get(i).getType());
                        if (type.equals("I") || type.equals("Z")) {
                            curActorFileData.write("iload " + String.valueOf(i + 2) + '\n');
                        } else if (type.equals("[I")) {
                            curActorFileData.write("aload " + String.valueOf(i + 2) + '\n');
                        } else {
                            curActorFileData.write("aload " + String.valueOf(i + 2) + '\n');
                        }
                    }
                    curActorFileData.write("invokespecial " + curActorName + '_' + curHandlerName + "/<init>(L" + curActorName + ";LActor;");
                    for (int i = 0; i < handlerDeclaration.getArgs().size(); i++) {
                        curActorFileData.write(findTypeCode(handlerDeclaration.getArgs().get(i).getType()));
                        if ((i != handlerDeclaration.getArgs().size() - 1)) {
//                            curActorFileData.write(';');
                        }
                    }
                    curActorFileData.write(")V\n");
                    curActorFileData.write("invokevirtual " + curActorName + "/send(LMessage;)V\nreturn\n" +
                            ".end method\n\n");

                } catch (IOException e) {
                    System.err.println("Error: " + e.getMessage());
                }

                try {
                    curActorFileData.write(".method public " + curHandlerName + "(LActor;");
                    if (handlerDeclaration.getArgs() != null) {
                        for (int i = 0; i < handlerDeclaration.getArgs().size(); i++) {
                            //should we include local vars also?
                            curActorFileData.write(findTypeCode(handlerDeclaration.getArgs().get(i).getType()));
                            if ((i != handlerDeclaration.getArgs().size() - 1)) {
//                                curActorFileData.write(';');
                            }
                        }
                        curActorFileData.write(")V\n.limit stack 16\n.limit locals 16\n");
                        for (int i = 0; i < handlerDeclaration.getLocalVars().size(); i++) {
                            try {
                                String type = findTypeCode(handlerDeclaration.getLocalVars().get(i).getType());
                                if (type.equals("I") || type.equals("Z")) {
                                    curActorFileData.write("ldc 0\nistore " + String.valueOf(indexOf(handlerDeclaration.getLocalVars().get(i).getIdentifier().getName())) + '\n');
                                } else if (type.equals("[I")) {
                                    curActorFileData.write("ldc " + String.valueOf(((ArrayType) handlerDeclaration.getLocalVars().get(i).getType()).getSize()) + '\n' + "newarray int\n" + "astore " + String.valueOf(indexOf(handlerDeclaration.getLocalVars().get(i).getIdentifier().getName())) + '\n');
                                } else {
                                    curActorFileData.write("ldc \"\"\nastore " + String.valueOf(indexOf(handlerDeclaration.getLocalVars().get(i).getIdentifier().getName())) + '\n');
                                }

                            } catch (IOException e) {
                                System.err.println("Error: " + e.getMessage());
                            }

                        }

                    }
                } catch (IOException e) {
                    System.err.println("Error: " + e.getMessage());
                }

            }


            //for handler.j
            if (handlerDeclaration.getName() != null) {
                curHandlerName = handlerDeclaration.getName().getName();
                try {
                    curHandlerFileData = new FileWriter(curActorName + '_' + curHandlerName + ".j");
                    curHandlerFileData.write(".class public " + curActorName + '_' + curHandlerName + "\n.super Message\n\n");
                } catch (IOException e) {
                    System.err.println("Error: " + e.getMessage());
                }
            }
            if (handlerDeclaration.getArgs() != null && handlerDeclaration.getLocalVars() != null) {
                // double null what the hell
                for (int i = 0; i < handlerDeclaration.getArgs().size(); i++) {
                    try {
                        curHandlerFileData.write(".field private " + handlerDeclaration.getArgs().get(i).getIdentifier().getName() + ' ' + findTypeCode(handlerDeclaration.getArgs().get(i).getType()) + '\n');
                    } catch (IOException e) {
                        System.err.println("Error: " + e.getMessage());
                    }

                }

//                for (int i = 0; i < handlerDeclaration.getLocalVars().size(); i++) {
//                    try
//                    {
//                        curHandlerFileData.write(".field private " + handlerDeclaration.getLocalVars().get(i).getIdentifier().getName() + ' ' + findTypeCode(handlerDeclaration.getLocalVars().get(i).getType()) + '\n');
//                    } catch (IOException e) {
//                        System.err.println("Error: " + e.getMessage());
//                    }
//
//                }

            }

            try {
                curHandlerFileData.write(".field private receiver " + 'L' + curActorName + ';' + '\n');
                curHandlerFileData.write(".field private sender " + 'L' + "Actor" + ';' + '\n' + '\n');
            } catch (IOException e) {
                System.err.println("Error: " + e.getMessage());
            }


            if (handlerDeclaration.getBody() != null) {
                for (int i = 0; i < handlerDeclaration.getBody().size(); i++) {
                    if (handlerDeclaration.getBody().get(i) != null) {
                        handlerDeclaration.getBody().get(i).accept(this);
                    }
                }
            }

            try {
                curHandlerFileData.write(".method public <init>" + "(L" + curActorName + ';' + 'L' + "Actor" + ';');
                for (int i = 0; i < handlerDeclaration.getArgs().size(); i++) {
                    try {
//                        if (i == handlerDeclaration.getArgs().size() - 1)
//                        {
                        curHandlerFileData.write(findTypeCode(handlerDeclaration.getArgs().get(i).getType()));
//                        }
//                        else
//                        {
//                            curHandlerFileData.write(findTypeCode(handlerDeclaration.getArgs().get(i).getType()) + ';');
//                        }
                    } catch (IOException e) {
                        System.err.println("Error: " + e.getMessage());
                    }

                }
//                for (int i = 0; i < handlerDeclaration.getLocalVars().size(); i++) {
//                    try
//                    {
////                        if (i == handlerDeclaration.getLocalVars().size() - 1)
////                        {
//                            curHandlerFileData.write(findTypeCode(handlerDeclaration.getLocalVars().get(i).getType()));
////                        }
////                        else
////                        {
////                            curHandlerFileData.write(findTypeCode(handlerDeclaration.getLocalVars().get(i).getType()) + ';');
////
////                        }
//                    } catch (IOException e) {
//                        System.err.println("Error: " + e.getMessage());
//                    }
//
//                }
                curHandlerFileData.write(")V\n");
                curHandlerFileData.write(".limit stack 16\n");
                curHandlerFileData.write(".limit locals 16" + '\n');

                curHandlerFileData.write("aload_0\n" +
                        "invokespecial Message/<init>()V\n" +
                        "aload_0\n" +
                        "aload_1\n");
                curHandlerFileData.write("putfield " + curActorName + '_' + curHandlerName + '/' + "receiver " + 'L' + curActorName + ";\n");
                curHandlerFileData.write("aload_0\n" + "aload_2\n");
                curHandlerFileData.write("putfield " + curActorName + '_' + curHandlerName + '/' + "sender " + 'L' + "Actor" + ";\n");
                int index = 3;
                for (int i = 0; i < handlerDeclaration.getArgs().size(); i++) {
                    try {
                        curHandlerFileData.write("aload_0\n");
                        if (handlerDeclaration.getArgs().get(i).getType() instanceof IntType || handlerDeclaration.getArgs().get(i).getType() instanceof BooleanType) {
                            curHandlerFileData.write("iload " + String.valueOf(i + index) + '\n');
                        } else {
                            curHandlerFileData.write("aload " + String.valueOf(i + index) + '\n');
                        }
                        index = index + 1;
                        curHandlerFileData.write("putfield " + curActorName + '_' + curHandlerName + '/' + handlerDeclaration.getArgs().get(i).getIdentifier().getName() + ' ' + findTypeCode(handlerDeclaration.getArgs().get(i).getType()) + '\n');
                    } catch (IOException e) {
                        System.err.println("Error: " + e.getMessage());
                    }

                }
//                for (int i = 0; i < handlerDeclaration.getLocalVars().size(); i++) {
//                    try
//                    {
//                        curHandlerFileData.write("aload_0\n" + "iload_" + String.valueOf(i + index) + '\n');
//                        index = index + 1;
//                        curHandlerFileData.write("putfield " + curActorName + '_' + curHandlerName + '/' + handlerDeclaration.getLocalVars().get(i).getIdentifier().getName() + ' ' + findTypeCode(handlerDeclaration.getLocalVars().get(i).getType() ) + '\n');
//                    } catch (IOException e) {
//                        System.err.println("Error: " + e.getMessage());
//                    }
//                }
                curHandlerFileData.write("return\n" +
                        ".end method\n\n");
                curHandlerFileData.write(".method public execute()V\n" +
                        ".limit stack 16\n" +
                        ".limit locals 16\n");
                curHandlerFileData.write("aload_0\n");
                curHandlerFileData.write("getfield " + curActorName + '_' + curHandlerName + '/' + "receiver L" + curActorName + ";\n");
                curHandlerFileData.write("aload_0\n");
                curHandlerFileData.write("getfield " + curActorName + '_' + curHandlerName + '/' + "sender LActor;" + '\n');
                for (int i = 0; i < handlerDeclaration.getArgs().size(); i++) {
                    try {
                        curHandlerFileData.write("aload_0\n");
                        curHandlerFileData.write("getfield " + curActorName + '_' + curHandlerName + '/' + handlerDeclaration.getArgs().get(i).getIdentifier().getName() + ' ' + findTypeCode(handlerDeclaration.getArgs().get(i).getType()) + '\n');
                    } catch (IOException e) {
                        System.err.println("Error: " + e.getMessage());
                    }

                }
//                for (int i = 0; i < handlerDeclaration.getLocalVars().size(); i++) {
//                    try
//                    {
//                        curHandlerFileData.write("aload_0\n");
//                        curHandlerFileData.write("getfield " + curActorName + '_' + curHandlerName + '/' + handlerDeclaration.getLocalVars().get(i).getIdentifier().getName() + ' ' + findTypeCode(handlerDeclaration.getLocalVars().get(i).getType() ) + '\n');
//                    } catch (IOException e) {
//                        System.err.println("Error: " + e.getMessage());
//                    }
//                }
                curHandlerFileData.write("invokevirtual " + curActorName + '/' + curHandlerName + '(' + "LActor;");
                for (int i = 0; i < handlerDeclaration.getArgs().size(); i++) {
                    try {
//                        if(i == handlerDeclaration.getArgs().size()-1)
//                        {
                        curHandlerFileData.write(findTypeCode(handlerDeclaration.getArgs().get(i).getType()));
//                        }
//                        else
//                        {
//                            curHandlerFileData.write(findTypeCode(handlerDeclaration.getArgs().get(i).getType() ) + ';');
//                        }

                    } catch (IOException e) {
                        System.err.println("Error: " + e.getMessage());
                    }

                }
//                for (int i = 0; i < handlerDeclaration.getLocalVars().size(); i++) {
//                    try
//                    {
//                        if(i == handlerDeclaration.getArgs().size()-1)
//                        {
//                            curHandlerFileData.write(findTypeCode(handlerDeclaration.getLocalVars().get(i).getType() ) + ';');
//                        }
//                        else
//                        {
//                            curHandlerFileData.write(findTypeCode(handlerDeclaration.getLocalVars().get(i).getType() ) + ';');
//                        }
//                    } catch (IOException e) {
//                        System.err.println("Error: " + e.getMessage());
//                    }
//                }
                curHandlerFileData.write(")V\n" + "return\n" + ".end method");

            } catch (IOException e) {
                System.err.println("Error: " + e.getMessage());
            }
            try {
                if (handlerDeclaration.getArgs() != null) {
//                    for(int i = 0; i < handlerDeclaration.getArgs().size(); i++)
//                    {
//                        if(i == handlerDeclaration.getArgs().size()-1)
//                        {
//                            curHandlerFileData.write(findTypeCode(handlerDeclaration.getArgs().get(i).getType() ) );
//                        }
//                        else
//                        {
//                            curHandlerFileData.write(findTypeCode(handlerDeclaration.getArgs().get(i).getType() ) + ';');
//                        }
//                    }
                    curActorFileData.write(
                            "return\n" +
                                    ".end method\n\n");
                    //???
                }
            } catch (IOException e) {
                System.err.println("Error: " + e.getMessage());
            }

            try {
                curHandlerFileData.flush();
                curHandlerFileData.close();
            } catch (IOException e) {
                System.err.println("Error: " + e.getMessage());
            }
        }

        curHandlerLocalVars.clear();

    }

    @Override
    public void visit(VarDeclaration varDeclaration) {
        //nothing to do?
    }

    @Override
    public void visit(Main mainActors) {
        if (mainActors.getMainActors() != null) {
            try {
                mainFileData = new FileWriter("Main.j");
                mainFileData.write(".class public Main\n" +
                        ".super java/lang/Object\n" +
                        "\n" +
                        ".method public <init>()V\n" +
                        ".limit stack 16\n" +
                        ".limit locals 16\n" +
                        "0: aload_0\n" +
                        "1: invokespecial java/lang/Object/<init>()V\n" +
                        "4: return\n" +
                        ".end method\n" +
                        "\n" +
                        ".method public static main([Ljava/lang/String;)V\n" +
                        ".limit stack 16\n" +
                        ".limit locals 16 " + '\n');
            } catch (IOException e) {
                System.err.println("Error: " + e.getMessage());
            }
            ActorDeclaration curActor = null;
            for (int i = 0; i < mainActors.getMainActors().size(); i++) {
                curActor = findActor(mainActors.getMainActors().get(i).getType().toString());
                try {
                    mainFileData.write("new " + mainActors.getMainActors().get(i).getType().toString() + "\ndup\n");
                    mainFileData.write("ldc " + String.valueOf(curActor.getQueueSize()) + '\n');
                    mainFileData.write("invokespecial " + mainActors.getMainActors().get(i).getType().toString() + "/<init>(I)V\n");
                    mainFileData.write("astore " + String.valueOf(i + 1) + '\n');

                } catch (IOException e) {
                    System.err.println("Error: " + e.getMessage());
                }

            }

            for (int i = 0; i < mainActors.getMainActors().size(); i++) {
                curActor = findActor(mainActors.getMainActors().get(i).getType().toString());
                try {
                    mainFileData.write("aload " + String.valueOf(findActorIndex(mainActors.getMainActors(), mainActors.getMainActors().get(i).getIdentifier().getName()) + 1) + '\n');
                    for (int j = 0; j < mainActors.getMainActors().get(i).getKnownActors().size(); j++) {
                        mainFileData.write("aload " + String.valueOf(findActorIndex(mainActors.getMainActors(), mainActors.getMainActors().get(i).getKnownActors().get(j).getName()) + 1) + '\n');
                    }
                    mainFileData.write("invokevirtual " + mainActors.getMainActors().get(i).getType().toString() + "/setKnownActors(");
                    for (int j = 0; j < mainActors.getMainActors().get(i).getKnownActors().size(); j++) {
                        mainFileData.write('L' + mainActors.getMainActors().get(i).getKnownActors().get(j).getType().toString() + ';');

                    }
                    mainFileData.write(")V\n");

                } catch (IOException e) {
                    System.err.println("Error: " + e.getMessage());
                }

            }
            for (int i = 0; i < mainActors.getMainActors().size(); i++) {
                try {
                    if (doesHandlerExist(new Identifier("initial"), new Identifier(mainActors.getMainActors().get(i).getType().toString()))) {
                        mainFileData.write("aload " + String.valueOf(findActorIndex(mainActors.getMainActors(), mainActors.getMainActors().get(i).getIdentifier().getName()) + 1) + '\n');
                        for (int j = 0; j < mainActors.getMainActors().get(i).getInitArgs().size(); j++) {
                            mainActors.getMainActors().get(i).getInitArgs().get(j).accept(this);
                        }
                        mainFileData.write("invokevirtual " + mainActors.getMainActors().get(i).getType().toString() + "/initial(");
                        for (int j = 0; j < mainActors.getMainActors().get(i).getInitArgs().size(); j++) {
                            mainFileData.write(findTypeCode(mainActors.getMainActors().get(i).getInitArgs().get(j).getType()));
                        }
                        mainFileData.write(")V\n");
                    }

                } catch (IOException e) {
                    System.err.println("Error: " + e.getMessage());
                }

            }
            for (int i = 0; i < mainActors.getMainActors().size(); i++) {
                try {
                    mainFileData.write("aload " + String.valueOf(findActorIndex(mainActors.getMainActors(), mainActors.getMainActors().get(i).getIdentifier().getName()) + 1) + '\n');
                    mainFileData.write("invokevirtual " + mainActors.getMainActors().get(i).getType().toString() + "/start()V\n");
                } catch (IOException e) {
                    System.err.println("Error: " + e.getMessage());
                }

            }

            try {
                mainFileData.write("return\n" +
                        ".end method");
                mainFileData.flush();
                mainFileData.close();
            } catch (IOException e) {
                System.err.println("Error: " + e.getMessage());
            }
        }
    }

    @Override
    public void visit(ActorInstantiation actorInstantiation) {
        //nothing to do?

    }

    @Override
    public void visit(UnaryExpression unaryExpression) {
        if (unaryExpression.getUnaryOperator().toString().equals("not")) {
            unaryExpression.getOperand().accept(this);
            if (inMain) {
                try {
                    mainFileData.write("ifeq " + "Label" + String.valueOf(labelInt) + '\n');
                    labelInt++;
                    mainFileData.write("iconst_0\n");
                    mainFileData.write("goto " + "Label" + String.valueOf(labelInt) + '\n');
                    labelInt++;
                    mainFileData.write("Label" + String.valueOf(labelInt - 2) + ":\n");
                    mainFileData.write("iconst_1\n");
                    mainFileData.write("Label" + String.valueOf(labelInt - 1) + ":\n");
                } catch (IOException e) {
                    System.err.println("Error: " + e.getMessage());
                }

            } else {
                try {
                    curActorFileData.write("ifeq " + "Label" + String.valueOf(labelInt) + '\n');
                    labelInt++;
                    curActorFileData.write("iconst_0\n");
                    curActorFileData.write("goto " + "Label" + String.valueOf(labelInt) + '\n');
                    labelInt++;
                    curActorFileData.write("Label" + String.valueOf(labelInt - 2) + ":\n");
                    curActorFileData.write("iconst_1\n");
                    curActorFileData.write("Label" + String.valueOf(labelInt - 1) + ":\n");
                } catch (IOException e) {
                    System.err.println("Error: " + e.getMessage());
                }

            }


        } else if (unaryExpression.getUnaryOperator().toString().equals("minus")) {
            unaryExpression.getOperand().accept(this);
            if (inMain) {
                try {
                    mainFileData.write("ineg\n");
                } catch (IOException e) {
                    System.err.println("Error: " + e.getMessage());
                }

            } else {
                try {
                    curActorFileData.write("ineg\n");
                } catch (IOException e) {
                    System.err.println("Error: " + e.getMessage());
                }

            }

        } else if (unaryExpression.getUnaryOperator().toString().equals("preinc")) {
            inInc = true;
            try {
                if (unaryExpression.getOperand() instanceof ArrayCall) {
                    unaryExpression.getOperand().accept(this);
                    curActorFileData.write(
                            "dup2\n" +
                                    "iaload\n" +
                                    "iconst_1\n" +
                                    "iadd\n" +
                                    "dup_x2\n" +
                                    "iastore\n");

                } else if (unaryExpression.getOperand() instanceof Identifier) {
                    if (isLocal(((Identifier) unaryExpression.getOperand()).getName())) {
                        curActorFileData.write("iinc " + indexOf(((Identifier) unaryExpression.getOperand()).getName()) + " 1\n");
                        unaryExpression.getOperand().accept(this);
                    } else {
                        curActorFileData.write("aload_0\n" + "dup\n" + "getfield " + curActorName + '/' + ((Identifier) unaryExpression.getOperand()).getName()
                                + " " + findTypeCode(unaryExpression.getOperand().getType()) + '\n');
                        curActorFileData.write("iconst_1\niadd\ndup_x1\nputfield " + curActorName + '/' + ((Identifier) unaryExpression.getOperand()).getName()
                                + " " + findTypeCode(unaryExpression.getOperand().getType()) + '\n');

                    }
                }
            } catch (IOException e) {
                System.err.println("Error: " + e.getMessage());
            }
            inInc = false;
        } else if (unaryExpression.getUnaryOperator().toString().equals("postinc")) {
            inInc = true;
            unaryExpression.getOperand().accept(this);
            try {
                if (unaryExpression.getOperand() instanceof ArrayCall) {
                    unaryExpression.getOperand().accept(this);
                    curActorFileData.write(
                            "dup2\n" +
                                    "iaload\n" +
                                    "iconst_1\n" +
                                    "iadd\n" +
                                    "dup_x2\n" +
                                    "iastore\n");

                } else if (unaryExpression.getOperand() instanceof Identifier) {
                    if (isLocal(((Identifier) unaryExpression.getOperand()).getName())) {
                        curActorFileData.write("iinc " + indexOf(((Identifier) unaryExpression.getOperand()).getName()) + " 1\n");
                        unaryExpression.getOperand().accept(this);
                    } else {
                        curActorFileData.write("aload_0\n" + "dup\n" + "getfield " + curActorName + '/' + ((Identifier) unaryExpression.getOperand()).getName()
                                + " " + findTypeCode(unaryExpression.getOperand().getType()) + '\n');
                        curActorFileData.write("iconst_1\niadd\ndup_x1\nputfield " + curActorName + '/' + ((Identifier) unaryExpression.getOperand()).getName()
                                + " " + findTypeCode(unaryExpression.getOperand().getType()) + '\n');

                    }
                }
            } catch (IOException e) {
                System.err.println("Error: " + e.getMessage());
            }
            inInc = false;

        } else if (unaryExpression.getUnaryOperator().toString().equals("predec")) {
            inInc = true;
            try {
                if (unaryExpression.getOperand() instanceof ArrayCall) {
                    unaryExpression.getOperand().accept(this);
                    curActorFileData.write(
                            "dup2\n" +
                                    "iaload\n" +
                                    "iconst_1\n" +
                                    "isub\n" +
                                    "dup_x2\n" +
                                    "iastore\n");

                } else if (unaryExpression.getOperand() instanceof Identifier) {
                    if (isLocal(((Identifier) unaryExpression.getOperand()).getName())) {
                        curActorFileData.write("iinc " + indexOf(((Identifier) unaryExpression.getOperand()).getName()) + " -1\n");
                        unaryExpression.getOperand().accept(this);
                    } else {
                        curActorFileData.write("aload_0\n" + "dup\n" + "getfield " + curActorName + '/' + ((Identifier) unaryExpression.getOperand()).getName()
                                + " " + findTypeCode(unaryExpression.getOperand().getType()) + '\n');
                        curActorFileData.write("iconst_1\nisub\ndup_x1\nputfield " + curActorName + '/' + ((Identifier) unaryExpression.getOperand()).getName()
                                + " " + findTypeCode(unaryExpression.getOperand().getType()) + '\n');

                    }
                }
            } catch (IOException e) {
                System.err.println("Error: " + e.getMessage());
            }
            inInc = false;

        } else if (unaryExpression.getUnaryOperator().toString().equals("postdec")) {
            inInc = true;
            unaryExpression.getOperand().accept(this);
            try {
                if (unaryExpression.getOperand() instanceof ArrayCall) {
                    unaryExpression.getOperand().accept(this);
                    curActorFileData.write(
                            "dup2\n" +
                                    "iaload\n" +
                                    "iconst_1\n" +
                                    "isub\n" +
                                    "dup_x2\n" +
                                    "iastore\n");

                } else if (unaryExpression.getOperand() instanceof Identifier) {
                    if (isLocal(((Identifier) unaryExpression.getOperand()).getName())) {
                        curActorFileData.write("iinc " + indexOf(((Identifier) unaryExpression.getOperand()).getName()) + " -1\n");
                        unaryExpression.getOperand().accept(this);
                    } else {
                        curActorFileData.write("aload_0\n" + "dup\n" + "getfield " + curActorName + '/' + ((Identifier) unaryExpression.getOperand()).getName()
                                + " " + findTypeCode(unaryExpression.getOperand().getType()) + '\n');
                        curActorFileData.write("iconst_1\nisub\ndup_x1\nputfield " + curActorName + '/' + ((Identifier) unaryExpression.getOperand()).getName()
                                + " " + findTypeCode(unaryExpression.getOperand().getType()) + '\n');

                    }
                }
            } catch (IOException e) {
                System.err.println("Error: " + e.getMessage());
            }
            inInc = false;

        }


    }

    @Override
    public void visit(BinaryExpression binaryExpression) {
        if (binaryExpression.getLeft() != null && binaryExpression.getRight() != null) {

            if (binaryExpression.getBinaryOperator().toString().equals("and")) {
                if (inMain) {
                    try {
                        String label1 = String.valueOf(labelInt);
                        labelInt++;
                        String label2 = String.valueOf(labelInt);
                        labelInt++;
                        binaryExpression.getLeft().accept(this);
                        mainFileData.write("ifeq " + "Label" + label1 + '\n');
                        binaryExpression.getRight().accept(this);
                        mainFileData.write("ifeq " + "Label" + label1 + '\n');
                        mainFileData.write("iconst_1\n");
                        mainFileData.write("goto " + "Label" + label2 + '\n');
                        mainFileData.write("Label" + label1 + ":\n");
                        mainFileData.write("iconst_0\n");
                        mainFileData.write("Label" + label2 + ":\n");
                    } catch (IOException e) {
                        System.err.println("Error: " + e.getMessage());
                    }

                } else {
                    try {
                        String label1 = String.valueOf(labelInt);
                        labelInt++;
                        String label2 = String.valueOf(labelInt);
                        labelInt++;
                        binaryExpression.getLeft().accept(this);
                        curActorFileData.write("ifeq " + "Label" + label1 + '\n');
                        binaryExpression.getRight().accept(this);
                        curActorFileData.write("ifeq " + "Label" + label1 + '\n');
                        curActorFileData.write("iconst_1\n");
                        curActorFileData.write("goto " + "Label" + label2 + '\n');
                        curActorFileData.write("Label" + label1 + ":\n");
                        curActorFileData.write("iconst_0\n");
                        curActorFileData.write("Label" + label2 + ":\n");
                    } catch (IOException e) {
                        System.err.println("Error: " + e.getMessage());
                    }

                }

            } else if (binaryExpression.getBinaryOperator().toString().equals("or")) {
                if (inMain) {
                    try {
                        String label1 = String.valueOf(labelInt);
                        labelInt++;
                        String label2 = String.valueOf(labelInt);
                        labelInt++;
                        binaryExpression.getLeft().accept(this);
                        mainFileData.write("ifne " + "Label" + label1 + '\n');
                        binaryExpression.getRight().accept(this);
                        mainFileData.write("ifne " + "Label" + label1 + '\n');
                        mainFileData.write("iconst_0\n");
                        mainFileData.write("goto " + "Label" + label2 + '\n');
                        mainFileData.write("Label" + label1 + ':' + '\n');
                        mainFileData.write("iconst_1\n");
                        mainFileData.write("Label" + label2 + ':' + '\n');
                    } catch (IOException e) {
                        System.err.println("Error: " + e.getMessage());
                    }
                } else {
                    try {
                        String label1 = String.valueOf(labelInt);
                        labelInt++;
                        String label2 = String.valueOf(labelInt);
                        labelInt++;
                        binaryExpression.getLeft().accept(this);
                        curActorFileData.write("ifne " + "Label" + label1 + '\n');
                        binaryExpression.getRight().accept(this);
                        curActorFileData.write("ifne " + "Label" + label1 + '\n');
                        curActorFileData.write("iconst_0\n");
                        curActorFileData.write("goto " + "Label" + label2 + '\n');
                        curActorFileData.write("Label" + label1 + ':' + '\n');
                        curActorFileData.write("iconst_1\n");
                        curActorFileData.write("Label" + label2 + ':' + '\n');
                    } catch (IOException e) {
                        System.err.println("Error: " + e.getMessage());
                    }
                }

            } else if (binaryExpression.getBinaryOperator().toString().equals("add")) {
                binaryExpression.getLeft().accept(this);
                binaryExpression.getRight().accept(this);
                if (inMain) {
                    try {
                        mainFileData.write("iadd\n");
                    } catch (IOException e) {
                        System.err.println("Error: " + e.getMessage());
                    }
                } else {
                    try {
                        curActorFileData.write("iadd\n");
                    } catch (IOException e) {
                        System.err.println("Error: " + e.getMessage());
                    }
                }

            } else if (binaryExpression.getBinaryOperator().toString().equals("sub")) {
                binaryExpression.getLeft().accept(this);
                binaryExpression.getRight().accept(this);
                if (inMain) {
                    try {
                        mainFileData.write("isub\n");
                    } catch (IOException e) {
                        System.err.println("Error: " + e.getMessage());
                    }
                } else {
                    try {
                        curActorFileData.write("isub\n");
                    } catch (IOException e) {
                        System.err.println("Error: " + e.getMessage());
                    }
                }


            } else if (binaryExpression.getBinaryOperator().toString().equals("mult")) {
                binaryExpression.getLeft().accept(this);
                binaryExpression.getRight().accept(this);
                if (inMain) {
                    try {
                        mainFileData.write("imul\n");
                    } catch (IOException e) {
                        System.err.println("Error: " + e.getMessage());
                    }
                } else {
                    try {
                        curActorFileData.write("imul\n");
                    } catch (IOException e) {
                        System.err.println("Error: " + e.getMessage());
                    }
                }
            } else if (binaryExpression.getBinaryOperator().toString().equals("mod")) {
                binaryExpression.getLeft().accept(this);
                binaryExpression.getRight().accept(this);
                if (inMain) {
                    try {
                        mainFileData.write("irem\n");
                    } catch (IOException e) {
                        System.err.println("Error: " + e.getMessage());
                    }
                } else {
                    try {
                        curActorFileData.write("irem\n");
                    } catch (IOException e) {
                        System.err.println("Error: " + e.getMessage());
                    }

                }

            } else if (binaryExpression.getBinaryOperator().toString().equals("div")) {
                binaryExpression.getLeft().accept(this);
                binaryExpression.getRight().accept(this);
                if (inMain) {
                    try {
                        mainFileData.write("idiv\n");
                    } catch (IOException e) {
                        System.err.println("Error: " + e.getMessage());
                    }
                } else {
                    try {
                        curActorFileData.write("idiv\n");
                    } catch (IOException e) {
                        System.err.println("Error: " + e.getMessage());
                    }
                }


            } else if (binaryExpression.getBinaryOperator().toString().equals("lt")) {
                binaryExpression.getLeft().accept(this);
                binaryExpression.getRight().accept(this);
                if (inMain) {
                    try {
                        mainFileData.write("if_icmpge " + "Label" + String.valueOf(labelInt) + '\n');
                        labelInt++;
                        mainFileData.write("iconst_1\n");
                        mainFileData.write("goto " + "Label" + String.valueOf(labelInt) + '\n');
                        labelInt++;
                        mainFileData.write("Label" + String.valueOf(labelInt - 2) + ':' + '\n');
                        mainFileData.write("iconst_0\n");
                        mainFileData.write("Label" + String.valueOf(labelInt - 1) + ':' + '\n');
                    } catch (IOException e) {
                        System.err.println("Error: " + e.getMessage());
                    }
                } else {
                    try {
                        curActorFileData.write("if_icmpge " + "Label" + String.valueOf(labelInt) + '\n');
                        labelInt++;
                        curActorFileData.write("iconst_1\n");
                        curActorFileData.write("goto " + "Label" + String.valueOf(labelInt) + '\n');
                        labelInt++;
                        curActorFileData.write("Label" + String.valueOf(labelInt - 2) + ':' + '\n');
                        curActorFileData.write("iconst_0\n");
                        curActorFileData.write("Label" + String.valueOf(labelInt - 1) + ':' + '\n');
                    } catch (IOException e) {
                        System.err.println("Error: " + e.getMessage());
                    }
                }


            } else if (binaryExpression.getBinaryOperator().toString().equals("gt")) {
                binaryExpression.getLeft().accept(this);
                binaryExpression.getRight().accept(this);
                if (inMain) {
                    try {
                        mainFileData.write("if_icmple " + "Label" + String.valueOf(labelInt) + '\n');
                        labelInt++;
                        mainFileData.write("iconst_1\n");
                        mainFileData.write("goto " + "Label" + String.valueOf(labelInt) + '\n');
                        labelInt++;
                        mainFileData.write("Label" + String.valueOf(labelInt - 2) + ':' + '\n');
                        mainFileData.write("iconst_0\n");
                        mainFileData.write("Label" + String.valueOf(labelInt - 1) + ':' + '\n');
                    } catch (IOException e) {
                        System.err.println("Error: " + e.getMessage());
                    }
                } else {
                    try {
                        curActorFileData.write("if_icmple " + "Label" + String.valueOf(labelInt) + '\n');
                        labelInt++;
                        curActorFileData.write("iconst_1\n");
                        curActorFileData.write("goto " + "Label" + String.valueOf(labelInt) + '\n');
                        labelInt++;
                        curActorFileData.write("Label" + String.valueOf(labelInt - 2) + ':' + '\n');
                        curActorFileData.write("iconst_0\n");
                        curActorFileData.write("Label" + String.valueOf(labelInt - 1) + ':' + '\n');
                    } catch (IOException e) {
                        System.err.println("Error: " + e.getMessage());
                    }
                }


            } else if (binaryExpression.getBinaryOperator().toString().equals("eq")) {
                if (binaryExpression.getLeft().getType() instanceof IntType || binaryExpression.getLeft().getType() instanceof BooleanType) {
                    binaryExpression.getLeft().accept(this);
                    binaryExpression.getRight().accept(this);
                    if (inMain) {
                        try {
                            mainFileData.write("if_icmpne " + "Label" + String.valueOf(labelInt) + '\n');
                            labelInt++;
                            mainFileData.write("iconst_1\n");
                            mainFileData.write("goto " + "Label" + String.valueOf(labelInt) + '\n');
                            labelInt++;
                            mainFileData.write("Label" + String.valueOf(labelInt - 2) + ":\n");
                            mainFileData.write("iconst_0\n");
                            mainFileData.write("Label" + String.valueOf(labelInt - 1) + ":\n");

                        } catch (IOException e) {
                            System.err.println("Error: " + e.getMessage());
                        }
                    } else {
                        try {
                            curActorFileData.write("if_icmpne " + "Label" + String.valueOf(labelInt) + '\n');
                            labelInt++;
                            curActorFileData.write("iconst_1\n");
                            curActorFileData.write("goto " + "Label" + String.valueOf(labelInt) + '\n');
                            labelInt++;
                            curActorFileData.write("Label" + String.valueOf(labelInt - 2) + ":\n");
                            curActorFileData.write("iconst_0\n");
                            curActorFileData.write("Label" + String.valueOf(labelInt - 1) + ":\n");

                        } catch (IOException e) {
                            System.err.println("Error: " + e.getMessage());
                        }
                    }


                } else if (binaryExpression.getLeft().getType() instanceof ActorType) {
                    binaryExpression.getLeft().accept(this);
                    binaryExpression.getRight().accept(this);
                    try {
                        curActorFileData.write("invokevirtual java/lang/Object.equals(Ljava/lang/Object;)Z\n");
                    } catch (IOException e) {
                        System.err.println("Error: " + e.getMessage());
                    }
                } else {
                    //what should we do with actor
                    binaryExpression.getLeft().accept(this);
                    binaryExpression.getRight().accept(this);
                    if (inMain) {
                        try {
                            mainFileData.write("if_acmpne " + "Label" + String.valueOf(labelInt) + '\n');
                            labelInt++;
                            mainFileData.write("iconst_1\n");
                            mainFileData.write("goto " + "Label" + String.valueOf(labelInt) + '\n');
                            labelInt++;
                            mainFileData.write("Label" + String.valueOf(labelInt - 2) + ":\n");
                            mainFileData.write("iconst_0\n");
                            mainFileData.write("Label" + String.valueOf(labelInt - 1) + ":\n");

                        } catch (IOException e) {
                            System.err.println("Error: " + e.getMessage());
                        }
                    } else {
                        try {
                            curActorFileData.write("if_acmpne " + "Label" + String.valueOf(labelInt) + '\n');
                            labelInt++;
                            curActorFileData.write("iconst_1\n");
                            curActorFileData.write("goto " + "Label" + String.valueOf(labelInt) + '\n');
                            labelInt++;
                            curActorFileData.write("Label" + String.valueOf(labelInt - 2) + ":\n");
                            curActorFileData.write("iconst_0\n");
                            curActorFileData.write("Label" + String.valueOf(labelInt - 1) + ":\n");

                        } catch (IOException e) {
                            System.err.println("Error: " + e.getMessage());
                        }
                    }


                }

            } else if (binaryExpression.getBinaryOperator().toString().equals("neq")) {
                if (binaryExpression.getLeft().getType() instanceof IntType || binaryExpression.getLeft().getType() instanceof BooleanType) {
                    binaryExpression.getLeft().accept(this);
                    binaryExpression.getRight().accept(this);
                    if (inMain) {
                        try {
                            mainFileData.write("if_icmpeq " + "Label" + String.valueOf(labelInt) + '\n');
                            labelInt++;
                            mainFileData.write("iconst_1\n");
                            mainFileData.write("goto " + "Label" + String.valueOf(labelInt) + '\n');
                            labelInt++;
                            mainFileData.write("Label" + String.valueOf(labelInt - 2) + ":\n");
                            mainFileData.write("iconst_0\n");
                            mainFileData.write("Label" + String.valueOf(labelInt - 1) + ":\n");

                        } catch (IOException e) {
                            System.err.println("Error: " + e.getMessage());
                        }

                    } else {
                        try {
                            curActorFileData.write("if_icmpeq " + "Label" + String.valueOf(labelInt) + '\n');
                            labelInt++;
                            curActorFileData.write("iconst_1\n");
                            curActorFileData.write("goto " + "Label" + String.valueOf(labelInt) + '\n');
                            labelInt++;
                            curActorFileData.write("Label" + String.valueOf(labelInt - 2) + ":\n");
                            curActorFileData.write("iconst_0\n");
                            curActorFileData.write("Label" + String.valueOf(labelInt - 1) + ":\n");

                        } catch (IOException e) {
                            System.err.println("Error: " + e.getMessage());
                        }

                    }


                } else {
                    binaryExpression.getLeft().accept(this);
                    binaryExpression.getRight().accept(this);
                    //what should we do with actor
                    if (inMain) {
                        try {
                            mainFileData.write("if_acmpeq " + "Label" + String.valueOf(labelInt) + '\n');
                            labelInt++;
                            mainFileData.write("iconst_1\n");
                            mainFileData.write("goto " + "Label" + String.valueOf(labelInt) + '\n');
                            labelInt++;
                            mainFileData.write("Label" + String.valueOf(labelInt - 2) + ":\n");
                            mainFileData.write("iconst_0\n");
                            mainFileData.write("Label" + String.valueOf(labelInt - 1) + ":\n");

                        } catch (IOException e) {
                            System.err.println("Error: " + e.getMessage());
                        }

                    } else {
                        try {
                            curActorFileData.write("if_acmpeq " + "Label" + String.valueOf(labelInt) + '\n');
                            labelInt++;
                            curActorFileData.write("iconst_1\n");
                            curActorFileData.write("goto " + "Label" + String.valueOf(labelInt) + '\n');
                            labelInt++;
                            curActorFileData.write("Label" + String.valueOf(labelInt - 2) + ":\n");
                            curActorFileData.write("iconst_0\n");
                            curActorFileData.write("Label" + String.valueOf(labelInt - 1) + ":\n");

                        } catch (IOException e) {
                            System.err.println("Error: " + e.getMessage());
                        }

                    }


                }

            } else if (binaryExpression.getBinaryOperator().toString().equals("assign")) {
                if (binaryExpression.getLeft() != null && binaryExpression.getRight() != null) {
                    if (binaryExpression.getLeft() instanceof ArrayCall) {
                        if (((ArrayCall) binaryExpression.getLeft()).getArrayInstance() instanceof ActorVarAccess) {
                            if (!(isLocal(((ActorVarAccess) ((ArrayCall) binaryExpression.getLeft()).getArrayInstance()).getVariable().getName()))) {
                                try {
                                    curActorFileData.write("aload_0\n");
                                    curActorFileData.write("getfield " + curActorName + '/' + ((ActorVarAccess) ((ArrayCall) binaryExpression.getLeft()).getArrayInstance()).getVariable().getName() + ' ' + "[I" + '\n');
                                    ((ArrayCall) binaryExpression.getLeft()).getIndex().accept(this);
                                } catch (IOException e) {
                                    System.err.println("Error: " + e.getMessage());
                                }
                            }


                        } else if (((ArrayCall) binaryExpression.getLeft()).getArrayInstance() instanceof Identifier) {
                            try {
                                String temp = ((Identifier) ((ArrayCall) binaryExpression.getLeft()).getArrayInstance()).getName();
                                if (isLocal(temp)) {
                                    curActorFileData.write("aload " + indexOf(temp) + '\n');
                                    ((ArrayCall) binaryExpression.getLeft()).getIndex().accept(this);
                                } else {
                                    curActorFileData.write("aload_0\n");
                                    curActorFileData.write("getfield " + curActorName + '/' + ((Identifier) ((ArrayCall) binaryExpression.getLeft()).getArrayInstance()).getName() + ' ' + "[I" + '\n');
                                    ((ArrayCall) binaryExpression.getLeft()).getIndex().accept(this);
                                }

                            } catch (IOException e) {
                                System.err.println("Error: " + e.getMessage());
                            }

                        }

                    } else if (binaryExpression.getLeft() instanceof Identifier) {
                        if (!(isLocal(((Identifier) binaryExpression.getLeft()).getName()))) {
                            try {
                                curActorFileData.write("aload_0\n");
                            } catch (IOException e) {
                                System.err.println("Error: " + e.getMessage());
                            }
                        }

                    } else if (binaryExpression.getLeft() instanceof ActorVarAccess) {
                        ((ActorVarAccess) binaryExpression.getLeft()).getSelf().accept(this);
                    }

                    //hereeee

                    binaryExpression.getRight().accept(this);

                    if (binaryExpression.getLeft() instanceof ArrayCall) {
                        try {
                            curActorFileData.write("iastore\n");
                        } catch (IOException e) {
                            System.err.println("Error: " + e.getMessage());
                        }

                    } else if (binaryExpression.getLeft() instanceof Identifier) {
                        if (isLocal(((Identifier) binaryExpression.getLeft()).getName())) {
                            try {
                                if (binaryExpression.getLeft().getType() instanceof IntType || binaryExpression.getLeft().getType() instanceof BooleanType) {
                                    curActorFileData.write("istore " + indexOf(((Identifier) binaryExpression.getLeft()).getName()) + '\n');
                                } else {
                                    curActorFileData.write("astore " + indexOf(((Identifier) binaryExpression.getLeft()).getName()) + '\n');
                                }
                            } catch (IOException e) {
                                System.err.println("Error: " + e.getMessage());
                            }

                        } else {
                            try {
                                curActorFileData.write("putfield " + curActorName + '/' + ((Identifier) binaryExpression.getLeft()).getName() + ' ' + findTypeCode(binaryExpression.getLeft().getType()) + '\n');
                            } catch (IOException e) {
                                System.err.println("Error: " + e.getMessage());
                            }
                        }

                    } else if (binaryExpression.getLeft() instanceof ActorVarAccess) {
                        try {
                            curActorFileData.write("putfield " + curActorName + '/' + ((ActorVarAccess) binaryExpression.getLeft()).getVariable().getName() + ' ' + findTypeCode(binaryExpression.getLeft().getType()) + '\n');
                        } catch (IOException e) {
                            System.err.println("Error: " + e.getMessage());
                        }


                    }
                }


            }
        }

    }

    @Override
    public void visit(ArrayCall arrayCall) {
        if (arrayCall.getArrayInstance() != null && arrayCall.getIndex() != null) {
            try {
                if (isLocal(((Identifier) arrayCall.getArrayInstance()).getName())) {
                    curActorFileData.write("aload " + indexOf(((Identifier) arrayCall.getArrayInstance()).getName()) + '\n');
                    arrayCall.getIndex().accept(this);
                    if(!inInc)
                    {
                        curActorFileData.write("iaload\n");
                    }

                } else {
                    curActorFileData.write("aload_0\n" + "getfield " + curActorName + '/' + ((Identifier) arrayCall.getArrayInstance()).getName() + " [I\n");
                    arrayCall.getIndex().accept(this);
                    if(!inInc)
                    {
                        curActorFileData.write("iaload\n");
                    }
                }

            } catch (IOException e) {
                System.err.println("Error: " + e.getMessage());
            }
        }

    }

    @Override
    public void visit(ActorVarAccess actorVarAccess) {
//        if (actorVarAccess.getSelf() != null) {
//            actorVarAccess.getSelf().accept(this);
//        }
        if (actorVarAccess.getVariable() != null) {
            actorVarAccess.getVariable().setType(actorVarAccess.getType());
            actorVarAccess.getVariable().accept(this);
        }

    }

    @Override
    public void visit(Identifier identifier) {
        if (isLocal(identifier.getName())) {

            if (identifier.getType() instanceof IntType) {
                try {
                    curActorFileData.write("iload " + indexOf(identifier.getName()) + '\n');
                } catch (IOException e) {
                    System.err.println("Error: " + e.getMessage());
                }

            } else if (identifier.getType() instanceof BooleanType) {
                try {
                    curActorFileData.write("iload " + indexOf(identifier.getName()) + '\n');
                } catch (IOException e) {
                    System.err.println("Error: " + e.getMessage());
                }

            } else if (identifier.getType() instanceof StringType) {
                try {
                    curActorFileData.write("aload " + indexOf(identifier.getName()) + '\n');
                } catch (IOException e) {
                    System.err.println("Error: " + e.getMessage());
                }

            } else if (identifier.getType() instanceof ArrayType) {
                try {
                    curActorFileData.write("aload " + indexOf(identifier.getName()) + '\n');
                } catch (IOException e) {
                    System.err.println("Error: " + e.getMessage());
                }

            } else if (identifier.getType() instanceof ActorType) {
                //?

            }
        } else {
            if (identifier.getType() instanceof IntType) {
                try {
                    curActorFileData.write("aload_0 " + '\n');
                    curActorFileData.write("getfield " + curActorName + '/' + identifier.getName() + " I\n");
                } catch (IOException e) {
                    System.err.println("Error: " + e.getMessage());
                }
            } else if (identifier.getType() instanceof BooleanType) {
                try {
                    curActorFileData.write("aload_0 " + '\n');
                    curActorFileData.write("getfield " + curActorName + '/' + identifier.getName() + " Z\n");
                } catch (IOException e) {
                    System.err.println("Error: " + e.getMessage());
                }

            } else if (identifier.getType() instanceof StringType) {
                try {
                    curActorFileData.write("aload_0 " + '\n');
                    curActorFileData.write("getfield " + curActorName + '/' + identifier.getName() + " Ljava/lang/String;\n");
                } catch (IOException e) {
                    System.err.println("Error: " + e.getMessage());
                }

            } else if (identifier.getType() instanceof ArrayType) {
                try {
                    curActorFileData.write("aload_0 " + '\n');
                    curActorFileData.write("getfield " + curActorName + '/' + identifier.getName() + " [I\n");
                } catch (IOException e) {
                    System.err.println("Error: " + e.getMessage());
                }

            } else if (identifier.getType() instanceof ActorType) {
                try {
                    curActorFileData.write("aload_0\ngetfield " + curActorName + '/' + identifier.getName() + ' ' + 'L' + getKnownActorType(identifier.getName()) + ";\n");
                } catch (IOException e) {
                    System.err.println("Error: " + e.getMessage());
                }

            }

        }

    }

    @Override
    public void visit(Self self) {
        try {
            curActorFileData.write("aload_0\n");
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    @Override
    public void visit(Sender sender) {
        try {
            curActorFileData.write("aload_1\n");
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    @Override
    public void visit(BooleanValue value) {
        if (inMain) {
            try {
                if (value.getConstant() == true) {
                    mainFileData.write("iconst_1\n");
                } else {
                    mainFileData.write("iconst_0\n");
                }
            } catch (IOException e) {
                System.err.println("Error: " + e.getMessage());
            }

        } else {
            try {
                if (value.getConstant() == true) {
                    curActorFileData.write("iconst_1\n");
                } else {
                    curActorFileData.write("iconst_0\n");
                }
            } catch (IOException e) {
                System.err.println("Error: " + e.getMessage());
            }

        }

    }

    @Override
    public void visit(IntValue value) {
        if (inMain) {
            try {
                if (value.getConstant() < 6) {
                    mainFileData.write("ldc " + String.valueOf(value.getConstant()) + '\n');
                } else {
                    mainFileData.write("ldc " + String.valueOf(value.getConstant()) + '\n');
                }
            } catch (IOException e) {
                System.err.println("Error: " + e.getMessage());
            }

        } else {
            try {
                if (value.getConstant() < 6) {
                    curActorFileData.write("ldc " + String.valueOf(value.getConstant()) + '\n');
                } else {
                    curActorFileData.write("ldc " + String.valueOf(value.getConstant()) + '\n');
                }
            } catch (IOException e) {
                System.err.println("Error: " + e.getMessage());
            }

        }

    }

    @Override
    public void visit(StringValue value) {
        if (inMain) {
            try {
                mainFileData.write("ldc " + value.getConstant() + '\n');
            } catch (IOException e) {
                System.err.println("Error: " + e.getMessage());
            }

        } else {
            try {
                curActorFileData.write("ldc " + value.getConstant() + '\n');
            } catch (IOException e) {
                System.err.println("Error: " + e.getMessage());
            }

        }

    }

    @Override
    public void visit(Block block) {
        if (block.getStatements() != null) {
            for (int i = 0; i < block.getStatements().size(); i++) {
                block.getStatements().get(i).accept(this);
            }
        }

    }

    @Override
    public void visit(Conditional conditional) {

        if (conditional.getExpression() != null) {
            conditional.getExpression().accept(this);

            try {
                String startIf = "Label" + String.valueOf(labelInt);
                curActorFileData.write("ifeq " + startIf + '\n');
                labelInt++;
                String endIf = "Label" + String.valueOf(labelInt);
                labelInt++;
                if (conditional.getThenBody() != null) {
                    conditional.getThenBody().accept(this);
                }
                curActorFileData.write("goto " + endIf + '\n');

                curActorFileData.write(startIf + ":\n");

                if (conditional.getElseBody() != null) {
                    conditional.getElseBody().accept(this);
                }
                curActorFileData.write(endIf + ":\n");

            } catch (IOException e) {
                System.err.println("Error: " + e.getMessage());
            }
        }

    }



    @Override
    public void visit(For loop) {
        if(loop.getInitialize() != null)
        {
            loop.getInitialize().accept(this);
        }
        try
        {
            curActorFileData.write("Label" + String.valueOf(labelInt) + ":\n");
            String forStart = "Label" + String.valueOf(labelInt);
            labelInt++;
            forStartLabel = "Label" + String.valueOf(labelInt);
            String update = forStartLabel;
            labelInt++;
            String forEnd = "Label" + String.valueOf(labelInt);
            forEndLabel = "Label" + String.valueOf(labelInt);
            labelInt++;
            if(loop.getCondition() != null)
            {
                loop.getCondition().accept(this);
                curActorFileData.write("ifeq " + forEnd + '\n');
            }

            if(loop.getBody() != null)
            {
                loop.getBody().accept(this);
            }
            if(loop.getUpdate() != null)
            {
                curActorFileData.write(update + ":\n");
                loop.getUpdate().accept(this);
            }
            curActorFileData.write("goto " + forStart + '\n');
            curActorFileData.write(forEnd + ":\n");


        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }


    }

    @Override
    public void visit(Break breakLoop) {
        try
        {
            curActorFileData.write("goto " + forEndLabel + '\n');

        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    @Override
    public void visit(Continue continueLoop) {
        try
        {
            curActorFileData.write("goto " + forStartLabel + '\n');

        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    @Override
    public void visit(MsgHandlerCall msgHandlerCall) {
        try
        {
            if(msgHandlerCall.getInstance() instanceof Sender)
            {
                curActorFileData.write("aload_1\n");
            }
            else if(msgHandlerCall.getInstance() instanceof Identifier)
            {
                curActorFileData.write("aload_0\n");
                curActorFileData.write("getfield " + curActorName + "/" + ((Identifier)msgHandlerCall.getInstance()).getName() + ' ' + "L" + getKnownActorType(((Identifier) msgHandlerCall.getInstance()).getName()) + ";\n");
            }
            else if(msgHandlerCall.getInstance() instanceof Self)
            {
                msgHandlerCall.getInstance().accept(this);
            }
            curActorFileData.write("aload_0\n");
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
        if (msgHandlerCall.getArgs() != null) {
            for (int i = 0; i < msgHandlerCall.getArgs().size(); i++)
            {
                msgHandlerCall.getArgs().get(i).accept(this);
            }
        }
        try
        {
            if(msgHandlerCall.getInstance() instanceof Sender)
            {
                curActorFileData.write("invokevirtual "  + "Actor/send_" + msgHandlerCall.getMsgHandlerName().getName() + "(LActor;");

            }
            else if(msgHandlerCall.getInstance() instanceof Identifier)
            {
                curActorFileData.write("invokevirtual " + getKnownActorType(((Identifier) msgHandlerCall.getInstance()).getName()) + "/send_" + msgHandlerCall.getMsgHandlerName().getName() + "(LActor;");

            }
            if(msgHandlerCall.getInstance() instanceof Self)
            {
                curActorFileData.write("invokevirtual "  + curActorName + '/' + msgHandlerCall.getMsgHandlerName().getName() + "(LActor;");

            }
            if (msgHandlerCall.getArgs() != null) {
                for (int i = 0; i < msgHandlerCall.getArgs().size(); i++) {
                    curActorFileData.write(findTypeCode(msgHandlerCall.getArgs().get(i).getType()));
                    if (i != msgHandlerCall.getArgs().size() - 1)
                    {
//                        curActorFileData.write(";");
                    }

                }
            }
            curActorFileData.write(")V\n");
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    @Override
    public void visit(Print print) {
        if(print.getArg() != null)
        {
            try
            {
                curActorFileData.write("getstatic java/lang/System/out Ljava/io/PrintStream;\n");
                print.getArg().accept(this);
                if(print.getArg().getType() instanceof ArrayType)
                {
                    curActorFileData.write("invokestatic java/util/Arrays/toString([I)Ljava/lang/String;\n");
                    curActorFileData.write("invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V\n");
                }
                else
                {
                    curActorFileData.write("invokevirtual java/io/PrintStream/println(" + findTypeCode(print.getArg().getType()) + ")V\n");
                }
            }
            catch (IOException e) {
                System.err.println("Error: " + e.getMessage());
            }
        }
    }

    @Override
    public void visit(Assign assign) {
        if (assign.getlValue() != null && assign.getrValue() != null)
        {
            if(assign.getlValue() instanceof ArrayCall)
            {
                if(((ArrayCall) assign.getlValue()).getArrayInstance() instanceof ActorVarAccess)
                {
                    if(!(isLocal( ((ActorVarAccess) ((ArrayCall) assign.getlValue()).getArrayInstance()).getVariable().getName() )))
                    {
                        try
                        {
                            curActorFileData.write("aload_0\n");
                            curActorFileData.write("getfield " + curActorName + '/' + ((ActorVarAccess) ((ArrayCall) assign.getlValue()).getArrayInstance()).getVariable().getName() + ' ' + "[I" + '\n');
                            ((ArrayCall) assign.getlValue()).getIndex().accept(this);
                        }
                        catch (IOException e) {
                            System.err.println("Error: " + e.getMessage());
                        }
                    }


                }
                else if(((ArrayCall) assign.getlValue()).getArrayInstance() instanceof Identifier)
                {
                    try
                    {
                        String temp = ((Identifier)((ArrayCall) assign.getlValue()).getArrayInstance()).getName();
                        if(isLocal(temp))
                        {
                            curActorFileData.write("aload " + indexOf(temp) + '\n');
                            ((ArrayCall) assign.getlValue()).getIndex().accept(this);
                        }
                        else
                        {
                            curActorFileData.write("aload_0\n");
                            curActorFileData.write("getfield " + curActorName + '/' + ((Identifier) ((ArrayCall) assign.getlValue()).getArrayInstance()).getName() + ' ' + "[I" + '\n');
                            ((ArrayCall) assign.getlValue()).getIndex().accept(this);
                        }

                    } catch (IOException e) {
                        System.err.println("Error: " + e.getMessage());
                    }

                }

            }
            else if(assign.getlValue() instanceof Identifier)
            {
                if(!(isLocal( ((Identifier) assign.getlValue()).getName())))
                {
                    try
                    {
                        curActorFileData.write("aload_0\n");
                    }
                    catch (IOException e) {
                        System.err.println("Error: " + e.getMessage());
                    }
                }

            }
            else if(assign.getlValue() instanceof ActorVarAccess)
            {
                ((ActorVarAccess) assign.getlValue()).getSelf().accept(this);
            }

            //hereeee

            assign.getrValue().accept(this);

            if(assign.getlValue() instanceof ArrayCall)
            {
               try
               {
                   curActorFileData.write("iastore\n");
               } catch (IOException e) {
                   System.err.println("Error: " + e.getMessage());
               }

            }
            else if(assign.getlValue() instanceof Identifier)
            {
                if(isLocal( ((Identifier) assign.getlValue()).getName()) )
                {
                    try
                    {
                        if(assign.getlValue().getType() instanceof IntType || assign.getlValue().getType() instanceof BooleanType)
                        {
                            curActorFileData.write("istore " + indexOf(((Identifier) assign.getlValue()).getName()) + '\n');
                        }
                        else
                        {
                            curActorFileData.write("astore " + indexOf(((Identifier) assign.getlValue()).getName()) + '\n');
                        }
                    }
                    catch (IOException e) {
                        System.err.println("Error: " + e.getMessage());
                    }

                }
                else
                {
                    try
                    {
                        curActorFileData.write("putfield " + curActorName + '/' + ((Identifier) assign.getlValue()).getName() + ' ' + findTypeCode(assign.getlValue().getType()) + '\n');
                    }
                    catch (IOException e) {
                        System.err.println("Error: " + e.getMessage());
                    }
                }

            }
            else if(assign.getlValue() instanceof ActorVarAccess)
            {
                try
                {
                    curActorFileData.write("putfield " + curActorName + '/' + ((ActorVarAccess) assign.getlValue()).getVariable().getName() + ' ' + findTypeCode(assign.getlValue().getType()) + '\n');
                }
                catch (IOException e) {
                    System.err.println("Error: " + e.getMessage());
                }


            }

        }
    }
}

