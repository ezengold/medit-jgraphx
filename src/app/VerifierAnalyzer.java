package app;

import utils.Compiler;
import utils.XmlHandler;
import verifier.ast.*;
import verifier.lexer.Token;
import verifier.lexer.TokenType;
import verifier.lexer.VerifierLexer;
import verifier.parser.VerifierParser;
import verifier.visitor.PrintVisitor;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class VerifierAnalyzer {
    private StatusVerifier statusVerifier;
    private String expression;




    public VerifierAnalyzer(StatusVerifier statusVerifier) {
        this.statusVerifier = statusVerifier;
    }

    public void analyze(String expression) {
        this.expression = expression;
        try {
            long step1Time = System.currentTimeMillis();
            // GENERATE COMPILABLE FILE
            File file = getCompilablesFile();

            // PROCEED TO LEXER FOR THE GENERATED FILE
           testLexer(file);
            App.removeCurrentTempFile();

        } catch (IOException e) {
            statusVerifier.error(e.getMessage());
        }
    }

    private File getCompilablesFile() {
        File outputFile = App.createCurrentTempFile();
        try {
            FileWriter writer = new FileWriter(outputFile);
            writer.write(expression);
            System.out.println("Expression: "+expression);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return outputFile;
    }




    private void testLexer(File inputFile) throws IOException {
        FileReader file = null;
        // attempt to open file
        try {
            file = new FileReader(inputFile.getAbsoluteFile());
        } catch (FileNotFoundException e) {
           e.printStackTrace();
        }

        // create lexer
        VerifierLexer lexer = new VerifierLexer(file);

        // start tokenizing file
        statusVerifier.normal("Tokenizing " + inputFile.getAbsolutePath()+ "...");
        long startTime = System.currentTimeMillis();
        int numTokens = 0;
        Token token;
        do {
            token = lexer.getToken();
            numTokens++;

            if(token.getType() == TokenType.UNKNOWN){
                // print token type and location
                statusVerifier.error(token.getType().toString());
                statusVerifier.error(" (" + token.getLineNumber() + "," + token.getColumnNumber() + ")");
                continue;
            }

            statusVerifier.normal(token.getType().toString()+" (" + token.getLineNumber() + "," + token.getColumnNumber() + ")");

            // print out semantic values for ID and INT_CONST tokens
            if (token.getType() == TokenType.ID)
                statusVerifier.normal(": " + token.getAttribute().getIdVal());
            else if (token.getType() == TokenType.INT_CONST)
                statusVerifier.normal(": " + token.getAttribute().getIntVal());
            else if (token.getType() == TokenType.BOOLEAN_CONST)
                statusVerifier.normal(": " + token.getAttribute().getBooleanVal());
            else
                System.out.println();

        } while (token.getType() != TokenType.EOF);

        long endTime = System.currentTimeMillis();

        // print out statistics
        statusVerifier.success("Number of tokens: " + numTokens);
        System.out.println("Number of tokens: " + numTokens);
        statusVerifier.success("Execution time: " + (endTime - startTime) + "ms");
        System.out.println("Execution time: " + (endTime - startTime) + "ms");

    }

    private void testParser(File fileReader) throws IOException {
        FileReader file = null;

        // attempt to open file
        try {
            file = new FileReader(fileReader.getAbsoluteFile());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // create parser
        VerifierParser parser = new VerifierParser(file,statusVerifier);
        statusVerifier.normal("Parsing...");

        // initiate parse and clock time
        long startTime = System.currentTimeMillis();
//				Program program = parser.parseProgram();
        Exp expVerifier = parser.parseExpVerifier();
        long endTime = System.currentTimeMillis();

        // print out statistics
        statusVerifier.normal("File has finished parsing!");
        statusVerifier.normal("Execution time: " + (endTime - startTime) + "ms");
        statusVerifier.normal(parser.getErrors() + " errors reported");
        statusVerifier.normal("---");

        // print out ASTs
        PrintVisitor printer = new PrintVisitor();


        if (expVerifier instanceof AlwaysGlobally) {
            printer.visit((AlwaysGlobally) expVerifier);
        } else if(expVerifier instanceof AlwaysEventually) {
            printer.visit((AlwaysEventually) expVerifier);
        } else if(expVerifier instanceof AlwaysNext) {
            printer.visit((AlwaysNext) expVerifier);
        }
        else if(expVerifier instanceof  ExistsGlobally) {
            printer.visit((ExistsGlobally) expVerifier);
        } else if (expVerifier instanceof  ExistsEventually) {
            printer.visit((ExistsEventually) expVerifier);
        } else if(expVerifier instanceof  ExistsNext) {
            printer.visit((ExistsNext) expVerifier);
        }

        System.out.println();
    }










}
