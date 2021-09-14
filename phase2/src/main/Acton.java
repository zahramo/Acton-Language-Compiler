package main;

import main.ast.node.Program;
import main.visitor.Visitor;
import main.visitor.VisitorImpl;
import main.visitor.VisitorPass1;
import main.visitor.VisitorPass2;
import main.visitor.ErrorItem;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import main.parsers.actonLexer; // import from the package your parser & lexer is generated in
import main.parsers.actonParser;

import java.io.IOException;
import java.util.ArrayList;

// Visit https://stackoverflow.com/questions/26451636/how-do-i-use-antlr-generated-parser-and-lexer
public class Acton {
    public static void main(String[] args) throws IOException {
        CharStream reader = CharStreams.fromFileName(args[1]);
        actonLexer lexer = new actonLexer(reader);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        actonParser parser = new actonParser(tokens);
        Program program = parser.program().p; /* assuming that the name of the Program ast node
                                                 that the program rule returns is p */
        Visitor visitor = new VisitorImpl();
        Visitor pass1 = new VisitorPass1();
        Visitor pass2 = new VisitorPass2();
        program.accept(pass1);
        program.accept(pass2);
        if(((VisitorPass1) pass1).getErrors().size() == 0  && ((VisitorPass2) pass2).getErrors().size() == 0)
        {
            program.accept(visitor);
        }
        else
        {
            ArrayList<ErrorItem> errors = new ArrayList<>();
            ArrayList<ErrorItem> errors1 = ((VisitorPass1) pass1).getErrors();
            errors = ((VisitorPass2) pass2).getErrors();
            for(int i=0; i<errors1.size(); i++)
            {
               errors.add(errors1.get(i));
            }
            errors.sort((o1, o2) -> o1.getLine().compareTo(o2.getLine()));
            for(int i=0; i<errors.size(); i++)
            {
                ErrorItem error = errors.get(i);
                System.out.print("Line:");
                System.out.print(error.getLine());
                System.out.print(":");
                System.out.println(error.getError());
            }
        }
    }
}