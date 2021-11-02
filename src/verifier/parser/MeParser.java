package verifier.parser;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import app.Console;
import models.Automata;
import verifier.ast.And;
import verifier.ast.ArrayLookup;
import verifier.ast.Assign;
import verifier.ast.Block;
import verifier.ast.BooleanLiteral;
import verifier.ast.BooleanType;
import verifier.ast.ClockLiteral;
import verifier.ast.ClockType;
import verifier.ast.Declarations;
import verifier.ast.Divide;
import verifier.ast.Equal;
import verifier.ast.Exp;
import verifier.ast.Identifier;
import verifier.ast.IdentifierExp;
import verifier.ast.If;
import verifier.ast.IntegerLiteral;
import verifier.ast.IntegerType;
import verifier.ast.Invariant;
import verifier.ast.LessThan;
import verifier.ast.LessThanEqual;
import verifier.ast.Minus;
import verifier.ast.Modules;
import verifier.ast.MoreThan;
import verifier.ast.MoreThanEqual;
import verifier.ast.Negative;
import verifier.ast.Not;
import verifier.ast.NotEqual;
import verifier.ast.Or;
import verifier.ast.Plus;
import verifier.ast.Program;
import verifier.ast.Statement;
import verifier.ast.StatementList;
import verifier.ast.Times;
import verifier.ast.Type;
import verifier.ast.VarDecl;
import verifier.ast.VarDeclList;
import verifier.lexer.MeLexer;
import verifier.lexer.Token;
import verifier.lexer.TokenType;

public class MeParser {
	private MeLexer lexer;
	private Token token;
	private Token errorToken;

	private Console log;

	private Automata automata;

	// hash table for operator precedence levels
	private final static Map<TokenType, Integer> binopLevels;

	private ArrayList<VarDecl> decelarations; // declarations symbol table
	private ArrayList<Identifier> identifiers; // identifiers symbol table
	private ArrayList<Assign> assigns; // assigns symbol table
	private ArrayList<Exp> conditions; // conditions symbol table

	private int errors;

	static {
		binopLevels = new HashMap<TokenType, Integer>();
		binopLevels.put(TokenType.AND, 10);
		binopLevels.put(TokenType.OR, 10);
		binopLevels.put(TokenType.LT, 20);
		binopLevels.put(TokenType.RT, 20);
		binopLevels.put(TokenType.LT_EQ, 20);
		binopLevels.put(TokenType.RT_EQ, 20);
		binopLevels.put(TokenType.EQ, 20);
		binopLevels.put(TokenType.NEQ, 20);
		binopLevels.put(TokenType.PLUS, 30);
		binopLevels.put(TokenType.MINUS, 30);
		binopLevels.put(TokenType.TIMES, 40);
		binopLevels.put(TokenType.DIV, 40);
		binopLevels.put(TokenType.MOD, 40);
		binopLevels.put(TokenType.LBRACKET, 50);
	}

	public MeParser(FileReader file, Automata automata, Console log) throws IOException {
		this.lexer = new MeLexer(file);
		this.token = lexer.getToken();
		this.decelarations = new ArrayList<VarDecl>();
		this.identifiers = new ArrayList<Identifier>();
		this.assigns = new ArrayList<Assign>();
		this.conditions = new ArrayList<Exp>();
		this.log = log;
		this.automata = automata;
	}

	// verifies current token type and grabs next token or reports error
	private boolean eat(TokenType type) throws IOException {
		if (token.getType() == type) {
			token = lexer.getToken();
			return true;
		} else {
			error(type);
			return false;
		}
	}

	// reports an error to the console
	private void error(TokenType type) {
		// only report error once per erroneous token
		if (token == errorToken)
			return;
		// print error report
		log.error(token.getType().toString() + " at line " + token.getLineNumber() + ", column "
				+ token.getColumnNumber() + "; Expected " + type);
		errorToken = token; // set error token to prevent cascading
		errors++; // increment error counter
	}

