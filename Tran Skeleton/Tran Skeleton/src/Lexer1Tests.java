import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;

public class Lexer1Tests {

    @Test
    public void SimpleLexerTest() {
        var l = new Lexer("ab cd ef gh");
        try {
            var res = l.Lex();
            Assertions.assertEquals(4, res.size());
            Assertions.assertEquals("ab", res.get(0).getValue());
            Assertions.assertEquals("cd", res.get(1).getValue());
            Assertions.assertEquals("ef", res.get(2).getValue());
            Assertions.assertEquals("gh", res.get(3).getValue());
            for (var result : res)
                Assertions.assertEquals(Token.TokenTypes.WORD, result.getType());
        }
        catch (Exception e) {
            Assertions.fail("exception occurred: " +  e.getMessage());
        }
    }

    @Test
    public void MultilineLexerTest() {
        var l = new Lexer("ab cd ef gh\nasdjkdsajkl\ndsajkdsa asdjksald dsajhkl \n");
        try {
            var res = l.Lex();
            Assertions.assertEquals(11, res.size());
            Assertions.assertEquals("ab", res.get(0).getValue());
            Assertions.assertEquals("cd", res.get(1).getValue());
            Assertions.assertEquals("ef", res.get(2).getValue());
            Assertions.assertEquals("gh", res.get(3).getValue());
            Assertions.assertEquals(Token.TokenTypes.NEWLINE, res.get(4).getType());
            Assertions.assertEquals("asdjkdsajkl", res.get(5).getValue());
            Assertions.assertEquals(Token.TokenTypes.NEWLINE, res.get(6).getType());
            Assertions.assertEquals("dsajkdsa", res.get(7).getValue());
            Assertions.assertEquals("asdjksald", res.get(8).getValue());
            Assertions.assertEquals("dsajhkl", res.get(9).getValue());
            Assertions.assertEquals(Token.TokenTypes.NEWLINE, res.get(10).getType());
        }
        catch (Exception e) {
            Assertions.fail("exception occurred: " +  e.getMessage());
        }
    }

    @Test
    public void NotEqualsTest() {
        var l = new Lexer("!=");
        try {
            var res = l.Lex();
            Assertions.assertEquals(1, res.size());
        }
        catch (Exception e) {
            Assertions.fail("exception occurred: " +  e.getMessage());
        }
    }

    @Test
    public void IndentTest() {
        var l = new Lexer(
                "loop keepGoing\n" +
                    "    if n >= 15\n" +
	                "        keepGoing = false\n"
                 );
        try {
            var res = l.Lex();
            Assertions.assertEquals(16, res.size());
        }
        catch (Exception e) {
            Assertions.fail("exception occurred: " +  e.getMessage());
        }
    }

    @Test
    public void TwoCharacterTest() {
        var l = new Lexer(">= > <= < = == !=");
        try {
            var res = l.Lex();
            Assertions.assertEquals(7, res.size());
            Assertions.assertEquals(Token.TokenTypes.GREATERTHANEQUAL, res.get(0).getType());
            Assertions.assertEquals(Token.TokenTypes.GREATERTHAN, res.get(1).getType());
            Assertions.assertEquals(Token.TokenTypes.LESSTHANEQUAL, res.get(2).getType());
            Assertions.assertEquals(Token.TokenTypes.LESSTHAN, res.get(3).getType());
            Assertions.assertEquals(Token.TokenTypes.ASSIGN, res.get(4).getType());
            Assertions.assertEquals(Token.TokenTypes.EQUAL, res.get(5).getType());
            Assertions.assertEquals(Token.TokenTypes.NOTEQUAL, res.get(6).getType());
        }
        catch (Exception e) {
            Assertions.fail("exception occurred: " +  e.getMessage());
        }
    }

    @Test
    public void MixedTest() {
        var l = new Lexer("word 1.2 : ( )");
        try {
            var res = l.Lex();
            Assertions.assertEquals(5, res.size());
            Assertions.assertEquals(Token.TokenTypes.WORD, res.get(0).getType());
            Assertions.assertEquals("word", res.get(0).getValue());
            Assertions.assertEquals(Token.TokenTypes.NUMBER, res.get(1).getType());
            Assertions.assertEquals("1.2", res.get(1).getValue());
            Assertions.assertEquals(Token.TokenTypes.COLON, res.get(2).getType());
            Assertions.assertEquals(Token.TokenTypes.LPAREN, res.get(3).getType());
            Assertions.assertEquals(Token.TokenTypes.RPAREN, res.get(4).getType());
        }
        catch (Exception e) {
            Assertions.fail("exception occurred: " +  e.getMessage());
        }
    }

