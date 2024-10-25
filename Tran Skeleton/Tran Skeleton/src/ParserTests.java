import AST.TranNode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

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
        tokens.add(new Token(Token.TokenTypes.NEWLINE, 1, 19));
        tokens.add(new Token(Token.TokenTypes.NEWLINE, 1, 19));
        tokens.add(new Token(Token.TokenTypes.NEWLINE, 1, 19));
        tokens.add(new Token(Token.TokenTypes.INTERFACE, 1, 20, "interface"));
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
        tokens.add(new Token(Token.TokenTypes.NEWLINE, 1, 19));
        tokens.add(new Token(Token.TokenTypes.NEWLINE, 1, 19));
        tokens.add(new Token(Token.TokenTypes.NEWLINE, 1, 19));
        tokens.add(new Token(Token.TokenTypes.NEWLINE, 1, 19));
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
        tokens.add(new Token(Token.TokenTypes.NEWLINE, 1, 19));
        tokens.add(new Token(Token.TokenTypes.DEDENT, 4, 23));
        tokens.add(new Token(Token.TokenTypes.NEWLINE, 1, 19));
        tokens.add(new Token(Token.TokenTypes.NEWLINE, 1, 19));



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

    @Test
    public void tokenManagerDoneTest(){
        // 0 Token Input
        TokenManager emptyManager = new TokenManager(new LinkedList<>());
        assertTrue(emptyManager.done(), "Token manager should be empty");

        // 1 Token Input
        Token wordToken = createToken(Token.TokenTypes.WORD, 1, 1, "test");
        TokenManager wordTokenManager = new TokenManager(new LinkedList<>(Arrays.asList(wordToken)));
        wordTokenManager.matchAndRemove(Token.TokenTypes.WORD);
        assertTrue(wordTokenManager.done(), "Token manager should be done");
    }

    @Test
    public void tokenManagerMatchAndRemoveTest(){
        Token wordToken = createToken(Token.TokenTypes.WORD, 1, 1, "test");
        TokenManager wordTokenManager = new TokenManager(new LinkedList<>(Arrays.asList(wordToken)));
        wordTokenManager.matchAndRemove(Token.TokenTypes.WORD);
        assertTrue(wordTokenManager.done(), "Token manager should be done");
    }

    @Test
    public void tokenManagerPeekTest(){
        Token wordToken = createToken(Token.TokenTypes.WORD, 1, 1, "test");
        Token numberToken = createToken(Token.TokenTypes.NUMBER, 1, 2, "123");
        TokenManager tokenManager = new TokenManager(new LinkedList<>(Arrays.asList(wordToken, numberToken)));
        Optional<Token> peekToken = tokenManager.peek(0);
        assertTrue(peekToken.isPresent(), "First token should be peeked");
        peekToken = tokenManager.peek(1);
        assertTrue(peekToken.isPresent(), "Second token should be peeked");
        assertThrows(IndexOutOfBoundsException.class, () -> tokenManager.peek(2));
        assertThrows(IndexOutOfBoundsException.class, () -> tokenManager.peek(-1));
    }

    @Test
    public void tokenManagerNextTwoTokensMatchTest(){
        Token wordToken = createToken(Token.TokenTypes.WORD, 1, 1, "test");
        Token numberToken = createToken(Token.TokenTypes.NUMBER, 1, 2, "123");
        TokenManager tokenManager = new TokenManager(new LinkedList<>(Arrays.asList(wordToken, numberToken)));
        assertTrue(tokenManager.nextTwoTokensMatch(Token.TokenTypes.WORD,Token.TokenTypes.NUMBER));
    }

    @Test
    public void tokenManagerGetCurrentLineTest(){
        Token wordToken = createToken(Token.TokenTypes.WORD, 1, 1, "test");
        Token newLineToken = createToken(Token.TokenTypes.NEWLINE, 1, 2, "newline");
        Token numberToken = createToken(Token.TokenTypes.NUMBER, 2, 3, "123");
        TokenManager tokenManager = new TokenManager(new LinkedList<>(Arrays.asList(wordToken, numberToken)));
        assertTrue(tokenManager.getCurrentLine() == 1, "The current line should be 1");
        tokenManager.matchAndRemove(Token.TokenTypes.WORD);
        tokenManager.matchAndRemove(Token.TokenTypes.NEWLINE);
        assertTrue(tokenManager.getCurrentLine() == 2, "The current line should be 2");
    }

    @Test
    public void tokenManagerGetCurrentColumnNumberTest(){
        Token wordToken = createToken(Token.TokenTypes.WORD, 1, 1, "test");
        Token newLineToken = createToken(Token.TokenTypes.INDENT, 1, 2, "newline");
        Token numberToken = createToken(Token.TokenTypes.NUMBER, 2, 5, "123");
        TokenManager tokenManager = new TokenManager(new LinkedList<>(Arrays.asList(wordToken, numberToken)));
        assertTrue(tokenManager.getCurrentColumnNumber() == 1, "The current column should be 1");
        tokenManager.matchAndRemove(Token.TokenTypes.WORD);
        tokenManager.matchAndRemove(Token.TokenTypes.INDENT);
        assertTrue(tokenManager.getCurrentColumnNumber() == 5, "The current column should be 5");
    }


    //Parser 2 tests

    @Test
    public void testClassParsing() throws Exception {
        var tran = new TranNode();
        List list = List.of(
                new Token(Token.TokenTypes.CLASS, 1, 1),
                new Token(Token.TokenTypes.WORD, 1, 2, "Tran"),
                new Token(Token.TokenTypes.NEWLINE, 1, 3),
                new Token(Token.TokenTypes.INDENT, 2, 1),
                new Token(Token.TokenTypes.WORD, 2, 2, "number"),
                new Token(Token.TokenTypes.WORD, 2, 3, "x"),
                new Token(Token.TokenTypes.NEWLINE, 2, 4),
                new Token(Token.TokenTypes.WORD, 4, 2, "string"),
                new Token(Token.TokenTypes.WORD, 4, 3, "y"),
                new Token(Token.TokenTypes.DEDENT, 5, 1)
        );
        var tokens = new LinkedList<>(list);//converting list to linked list so the token manager can handle this
        var p = new Parser(tran, tokens);
        p.Tran();
        Assertions.assertEquals(1, tran.Classes.size());
        Assertions.assertEquals("Tran", tran.Classes.getFirst().name);

    }

    // Test 2: Class with Implements
    /*
The below code is in the token list for this test:
interface someName
    square() : number s
interface someNameTwo
    squareTwo() : number STwo
class TranExample implements someName,someNameTwo
    number x
    string y
     */
    @Test
    public void testClassImplements() throws Exception {
        var tran = new TranNode();
        List list = List.of(
                new Token(Token.TokenTypes.INTERFACE, 1, 1),
                new Token(Token.TokenTypes.WORD, 1, 1, "someName"),
                new Token(Token.TokenTypes.NEWLINE, 1, 3),
                new Token(Token.TokenTypes.INDENT, 2, 1),
                new Token(Token.TokenTypes.WORD, 2, 2, "square"),
                new Token(Token.TokenTypes.LPAREN, 2, 3),
                new Token(Token.TokenTypes.RPAREN, 2, 4),
                new Token(Token.TokenTypes.COLON, 2, 5),
                new Token(Token.TokenTypes.WORD, 2, 6, "number"),
                new Token(Token.TokenTypes.WORD, 2, 7, "s"),
                new Token(Token.TokenTypes.NEWLINE, 2, 8),
                new Token(Token.TokenTypes.DEDENT, 3, 1),
                new Token(Token.TokenTypes.NEWLINE, 4, 2),

                new Token(Token.TokenTypes.INTERFACE, 5, 1),
                new Token(Token.TokenTypes.WORD, 5, 1, "someNameTwo"),
                new Token(Token.TokenTypes.NEWLINE, 5, 3),
                new Token(Token.TokenTypes.INDENT, 6, 1),
                new Token(Token.TokenTypes.WORD, 6, 2, "squareTwo"),
                new Token(Token.TokenTypes.LPAREN, 6, 3),
                new Token(Token.TokenTypes.RPAREN, 6, 4),
                new Token(Token.TokenTypes.COLON, 6, 5),
                new Token(Token.TokenTypes.WORD, 6, 6, "number"),
                new Token(Token.TokenTypes.WORD, 6, 7, "STwo"),
                new Token(Token.TokenTypes.NEWLINE, 6, 8),
                new Token(Token.TokenTypes.DEDENT, 7, 1),
                new Token(Token.TokenTypes.NEWLINE, 8, 2),


                new Token(Token.TokenTypes.CLASS, 9, 1),
                new Token(Token.TokenTypes.WORD, 9, 2, "Tran"),
                new Token(Token.TokenTypes.IMPLEMENTS, 9, 3),
                new Token(Token.TokenTypes.WORD, 9, 4, "someName"),
                new Token(Token.TokenTypes.COMMA, 9, 9),
                new Token(Token.TokenTypes.WORD, 9, 4, "someNameTwo"),
                new Token(Token.TokenTypes.NEWLINE, 9, 3),
                new Token(Token.TokenTypes.INDENT, 10, 1),
                new Token(Token.TokenTypes.WORD, 10, 2, "number"),
                new Token(Token.TokenTypes.WORD, 10, 3, "x"),
                new Token(Token.TokenTypes.NEWLINE, 10, 4),
                new Token(Token.TokenTypes.WORD, 11, 2, "string"),
                new Token(Token.TokenTypes.WORD, 11, 3, "y"),
                new Token(Token.TokenTypes.DEDENT, 11, 1)
        );
        var tokens = new LinkedList<>(list);//converting list to linked list so the token manager can handle this
        var p = new Parser(tran, tokens);
        p.Tran();
        var clazz = tran.Classes.getFirst();
        Assertions.assertEquals("Tran", clazz.name);
        Assertions.assertEquals(2, clazz.interfaces.size());
        Assertions.assertEquals("someName", clazz.interfaces.getFirst());
        Assertions.assertEquals("someNameTwo", clazz.interfaces.get(1));
        Assertions.assertEquals(2, tran.Interfaces.size());


    }
    // Test 3: Constructor Parsing
    /*
class Tran
    number x
    string y
    construct()
     */
    @Test
    public void testConstructorParsing() throws Exception {
        var tran = new TranNode();
        var list = List.of(
                new Token(Token.TokenTypes.CLASS, 1, 1),
                new Token(Token.TokenTypes.WORD, 1, 2, "Tran"),
                new Token(Token.TokenTypes.NEWLINE, 1, 3),
                new Token(Token.TokenTypes.INDENT, 2, 1),
                new Token(Token.TokenTypes.WORD, 2, 2, "number"),
                new Token(Token.TokenTypes.WORD, 2, 3, "x"),
                new Token(Token.TokenTypes.NEWLINE, 2, 4),
                new Token(Token.TokenTypes.WORD, 4, 2, "string"),
                new Token(Token.TokenTypes.WORD, 4, 3, "y"),
                new Token(Token.TokenTypes.NEWLINE, 4, 3),
                new Token(Token.TokenTypes.CONSTRUCT, 5, 2),
                new Token(Token.TokenTypes.LPAREN, 5, 3),
                new Token(Token.TokenTypes.RPAREN, 5, 4),
                new Token(Token.TokenTypes.NEWLINE, 5, 5),
                new Token(Token.TokenTypes.INDENT, 2, 1),
                new Token(Token.TokenTypes.DEDENT, 2, 1),

                new Token(Token.TokenTypes.DEDENT, 8, 1)

        );
        var tokens = new LinkedList<>(list);//converting list to linked list so the token manager can handle this
        var p = new Parser(tran, tokens);
        p.Tran();
        Assertions.assertEquals(1, tran.Classes.getFirst().constructors.size());
        Assertions.assertEquals(0, tran.Classes.getFirst().constructors.getFirst().statements.size());//

    }


    // Test 4: Class with members
    /*
The below code is in the token list for this test:
interface someName
    square() : number s
class TranExample implements someName
    number m
    start()
        number x
        number y
     */
    @Test
    public void testMembers_and_methoddeclaration() throws Exception {
        var tran = new TranNode();
        //Ignore the line and column number here, all you will be using the line number and columnNumber in parser is for printing syntax error in Tran code lexed by you.
        List<Token> list = List.of(
                new Token(Token.TokenTypes.INTERFACE, 1, 9),
                new Token(Token.TokenTypes.WORD, 1, 18, "someName"),
                new Token(Token.TokenTypes.NEWLINE, 2, 0),
                new Token(Token.TokenTypes.INDENT, 2, 4),
                new Token(Token.TokenTypes.WORD, 2, 10, "square"),
                new Token(Token.TokenTypes.LPAREN, 2, 11),
                new Token(Token.TokenTypes.RPAREN, 2, 12),
                new Token(Token.TokenTypes.COLON, 2, 14),
                new Token(Token.TokenTypes.WORD, 2, 21, "number"),
                new Token(Token.TokenTypes.WORD, 2, 23, "s"),
                new Token(Token.TokenTypes.NEWLINE, 3, 0),
                new Token(Token.TokenTypes.DEDENT, 3, 0),
                new Token(Token.TokenTypes.CLASS, 3, 5),
                new Token(Token.TokenTypes.WORD, 3, 17, "TranExample"),
                new Token(Token.TokenTypes.IMPLEMENTS, 3, 28),
                new Token(Token.TokenTypes.WORD, 3, 37, "someName"),
                new Token(Token.TokenTypes.NEWLINE, 4, 0),
                new Token(Token.TokenTypes.INDENT, 4, 4),
                new Token(Token.TokenTypes.WORD, 4, 10, "number"),
                new Token(Token.TokenTypes.WORD, 4, 12, "m"),
                new Token(Token.TokenTypes.NEWLINE, 5, 0),
                new Token(Token.TokenTypes.WORD, 4, 10, "string"),
                new Token(Token.TokenTypes.WORD, 4, 12, "str"),
                new Token(Token.TokenTypes.NEWLINE, 5, 0),
                new Token(Token.TokenTypes.WORD, 5, 9, "start"),
                new Token(Token.TokenTypes.LPAREN, 5, 10),
                new Token(Token.TokenTypes.RPAREN, 5, 11),
                new Token(Token.TokenTypes.NEWLINE, 6, 0),
                new Token(Token.TokenTypes.INDENT, 6, 8),
                new Token(Token.TokenTypes.WORD, 6, 14, "number"),
                new Token(Token.TokenTypes.WORD, 6, 16, "x"),
                new Token(Token.TokenTypes.NEWLINE, 7, 0),
                new Token(Token.TokenTypes.WORD, 7, 14, "number"),
                new Token(Token.TokenTypes.WORD, 7, 16, "y"),
                new Token(Token.TokenTypes.NEWLINE, 8, 0),
                new Token(Token.TokenTypes.DEDENT, 8, 4),
                new Token(Token.TokenTypes.DEDENT, 8, 4)

        );

           /*
        Lexer L= new Lexer("interface someName\n" +
                "    square() : number s\n" +
                "class TranExample implements someName\n" +
                "    number m\n"+
                "    start()\n" +
                "        number x\n" +
                "        number y\n" );
        var LT= L.Lex();
         System.out.println(LT);
        */

        var tokens = new LinkedList<>(list);//converting list to linked list so the token manager can handle this
        var p = new Parser(tran, tokens);
        p.Tran();
        var clazz = tran.Classes.getFirst();
        Assertions.assertEquals("s", tran.Interfaces.get(0).methods.getFirst().returns.get(0).name);
        Assertions.assertEquals("someName", clazz.interfaces.getFirst());
        Assertions.assertEquals(2, tran.Classes.getFirst().members.size());
        Assertions.assertEquals("m", tran.Classes.getFirst().members.getFirst().declaration.name);
        Assertions.assertEquals(2, tran.Classes.getFirst().methods.getFirst().locals.size());
        Assertions.assertEquals("x", tran.Classes.getFirst().methods.getFirst().locals.get(0).name);
        Assertions.assertEquals("y", tran.Classes.getFirst().methods.getFirst().locals.get(1).name);
        Assertions.assertEquals("start", tran.Classes.getFirst().methods.getFirst().name); //Added test testing for name
    }

    /*
    Test partially broken
     */
    @Test
    public void testAccessors() throws Exception {
        var tran = new TranNode();
        //Ignore the line and column number here, all you will be using the line number and columnNumber in parser is for printing syntax error in Tran code lexed by you.
        Lexer L= new Lexer("interface someName\n" +
                "    square() : number s\n" +
                "class TranExample implements someName\n" +
                "\tnumber m\n" +
                "\t\taccessor:\n"
                + "\t\t\ta=b"

        );
        var LT= L.Lex();
        System.out.println(LT);
        var tokens = new LinkedList<>(LT);//converting list to linked list so the token manager can handle this
        var p = new Parser(tran, LT);
        p.Tran();
        var clazz = tran.Classes.getFirst();
    }

    /*
    Test Partially Broken
     */
    @Test
    public void testmutator() throws Exception {
        var tran = new TranNode();
        //Ignore the line and column number here, all you will be using the line number and columnNumber in parser is for printing syntax error in Tran code lexed by you.
        Lexer L= new Lexer("interface someName\n" +
                "    square() : number s\n" +
                "class TranExample implements someName\n" +
                "\tnumber m\n" +
                "\t\tmutator:\n"
                +"\t\t\ta=b"

        );
        var LT= L.Lex();
        //System.out.println(LT);LT.add(new Token(Token.TokenTypes.DEDENT, 9, 18)); for some reason added a ghost dedent

        var tokens = new LinkedList<>(LT);//converting list to linked list so the token manager can handle this
        var p = new Parser(tran, LT);
        p.Tran();
        var clazz = tran.Classes.getFirst();
    }
    @Test
    public void testLoop() throws Exception {
        Lexer L = new Lexer("class Tran\n" +
                "\thelloWorld()\n" +
                "\t\tloop(a==b)\n" +
                "\t\t\ta=b" );
        var rev= L.Lex();
        TranNode t= new TranNode();
        Parser p= new Parser(t,rev);
        p.Tran();
    }

    @Test
    public void testClassIf() throws Exception {
        Lexer L = new Lexer("class Tran\n" +
                "\thelloWorld()\n" +
                "\t\tif c>=d\n" +
                "\t\t\ta=b");
        var rev= L.Lex();
        TranNode t= new TranNode();
        Parser p= new Parser(t,rev);
        p.Tran();
    }



    //Custom Parser 2 tests
    @Test
    public void testConstructorParsing_extra() throws Exception {
        var tran = new TranNode();
        var list = List.of(
                new Token(Token.TokenTypes.CLASS, 1, 1),
                new Token(Token.TokenTypes.WORD, 1, 2, "Tran"),
                new Token(Token.TokenTypes.NEWLINE, 1, 3),
                new Token(Token.TokenTypes.INDENT, 2, 1),
                new Token(Token.TokenTypes.WORD, 2, 2, "number"),
                new Token(Token.TokenTypes.WORD, 2, 3, "x"),
                new Token(Token.TokenTypes.NEWLINE, 2, 4),
                new Token(Token.TokenTypes.WORD, 4, 2, "string"),
                new Token(Token.TokenTypes.WORD, 4, 3, "y"),
                new Token(Token.TokenTypes.NEWLINE, 4, 3),
                new Token(Token.TokenTypes.CONSTRUCT, 5, 2),
                new Token(Token.TokenTypes.LPAREN, 5, 3),
                //Add parameters
                new Token(Token.TokenTypes.WORD, 3, 13, "number"),
                new Token(Token.TokenTypes.WORD, 3, 20, "s"),
                new Token(Token.TokenTypes.COMMA, 3, 9),
                new Token(Token.TokenTypes.WORD, 3, 13, "String"),
                new Token(Token.TokenTypes.WORD, 3, 20, "salty"),
                new Token(Token.TokenTypes.RPAREN, 5, 4),
                new Token(Token.TokenTypes.NEWLINE, 5, 5),
                new Token(Token.TokenTypes.INDENT, 2, 1),
                //Add Variable declarations
                new Token(Token.TokenTypes.WORD, 3, 13, "number"),
                new Token(Token.TokenTypes.WORD, 3, 20, "s"),
                new Token(Token.TokenTypes.NEWLINE, 5, 5),
                new Token(Token.TokenTypes.NEWLINE, 5, 5),
                new Token(Token.TokenTypes.NEWLINE, 5, 5),
                new Token(Token.TokenTypes.WORD, 3, 13, "number"),
                new Token(Token.TokenTypes.WORD, 3, 20, "x"),
                new Token(Token.TokenTypes.NEWLINE, 5, 5),
                new Token(Token.TokenTypes.DEDENT, 2, 1),
                new Token(Token.TokenTypes.DEDENT, 8, 1)

        );
        var tokens = new LinkedList<>(list);//converting list to linked list so the token manager can handle this
        var p = new Parser(tran, tokens);
        p.Tran();
        Assertions.assertEquals(1, tran.Classes.getFirst().constructors.size());
        Assertions.assertEquals(2, tran.Classes.getFirst().constructors.getFirst().parameters.size()); //Asserting that the number of parameters should be 2 in the constructor
        Assertions.assertEquals(0, tran.Classes.getFirst().constructors.getFirst().statements.size());//
        Assertions.assertEquals(2, tran.Classes.getFirst().constructors.getFirst().locals.size()); //Asserting that the number of parameters should be 2 in the constructor

    }

    @Test
    public void testMembers_and_methoddeclaration_extra() throws Exception {
        var tran = new TranNode();
        //Ignore the line and column number here, all you will be using the line number and columnNumber in parser is for printing syntax error in Tran code lexed by you.
        List<Token> list = List.of(
                new Token(Token.TokenTypes.INTERFACE, 1, 9),
                new Token(Token.TokenTypes.WORD, 1, 18, "someName"),
                new Token(Token.TokenTypes.NEWLINE, 2, 0),
                new Token(Token.TokenTypes.INDENT, 2, 4),
                new Token(Token.TokenTypes.WORD, 2, 10, "square"),
                new Token(Token.TokenTypes.LPAREN, 2, 11),
                new Token(Token.TokenTypes.RPAREN, 2, 12),
                new Token(Token.TokenTypes.COLON, 2, 14),
                new Token(Token.TokenTypes.WORD, 2, 21, "number"),
                new Token(Token.TokenTypes.WORD, 2, 23, "s"),
                new Token(Token.TokenTypes.NEWLINE, 3, 0),
                new Token(Token.TokenTypes.DEDENT, 3, 0),
                new Token(Token.TokenTypes.CLASS, 3, 5),
                new Token(Token.TokenTypes.WORD, 3, 17, "TranExample"),
                new Token(Token.TokenTypes.IMPLEMENTS, 3, 28),
                new Token(Token.TokenTypes.WORD, 3, 37, "someName"),
                new Token(Token.TokenTypes.NEWLINE, 4, 0),
                new Token(Token.TokenTypes.INDENT, 4, 4),
                new Token(Token.TokenTypes.WORD, 4, 10, "number"),
                new Token(Token.TokenTypes.WORD, 4, 12, "m"),
                new Token(Token.TokenTypes.NEWLINE, 5, 0),
                new Token(Token.TokenTypes.WORD, 4, 10, "string"),
                new Token(Token.TokenTypes.WORD, 4, 12, "str"),
                new Token(Token.TokenTypes.NEWLINE, 5, 0),
                new Token(Token.TokenTypes.PRIVATE, 4, 12, "private"),
                new Token(Token.TokenTypes.WORD, 5, 9, "start"),
                new Token(Token.TokenTypes.LPAREN, 5, 10),
                //add parameters
                new Token(Token.TokenTypes.WORD, 3, 13, "number"),
                new Token(Token.TokenTypes.WORD, 3, 20, "s"),
                new Token(Token.TokenTypes.COMMA, 3, 9),
                new Token(Token.TokenTypes.WORD, 3, 13, "String"),
                new Token(Token.TokenTypes.WORD, 3, 20, "salty"),
                new Token(Token.TokenTypes.RPAREN, 5, 11),
                //add returns
                new Token(Token.TokenTypes.COLON, 5, 0),
                new Token(Token.TokenTypes.WORD, 3, 13, "number"),
                new Token(Token.TokenTypes.WORD, 3, 20, "s"),
                new Token(Token.TokenTypes.COMMA, 3, 9),
                new Token(Token.TokenTypes.WORD, 3, 13, "String"),
                new Token(Token.TokenTypes.WORD, 3, 20, "salty"),
                new Token(Token.TokenTypes.NEWLINE, 6, 0),
                new Token(Token.TokenTypes.INDENT, 6, 8),
                new Token(Token.TokenTypes.WORD, 6, 14, "number"),
                new Token(Token.TokenTypes.WORD, 6, 16, "x"),
                new Token(Token.TokenTypes.NEWLINE, 7, 0), //Adding new newlines just for testing
                new Token(Token.TokenTypes.NEWLINE, 7, 0),
                new Token(Token.TokenTypes.NEWLINE, 7, 0),
                new Token(Token.TokenTypes.NEWLINE, 7, 0),
                new Token(Token.TokenTypes.WORD, 7, 14, "number"),
                new Token(Token.TokenTypes.WORD, 7, 16, "y"),
                new Token(Token.TokenTypes.NEWLINE, 7, 0),
                new Token(Token.TokenTypes.WORD, 6, 14, "number"),
                new Token(Token.TokenTypes.WORD, 6, 16, "x"),
                new Token(Token.TokenTypes.NEWLINE, 8, 0), //Adding extra newlines just for testing
                new Token(Token.TokenTypes.WORD, 6, 14, "number"),
                new Token(Token.TokenTypes.WORD, 7, 16, "y"),
                new Token(Token.TokenTypes.NEWLINE, 7, 0),
                new Token(Token.TokenTypes.NEWLINE, 7, 0),
                new Token(Token.TokenTypes.NEWLINE, 7, 0),
                new Token(Token.TokenTypes.DEDENT, 8, 4),
                new Token(Token.TokenTypes.DEDENT, 8, 4)

        );

        var tokens = new LinkedList<>(list);//converting list to linked list so the token manager can handle this
        var p = new Parser(tran, tokens);
        p.Tran();
        var clazz = tran.Classes.getFirst();
        Assertions.assertEquals("s", tran.Interfaces.get(0).methods.getFirst().returns.get(0).name);
        Assertions.assertEquals("someName", clazz.interfaces.getFirst());
        Assertions.assertEquals(2, tran.Classes.getFirst().members.size());
        Assertions.assertEquals("m", tran.Classes.getFirst().members.getFirst().declaration.name);
        Assertions.assertEquals(4, tran.Classes.getFirst().methods.getFirst().locals.size());
        Assertions.assertEquals("x", tran.Classes.getFirst().methods.getFirst().locals.get(0).name);
        Assertions.assertEquals("y", tran.Classes.getFirst().methods.getFirst().locals.get(1).name);
        Assertions.assertEquals("start", tran.Classes.getFirst().methods.getFirst().name); //Added test testing for name
        Assertions.assertEquals("s", tran.Classes.getFirst().methods.getFirst().parameters.get(0).name);
        Assertions.assertEquals("salty", tran.Classes.getFirst().methods.getFirst().parameters.get(1).name);
        Assertions.assertEquals("s", tran.Classes.getFirst().methods.getFirst().returns.get(0).name);
        Assertions.assertEquals("salty", tran.Classes.getFirst().methods.getFirst().returns.get(1).name);
        Assertions.assertEquals(true, tran.Classes.getFirst().methods.getFirst().isPrivate);
    }









}