	// skip tokens until match in follow set for error recovery
	private void skipTo(TokenType... follow) throws IOException {
		while (token.getType() != TokenType.EOF) {
			for (TokenType skip : follow) {
				if (token.getType() == skip)
					return;
			}
			token = lexer.getToken();
		}
	}

	// number of reported syntax errors
	public int getErrors() {
		return errors;
	}

	public ArrayList<VarDecl> getDecelarations() {
		return decelarations;
	}

	public ArrayList<Identifier> getIdentifiers() {
		return identifiers;
	}

	public ArrayList<Assign> getAssigns() {
		return assigns;
	}

	public ArrayList<Exp> getConditions() {
		return conditions;
	}

	// Program ::= int main '('')' { Declarations StatementList }
	public Program parseProgram() throws IOException {
		eat(TokenType.INT);
		eat(TokenType.MAIN);
		eat(TokenType.LPAREN);
		eat(TokenType.RPAREN);
		eat(TokenType.LBRACE);

		Declarations declarations = parseDeclarations();
		StatementList statementList = parseStatementList();

		eat(TokenType.RBRACE);
		eat(TokenType.EOF);
		return new Program(statementList, declarations);
	}

	// Declarations ::= { VarDeclList }
	private Declarations parseDeclarations() throws IOException {
		Declarations declarations = new Declarations();
		while (token.getType() == TokenType.INT || token.getType() == TokenType.BOOLEAN
				|| token.getType() == TokenType.CLOCK)
			declarations.addElement(parseVarDecList());
		return declarations;
	}

	// VarDeclList ::= VarDecl { , Identifier };
	private VarDeclList parseVarDecList() throws IOException {
		VarDeclList varDeclList = new VarDeclList();
		VarDecl varDecl = parseVarDecl();
		varDeclList.addElement(varDecl);
		getDecelarations().add(varDecl);

		// check for additional varDecl
		while (token.getType() == TokenType.COMMA) {
			eat(TokenType.COMMA);

			Identifier id = parseIdentifier();

			if (token.getType() == TokenType.ASSIGN) {
				eat(TokenType.ASSIGN);

				Exp value = parseExp();

				VarDecl newVarDecl = new VarDecl(varDecl.getType(), id, value);
				varDeclList.addElement(newVarDecl);
				getDecelarations().add(newVarDecl);
				getAssigns().add(new Assign(id, value));

			} else {
				VarDecl newVarDecl = new VarDecl(varDecl.getType(), parseIdentifier());
				varDeclList.addElement(newVarDecl);
				getDecelarations().add(newVarDecl);
			}
		}
		eat(TokenType.SEMI);

		return varDeclList;
	}

	// VarDecl ::= Type Identifier
	private VarDecl parseVarDecl() throws IOException {
		Type type = parseType();
		Identifier id = parseIdentifier();

		if (token.getType() == TokenType.ASSIGN) {
			eat(TokenType.ASSIGN);

			Exp value = parseExp();

			if (type instanceof IntegerType) {
				if (value instanceof IntegerLiteral) {
					this.automata.addIntVariable(id.getName(), ((IntegerLiteral) value).getValue());
				} else {
					this.automata.addIntVariable(id.getName());
				}
			} else if (type instanceof BooleanType) {
				if (value instanceof BooleanLiteral) {
					this.automata.addBooleanVariable(id.getName(), ((BooleanLiteral) value).getValue());
				} else {
					this.automata.addBooleanVariable(id.getName());
				}
			} else if (type instanceof ClockType) {
				if (value instanceof ClockLiteral) {
					this.automata.addClockVariable(id.getName(), ((ClockLiteral) value).getValue());
				} else {
					this.automata.addClockVariable(id.getName());
				}
			}

			return new VarDecl(type, id, value);
		}

		if (type instanceof IntegerType) {
			this.automata.addIntVariable(id.getName());
		} else if (type instanceof BooleanType) {
			this.automata.addBooleanVariable(id.getName());
		} else if (type instanceof ClockType) {
			this.automata.addClockVariable(id.getName());
		}

		return new VarDecl(type, id);
	}

