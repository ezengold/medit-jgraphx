package utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import app.Console;
import verifier.ast.Program;
import verifier.lexer.MeLexer;
import verifier.lexer.Token;
import verifier.lexer.TokenType;
import verifier.parser.MeParser;
import verifier.semantic.MeSemanticAnalyzer;
import verifier.visitor.PrintVisitor;

public class Compiler {
	public static void testLexer(File inputFile, Console log) throws IOException {
		FileReader file = null;

		// attempt to open file
		try {
			file = new FileReader(inputFile.getAbsoluteFile());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		// create lexer
		MeLexer lexer = new MeLexer(file);

		// start tokenizing file
		log.success("\nTokenizing " + inputFile.getAbsolutePath() + "...\n");
		long startTime = System.currentTimeMillis();
		int numTokens = 0;
		Token token;
		do {
			token = lexer.getToken();
			numTokens++;

			if (token.getType() == TokenType.UNKNOWN) {
				// print token type and location
				log.error(token.getType().toString());
				log.error(" (" + token.getLineNumber() + "," + token.getColumnNumber() + ")");
				log.error("\n");
				continue;
			}

			log.success(token.getType().toString());
			log.success(" (" + token.getLineNumber() + "," + token.getColumnNumber() + ")");

			// print out semantic values for ID and INT_CONST tokens
			if (token.getType() == TokenType.ID)
				log.success(": " + token.getAttribute().getIdVal());
			else if (token.getType() == TokenType.INT_CONST)
				log.success(": " + token.getAttribute().getIntVal());
			else if (token.getType() == TokenType.BOOLEAN_CONST)
				log.success(": " + token.getAttribute().getBooleanVal());
			else
				log.success("\n");

		} while (token.getType() != TokenType.EOF);

		long endTime = System.currentTimeMillis();

		// print out statistics
		log.success("\n---");
		log.success("\nNumber of tokens: " + numTokens);
		log.success("\nExecution time: " + (endTime - startTime) + "ms\n");
	}

	public static void testParser(File inputFile, Console log) throws IOException {
		FileReader file = null;

		// attempt to open file
		try {
			file = new FileReader(inputFile);
		} catch (FileNotFoundException e) {
			log.error("Compilaton generated file was not found!");
		}

		// create parser
		MeParser parser = new MeParser(file, log);
		log.success("Parsing...");

		// initiate parse and clock time
		long startTime = System.currentTimeMillis();
		Program program = parser.parseProgram();
		long endTime = System.currentTimeMillis();

		// print out statistics
		log.success("File has finished parsing ! \n");
		log.success("Execution time: " + (endTime - startTime) + "ms");
		log.success(parser.getErrors() + " errors reported \n");

		// print out ASTs
		PrintVisitor printer = new PrintVisitor();
		printer.visit(program);
		System.out.println();
	}

	public static Program testSementic(File inputFile, Console log) throws IOException {
		FileReader file = null;

		// attempt to open file
		try {
			file = new FileReader(inputFile);
		} catch (FileNotFoundException e) {
			log.error(inputFile.getAbsolutePath() + " was not found!");
		}

		// create semantic analyzer
		MeSemanticAnalyzer semantic = new MeSemanticAnalyzer(file, log);
		log.success("Analyzing...");

		// initiate parse and clock time
		long startTime = System.currentTimeMillis();
		semantic.analyzeProgram();
		long endTime = System.currentTimeMillis();

		// print out statistics
		log.success("File has finished analyzing!");
		log.success("Execution time: " + (endTime - startTime) + "ms");
		log.success(semantic.getErrors() + " errors reported");
		
		return semantic.getFinalProgram();
	}
}