import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class Lexer2Tests {
    @Test
    public void KeyWordLexerTest() {
        var l = new Lexer("class interface something accessor: mutator: if else loop");
        try {
            var res = l.Lex();
            Assertions.assertEquals(10, res.size());
            Assertions.assertEquals(Token.TokenTypes.CLASS, res.get(0).getType());
            Assertions.assertEquals(Token.TokenTypes.INTERFACE, res.get(1).getType());
            Assertions.assertEquals(Token.TokenTypes.WORD, res.get(2).getType());
            Assertions.assertEquals("something", res.get(2).getValue());
            Assertions.assertEquals(Token.TokenTypes.ACCESSOR, res.get(3).getType());
            Assertions.assertEquals(Token.TokenTypes.COLON, res.get(4).getType());
            Assertions.assertEquals(Token.TokenTypes.MUTATOR, res.get(5).getType());
            Assertions.assertEquals(Token.TokenTypes.COLON, res.get(6).getType());
            Assertions.assertEquals(Token.TokenTypes.IF, res.get(7).getType());
            Assertions.assertEquals(Token.TokenTypes.ELSE, res.get(8).getType());
            Assertions.assertEquals(Token.TokenTypes.LOOP, res.get(9).getType());
        }
        catch (Exception e) {
            Assertions.fail("exception occurred: " +  e.getMessage());
        }
    }



    @Test
    public void QuotedStringLexerTest() {
        var l = new Lexer("test \"hello\" \"there\" 1.2");
        try {
            var res = l.Lex();
            Assertions.assertEquals(4, res.size());
            Assertions.assertEquals(Token.TokenTypes.WORD, res.get(0).getType());
            Assertions.assertEquals("test", res.get(0).getValue());
            Assertions.assertEquals(Token.TokenTypes.QUOTEDSTRING, res.get(1).getType());
            Assertions.assertEquals("hello", res.get(1).getValue());
            Assertions.assertEquals(Token.TokenTypes.QUOTEDSTRING, res.get(2).getType());
            Assertions.assertEquals("there", res.get(2).getValue());
            Assertions.assertEquals(Token.TokenTypes.NUMBER, res.get(3).getType());
            Assertions.assertEquals("1.2", res.get(3).getValue());

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
    public void QuotedStringTest() {
        var l = new Lexer("test\n" +
                "\t\ttest2\n" +
                "\ttest\n" +
                "test\n" +
                "\ttestagain");
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
    public void LexerComprehensiveTest() {
        String input = "\"hello\"\n123.45 if else == != >= <= \n\'a\' {this is a comment}\nword";

        Lexer lexer = new Lexer(input);

        try {
            var res = lexer.Lex();

            Assertions.assertEquals(13, res.size());

            // Token positions and values
            Assertions.assertEquals(Token.TokenTypes.QUOTEDSTRING, res.get(0).getType());
            Assertions.assertEquals("hello", res.get(0).getValue());
            Assertions.assertEquals(1, res.get(0).getLineNumber());
            Assertions.assertEquals(1, res.get(0).getColumnNumber());

            Assertions.assertEquals(Token.TokenTypes.NEWLINE, res.get(1).getType());
            Assertions.assertEquals(1, res.get(1).getLineNumber());

            Assertions.assertEquals(Token.TokenTypes.NUMBER, res.get(2).getType());
            Assertions.assertEquals("123.45", res.get(2).getValue());
            Assertions.assertEquals(2, res.get(2).getLineNumber());
            Assertions.assertEquals(1, res.get(2).getColumnNumber());

            Assertions.assertEquals(Token.TokenTypes.IF, res.get(3).getType());
            Assertions.assertEquals(2, res.get(3).getLineNumber());

            Assertions.assertEquals(Token.TokenTypes.ELSE, res.get(4).getType());
            Assertions.assertEquals(2, res.get(4).getLineNumber());

            Assertions.assertEquals(Token.TokenTypes.EQUAL, res.get(5).getType());
            Assertions.assertEquals(2, res.get(5).getLineNumber());

            Assertions.assertEquals(Token.TokenTypes.NOTEQUAL, res.get(6).getType());
            Assertions.assertEquals(2, res.get(6).getLineNumber());

            Assertions.assertEquals(Token.TokenTypes.GREATERTHANEQUAL, res.get(7).getType());
            Assertions.assertEquals(2, res.get(7).getLineNumber());

            Assertions.assertEquals(Token.TokenTypes.LESSTHANEQUAL, res.get(8).getType());
            Assertions.assertEquals(2, res.get(8).getLineNumber());

            Assertions.assertEquals(Token.TokenTypes.NEWLINE, res.get(9).getType());
            Assertions.assertEquals(2, res.get(9).getLineNumber());

            Assertions.assertEquals(Token.TokenTypes.QUOTEDCHARACTER, res.get(10).getType());
            Assertions.assertEquals("a", res.get(10).getValue());
            Assertions.assertEquals(3, res.get(10).getLineNumber());
            Assertions.assertEquals(1, res.get(10).getColumnNumber());

            Assertions.assertEquals(Token.TokenTypes.NEWLINE, res.get(11).getType());
            Assertions.assertEquals(3, res.get(11).getLineNumber());

            Assertions.assertEquals(Token.TokenTypes.WORD, res.get(12).getType());
            Assertions.assertEquals("word", res.get(12).getValue());
            Assertions.assertEquals(4, res.get(12).getLineNumber());
            Assertions.assertEquals(1, res.get(12).getColumnNumber());

            // Print tokens for verification
            for (Token token : res) {
                System.out.println("Type: " + token.getType() + ", Value: " + token.getValue() +
                        ", Line: " + token.getLineNumber() + ", Column: " + token.getColumnNumber());
            }

        } catch (Exception e) {
            Assertions.fail("Exception occurred: " + e.getMessage());
        }
    }

    @Test
    public void comment_test() {
        var l = new Lexer("{t{more}}he");
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




}