	/*
	 * Type ::= 'int' | 'boolean' | 'clock'
	 */
	private Type parseType() throws IOException {
		switch (token.getType()) {

		case INT:
			eat(TokenType.INT);

			return new IntegerType();

		case BOOLEAN:
			eat(TokenType.BOOLEAN);

			return new BooleanType();

		case CLOCK:
			eat(TokenType.CLOCK);

			return new ClockType();

		default:
			// unknown type
			eat(TokenType.TYPE);
			return null;

		}
	}

	// Identifier ::= Letter { Letter | Digit }
	private Identifier parseIdentifier() throws IOException {
		Identifier identifier = null;

		// grab ID value if token type is ID
		if (token.getType() == TokenType.ID)
			identifier = new Identifier(token.getAttribute().getIdVal());

		eat(TokenType.ID);

		return identifier;
	}

	// StatementList ::= { Statement }
	private StatementList parseStatementList() throws IOException {
		StatementList statementList = new StatementList();
		while (isStatement())
			statementList.addElement(parseStatement());
		return statementList;
	}

	// checks the beginning of a new statement
	private boolean isStatement() {
		switch (token.getType()) {
		case SEMI:
		case IF:
		case LPAREN:
		case LBRACE:
		case ID:
		case INV:
			return true;
		default:
			return false;
		}
	}

	// Statement ::= IfStatement | InvariantStatement
	private Statement parseStatement() throws IOException {

		// IfStatement ::= 'if' '(' Expression ')' { Identifier Assign Statement }
		if (token.getType() == TokenType.IF) {
			eat(TokenType.IF);

			// parse conditional expression
			if (!eat(TokenType.LPAREN))
				skipTo(TokenType.RPAREN, TokenType.LBRACE, TokenType.RBRACE);

			Exp condExp = parseExp();
			conditions.add(condExp);

			if (!eat(TokenType.RPAREN))
				skipTo(TokenType.LBRACE, TokenType.SEMI, TokenType.RBRACE);

			Block trueStm = parseBlock();

			return new If(condExp, trueStm, null);
		}

		// Identifier Assign Statement
		if (token.getType() == TokenType.ID) {

			Identifier id = new Identifier(token.getAttribute().getIdVal());
			identifiers.add(id);
			eat(TokenType.ID);

			// Assignment statement: id = Exp ;
			if (token.getType() == TokenType.ASSIGN) {
				eat(TokenType.ASSIGN);
				Exp value = parseExp();

				eat(TokenType.SEMI);

				Assign assign = new Assign(id, value);
				assigns.add(assign);
				return assign;
			}
		}

		// InvariantStatement ::= 'inv' Expression
		if (token.getType() == TokenType.INV) {
			eat(TokenType.INV);

			Exp value = parseExp();

			eat(TokenType.SEMI);

			return new Invariant(value);
		}

		// statement type unknown
		eat(TokenType.STATEMENT);
		token = lexer.getToken();
		return null;
	}

	// BLock ::= '{' StatementList '}'
	private Block parseBlock() throws IOException {
		eat(TokenType.LBRACE);

		// recursively call parseStatement() until closing brace
		StatementList stms = new StatementList();
		while (token.getType() != TokenType.RBRACE && token.getType() != TokenType.EOF)
			stms.addElement(parseStatement());

		if (!eat(TokenType.RBRACE))
			skipTo(TokenType.RBRACE, TokenType.SEMI);

		return new Block(stms);
	}

	// Exp ::= PrimaryExp | BinopRHS
	// top-level parsing function for an expression
	private Exp parseExp() throws IOException {
		Exp lhs = parsePrimaryExp();
		return parseBinopRHS(0, lhs); // check for binops following exp
	}

