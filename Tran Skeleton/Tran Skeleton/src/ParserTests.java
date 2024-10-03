import AST.TranNode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ParserTests {
    @Test
    public void testInterface() throws Exception {
        List<Token> tokens = new ArrayList<>();
        tokens.add(new Token(Token.TokenTypes.INTERFACE, 1, 1, "interface"));
        tokens.add(new Token(Token.TokenTypes.WORD, 1, 11, "someName"));
        tokens.add(new Token(Token.TokenTypes.NEWLINE, 1, 19));
        tokens.add(new Token(Token.TokenTypes.INDENT, 2, 1));
        tokens.add(new Token(Token.TokenTypes.WORD, 2, 2, "updateClock"));
        tokens.add(new Token(Token.TokenTypes.LPAREN, 2, 13));
        tokens.add(new Token(Token.TokenTypes.RPAREN, 2, 14));
        tokens.add(new Token(Token.TokenTypes.NEWLINE, 2, 15));
        tokens.add(new Token(Token.TokenTypes.WORD, 3, 2, "square"));
        tokens.add(new Token(Token.TokenTypes.LPAREN, 3, 8));
        tokens.add(new Token(Token.TokenTypes.RPAREN, 3, 9));
        tokens.add(new Token(Token.TokenTypes.COLON, 3, 11));
        tokens.add(new Token(Token.TokenTypes.WORD, 3, 13, "number"));
        tokens.add(new Token(Token.TokenTypes.WORD, 3, 20, "s"));
        tokens.add(new Token(Token.TokenTypes.DEDENT, 4, 23));

        var tran = new TranNode();
        var p = new Parser(tran, tokens);
        p.Tran();
        Assertions.assertEquals(1, tran.Interfaces.size());
        Assertions.assertEquals(2, tran.Interfaces.getFirst().methods.size());
    }

    @Test
    public void testParserConstructor() throws Exception {
        // Given an input string and expected token count
        List<Token> tokens = new ArrayList<>();
        tokens.add(new Token(Token.TokenTypes.INTERFACE, 1, 1, "interface"));
        tokens.add(new Token(Token.TokenTypes.WORD, 1, 11, "someName"));
        tokens.add(new Token(Token.TokenTypes.NEWLINE, 1, 19));
        tokens.add(new Token(Token.TokenTypes.INDENT, 2, 1));
        tokens.add(new Token(Token.TokenTypes.WORD, 2, 2, "updateClock"));
        tokens.add(new Token(Token.TokenTypes.LPAREN, 2, 13));
        tokens.add(new Token(Token.TokenTypes.RPAREN, 2, 14));
        tokens.add(new Token(Token.TokenTypes.NEWLINE, 2, 15));
        tokens.add(new Token(Token.TokenTypes.WORD, 3, 2, "square"));
        tokens.add(new Token(Token.TokenTypes.LPAREN, 3, 8));
        tokens.add(new Token(Token.TokenTypes.RPAREN, 3, 9));
        tokens.add(new Token(Token.TokenTypes.COLON, 3, 11));
        tokens.add(new Token(Token.TokenTypes.WORD, 3, 13, "number"));
        tokens.add(new Token(Token.TokenTypes.WORD, 3, 20, "s"));
        tokens.add(new Token(Token.TokenTypes.DEDENT, 4, 23));


        var tran = new TranNode();
        var p = new Parser(tran, tokens);

        // Create a TranNode
        TranNode tranNode = new TranNode();

        // Create the Parser with the TranNode and tokens
        Parser parser = new Parser(tranNode, tokens);

    }

    // Helper method to create tokens
    private Token createToken(Token.TokenTypes type, int line, int column, String value) {
        return new Token(type, line, column, value);
    }

    private Token createToken(Token.TokenTypes type, int line, int column) {
        return new Token(type, line, column);
    }

    @Test
    public void testMatchAndRemove() {
        Token token1 = createToken(Token.TokenTypes.WORD, 1, 1, "hello");
        Token token2 = createToken(Token.TokenTypes.NUMBER, 1, 2, "123");
        TokenManager tokenManager = new TokenManager(new LinkedList<>(Arrays.asList(token1, token2)));

        // Check if the first token matches and is removed
        Optional<Token> matchedToken = tokenManager.matchAndRemove(Token.TokenTypes.WORD);
        assertTrue(matchedToken.isPresent(), "Token should match WORD and be removed");
        assertEquals(token1, matchedToken.get(), "The matched token should be the first token");

        // Check if the second token is now the first
        Optional<Token> nextToken = tokenManager.matchAndRemove(Token.TokenTypes.NUMBER);
        assertTrue(nextToken.isPresent(), "Token should match NUMBER and be removed");
        assertEquals(token2, nextToken.get(), "The next token should be the second token");

        // Check if token manager is empty
        assertTrue(tokenManager.done(), "Token manager should be empty");
    }

    @Test
    public void testPeek() {
        Token token1 = createToken(Token.TokenTypes.WORD, 1, 1, "hello");
        Token token2 = createToken(Token.TokenTypes.NUMBER, 1, 2, "123");
        TokenManager tokenManager = new TokenManager(new LinkedList<>(Arrays.asList(token1, token2)));

        // Check peeking the first token
        Optional<Token> peekToken = tokenManager.peek(0);
        assertTrue(peekToken.isPresent(), "First token should be peeked");
        assertEquals(token1, peekToken.get(), "The first peeked token should be the first token");

        // Check peeking the second token
        Optional<Token> secondPeekToken = tokenManager.peek(1);
        assertTrue(secondPeekToken.isPresent(), "Second token should be peeked");
        assertEquals(token2, secondPeekToken.get(), "The second peeked token should be the second token");


    }

    @Test
    public void testNextTwoTokensMatch() {
        Token token1 = createToken(Token.TokenTypes.WORD, 1, 1, "hello");
        Token token2 = createToken(Token.TokenTypes.NUMBER, 1, 2, "123");
        TokenManager tokenManager = new TokenManager(new LinkedList<>(Arrays.asList(token1, token2)));

        // Check if the first two tokens match the given types
        assertTrue(tokenManager.nextTwoTokensMatch(Token.TokenTypes.WORD, Token.TokenTypes.NUMBER),
                "First two tokens should match WORD and NUMBER");


    }

    @Test
    public void testGetCurrentLine() {
        Token token1 = createToken(Token.TokenTypes.WORD, 1, 1, "hello");
        TokenManager tokenManager = new TokenManager(new LinkedList<>(Arrays.asList(token1)));

        // Check if the current line is returned correctly
        assertEquals(1, tokenManager.getCurrentLine(), "The current line should be 1");
    }

    @Test
    public void testGetCurrentColumnNumber() {
        Token token1 = createToken(Token.TokenTypes.WORD, 1, 5, "hello");
        TokenManager tokenManager = new TokenManager(new LinkedList<>(Arrays.asList(token1)));

        // Check if the current column is returned correctly
        assertEquals(5, tokenManager.getCurrentColumnNumber(), "The current column should be 5");
    }

    @Test
    public void customtest() throws Exception {
        List<Token> tokens = new ArrayList<>();
        tokens.add(new Token(Token.TokenTypes.INTERFACE, 1, 1, "interface"));
        tokens.add(new Token(Token.TokenTypes.WORD, 1, 11, "someName"));
        tokens.add(new Token(Token.TokenTypes.NEWLINE, 1, 19));
        tokens.add(new Token(Token.TokenTypes.INDENT, 2, 1));
        tokens.add(new Token(Token.TokenTypes.WORD, 2, 2, "updateClock"));
        tokens.add(new Token(Token.TokenTypes.LPAREN, 2, 13));
        tokens.add(new Token(Token.TokenTypes.RPAREN, 2, 14));
        tokens.add(new Token(Token.TokenTypes.NEWLINE, 2, 15));
        tokens.add(new Token(Token.TokenTypes.WORD, 3, 2, "JacksMethod"));
        tokens.add(new Token(Token.TokenTypes.LPAREN, 3, 8));
        tokens.add(new Token(Token.TokenTypes.RPAREN, 3, 9));
        tokens.add(new Token(Token.TokenTypes.COLON, 3, 11));
        tokens.add(new Token(Token.TokenTypes.WORD, 3, 13, "number"));
        tokens.add(new Token(Token.TokenTypes.WORD, 3, 20, "s"));
        tokens.add(new Token(Token.TokenTypes.NEWLINE, 2, 15));
        tokens.add(new Token(Token.TokenTypes.DEDENT, 4, 23));
        tokens.add(new Token(Token.TokenTypes.INTERFACE, 1, 1, "interface"));
        tokens.add(new Token(Token.TokenTypes.WORD, 1, 11, "someName"));
        tokens.add(new Token(Token.TokenTypes.NEWLINE, 1, 19));
        tokens.add(new Token(Token.TokenTypes.INDENT, 2, 1));
        tokens.add(new Token(Token.TokenTypes.WORD, 2, 2, "updateClock"));
        tokens.add(new Token(Token.TokenTypes.LPAREN, 2, 13));
        tokens.add(new Token(Token.TokenTypes.RPAREN, 2, 14));
        tokens.add(new Token(Token.TokenTypes.NEWLINE, 2, 15));
        tokens.add(new Token(Token.TokenTypes.WORD, 3, 2, "square"));
        tokens.add(new Token(Token.TokenTypes.LPAREN, 3, 8));
        tokens.add(new Token(Token.TokenTypes.RPAREN, 3, 9));
        tokens.add(new Token(Token.TokenTypes.COLON, 3, 11));
        tokens.add(new Token(Token.TokenTypes.WORD, 3, 13, "number"));
        tokens.add(new Token(Token.TokenTypes.WORD, 3, 20, "s"));
        tokens.add(new Token(Token.TokenTypes.COMMA, 3, 9));
        tokens.add(new Token(Token.TokenTypes.WORD, 3, 13, "String"));
        tokens.add(new Token(Token.TokenTypes.WORD, 3, 20, "salty"));
        tokens.add(new Token(Token.TokenTypes.DEDENT, 4, 23));


        var tran = new TranNode();
        var p = new Parser(tran, tokens);
        p.Tran();
        Assertions.assertEquals("someName", tran.Interfaces.get(1).name);
        Assertions.assertEquals(2, tran.Interfaces.get(1).methods.size());
        Assertions.assertEquals("JacksMethod", tran.Interfaces.get(0).methods.get(1).name);
        Assertions.assertEquals("s", tran.Interfaces.get(0).methods.get(1).returns.get(0).name);
        Assertions.assertEquals("number", tran.Interfaces.get(0).methods.get(1).returns.get(0).type);
        Assertions.assertEquals("s", tran.Interfaces.get(1).methods.get(1).returns.get(0).name);
        Assertions.assertEquals("number", tran.Interfaces.get(1).methods.get(1).returns.get(0).type);
        Assertions.assertEquals("salty", tran.Interfaces.get(1).methods.get(1).returns.get(1).name);
        Assertions.assertEquals("String", tran.Interfaces.get(1).methods.get(1).returns.get(1).type);

        for(int i = 0; i < tran.Interfaces.get(0).methods.get(1).returns.size(); ++i) {
            System.out.println("type: " + tran.Interfaces.get(0).methods.get(1).returns.get(i).type + "\nname: " + tran.Interfaces.get(0).methods.get(1).returns.get(i).name);
        }

        //Assertions.assertEquals("number", tran.Interfaces.get(0).methods.get(1).parameters.get(0).type);
    }
}