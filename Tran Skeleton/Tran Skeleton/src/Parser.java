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

        return Optional.of(methodNode);
    }

    //Constructor = "construct" "(" VariableDeclarations ")" NEWLINE MethodBody
    private Optional<ConstructorNode> parseConstructor() throws SyntaxErrorException {
        ConstructorNode constructorNode = new ConstructorNode();
        if(tokens.matchAndRemove(Token.TokenTypes.CONSTRUCT).isEmpty())
        {
            return Optional.empty();
        }

        if(!tokens.matchAndRemove(Token.TokenTypes.LPAREN).isEmpty())
        {
            throw new SyntaxErrorException("Expected Left Paren", tokens.getCurrentLine(), tokens.getCurrentColumnNumber());
        }

        if(!tokens.matchAndRemove(Token.TokenTypes.WORD).isEmpty())
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
        RequireNewLine();

        //parse method body
        //MethodBody = INDENT { VariableDeclaration NEWLINE } {Statement} DEDENT

        return Optional.of(constructorNode);
    }

    //Member = VariableDeclaration ["accessor:" Statements] ["mutator:" Statements]
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
        if(tokens.matchAndRemove(Token.TokenTypes.ACCESSOR).isPresent())
        {
            if(tokens.matchAndRemove(Token.TokenTypes.COLON).isEmpty())
            {
                throw new SyntaxErrorException("Expected Accessor statements", tokens.getCurrentLine(), tokens.getCurrentColumnNumber());
            }
            //Parse statements


        }
        if(tokens.matchAndRemove(Token.TokenTypes.MUTATOR).isPresent())
        {
            if(tokens.matchAndRemove(Token.TokenTypes.COLON).isEmpty())
            {
                throw new SyntaxErrorException("Expected Mutator statements", tokens.getCurrentLine(), tokens.getCurrentColumnNumber());
            }
            //Parse statements
        }


        return Optional.of(memberNode);
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