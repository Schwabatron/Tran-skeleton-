import AST.*;
import java.util.ArrayList;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class Parser {

    private TokenManager tokens; //List of tokens that will be parsed through
    private TranNode top; //TranNode we will add classnodes and interface nodes to
    public Parser(TranNode top, List<Token> tokens) {
        this.top = top;
        this.tokens = new TokenManager(tokens);
    }

    // Tran = { Class | Interface }
    public void Tran() throws SyntaxErrorException {
        while(!tokens.done())
        {
            if (tokens.peek(0).get().getType() == Token.TokenTypes.INTERFACE)
            {
                Optional<InterfaceNode> interfaceNode = parseInterface();
                if (interfaceNode.isPresent()) {
                    top.Interfaces.add(interfaceNode.get());
                }
            }
            else if(tokens.peek(0).get().getType() == Token.TokenTypes.CLASS)
            {
                Optional<ClassNode> classnode = parseClass();
                if (classnode.isPresent()) {
                    top.Classes.add(classnode.get());
                }
            }
            else
            {
                RequireNewLine();
            }


        }
    }

     private Optional<InterfaceNode> parseInterface() throws SyntaxErrorException {
        InterfaceNode interfaceNode = new InterfaceNode();
         if(tokens.matchAndRemove(Token.TokenTypes.INTERFACE).isEmpty())
         {
             return Optional.empty(); //Not a interface return empty
         }
         String name = tokens.peek(0).get().getValue();
         if(tokens.matchAndRemove(Token.TokenTypes.WORD).isEmpty())
         {
             throw new SyntaxErrorException("Expected Interface Name", tokens.getCurrentLine(), tokens.getCurrentColumnNumber()); //Interface needs a name following it
         }
         interfaceNode.name = name;

         RequireNewLine(); //Checking for newline

         if (tokens.matchAndRemove(Token.TokenTypes.INDENT).isEmpty())
         {
             throw new SyntaxErrorException("Expected Indent", tokens.getCurrentLine(), tokens.getCurrentColumnNumber());
         }

         while(tokens.matchAndRemove(Token.TokenTypes.DEDENT).isEmpty())
         {
             if(tokens.peek(0).get().getType() == Token.TokenTypes.WORD)
             {
                 Optional<MethodHeaderNode> methodNode = parseMethod();
                 if(methodNode.isPresent())
                 {
                     interfaceNode.methods.add(methodNode.get());
                 }
             }
             else
             {
                 RequireNewLine();
             }

         }

         return Optional.of(interfaceNode);

    }

    private Optional<MethodHeaderNode> parseMethod() throws SyntaxErrorException {
        MethodHeaderNode methodNode = new MethodHeaderNode();
        String name = tokens.peek(0).get().getValue();
        if(tokens.matchAndRemove(Token.TokenTypes.WORD).isEmpty())
        {
            //throw new SyntaxErrorException("Expected Method", tokens.getCurrentLine(), tokens.getCurrentColumnNumber());
            return Optional.empty();
        }
        methodNode.name = name;
        if(tokens.matchAndRemove(Token.TokenTypes.LPAREN).isEmpty())
        {
            throw new SyntaxErrorException("Expected Lparen", tokens.getCurrentLine(), tokens.getCurrentColumnNumber());
        }

        if(tokens.peek(0).get().getType() == Token.TokenTypes.WORD)
        {
            do
            {
                Optional<VariableDeclarationNode> variableDeclarationNode = parseVariable();
                if(variableDeclarationNode.isPresent())
                {
                    methodNode.parameters.add(variableDeclarationNode.get());
                }
            }while(!tokens.matchAndRemove(Token.TokenTypes.COMMA).isEmpty());
        }

        if(tokens.matchAndRemove(Token.TokenTypes.RPAREN).isEmpty())
        {
            throw new SyntaxErrorException("Expected Rparen", tokens.getCurrentLine(), tokens.getCurrentColumnNumber());
        }
        if(!tokens.matchAndRemove(Token.TokenTypes.COLON).isEmpty())
        {
            do
            {
                Optional<VariableDeclarationNode> variableDeclarationNode = parseVariable();
                if(variableDeclarationNode.isPresent())
                {
                    methodNode.returns.add(variableDeclarationNode.get());
                }
            }while(!tokens.matchAndRemove(Token.TokenTypes.COMMA).isEmpty());

        }
        return Optional.of(methodNode);
    }

    private Optional<VariableDeclarationNode> parseVariable() throws SyntaxErrorException {

        VariableDeclarationNode variableNode = new VariableDeclarationNode();
        String type = tokens.peek(0).get().getValue();
        if(tokens.matchAndRemove(Token.TokenTypes.WORD).isEmpty())
        {
            //throw new SyntaxErrorException("Expected Variable type", tokens.getCurrentLine(), tokens.getCurrentColumnNumber());
            return Optional.empty();
        }
        variableNode.type = type;
        String name = tokens.peek(0).get().getValue();
        if(tokens.matchAndRemove(Token.TokenTypes.WORD).isEmpty())
        {
            throw new SyntaxErrorException("Expected Variable name", tokens.getCurrentLine(), tokens.getCurrentColumnNumber());
        }
        variableNode.name = name;
        return Optional.of(variableNode);
    }

    //EBSN
    //Class = "class" Identifier [ "implements" Identifier { "," Identifier } ] NEWLINE INDENT {Constructor NEWLINE | MethodDeclaration NEWLINE | Member NEWLINE } DEDENT
    private Optional<ClassNode> parseClass() throws SyntaxErrorException {
        ClassNode classNode = new ClassNode();
        if(tokens.matchAndRemove(Token.TokenTypes.CLASS).isEmpty())
        {
            return Optional.empty();
        }
        String name = tokens.peek(0).get().getValue();
        if(tokens.matchAndRemove(Token.TokenTypes.WORD).isEmpty())
        {
            throw new SyntaxErrorException("Expected Class Name", tokens.getCurrentLine(), tokens.getCurrentColumnNumber());
        }
        classNode.name = name;
        if(tokens.matchAndRemove(Token.TokenTypes.IMPLEMENTS).isPresent())
        {
            //Loop to get all possible interface additions
            do
            {
                String interfacename = tokens.peek(0).get().getValue();
                if(tokens.matchAndRemove(Token.TokenTypes.WORD).isEmpty())
                {
                    throw new SyntaxErrorException("Expected Interface name", tokens.getCurrentLine(), tokens.getCurrentColumnNumber());
                }
                classNode.interfaces.add(interfacename);
            }while(!tokens.matchAndRemove(Token.TokenTypes.COMMA).isEmpty());

        }

        RequireNewLine();

        if(tokens.matchAndRemove(Token.TokenTypes.INDENT).isEmpty())
        {
            throw new SyntaxErrorException("Expected Indent", tokens.getCurrentLine(), tokens.getCurrentColumnNumber());
        }

        //Logic to get all the Constructors, method declarations, and members before the dedent
        while(tokens.matchAndRemove(Token.TokenTypes.DEDENT).isEmpty() && !tokens.done())
        {

            //Constructor = "construct" "(" VariableDeclarations ")" NEWLINE MethodBody
            if(tokens.peek(0).get().getType() == Token.TokenTypes.CONSTRUCT)
            {
                Optional<ConstructorNode> constructorNode = parseConstructor();
                if(constructorNode.isPresent())
                {
                    classNode.constructors.add(constructorNode.get());
                }
            }
            //MethodDeclaration = ["private"] ["shared"] MethodHeader NEWLINE MethodBody
            else if(tokens.nextTwoTokensMatch(Token.TokenTypes.WORD, Token.TokenTypes.LPAREN) || tokens.peek(0).get().getType() == Token.TokenTypes.SHARED || tokens.peek(0).get().getType() == Token.TokenTypes.PRIVATE)
            {
                Optional<MethodDeclarationNode> methodDeclarationNode = parseMethodDeclaration();
                if(methodDeclarationNode.isPresent())
                {
                    classNode.methods.add(methodDeclarationNode.get());
                }
            }
            //Member = VariableDeclaration ["accessor:" Statements] ["mutator:" Statements]
            else if(tokens.nextTwoTokensMatch(Token.TokenTypes.WORD, Token.TokenTypes.WORD))
            {
                Optional<MemberNode> memberNode = parseMember();
                if(memberNode.isPresent())
                {
                    classNode.members.add(memberNode.get());
                }
            }
            else
            {
                RequireNewLine();
            }

        }


        return Optional.of(classNode);
    }

    //MethodDeclaration = ["private"] ["shared"] MethodHeader NEWLINE MethodBody
    private Optional<MethodDeclarationNode> parseMethodDeclaration() throws SyntaxErrorException {
        MethodDeclarationNode methodNode = new MethodDeclarationNode();
        if(tokens.matchAndRemove(Token.TokenTypes.SHARED).isPresent())
        {
            methodNode.isShared = true;
        }
        if(tokens.matchAndRemove(Token.TokenTypes.PRIVATE).isPresent())
        {
            methodNode.isPrivate = true;
        }

        Optional<MethodHeaderNode> methodHeaderNode = parseMethod();

        if(methodHeaderNode.isPresent()) // transfer all the info from the methodheader node into the method declaration node
        {
            methodNode.name = methodHeaderNode.get().name;
            methodNode.returns = methodHeaderNode.get().returns;
            methodNode.parameters = methodHeaderNode.get().parameters;
        }
        else
        {
            throw new SyntaxErrorException("Expected MethodHeader", tokens.getCurrentLine(), tokens.getCurrentColumnNumber());
        }

        RequireNewLine();

        //Parse Method body for statments and local var declarations
        //MethodBody = INDENT { VariableDeclaration NEWLINE } {Statement} DEDENT
        if(tokens.matchAndRemove(Token.TokenTypes.INDENT).isEmpty())
        {
            throw new SyntaxErrorException("Expected Indent", tokens.getCurrentLine(), tokens.getCurrentColumnNumber());
        }

        //Statement = If | Loop | MethodCall | Assignment
        while(tokens.matchAndRemove(Token.TokenTypes.DEDENT).isEmpty())//Now I need to parse statements and variable declarations
        {
            if(tokens.nextTwoTokensMatch(Token.TokenTypes.WORD, Token.TokenTypes.WORD))
            {
                Optional<VariableDeclarationNode> variableDeclarationNode = parseVariable();
                if(variableDeclarationNode.isPresent()) {
                    methodNode.locals.add(variableDeclarationNode.get());
                    RequireNewLine();
                }

            }
            else
            {
                Optional<StatementNode> statementNode = parseStatementNode();
                if(statementNode.isPresent())
                {
                    methodNode.statements.add(statementNode.get());
                }
                else
                {
                    RequireNewLine();
                }
            }

        }

        return Optional.of(methodNode);
    }

    //Constructor = "construct" "(" VariableDeclarations ")" NEWLINE MethodBody
    private Optional<ConstructorNode> parseConstructor() throws SyntaxErrorException {
        ConstructorNode constructorNode = new ConstructorNode();
        if(tokens.matchAndRemove(Token.TokenTypes.CONSTRUCT).isEmpty())
        {
            return Optional.empty();
        }

        if(tokens.matchAndRemove(Token.TokenTypes.LPAREN).isEmpty())
        {
            throw new SyntaxErrorException("Expected Left Paren", tokens.getCurrentLine(), tokens.getCurrentColumnNumber());
        }

        if(tokens.peek(0).get().getType() == Token.TokenTypes.WORD)
        {
            do
            {
                Optional<VariableDeclarationNode> variableDeclarationNode = parseVariable();
                if(variableDeclarationNode.isPresent())
                {
                    constructorNode.parameters.add(variableDeclarationNode.get());
                }
            }while(!tokens.matchAndRemove(Token.TokenTypes.COMMA).isEmpty());

        }

        if(tokens.matchAndRemove(Token.TokenTypes.RPAREN).isEmpty())
        {
            throw new SyntaxErrorException("Expected RParen", tokens.getCurrentLine(), tokens.getCurrentColumnNumber());
        }

        RequireNewLine();

        //parse method body
        //MethodBody = INDENT { VariableDeclaration NEWLINE } {Statement} DEDENT
        if(tokens.matchAndRemove(Token.TokenTypes.INDENT).isEmpty())
        {
            throw new SyntaxErrorException("Expected Indent", tokens.getCurrentLine(), tokens.getCurrentColumnNumber());
        }

        //Statement = If | Loop | MethodCall | Assignment
        // Or method declarations
        while(tokens.matchAndRemove(Token.TokenTypes.DEDENT).isEmpty())//Now I need to parse statements and variable declarations
        {
            if(tokens.nextTwoTokensMatch(Token.TokenTypes.WORD, Token.TokenTypes.WORD))
            {
                Optional<VariableDeclarationNode> variableDeclarationNode = parseVariable();
                if(variableDeclarationNode.isPresent()) {
                    constructorNode.locals.add(variableDeclarationNode.get());
                    RequireNewLine();
                }

            }
            else
            {
                Optional<StatementNode> statementNode = parseStatementNode();
                if(statementNode.isPresent())
                {
                    constructorNode.statements.add(statementNode.get());
                }
                else
                {
                    RequireNewLine();
                }
            }
        }

        return Optional.of(constructorNode);
    }

    //Member = VariableDeclaration NEWLINE [ INDENT [ "accessor:" Statements] ["mutator:" Statements] DEDENT]
    /*
    number t
        accessor:
            value=t
        mutator:
            t=value

     */
    /*
    **DEFINITELY problems in this function**
     */
    private Optional<MemberNode> parseMember() throws SyntaxErrorException {
        MemberNode memberNode = new MemberNode();

        Optional<VariableDeclarationNode> declaration = parseVariable();
        if(declaration.isPresent())
        {
            memberNode.declaration = declaration.get();
        }
        else
        {
            throw new SyntaxErrorException("Expected Member", tokens.getCurrentLine(), tokens.getCurrentColumnNumber());
        }

        if(tokens.peek(0).get().getType() == Token.TokenTypes.DEDENT)
        {
            return Optional.of(memberNode);
        }

        RequireNewLine();

        if(tokens.matchAndRemove(Token.TokenTypes.INDENT).isPresent()) {

            if (tokens.matchAndRemove(Token.TokenTypes.ACCESSOR).isPresent()) {
                if (tokens.matchAndRemove(Token.TokenTypes.COLON).isEmpty()) {
                    throw new SyntaxErrorException("Expected Accessor statements", tokens.getCurrentLine(), tokens.getCurrentColumnNumber());
                }

                RequireNewLine();

                //Statements = INDENT {Statement NEWLINE } DEDENT
                //Parse statements
                if(tokens.matchAndRemove(Token.TokenTypes.INDENT).isEmpty()) {
                    throw new SyntaxErrorException("Expected Indent ", tokens.getCurrentLine(), tokens.getCurrentColumnNumber());
                }
                List<StatementNode> statements = new ArrayList<>();
                while(tokens.matchAndRemove(Token.TokenTypes.DEDENT).isEmpty())
                {
                    Optional<StatementNode> statementNode = parseStatementNode();
                    if(statementNode.isPresent())
                    {
                        statements.add(statementNode.get());
                    }
                    else
                    {
                        RequireNewLine();
                    }

                }
                if(!statements.isEmpty())
                {
                    memberNode.accessor = Optional.of(statements);
                }
                if(tokens.matchAndRemove(Token.TokenTypes.DEDENT).isEmpty())
                {
                    throw new SyntaxErrorException("Expected DEDENT", tokens.getCurrentLine(), tokens.getCurrentColumnNumber());
                }


            }
            if (tokens.matchAndRemove(Token.TokenTypes.MUTATOR).isPresent()) {
                if (tokens.matchAndRemove(Token.TokenTypes.COLON).isEmpty()) {
                    throw new SyntaxErrorException("Expected Mutator statements", tokens.getCurrentLine(), tokens.getCurrentColumnNumber());
                }

                RequireNewLine();

                //Statements = INDENT {Statement NEWLINE } DEDENT
                //Parse statements
                if(tokens.matchAndRemove(Token.TokenTypes.INDENT).isEmpty()) {
                    throw new SyntaxErrorException("Expected Indent ", tokens.getCurrentLine(), tokens.getCurrentColumnNumber());
                }
                List<StatementNode> statements = new ArrayList<>();
                while(tokens.matchAndRemove(Token.TokenTypes.DEDENT).isEmpty())
                {
                    Optional<StatementNode> statementNode = parseStatementNode();
                    if(statementNode.isPresent())
                    {
                        statements.add(statementNode.get());
                    }
                    else
                    {
                        RequireNewLine();
                    }

                }
                if(!statements.isEmpty())
                {
                    memberNode.mutator = Optional.of(statements);
                }
                if(tokens.matchAndRemove(Token.TokenTypes.DEDENT).isEmpty())
                {
                    throw new SyntaxErrorException("Expected DEDENT", tokens.getCurrentLine(), tokens.getCurrentColumnNumber());
                }

            }

        }

        return Optional.of(memberNode);
    } //BROKEN

    //If = "if" BoolExp NEWLINE Statements ["else" NEWLINE (Statement | Statements)]
    private Optional<IfNode> parseIfNode() throws SyntaxErrorException {
        IfNode ifNode = new IfNode();
        if(tokens.matchAndRemove(Token.TokenTypes.IF).isEmpty())
        {
            return Optional.empty();
        }


        var boolexp = BoolexpTerm();
        if(boolexp.isPresent())
        {
            ifNode.condition = boolexp.get();
        }

        if(tokens.matchAndRemove(Token.TokenTypes.NEWLINE).isEmpty())
        {
            throw new SyntaxErrorException("Expected Newline", tokens.getCurrentLine(), tokens.getCurrentColumnNumber());
        }
        //parse statements

        if(tokens.matchAndRemove(Token.TokenTypes.INDENT).isEmpty()) {
            throw new SyntaxErrorException("Expected Indent ", tokens.getCurrentLine(), tokens.getCurrentColumnNumber());
        }
        List<StatementNode> statements = new ArrayList<>();
        while(tokens.matchAndRemove(Token.TokenTypes.DEDENT).isEmpty())
        {
            Optional<StatementNode> statementNode = parseStatementNode();
            if(statementNode.isPresent())
            {
                statements.add(statementNode.get());
            }
            else
            {
                RequireNewLine();
            }
        }
        if(!statements.isEmpty())
        {
            ifNode.statements = statements;
        }
        if(tokens.matchAndRemove(Token.TokenTypes.DEDENT).isEmpty())
        {
            throw new SyntaxErrorException("Expected DEDENT", tokens.getCurrentLine(), tokens.getCurrentColumnNumber());
        }

        if(tokens.peek(0).get().getType() == Token.TokenTypes.ELSE)
        {
            Optional<ElseNode> elseNode = parseElseNode();
            if(elseNode.isPresent())
            {
                ifNode.elseStatement = Optional.of(elseNode.get());
            }

        }
        else
        {
            ifNode.elseStatement = Optional.empty();
        }

        return Optional.of(ifNode);
    } //BROKEN

    //Loop = [VariableReference "=" ] "loop" ( BoolExpTerm ) NEWLINE Statements //BROKEN
    private Optional<LoopNode> parseLoopNode() throws SyntaxErrorException {
        LoopNode loopNode = new LoopNode();
        Optional<VariableReferenceNode> variableReferenceNode = parseVariableReference();
        if(variableReferenceNode.isPresent())
        {
            if(tokens.matchAndRemove(Token.TokenTypes.ASSIGN).isEmpty())
            {
                throw new SyntaxErrorException("Expected Assign for variable", tokens.getCurrentLine(), tokens.getCurrentColumnNumber());
            }
            loopNode.assignment = Optional.of(variableReferenceNode.get());
        }

        if(tokens.matchAndRemove(Token.TokenTypes.LOOP).isEmpty())
        {
            return Optional.empty();
        }

        if(tokens.matchAndRemove(Token.TokenTypes.LPAREN).isEmpty())
        {
            throw new SyntaxErrorException("Expected LPAREN", tokens.getCurrentLine(), tokens.getCurrentColumnNumber());
        }

        var boolexp = BoolexpTerm();
        if(boolexp.isPresent())
        {
            loopNode.expression = boolexp.get();
        }
        //loopNode.expression = boolexp;

        if(tokens.matchAndRemove(Token.TokenTypes.RPAREN).isEmpty())
        {
            throw new SyntaxErrorException("Expected RPAREN", tokens.getCurrentLine(), tokens.getCurrentColumnNumber());
        }

        if(tokens.matchAndRemove(Token.TokenTypes.NEWLINE).isEmpty())
        {
            throw new SyntaxErrorException("Expected Newline", tokens.getCurrentLine(), tokens.getCurrentColumnNumber());
        }

        //parse statements
        if(tokens.matchAndRemove(Token.TokenTypes.INDENT).isEmpty()) {
            throw new SyntaxErrorException("Expected Indent ", tokens.getCurrentLine(), tokens.getCurrentColumnNumber());
        }
        List<StatementNode> statements = new ArrayList<>();
        while(tokens.matchAndRemove(Token.TokenTypes.DEDENT).isEmpty())
        {
            Optional<StatementNode> statementNode = parseStatementNode();
            if(statementNode.isPresent())
            {
                statements.add(statementNode.get());
            }
            else
            {
                RequireNewLine();
            }

        }
        if(!statements.isEmpty())
        {
            loopNode.statements = statements;
        }
        if(tokens.matchAndRemove(Token.TokenTypes.DEDENT).isEmpty())
        {
            throw new SyntaxErrorException("Expected DEDENT", tokens.getCurrentLine(), tokens.getCurrentColumnNumber());
        }


        return Optional.of(loopNode);
    }

    private Optional<VariableReferenceNode> parseVariableReference() throws SyntaxErrorException {
        VariableReferenceNode variableReferenceNode = new VariableReferenceNode();
        String name = tokens.peek(0).get().getValue();
        if(tokens.matchAndRemove(Token.TokenTypes.WORD).isEmpty())
        {
            return Optional.empty();
        }
        variableReferenceNode.name = name;
        return Optional.of(variableReferenceNode);
    }

    private Optional<ElseNode> parseElseNode() throws SyntaxErrorException
    {
        ElseNode elseNode = new ElseNode();
        if(tokens.matchAndRemove(Token.TokenTypes.ELSE).isEmpty())
        {
            return Optional.empty();
        }

        if(tokens.matchAndRemove(Token.TokenTypes.NEWLINE).isEmpty())
        {
            throw new SyntaxErrorException("Expected Newline", tokens.getCurrentLine(), tokens.getCurrentColumnNumber());
        }

        //parse statements
        if(tokens.matchAndRemove(Token.TokenTypes.INDENT).isEmpty()) {
            throw new SyntaxErrorException("Expected Indent ", tokens.getCurrentLine(), tokens.getCurrentColumnNumber());
        }
        while(tokens.matchAndRemove(Token.TokenTypes.DEDENT).isEmpty())
        {
            Optional<StatementNode> statementNode = parseStatementNode();
            if(statementNode.isPresent())
            {
                elseNode.statements.add(statementNode.get());
            }
            else
            {
                RequireNewLine();
            }

        }
        if(tokens.matchAndRemove(Token.TokenTypes.DEDENT).isEmpty())
        {
            throw new SyntaxErrorException("Expected DEDENT", tokens.getCurrentLine(), tokens.getCurrentColumnNumber());
        }

        return Optional.of(elseNode);
    }

    //Expression = Term { ("+"|"-") Term }
    private Optional<ExpressionNode> Expression() throws SyntaxErrorException {
        Optional<ExpressionNode> left = parseTerm();
        if(left.isEmpty())
        {
            return Optional.empty();
        }

        var peekedToken = tokens.peek(0).get().getType();

        while(peekedToken == Token.TokenTypes.PLUS || peekedToken == Token.TokenTypes.MINUS)
        {
            MathOpNode mathOpNode = new MathOpNode();
            if(peekedToken == Token.TokenTypes.PLUS) {
                tokens.matchAndRemove(Token.TokenTypes.PLUS);
                mathOpNode.op = MathOpNode.MathOperations.add;
            }
            else if(peekedToken == Token.TokenTypes.MINUS) {
                tokens.matchAndRemove(Token.TokenTypes.MINUS);
                mathOpNode.op = MathOpNode.MathOperations.subtract;
            }
            mathOpNode.left = left.get();
            Optional<ExpressionNode> right = parseTerm();
            if(right.isEmpty())
            {
                throw new SyntaxErrorException("Expected Right side of the equation", tokens.getCurrentLine(), tokens.getCurrentColumnNumber());
            }
            mathOpNode.right = right.get();
            left = Optional.of(mathOpNode);
            peekedToken = tokens.peek(0).get().getType();
        }
        //return Optional.of(parseVariableReference().get());
        return left;
    }

    //Term = Factor { ("*"|"/"|"%") Factor }
    private Optional<ExpressionNode> parseTerm() throws SyntaxErrorException {
        Optional<ExpressionNode> left = parseFactor();
        if(left.isEmpty())
        {
            return Optional.empty();
        }

        var peekedToken = tokens.peek(0).get().getType();
        while(peekedToken == Token.TokenTypes.TIMES || peekedToken == Token.TokenTypes.DIVIDE || peekedToken == Token.TokenTypes.MODULO)
        {
            MathOpNode mathOpNode = new MathOpNode();
            if(peekedToken == Token.TokenTypes.TIMES) {
                tokens.matchAndRemove(Token.TokenTypes.TIMES);
                mathOpNode.op = MathOpNode.MathOperations.multiply;
            }
            else if(peekedToken == Token.TokenTypes.DIVIDE) {
                tokens.matchAndRemove(Token.TokenTypes.DIVIDE);
                mathOpNode.op = MathOpNode.MathOperations.divide;
            }
            else if(peekedToken == Token.TokenTypes.MODULO) {
                tokens.matchAndRemove(Token.TokenTypes.MODULO);
                mathOpNode.op = MathOpNode.MathOperations.modulo;
            }
            mathOpNode.left = left.get();
            Optional<ExpressionNode> right = parseFactor();
            if(right.isEmpty())
            {
                throw new SyntaxErrorException("Expected right hand of the equation term", tokens.getCurrentLine(), tokens.getCurrentColumnNumber());
            }
            mathOpNode.right = right.get();
            left = Optional.of(mathOpNode);
            peekedToken = tokens.peek(0).get().getType();
        }
        return left;
    }

    //Factor = NumberLiteral | VariableReference | "true" | "false" | StringLiteral | CharacterLiteral
    //| MethodCallExpression | "(" Expression ")" | "new" Identifier "(" [Expression {"," Expression }]
    //")"
    private Optional<ExpressionNode> parseFactor() throws SyntaxErrorException {
        var peekedToken = tokens.peek(0).get().getType();

        //Number Literals
        if(peekedToken == Token.TokenTypes.NUMBER)
        {
            NumericLiteralNode numberNode = new NumericLiteralNode();
            float numberliteral = Float.parseFloat(tokens.matchAndRemove(Token.TokenTypes.NUMBER).get().getValue());
            numberNode.value = numberliteral;
            return Optional.of(numberNode);
        }
        if(tokens.nextTwoTokensMatch(Token.TokenTypes.WORD, Token.TokenTypes.DOT) || tokens.nextTwoTokensMatch(Token.TokenTypes.WORD, Token.TokenTypes.LPAREN))
        {
            Optional<MethodCallExpressionNode> methodCallExpressionNode = MethodCallExpression();
            if(methodCallExpressionNode.isPresent())
            {
                return Optional.of(methodCallExpressionNode.get());
            }
        }
        if(peekedToken == Token.TokenTypes.WORD)
        {
            Optional<VariableReferenceNode> variableReferenceNode = parseVariableReference();
            if(variableReferenceNode.isPresent())
            {
                return Optional.of(variableReferenceNode.get());
            }
        }
        if(peekedToken == Token.TokenTypes.TRUE)
        {
           tokens.matchAndRemove(Token.TokenTypes.TRUE);
           BooleanLiteralNode booleanLiteralNode = new BooleanLiteralNode(true);
           return Optional.of(booleanLiteralNode);
        }
        if(peekedToken == Token.TokenTypes.FALSE)
        {
            tokens.matchAndRemove(Token.TokenTypes.FALSE);
            BooleanLiteralNode booleanLiteralNode = new BooleanLiteralNode(false);
            return Optional.of(booleanLiteralNode);
        }
        if(peekedToken == Token.TokenTypes.QUOTEDSTRING)
        {
            StringLiteralNode stringLiteralNode = new StringLiteralNode();
            stringLiteralNode.value = tokens.matchAndRemove(Token.TokenTypes.QUOTEDSTRING).get().getValue();
            return Optional.of(stringLiteralNode);
        }
        if(peekedToken == Token.TokenTypes.QUOTEDCHARACTER)
        {
            String char_value = tokens.matchAndRemove(Token.TokenTypes.QUOTEDCHARACTER).get().getValue();
            char character = char_value.charAt(0);
            CharLiteralNode charLiteralNode = new CharLiteralNode();
            charLiteralNode.value = character;
            return Optional.of(charLiteralNode);

        }
        if(peekedToken == Token.TokenTypes.LPAREN)
        {
            tokens.matchAndRemove(Token.TokenTypes.LPAREN);

            Optional<ExpressionNode> expressionNode = Expression();
            if(tokens.matchAndRemove(Token.TokenTypes.RPAREN).isEmpty())
            {
                throw new SyntaxErrorException("Expected right paren at expression", tokens.getCurrentLine(), tokens.getCurrentColumnNumber());
            }

            if(expressionNode.isPresent())
            {
                return Optional.of(expressionNode.get());
            }
            else {
                throw new SyntaxErrorException("Something went wrong in the parse factor", tokens.getCurrentLine(), tokens.getCurrentColumnNumber());
            }
        }
        if(peekedToken == Token.TokenTypes.NEW)
        {
            NewNode newNode = new NewNode();
            tokens.matchAndRemove(Token.TokenTypes.NEW);

            newNode.className = tokens.matchAndRemove(Token.TokenTypes.WORD).get().getValue(); //Setting the Method name
            if(tokens.matchAndRemove(Token.TokenTypes.LPAREN).isEmpty())
            {
                throw new SyntaxErrorException("Expected left paren at new(factor method)", tokens.getCurrentLine(), tokens.getCurrentColumnNumber());
            } //parsing the left paren

            do
            {
                Optional<ExpressionNode> expressionNode = Expression();
                if(expressionNode.isPresent())
                {
                    newNode.parameters.add(expressionNode.get());
                }
                else {
                    break;
                }

            }while(tokens.matchAndRemove(Token.TokenTypes.COMMA).isPresent());

            if(tokens.matchAndRemove(Token.TokenTypes.RPAREN).isEmpty())
            {
                throw new SyntaxErrorException("Expected closing parenthesis for new node(inside parse factor)", tokens.getCurrentLine(), tokens.getCurrentColumnNumber());
            }

            return Optional.of(newNode);

        }

        return Optional.empty();
    }

    //MethodCall = [VariableReference { "," VariableReference } "=" MethodCallExpression
    private Optional<MethodCallStatementNode> parseMethodCall() throws SyntaxErrorException {

        ArrayList<VariableReferenceNode> returnValue= new ArrayList<VariableReferenceNode>();
        do {

            Optional<VariableReferenceNode> variableReferenceNode = parseVariableReference();
            if(variableReferenceNode.isPresent())
            {
                returnValue.add(variableReferenceNode.get());
            }

        }while(tokens.matchAndRemove(Token.TokenTypes.COMMA).isPresent());


        if(tokens.matchAndRemove(Token.TokenTypes.ASSIGN).isEmpty())
        {
            throw new SyntaxErrorException("Expected Assign to method call", tokens.getCurrentLine(), tokens.getCurrentColumnNumber());
        }

        Optional<MethodCallExpressionNode> methodCallExpressionNode = MethodCallExpression();
        if(methodCallExpressionNode.isEmpty())
        {
            throw new SyntaxErrorException("Expected MethodCall to method call", tokens.getCurrentLine(), tokens.getCurrentColumnNumber());
            //MethodCallStatementNode methodcall = new MethodCallStatementNode(methodCallExpressionNode.get());
            //methodcall.returnValues  = returnValue;
        }
        MethodCallStatementNode methodcall = new MethodCallStatementNode(methodCallExpressionNode.get());
        methodcall.returnValues  = returnValue;

        return Optional.of(methodcall);
    }


    //First draft done 10/30/2024: 3:01pm
    //MethodCallExpression = [Identifier "."] Identifier "(" [Expression {"," Expression }] ")"
    private Optional<MethodCallExpressionNode> MethodCallExpression() throws SyntaxErrorException {
        MethodCallExpressionNode methodCallExpressionNode = new MethodCallExpressionNode();
        //Checks if there is an obj name such as test.
        if(tokens.nextTwoTokensMatch(Token.TokenTypes.WORD, Token.TokenTypes.DOT))
        {
            methodCallExpressionNode.objectName = Optional.of(tokens.matchAndRemove(Token.TokenTypes.WORD).get().getValue()); //Setting the obj name
            tokens.matchAndRemove(Token.TokenTypes.DOT); //parsing the dot(doesn't really do anything
        }
        if(tokens.nextTwoTokensMatch(Token.TokenTypes.WORD, Token.TokenTypes.LPAREN))
        {
            methodCallExpressionNode.methodName = tokens.matchAndRemove(Token.TokenTypes.WORD).get().getValue(); //Setting the Method name
            tokens.matchAndRemove(Token.TokenTypes.LPAREN); //parsing the left paren

            do
            {
                Optional<ExpressionNode> expressionNode = Expression();
                if(expressionNode.isPresent())
                {
                    methodCallExpressionNode.parameters.add(expressionNode.get());
                }
                else {
                    break;
                }

            }while(tokens.matchAndRemove(Token.TokenTypes.COMMA).isPresent());

            if(tokens.matchAndRemove(Token.TokenTypes.RPAREN).isEmpty())
            {
                throw new SyntaxErrorException("Expected closing parenthesis for method call", tokens.getCurrentLine(), tokens.getCurrentColumnNumber());
            }

            return Optional.of(methodCallExpressionNode);
        }
        else
        {
            return Optional.empty();
        }
    }

    private Optional<StatementNode> disambiguate() throws SyntaxErrorException {

        if(tokens.nextTwoTokensMatch(Token.TokenTypes.WORD, Token.TokenTypes.COMMA))
        {
            Optional<MethodCallStatementNode> methodcallstatement = parseMethodCall();
            if(methodcallstatement.isPresent())
            {
                return Optional.of(methodcallstatement.get());
            }
        }
//        Optional<MethodCallExpressionNode> methodCallExpression = MethodCallExpression();
//        if(methodCallExpression.isPresent())
//        {
//            return Optional.of(new MethodCallStatementNode(methodCallExpression.get()));
//        }


        Optional<VariableReferenceNode> variableReferenceNode = parseVariableReference();
        if(variableReferenceNode.isPresent())
        {
            if(tokens.matchAndRemove(Token.TokenTypes.ASSIGN).isPresent())
            {
                Optional<ExpressionNode> expressionNode = Expression();
                if(expressionNode.isPresent())
                {
                    Optional<AssignmentNode> assignmentNode = Optional.of(new AssignmentNode());
                    assignmentNode.get().target = variableReferenceNode.get();
                    assignmentNode.get().expression = expressionNode.get();
                    return Optional.of(assignmentNode.get());
                }
                else
                {
                    return Optional.empty();
                }
            }
        }
        else
        {
            return Optional.empty();
        }
        return Optional.empty();
    }

    //Took the code above and commented it out, instead calling specific AND OR and NOT terms as separate functions in order to implement an order of precedence ie NOT -> AND -> OR
    //The method to call at the begging of each function was from the discussion so the highest precedence(NOT) will be at the bottom as the last call
    private Optional<ExpressionNode> BoolexpTerm() throws SyntaxErrorException {
        Optional<ExpressionNode> left = BoolexpAnd(); //Call to and
        if (left.isEmpty()) {
            return Optional.empty();
        }

        while (tokens.peek(0).get().getType() == Token.TokenTypes.OR) {
            tokens.matchAndRemove(Token.TokenTypes.OR);
            Optional<ExpressionNode> right = BoolexpAnd();
            if (right.isEmpty()) {
                throw new SyntaxErrorException("Expected expression after 'or'",
                        tokens.getCurrentLine(), tokens.getCurrentColumnNumber());
            }

            BooleanOpNode booleanOpNode = new BooleanOpNode();
            booleanOpNode.left = left.get();
            booleanOpNode.right = right.get();
            booleanOpNode.op = BooleanOpNode.BooleanOperations.or;
            left = Optional.of(booleanOpNode);
        }
        return left;
    }

    private Optional<ExpressionNode> BoolexpAnd() throws SyntaxErrorException {
        Optional<ExpressionNode> left = BoolexpNot(); //call to not
        if (left.isEmpty()) {
            return Optional.empty();
        }

        while (tokens.peek(0).get().getType() == Token.TokenTypes.AND) {
            tokens.matchAndRemove(Token.TokenTypes.AND);
            Optional<ExpressionNode> right = BoolexpNot();
            if (right.isEmpty()) {
                throw new SyntaxErrorException("Expected expression after 'and'",
                        tokens.getCurrentLine(), tokens.getCurrentColumnNumber());
            }
            BooleanOpNode booleanOpNode = new BooleanOpNode();
            booleanOpNode.left = left.get();
            booleanOpNode.right = right.get();
            booleanOpNode.op = BooleanOpNode.BooleanOperations.and;
            left = Optional.of(booleanOpNode);
        }
        return left;
    }
    //Since not has the highest precedence it will be parsed first
    private Optional<ExpressionNode> BoolexpNot() throws SyntaxErrorException {
        if (tokens.matchAndRemove(Token.TokenTypes.NOT).isPresent()) {
            Optional<ExpressionNode> operand = BoolexpNot(); //If not is found recursively call function again to parse either another not or the factor
            if (operand.isEmpty()) {
                throw new SyntaxErrorException("Expected expression after 'not'",
                        tokens.getCurrentLine(), tokens.getCurrentColumnNumber());
            }
            NotOpNode notNode = new NotOpNode();
            notNode.left= operand.get();
            return Optional.of(notNode);
        } else {
            return BoolexpFactor();
        }
    }

//    BoolExpFactor = MethodCallExpression | (Expression ( "==" | "!=" | "<=" | ">=" | ">" | "<" )
//    Expression) | VariableReference
    private Optional<ExpressionNode> BoolexpFactor() throws SyntaxErrorException {
        //CompareNode compareNode = new CompareNode();
        //Step 1 look for a methodCallExpression.
        Optional<MethodCallExpressionNode> methodCall = MethodCallExpression();
        if(methodCall.isPresent())
        {
            return Optional.of(methodCall.get());
        }


        //Step 2 look for a comparison expression
        Optional<CompareNode> compareNode = Optional.of(new CompareNode());
        Optional<ExpressionNode> expressionNodeLeft = Expression();
        if(expressionNodeLeft.isPresent())
        {
            compareNode.get().left = expressionNodeLeft.get(); //If the expression is present then we will check for the left side
        }
        if(tokens.matchAndRemove(Token.TokenTypes.EQUAL).isPresent())
        {
            compareNode.get().op = CompareNode.CompareOperations.eq;
        }
        else if(tokens.matchAndRemove(Token.TokenTypes.NOTEQUAL).isPresent())
        {
            compareNode.get().op = CompareNode.CompareOperations.ne;
        }
        else if(tokens.matchAndRemove(Token.TokenTypes.LESSTHANEQUAL).isPresent())
        {
            compareNode.get().op = CompareNode.CompareOperations.le;
        }
        else if(tokens.matchAndRemove(Token.TokenTypes.GREATERTHANEQUAL).isPresent())
        {
            compareNode.get().op = CompareNode.CompareOperations.ge;
        }
        else if(tokens.matchAndRemove(Token.TokenTypes.LESSTHAN).isPresent())
        {
            compareNode.get().op = CompareNode.CompareOperations.lt;
        }
        else if(tokens.matchAndRemove(Token.TokenTypes.GREATERTHAN).isPresent())
        {
            compareNode.get().op = CompareNode.CompareOperations.gt;
        }

        if (compareNode.get().op != null) {
            Optional<ExpressionNode> rightExpression = Expression();  // Parse RHS expression
            if (rightExpression.isPresent()) {
                // Create and return the CompareNode
                compareNode.get().right = rightExpression.get();
                return Optional.of(compareNode.get());
            } else {
                // If we have a left expression and operator but no right expression, throw an error
                throw new SyntaxErrorException("error: expected a boolean op and a right side", tokens.getCurrentLine(), tokens.getCurrentColumnNumber());
            }
        }
        else
        {
            return Optional.of(expressionNodeLeft.get());
        }

    }

    Optional<StatementNode> parseStatementNode() throws SyntaxErrorException {
        var peekedToken = tokens.peek(0).get().getType();
        if(peekedToken == Token.TokenTypes.IF)
        {
            Optional<IfNode> ifNode_1 = parseIfNode();
            if(ifNode_1.isPresent())
            {
                return Optional.of(ifNode_1.get());
                //RequireNewLine();
            }
        }
        else if(peekedToken == Token.TokenTypes.LOOP /*|| tokens.nextTwoTokensMatch(Token.TokenTypes.WORD, Token.TokenTypes.ASSIGN)*/)
        {
            Optional<LoopNode> loopNode = parseLoopNode();
            if(loopNode.isPresent())
            {
                return Optional.of(loopNode.get());
                //RequireNewLine();
            }
        }
        else
        {
            Optional<StatementNode> disambiguate = disambiguate();
            if(disambiguate.isPresent())
            {
                return Optional.of(disambiguate.get());
            }

            //RequireNewLine();
        }
        return Optional.empty();
    }


    /*
    Looks at the current token, if there is supposed to be a newline there and there is not it will return a error,
    if there is a newline there nothing will happen and the parser will continue to parse
     */
    public void RequireNewLine() throws SyntaxErrorException {
        if(!tokens.matchAndRemove(Token.TokenTypes.NEWLINE).isPresent())
        {
            throw new SyntaxErrorException("expected a newline", tokens.getCurrentLine(), tokens.getCurrentColumnNumber());
        }
    }
}