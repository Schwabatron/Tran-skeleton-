import AST.*;

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
        while(tokens.matchAndRemove(Token.TokenTypes.DEDENT).isEmpty())
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
            if(tokens.peek(0).get().getType() == Token.TokenTypes.WORD)
            {
                Optional<VariableDeclarationNode> variableDeclarationNode = parseVariable();
                if(variableDeclarationNode.isPresent()) {
                    methodNode.locals.add(variableDeclarationNode.get());
                    RequireNewLine();
                }

            }
            else if(tokens.peek(0).get().getType() == Token.TokenTypes.IF)
            {
                Optional<IfNode> ifNode = parseIfNode();
                if(ifNode.isPresent())
                {
                    methodNode.statements.add(ifNode.get());
                }
            }
            else if(tokens.peek(0).get().getType() == Token.TokenTypes.LOOP)
            {
                Optional<LoopNode> loopNode = parseLoopNode();
                if(loopNode.isPresent())
                {
                    methodNode.statements.add(loopNode.get());
                }
            }
            else
            {
                RequireNewLine();
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
            if(tokens.peek(0).get().getType() == Token.TokenTypes.WORD)
            {
                Optional<VariableDeclarationNode> variableDeclarationNode = parseVariable();
                if(variableDeclarationNode.isPresent()) {
                    constructorNode.locals.add(variableDeclarationNode.get());

                        RequireNewLine();

                }

            }
            else if(tokens.peek(0).get().getType() == Token.TokenTypes.IF)
            {
                Optional<IfNode> ifNode = parseIfNode();
                if(ifNode.isPresent())
                {
                    constructorNode.statements.add(ifNode.get());
                }
            }
            else if(tokens.peek(0).get().getType() == Token.TokenTypes.LOOP || tokens.nextTwoTokensMatch(Token.TokenTypes.WORD, Token.TokenTypes.ASSIGN))
            {
                Optional<LoopNode> loopNode = parseLoopNode();
                if(loopNode.isPresent())
                {
                    constructorNode.statements.add(loopNode.get());
                }
            }
            else
            {
                RequireNewLine();
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
                while(tokens.matchAndRemove(Token.TokenTypes.DEDENT).isEmpty())
                {
                    if(tokens.peek(0).get().getType() == Token.TokenTypes.IF)
                    {
                        Optional<IfNode> ifNode = parseIfNode();
                        if(ifNode.isPresent())
                        {
                            memberNode.accessor.get().add(ifNode.get());
                            RequireNewLine();
                        }
                    }
                    else if(tokens.peek(0).get().getType() == Token.TokenTypes.LOOP || tokens.nextTwoTokensMatch(Token.TokenTypes.WORD, Token.TokenTypes.ASSIGN))
                    {
                        Optional<LoopNode> loopNode = parseLoopNode();
                        if(loopNode.isPresent())
                        {
                            memberNode.accessor.get().add(loopNode.get());
                            RequireNewLine();
                        }
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
                while(tokens.matchAndRemove(Token.TokenTypes.DEDENT).isEmpty())
                {
                    if(tokens.peek(0).get().getType() == Token.TokenTypes.IF)
                    {
                        Optional<IfNode> ifNode = parseIfNode();
                        if(ifNode.isPresent())
                        {
                            memberNode.mutator.get().add(ifNode.get());
                            RequireNewLine();
                        }
                    }
                    else if(tokens.peek(0).get().getType() == Token.TokenTypes.LOOP || tokens.nextTwoTokensMatch(Token.TokenTypes.WORD, Token.TokenTypes.ASSIGN))
                    {
                        Optional<LoopNode> loopNode = parseLoopNode();
                        if(loopNode.isPresent())
                        {
                            memberNode.mutator.get().add(loopNode.get());
                            RequireNewLine();
                        }
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

        /*
        temporally using a null node for the boolexp
         */
        BooleanOpNode boolexp = new BooleanOpNode();
        boolexp = null;
        ifNode.condition = boolexp;

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
            if(tokens.matchAndRemove(Token.TokenTypes.IF).isPresent())
            {
                Optional<IfNode> ifNode_1 = parseIfNode();
                if(ifNode_1.isPresent())
                {
                    ifNode.statements.add(ifNode_1.get());
                    RequireNewLine();
                }
            }
            else if(tokens.matchAndRemove(Token.TokenTypes.LOOP).isPresent() || tokens.nextTwoTokensMatch(Token.TokenTypes.WORD, Token.TokenTypes.ASSIGN))
            {
                Optional<LoopNode> loopNode = parseLoopNode();
                if(loopNode.isPresent())
                {
                    ifNode.statements.add(loopNode.get());
                    RequireNewLine();
                }
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

        if(tokens.peek(0).get().getType() == Token.TokenTypes.ELSE)
        {
            Optional<ElseNode> elseNode = parseElseNode();
            if(elseNode.isPresent())
            {
                ifNode.elseStatement = Optional.of(elseNode.get());
            }
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

        BooleanOpNode boolexp = new BooleanOpNode();
        boolexp = null;
        loopNode.expression = boolexp;

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
        while(tokens.matchAndRemove(Token.TokenTypes.DEDENT).isEmpty())
        {
            if(tokens.matchAndRemove(Token.TokenTypes.IF).isPresent())
            {
                Optional<IfNode> ifNode = parseIfNode();
                if(ifNode.isPresent())
                {
                    loopNode.statements.add(ifNode.get());
                    RequireNewLine();
                }
            }
            else if(tokens.matchAndRemove(Token.TokenTypes.LOOP).isPresent() || tokens.nextTwoTokensMatch(Token.TokenTypes.WORD, Token.TokenTypes.ASSIGN))
            {
                Optional<LoopNode> loopNode_1 = parseLoopNode();
                if(loopNode_1.isPresent())
                {
                    loopNode.statements.add(loopNode_1.get());
                    RequireNewLine();
                }
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
            if(tokens.matchAndRemove(Token.TokenTypes.IF).isPresent())
            {
                Optional<IfNode> ifNode = parseIfNode();
                if(ifNode.isPresent())
                {
                    elseNode.statements.add(ifNode.get());
                    RequireNewLine();
                }
            }
            else if(tokens.matchAndRemove(Token.TokenTypes.LOOP).isPresent() || tokens.nextTwoTokensMatch(Token.TokenTypes.WORD, Token.TokenTypes.ASSIGN))
            {
                Optional<LoopNode> loopNode = parseLoopNode();
                if(loopNode.isPresent())
                {
                    elseNode.statements.add(loopNode.get());
                    RequireNewLine();
                }
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


    private Optional<ExpressionNode> Expression() throws SyntaxErrorException {
        return Optional.of(parseVariableReference().get());
    }

    /*
    temporarily returning a empty for Expression()
     */
    private Optional<MethodCallExpressionNode> MethodCallExpression(){
        return Optional.empty();
    }


    /*
    Done for first draft
     */
    private Optional<StatementNode> disambiguate() throws SyntaxErrorException {
        Optional<MethodCallExpressionNode> methodCallExpression = MethodCallExpression();
        if(methodCallExpression.isPresent())
        {
            return Optional.of(new MethodCallStatementNode(methodCallExpression.get()));
        }


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

   //BoolExpTerm = BoolExpFactor {("and"|"or") BoolExpTerm} | "not" BoolExpTerm
    private Optional<BooleanOpNode> BoolexpTerm() throws SyntaxErrorException {
        return Optional.empty();
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
        Optional<ExpressionNode> expressionNode = Expression();
        if(expressionNode.isPresent())
        {
            compareNode.get().left = expressionNode.get();
        }


        //step 3 look for a variable reference
        Optional<VariableReferenceNode> variableReferenceNode = parseVariableReference();
        if(variableReferenceNode.isPresent())
        {
            return Optional.of(variableReferenceNode.get());
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