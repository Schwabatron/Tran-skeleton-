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

    @Test
    public void KeyWordLexerTest2() { //Test contains error. OR AND NOT should be || && !
        var l = new Lexer("class interface something accessor: mutator: if else loop and or shared construct new private implements true false not");
        try {
            var res = l.Lex();

            // Let's check if we've got the right number of tokens!
            Assertions.assertEquals(20, res.size());

            // Checking all the individual tokens!
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
            Assertions.assertEquals(Token.TokenTypes.AND, res.get(10).getType());
            Assertions.assertEquals(Token.TokenTypes.OR, res.get(11).getType());
            Assertions.assertEquals(Token.TokenTypes.SHARED, res.get(12).getType());
            Assertions.assertEquals(Token.TokenTypes.CONSTRUCT, res.get(13).getType());
            Assertions.assertEquals(Token.TokenTypes.NEW, res.get(14).getType());
            Assertions.assertEquals(Token.TokenTypes.PRIVATE, res.get(15).getType());
            Assertions.assertEquals(Token.TokenTypes.IMPLEMENTS, res.get(16).getType());
            Assertions.assertEquals(Token.TokenTypes.TRUE, res.get(17).getType());
            Assertions.assertEquals(Token.TokenTypes.FALSE, res.get(18).getType());
            Assertions.assertEquals(Token.TokenTypes.NOT, res.get(19).getType());
        }
        catch (Exception e) {
            Assertions.fail("exception occurred: " +  e.getMessage());
        }
    }

    @Test
    public void QuotedStringLexerTest2() {
        var l = new Lexer("test \"hello\" \"\" 1.2");
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
        }
        catch (Exception e) {
            Assertions.fail("exception occurred: " +  e.getMessage());
        }
    }

    @Test
    public void character_and_line_position() //This test also contains an error the token is measured from where it starts not from where it ends
    {
        String program = "class LoopOne\n" +
                "    shared start()\n" +
                "        boolean keepGoing\n" +
                "        number n\n" +
                "        n = 0\n" +
                "        keepGoing = true\n" +
                "        loop keepGoing\n" +
                "            if n >= 15\n" +
                "                keepGoing = false\n" +
                "            else\n" +
                "                n = n + 1\n" +
                "                console.write(n)\n";
        var l = new Lexer(program);
        try
        {
            var res=  l.Lex();
            //Line and position test
            Assertions.assertEquals(1, res.get(0).getLineNumber());//class
            Assertions.assertEquals(5, res.get(0).getColumnNumber());

            Assertions.assertEquals(1, res.get(1).getLineNumber());//LoopOne
            Assertions.assertEquals(13, res.get(1).getColumnNumber());
            // New line is set to column 0
            Assertions.assertEquals(2, res.get(2).getLineNumber());// \n of 1st line
            Assertions.assertEquals(0, res.get(2).getColumnNumber());
            // New line is set to column 0
            Assertions.assertEquals(3, res.get(8).getLineNumber());// \n of 2nd line
            Assertions.assertEquals(0, res.get(8).getColumnNumber());


            Assertions.assertEquals(2, res.get(3).getLineNumber());// second line indent
            Assertions.assertEquals(4, res.get(3).getColumnNumber());


            Assertions.assertEquals(2, res.get(4).getLineNumber());// shared
            Assertions.assertEquals(4, res.get(13).getLineNumber());//n


        }
        catch (Exception e) {
            Assertions.fail("exception occurred: " +  e.getMessage());
        }

    }
    @Test
    public void IndentTest2() {
        var l = new Lexer(
                "loop keepGoing\n" +
                        "    if n >= 15\n" +
                        "        keepGoing = false\n"+
                        "    n++\n"+
                        "console.write(n)\n"+
                        "loop keepGoing\n"  +
                        "    if n >= 15\n" +
                        "        keepGoing = false\n"+
                        "    n++\n"
        );
        try {
            var res = l.Lex();
            Assertions.assertEquals(47, res.size());
        }
        catch (Exception e) {
            Assertions.fail("exception occurred: " +  e.getMessage());
        }
    }
    @Test
    public void Comments_test_multi_line() {
        var l = new Lexer(
                "{referencing x, y \n" +
                        "now would \n" + "be an error! }");
        try {
            var res = l.Lex();
            Assertions.assertEquals(0, res.size());
        }
        catch (Exception e) {
            Assertions.fail("exception occurred: " +  e.getMessage());
        }
    }
    @Test
    public void Comments_test_Single_line() {
        var l = new Lexer(
                "{This is a assignment}");
        try {
            var res = l.Lex();
            Assertions.assertEquals(0, res.size());
        }
        catch (Exception e) {
            Assertions.fail("exception occurred: " +  e.getMessage());
        }
    }
    @Test
    public void TestIt() { //This test also contains an error there is an unclosed comment which should invoke a syntax error
        var l = new Lexer("interface someName\n" +
                "\tupdateClock()\n" +
                "\tsquare() : number s\n" +
                "\t\n" +
                "class tran implements someName { no inheritance }\n" +
                "\tnumber x\n" +
                "\t\taccessor:  { a longer (although nonsensical) accessor }\n" +
                "\t\t\tvalue = 0\n" +
                "\t\t\tloop x.times()\n" +
                "\t\t\t\tvalue = value + 1\n" +
                "\t\t\t\t\n" +
                "\tstring y\n" +
                "\t\taccessor: value = y.clone()\n" +
                "\t\tmutator: y = value\n" +
                "\t{ \n" +
                "\t\tthese enable us to access the values:\n" +
                "\t\tmyTran.x = 5\n" +
                "\t\tconsole.print (myTran.y)\n" +
                "\t}\n" +
                "\n" +
                "\tdatetime now { this is a member}\n" +
                "\t\n" +
                "\tconstruct() \n" +
                "\t\tnow = clock.getDate()\n" +
                "\t\tx = 0\n" +
                "\t\ty = \"\"\n" +
                "\n" +
                "\tconstruct(number n) {overloading}\n" +
                "\t\tnow = clock.getDate()\n" +
                "\t\tx = n\n" +
                "\t\ty = \"\"\n" +
                "\n" +
                "\tupdateClock()\n" +
                "\t\tnow = clock.getDate()\t\t \n" +
                "\n" +
                "\tsquare() : number s\n" +
                "\t\ts = x * x\n" +
                "\n" +
                "\tallMyData() : number n, string t\n" +
                "\t\tt = y.clone() { some built in to string }\n" +
                "\t\tn = x\n" +
                "\n" +
                "\trepeat() : string t\n" +
                "\t\tt = \"\" { loop replaces for/while/do-until }\n" +
                "\t\tloop x.times() { times is a method on number that returns an iterator (1,2,3,4, ..., x)\n" +
                "\t\t\tt = t + y\n" +
                "\n" +
                "\twhileTest() \n" +
                "\t\tboolean keepGoing\n" +
                "\t\tkeepGoing = true\n" +
                "\t\tloop keepGoing\n" +
                "\t\t\tif n > 100\n" +
                "\t\t\t\tkeepGoing = false\n" +
                "\t\tconsole.print (temp)\n" +
                "\t\t\n" +
                "\n" +
                "\n" +
                "\tprintNumbers()\n" +
                "\t\tnumber temp\n" +
                "\t\ttemp = loop x.times()\n" +
                "\t\t\tconsole.print (temp)\n" +
                "\n" +
                "\tshared printNumbers(number t)\n" +
                "\t\t{ referencing x, y \n" +
                "now would \n" +
                "be an error! }\n" +
                "\t\tnumber temp\n" +
                "\t\ttemp=loop t.times()\n" +
                "\t\t\tconsole.print (temp)\n" +
                "\t\t\t\t\t\n" +
                "\t{ exceptions? }\n" +
                "\t{ generics? } \n" +
                "\n" +
                "\t{ \"normal\" math }\n" +
                "\n");
        try {
            var res = l.Lex();
            Assertions.assertEquals("updateClock",res.get(128).getValue());
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }




}
