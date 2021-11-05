package verifier.semantic;

import app.App;
import app.StatusVerifier;
import models.ModelCheckerCTL;
import verifier.ast.*;
import verifier.parser.VerifierParser;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class VerifierSemanticAnalyzer {
    private StatusVerifier statusVerifier;
    private VerifierParser parser;
    private int errors = 0;
    private Program program;
    private Exp expVerifier;
    private  HashMap<App.Compilables, ArrayList<String>> elements;
    private ArrayList<Identifier> identifiers;



    public VerifierSemanticAnalyzer(FileReader file, StatusVerifier statusVerifier) throws IOException {
        this.statusVerifier = statusVerifier;
        this.parser = new VerifierParser(file, statusVerifier);
        elements = App.COMPILABLES_ELEMENTS;

    }

    // get number of errors
    public int getErrors() {
        return errors;
    }

    public Exp analyzeProgram() throws IOException {
        program = App.FinalProgram;
        expVerifier = parser.parseExpVerifier();
        identifiers = parser.getIdentifiers();
        checkIdentifiers();
        if (errors == 0) {
            return expVerifier;
        } else {
            return null;
        }


//        if (expVerifier instanceof AlwaysGlobally) {
//            // A[] x>3
//            Exp exp = ((AlwaysGlobally) expVerifier).getExp();
//
//
//        } else if (expVerifier instanceof AlwaysEventually) {
//
//        } else if (expVerifier instanceof AlwaysNext) {
//
//        } else if (expVerifier instanceof ExistsGlobally) {
//
//        } else if (expVerifier instanceof ExistsEventually) {
//
//        } else if (expVerifier instanceof ExistsNext) {
//
//        } else {
//
//        }


    }

    private void checkIdentifiers() {
        for (Identifier identifier:identifiers) {
            if (!isIdentifierExists(identifier.getName())) {
                error(ErrorType.INVALID_IDENTIFIER,identifier.getName());
            }
        }
    }

    // check if a specific identifier name is exists
    private boolean isIdentifierExists(String name) {
        if (App.COMPILABLES_ELEMENTS == null) {
            return false;
        } else {
            for (String appIdentifier : App.COMPILABLES_ELEMENTS.get(App.Compilables.IDENTIFIERS)) {

                if (appIdentifier.equals(name))
                    return true;
            }
        }
        return false;
    }


    // print errors report
    private void error(ErrorType errorType, Object parm){
        errors++;
        switch (errorType) {
            case INVALID_IDENTIFIER:
                statusVerifier.error("ERROR Identifier: VARIABLE (" + (String) parm + ") doesn't exist");
                break;
            default:
                break;
        }
    }



}
