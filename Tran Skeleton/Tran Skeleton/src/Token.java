import java.util.Optional;

public class Token {
    public enum TokenTypes {
        WORD,  NUMBER, // These require a String value
        ASSIGN, LPAREN, RPAREN, COLON, DOT, // punctuation
        PLUS, MINUS, TIMES, DIVIDE, MODULO, COMMA, // punctuation
        EQUAL, NOTEQUAL, LESSTHAN, LESSTHANEQUAL, GREATERTHAN, GREATERTHANEQUAL, // punctuation
        ACCESSOR, MUTATOR, IMPLEMENTS, CLASS, INTERFACE, LOOP, IF, ELSE, // keywords
        INDENT, DEDENT, NEWLINE, // blocks
        QUOTEDSTRING, QUOTEDCHARACTER,
        TRUE, NEW, FALSE,
        PRIVATE, SHARED, CONSTRUCT,
        AND, OR, NOT
    }

    private Optional<String> value;

    private final TokenTypes type;

    private final int columnNumber;

    public int getColumnNumber() {
        return columnNumber;
    }

    private final int lineNumber;

    public int getLineNumber() {
        return lineNumber;
    }

    public Token(TokenTypes type, int lineNumber, int columnNumber ){
        this.lineNumber = lineNumber;
        this.columnNumber = columnNumber;
        this.type = type;
        this.value = Optional.empty();
    }

    public Token(TokenTypes type, int lineNumber, int columnNumber, String value ){
        this(type, lineNumber,columnNumber);
        this.value = Optional.of(value);
    }

    public TokenTypes getType() { return type; }

    public String getValue() {
           return value.orElse("");
    }

    @Override
    public String toString() {
       return type + " " + (value.orElse("")) + "@" + lineNumber + "," + columnNumber;
    }
}