	// parsePrimaryExp ::= INT_CONST | BOOLEAN_CONST | CLOCK_CONST | NEGATIVE | NOT
	// | Identifier
	// parse exp before any binop
	private Exp parsePrimaryExp() throws IOException {
		switch (token.getType()) {

		case INT_CONST:
			int intValue = token.getAttribute().getIntVal();
			eat(TokenType.INT_CONST);
			return new IntegerLiteral(intValue);

		case BOOLEAN_CONST:
			boolean booleanVal = token.getAttribute().getBooleanVal();
			eat(TokenType.BOOLEAN_CONST);
			return new BooleanLiteral(booleanVal);

		case CLOCK_CONST:
			long clockVal = token.getAttribute().getClockVal();
			eat(TokenType.CLOCK_CONST);
			return new ClockLiteral(clockVal);

		case ID:
			Identifier id = parseIdentifier();
			identifiers.add(id);
			return new IdentifierExp(id.getName());

		case NOT:
			eat(TokenType.NOT);
			return new Not(parseExp());

		case NEGATIVE:
			eat(TokenType.NEGATIVE);
			return new Negative(parseExp());

		case LPAREN:
			eat(TokenType.LPAREN);
			Exp exp = parseExp();
			eat(TokenType.RPAREN);
			return exp;

		default:
			// unrecognizable expression
			eat(TokenType.EXPRESSION);
			token = lexer.getToken();
			return null;
		}
	}

	// parse expressions according to operator precedence levels
	private Exp parseBinopRHS(int level, Exp lhs) throws IOException {
		// analyse continuellement exp jusqu'à ce qu'un opérateur d'ordre inférieur
		// apparaisse
		while (true) {
			// récupérer la priorité de l'opérateur (-1 pour le jeton non opérateur)
			Integer val = binopLevels.get(token.getType());
			int tokenLevel = (val != null) ? val.intValue() : -1;

			// soit la priorité de l'opération est inférieure à l'opération précédente, soit
			// le jeton n'est pas une opération
			if (tokenLevel < level)
				return lhs;

			// save binop before parsing rhs of exp
			TokenType binop = token.getType();
			eat(binop);

			Exp rhs = parsePrimaryExp(); // parse rhs of exp

			// grab operator precedence (-1 for non-operator token)
			val = binopLevels.get(token.getType());
			int nextLevel = (val != null) ? val.intValue() : -1;

			// if next op has higher precedence than prev op, make recursive call
			if (tokenLevel < nextLevel)
				rhs = parseBinopRHS(tokenLevel + 1, rhs);

			// build AST for exp
			switch (binop) {
			case AND:
				lhs = new And(lhs, rhs);
				break;
			case OR:
				lhs = new Or(lhs, rhs);
				break;
			case EQ:
				lhs = new Equal(lhs, rhs);
				break;
			case NEQ:
				lhs = new NotEqual(lhs, rhs);
				break;
			case LT:
				lhs = new LessThan(lhs, rhs);
				break;
			case RT:
				lhs = new MoreThan(lhs, rhs);
				break;
			case LT_EQ:
				lhs = new LessThanEqual(lhs, rhs);
				break;
			case RT_EQ:
				lhs = new MoreThanEqual(lhs, rhs);
				break;
			case PLUS:
				lhs = new Plus(lhs, rhs);
				break;
			case MINUS:
				lhs = new Minus(lhs, rhs);
				break;
			case TIMES:
				lhs = new Times(lhs, rhs);
				break;
			case DIV:
				lhs = new Divide(lhs, rhs);
				break;
			case MOD:
				lhs = new Modules(lhs, rhs);
				break;
			case LBRACKET:
				lhs = new ArrayLookup(lhs, rhs);
				eat(TokenType.RBRACKET);
				break;
			default:
				eat(TokenType.OPERATOR);
				break;
			}
		}
	}
}
