package verifier.lexer;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class VerifierLexer {
    private BufferedReader stream; //input stream reader
    private Token nextToken;
    private int nextChar;
    private int lineNumber = 1; //current line number
    private int columnNumber = 1; //current column number

    private final static Map<String, TokenType> reservedWords; //reserved words dictionary
    private final static Map<Character, TokenType> punctuation; //punctuation characters dictionary
    private final static Map<String, TokenType> operators; //operator characters dictionary

    private int errors; //number of errors

    static {
        reservedWords = new HashMap<String, TokenType>();
        reservedWords.put("A",TokenType.ALWAYS);
        reservedWords.put("E", TokenType.EXISTS);
        reservedWords.put("->", TokenType.NEXT);
        reservedWords.put("deadlock", TokenType.DEADLOCK);



        punctuation = new HashMap<Character, TokenType>();
        punctuation.put('(', TokenType.LPAREN);
        punctuation.put(')', TokenType.RPAREN);
        punctuation.put('[', TokenType.LBRACKET);
        punctuation.put(']', TokenType.RBRACKET);
//		punctuation.put('{', TokenType.LBRACE);
//		punctuation.put('}', TokenType.RBRACE);
//		punctuation.put('=', TokenType.ASSIGN);
        punctuation.put('-', TokenType.NEGATIVE);
        punctuation.put('!', TokenType.NOT);

        operators = new HashMap<String, TokenType>();
        operators.put("&&", TokenType.AND);
        operators.put("||", TokenType.OR);
        operators.put("==", TokenType.EQ);
        operators.put("!=", TokenType.NEQ);
        operators.put("<", TokenType.LT);
        operators.put(">", TokenType.RT);
        operators.put("<=", TokenType.LT_EQ);
        operators.put(">=", TokenType.RT_EQ);
        operators.put("+", TokenType.PLUS);
        operators.put("-", TokenType.MINUS);
        operators.put("*", TokenType.TIMES);
        operators.put("/", TokenType.DIV);
        operators.put("%", TokenType.MOD);
    }

    public VerifierLexer(FileReader file) throws FileNotFoundException {
        this.stream = new BufferedReader(file);
        nextChar = getChar();
    }

    public int getErrors() {
        return errors;
    }

    // handles I/O for char stream
    private int getChar() {
        try {
            return stream.read();
        } catch (IOException e) {
            System.err.print(e.getMessage());
            System.err.println("IOException occured in Lexer::getChar()");
            return -1;
        }
    }

    // detect and skip possible '\n', '\r' and '\rn' line breaks
    private boolean skipNewline() {
        if (nextChar == '\n') {
            lineNumber++;
            columnNumber = 1;
            nextChar = getChar();
            return true;
        }
        if (nextChar == '\r') {
            lineNumber++;
            columnNumber = 1;
            nextChar = getChar();

            // skip over next char if '\n'
            if (nextChar == '\n')
                nextChar = getChar();
            return true;
        }
        // newline char not found
        return false;
    }

    // return the next token without consuming it
    public Token peek() throws IOException {
        // advance token only if its been reset by getToken()
        if (nextToken == null)
            nextToken = getToken();

        return nextToken;
    }

    // return the next token in the input stream (EOF signals end of input)
    public Token getToken() throws IOException {
        // check if peek() was called
        if (nextToken != null) {
            Token token = nextToken;
            nextToken = null; // allow peek to call for next token
            return token;
        }

        // skip whitespace character
        while (Character.isWhitespace(nextChar)) {
            // check if whitespace char is a newline
            if (!skipNewline()) {
                columnNumber++;
                nextChar = getChar();
            }

            // offset colNum for tab chars
            if (nextChar == '\t')
                columnNumber += 3;
        }

        // identifier or reserved word ([a-zA-Z][a-zA-Z0-9_]*)
        if (Character.isLetter(nextChar)) {
            // create new idVal starting with first char of identifier
            String current = Character.toString((char) nextChar);
            columnNumber++;
            nextChar = getChar();

            // include remaining sequence of chars that are letters, digits, or _
            while (Character.isLetterOrDigit(nextChar)) {
                current += (char) nextChar;
                columnNumber++;
                nextChar = getChar();
            }

            // check if identifier is a reserved word
            TokenType type = reservedWords.get(current);



            //columnNumber - current.length() exprime la colonne de la premiere lettre du token trouve
            if (type != null)
                return new Token(type, new TokenAttribute(), lineNumber, columnNumber - current.length());

            if(current.equals("true"))
                return new Token(TokenType.BOOLEAN_CONST, new TokenAttribute(true), lineNumber, columnNumber - current.length());
            else if(current.equals("false"))
                return new Token(TokenType.BOOLEAN_CONST, new TokenAttribute(false), lineNumber, columnNumber - current.length());

            // token is an identifier
            return new Token(TokenType.ID, new TokenAttribute(current), lineNumber, columnNumber - current.length());
        }

        // integer literal ([0-9]+) OR float literal ([0-9]+.[0-9]+)
        if (Character.isDigit(nextChar)) {

            // create string representation of number
            String numString = Character.toString((char) nextChar);
            columnNumber++;
            nextChar = getChar();

            // concatenate remaining sequence of digits
            while (Character.isDigit(nextChar)) {
                numString += (char) nextChar;
                columnNumber++;
                nextChar = getChar();
            }



            // return integer literal token
            return new Token(TokenType.INT_CONST, new TokenAttribute(Integer.parseInt(numString)), lineNumber, columnNumber - numString.length());
        }



        // EOF reached
        if (nextChar == -1)
            return new Token(TokenType.EOF, new TokenAttribute(), lineNumber, columnNumber);

        // check for binops
        switch (nextChar) {

            case '&':
                columnNumber++;
                nextChar = getChar();

                // check if next char is '&' to match '&&' binop
                if (nextChar == '&') {
                    nextChar = getChar();
                    return new Token(TokenType.AND, new TokenAttribute(), lineNumber, columnNumber - 2);
                } else
                    return new Token(TokenType.UNKNOWN, new TokenAttribute(), lineNumber, columnNumber - 1);

            case '|':
                columnNumber++;
                nextChar = getChar();

                // check if next char is '|' to match '||' binop
                if (nextChar == '|') {
                    nextChar = getChar();
                    return new Token(TokenType.OR, new TokenAttribute(), lineNumber, columnNumber - 2);
                } else
                    return new Token(TokenType.UNKNOWN, new TokenAttribute(), lineNumber, columnNumber - 1);

            case '=':
                columnNumber++;
                nextChar = getChar();

                // check if next char is '=' to match '==' binop
                if (nextChar == '=') {
                    nextChar = getChar();
                    return new Token(TokenType.EQ, new TokenAttribute(), lineNumber, columnNumber - 2);
                }

            case '!':
                columnNumber++;
                nextChar = getChar();

                // check if next char is '!' to match '!=' binop
                if (nextChar == '=') {
                    nextChar = getChar();
                    return new Token(TokenType.NEQ, new TokenAttribute(), lineNumber, columnNumber - 2);
                }
                else
                    return new Token(TokenType.NOT, new TokenAttribute(), lineNumber, columnNumber - 1);

            case '[':
                columnNumber++;
                nextChar = getChar();
                //check if next char is ']' to match []globally reserved words
                if(nextChar == ']') {
                    nextChar = getChar();
                    return new Token(TokenType.GLOBALLY,new TokenAttribute(),lineNumber,columnNumber-1);
                } else {
                    return new Token(TokenType.UNKNOWN, new TokenAttribute(), lineNumber, columnNumber - 1);
                }



            case '<':
                columnNumber++;
                nextChar = getChar();

                // check if next char is '<' to match '<=' binop
                if(nextChar == '>') {
                    nextChar = getChar();
                    return new Token(TokenType.EVENTUALLY,new TokenAttribute(),lineNumber,columnNumber-1);
                } else if (nextChar == '=') {
                    nextChar = getChar();
                    return new Token(TokenType.LT_EQ, new TokenAttribute(), lineNumber, columnNumber - 2);
                } else
                    return new Token(TokenType.LT, new TokenAttribute(), lineNumber, columnNumber - 1);

            case '>':
                columnNumber++;
                nextChar = getChar();

                // check if next char is '<' to match '<=' binop
                if (nextChar == '=') {
                    nextChar = getChar();
                    return new Token(TokenType.RT_EQ, new TokenAttribute(), lineNumber, columnNumber - 2);
                } else
                    return new Token(TokenType.RT, new TokenAttribute(), lineNumber, columnNumber - 1);

            case '+':
                columnNumber++;
                nextChar = getChar();
                return new Token(TokenType.PLUS, new TokenAttribute(), lineNumber, columnNumber - 1);

            case '-':
                columnNumber++;
                nextChar = getChar();
                //check if next char is '>' to match '-> for Next verifier operator'
                if(nextChar == '>') {
                    nextChar = getChar();
                    return new Token(TokenType.NEXT,new TokenAttribute(),lineNumber,columnNumber-2);
                }
                return new Token(TokenType.MINUS, new TokenAttribute(), lineNumber, columnNumber - 1);

            case '*':
                columnNumber++;
                nextChar = getChar();
                return new Token(TokenType.TIMES, new TokenAttribute(), lineNumber, columnNumber - 1);

            case '/':
                columnNumber++;
                nextChar = getChar();
                return new Token(TokenType.DIV, new TokenAttribute(), lineNumber, columnNumber - 1);

            case '%':
                columnNumber++;
                nextChar = getChar();
                return new Token(TokenType.MOD, new TokenAttribute(), lineNumber, columnNumber - 1);
        }

        // check for punctuation
        TokenType type = punctuation.get((char) nextChar);
        columnNumber++;
        nextChar = getChar();

        // found punctuation token
        if (type != null)
            return new Token(type, new TokenAttribute(), lineNumber, columnNumber - 1);

        // token type is unknown
        return new Token(TokenType.UNKNOWN, new TokenAttribute(), lineNumber, columnNumber - 1);
    }
}