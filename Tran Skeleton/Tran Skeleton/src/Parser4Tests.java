import AST.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class Parser4Tests {
    @Test
    public void method_calltest() throws Exception {
        Lexer l= new Lexer("class Tran\n" +
                "\thelloWorld()\n" +
                "\t\ta,b,c,d,e = doSomething()\n");
        var rev= l.Lex();
        TranNode TN= new TranNode();
        Parser p= new Parser(TN, rev);
        p.Tran();
        var firstStatement = TN.Classes.get(0).methods.get(0).statements.getFirst();
        Assertions.assertInstanceOf(MethodCallStatementNode.class, firstStatement);
        Assertions.assertEquals(5, ((MethodCallStatementNode) firstStatement).returnValues.size());
        Assertions.assertEquals("a", ((MethodCallStatementNode) firstStatement).returnValues.getFirst().name);
        Assertions.assertEquals("b", ((MethodCallStatementNode) firstStatement).returnValues.get(1).name);
        Assertions.assertEquals("c", ((MethodCallStatementNode) firstStatement).returnValues.get(2).name);
        Assertions.assertEquals("d", ((MethodCallStatementNode) firstStatement).returnValues.get(3).name);
        Assertions.assertEquals("e", ((MethodCallStatementNode) firstStatement).returnValues.get(4).name);
    }

    @Test
    public void Test_expression () throws Exception {
        Lexer l= new Lexer("class Tran\n" +
                "\thelloWorld()\n" +
                "\t\ta= 1+(6-5)+7\n");
        var rev= l.Lex();
        TranNode TN= new TranNode();
        Parser p= new Parser(TN, rev);
        p.Tran();
        var firstStatement = TN.Classes.get(0).methods.get(0).statements.getFirst();
        Assertions.assertInstanceOf(AssignmentNode.class, firstStatement);
        Assertions.assertEquals("add", ((MathOpNode) ((AssignmentNode) firstStatement).expression).op.name());
        Assertions.assertEquals(1.0, ( (NumericLiteralNode) ((MathOpNode) ((MathOpNode) ((AssignmentNode) firstStatement).expression).left).left).value);
        Assertions.assertEquals("subtract", ( (MathOpNode)( ((MathOpNode) ((MathOpNode) ((AssignmentNode) firstStatement).expression).left).right)).op.name());
        Assertions.assertEquals(6.0, ((NumericLiteralNode) ((MathOpNode) (((MathOpNode) ((MathOpNode) ((AssignmentNode) firstStatement).expression).left).right)).left).value);
        Assertions.assertEquals(5.0, ((NumericLiteralNode) ((MathOpNode) (((MathOpNode) ((MathOpNode) ((AssignmentNode) firstStatement).expression).left).right)).right).value);
    }

    @Test
    public void termsTest() throws Exception {
        Lexer l = new Lexer("class Tran\n" +
                "\thelloWorld()\n" +
                "\t\tresult = 5 * 10\n");
        var rev = l.Lex();
        TranNode TN = new TranNode();
        Parser p = new Parser(TN, rev);
        p.Tran();
        var firstStatement = TN.Classes.get(0).methods.get(0).statements.getFirst();
        Assertions.assertInstanceOf(AssignmentNode.class, firstStatement);
        MathOpNode expression = (MathOpNode) ((AssignmentNode) firstStatement).expression;
        Assertions.assertEquals("multiply", expression.op.name());
        Assertions.assertEquals(5.0, ((NumericLiteralNode) expression.left).value);
        Assertions.assertEquals(10.0, ((NumericLiteralNode) expression.right).value);
    }

    @Test
    public void factorNumbersTest() throws Exception {
        Lexer l = new Lexer("class Tran\n" +
                "\thelloWorld()\n" +
                "\t\tresult = 5\n");
        var rev = l.Lex();
        TranNode TN = new TranNode();
        Parser p = new Parser(TN, rev);
        p.Tran();
        var firstStatement = TN.Classes.get(0).methods.get(0).statements.getFirst();
        Assertions.assertInstanceOf(AssignmentNode.class, firstStatement);
        Assertions.assertEquals(5.0, ((NumericLiteralNode) ((AssignmentNode) firstStatement).expression).value);
    }

    @Test
    public void factorVariableReferenceTest() throws Exception {
        Lexer l = new Lexer("class Tran\n" +
                "\thelloWorld()\n" +
                "\t\tresult = someVar\n");
        var rev = l.Lex();
        TranNode TN = new TranNode();
        Parser p = new Parser(TN, rev);
        p.Tran();
        var firstStatement = TN.Classes.get(0).methods.get(0).statements.getFirst();
        Assertions.assertInstanceOf(AssignmentNode.class, firstStatement);
        Assertions.assertEquals("someVar", ((VariableReferenceNode) ((AssignmentNode) firstStatement).expression).name);
    }

    @Test
    public void factorTrueFalseTest() throws Exception {
        Lexer l = new Lexer("class Tran\n" +
                "\thelloWorld()\n" +
                "\t\tresult = true\n");
        var rev = l.Lex();
        TranNode TN = new TranNode();
        Parser p = new Parser(TN, rev);
        p.Tran();
        var firstStatement = TN.Classes.get(0).methods.get(0).statements.getFirst();
        Assertions.assertInstanceOf(AssignmentNode.class, firstStatement);
        Assertions.assertEquals(true, ((BooleanLiteralNode) ((AssignmentNode) firstStatement).expression).value);
    }

    @Test
    public void factorStringTest() throws Exception {
        Lexer l = new Lexer("class Tran\n" +
                "\thelloWorld()\n" +
                "\t\tresult = \"hello world\"\n");
        var rev = l.Lex();
        TranNode TN = new TranNode();
        Parser p = new Parser(TN, rev);
        p.Tran();
        var firstStatement = TN.Classes.get(0).methods.get(0).statements.getFirst();
        Assertions.assertInstanceOf(AssignmentNode.class, firstStatement);
        Assertions.assertEquals("hello world", ((StringLiteralNode) ((AssignmentNode) firstStatement).expression).value);
    }

    @Test
    public void factorMethodCallExpressionTest() throws Exception {
        Lexer l = new Lexer("class Tran\n" +
                "\thelloWorld()\n" +
                "\t\tresult = someMethod()\n" +
                "\t\tn=18\n" +
                "\t\tloop x = n.times()\n" +
                "\t\t\tconsole.print(now)");
        var rev = l.Lex();
        TranNode TN = new TranNode();
        Parser p = new Parser(TN, rev);
        p.Tran();
        var firstStatement = TN.Classes.get(0).methods.get(0).statements.getFirst();
        Assertions.assertInstanceOf(MethodCallStatementNode.class, firstStatement);
        Assertions.assertEquals("result", (((MethodCallStatementNode) firstStatement).returnValues).getFirst().name);
        Assertions.assertEquals("someMethod", (((MethodCallStatementNode) firstStatement).methodName));
        var thirdStatement = TN.Classes.get(0).methods.get(0).statements.get(2);
        Assertions.assertEquals("x",  ((LoopNode) thirdStatement).assignment.get().name);
        Assertions.assertEquals("times", ( ((MethodCallExpressionNode) ((LoopNode) thirdStatement).expression).methodName));
        Assertions.assertEquals("n", ( ((MethodCallExpressionNode) ((LoopNode) thirdStatement).expression).objectName).get());

    }

    @Test
    public void factorNewTest() throws Exception {
        Lexer l = new Lexer("class Tran\n" +
                "\tnumber x\n" +
                "\tstring y\n" +
                "\tconstruct()\n" +
                "\t\tx = 0\n" +
                "\t\ty = \"\"\n" +
                "\tTran t\n" +
                "\tstart()\n" +
                "\t\tt = new Tran()\n" );
        var rev = l.Lex();
        TranNode TN = new TranNode();
        Parser p = new Parser(TN, rev);
        p.Tran();
        var firstStatement = TN.Classes.get(0).methods.get(0).statements.getFirst();
        Assertions.assertInstanceOf(AssignmentNode.class, firstStatement);
        Assertions.assertEquals("Tran", ((NewNode) ((AssignmentNode) firstStatement).expression).className);
        Assertions.assertEquals(0, ((NewNode) ((AssignmentNode) firstStatement).expression).parameters.size());



    }
}