    @Test
    public void first_test() {
        var l = new Lexer("h ! ! || && &");
        try {
            var res = l.Lex();
//            Assertions.assertEquals(4, res.size());
//            Assertions.assertEquals(Token.TokenTypes.NEWLINE, res.get(0).getType());
//            Assertions.assertEquals(Token.TokenTypes.WORD, res.get(1).getType());
//            Assertions.assertEquals("Hello", res.get(1).getValue());
//            Assertions.assertEquals(Token.TokenTypes.WORD, res.get(2).getType());
//            Assertions.assertEquals("World", res.get(2).getValue());
//            Assertions.assertEquals(Token.TokenTypes.NEWLINE, res.get(3).getType());

            for(int i = 0; i < res.size(); i++)
            {
                System.out.println(res.get(i).getValue());
                System.out.println(res.get(i).getType());
            }
        }
        catch (Exception e) {
            Assertions.fail("exception occurred: " +  e.getMessage());
        }
    }

    @Test
    public void test_numbers() {
        var l = new Lexer("\n1.1 123423 1.2342 .234");
        try {
            var res = l.Lex();
            Assertions.assertEquals(5, res.size());
            Assertions.assertEquals(Token.TokenTypes.NEWLINE, res.get(0).getType());
            Assertions.assertEquals("1.1", res.get(1).getValue());
            Assertions.assertEquals(Token.TokenTypes.NUMBER, res.get(1).getType());
            Assertions.assertEquals("123423", res.get(2).getValue());
            Assertions.assertEquals(Token.TokenTypes.NUMBER, res.get(2).getType());
            Assertions.assertEquals("1.2342", res.get(3).getValue());
            Assertions.assertEquals(Token.TokenTypes.NUMBER, res.get(3).getType());
            Assertions.assertEquals(".234", res.get(4).getValue());
            Assertions.assertEquals(Token.TokenTypes.NUMBER, res.get(4).getType());

            for(int i = 0; i < res.size(); i++)
            {
                System.out.println(res.get(i).getValue());
                System.out.println(res.get(i).getType());
            }
        }
        catch (Exception e) {
            Assertions.fail("exception occurred: " +  e.getMessage());
        }
    }

    @Test
    public void test_punctuation() {
        var l = new Lexer("<>= = / + % - * >= :,.");
        try {
            var res = l.Lex();


            for(int i = 0; i < res.size(); i++)
            {
                System.out.println(res.get(i).getValue());
                System.out.println(res.get(i).getType());
            }
        }
        catch (Exception e) {
            Assertions.fail("exception occurred: " +  e.getMessage());
        }
    }


    @Test
    public void DecimalNumberTest() {
        var lexer = new Lexer("12 .34 5.6 7.8.9");
        try {
            var lexedResult = lexer.Lex();
            Assertions.assertEquals(5, lexedResult.size());
            Assertions.assertEquals("12", lexedResult.get(0).getValue());
            Assertions.assertEquals(Token.TokenTypes.NUMBER, lexedResult.get(0).getType());
            Assertions.assertEquals(".34", lexedResult.get(1).getValue());
            Assertions.assertEquals(Token.TokenTypes.NUMBER, lexedResult.get(1).getType());
            Assertions.assertEquals("5.6", lexedResult.get(2).getValue());
            Assertions.assertEquals(Token.TokenTypes.NUMBER, lexedResult.get(2).getType());
            Assertions.assertEquals("7.8", lexedResult.get(3).getValue());
            Assertions.assertEquals(Token.TokenTypes.NUMBER, lexedResult.get(3).getType());
            Assertions.assertEquals(".9", lexedResult.get(4).getValue());
            Assertions.assertEquals(Token.TokenTypes.NUMBER, lexedResult.get(4).getType());
        } catch (Exception e) {
            Assertions.fail("exception occurred: " + e.getMessage());
        }
    }

    @Test
    public void Extra_Mixed_Test() {
        var lexer = new Lexer(",<=.!=");
        try {
            var lexedResult = lexer.Lex();
            for(int i = 0; i < lexedResult.size(); i++)
            {
                System.out.println(lexedResult.get(i).getValue());
                System.out.println(lexedResult.get(i).getType());
            }
        } catch (Exception e) {
            Assertions.fail("exception occurred: " + e.getMessage());
        }
    }





}
