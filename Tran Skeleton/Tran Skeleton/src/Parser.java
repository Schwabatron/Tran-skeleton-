import AST.InterfaceNode;
import AST.MethodHeaderNode;
import AST.TranNode;
import AST.VariableDeclarationNode;

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
            if (tokens.peek(0).get().getType() != Token.TokenTypes.INTERFACE)
            {
                RequireNewLine();
            }
            Optional<InterfaceNode> interfaceNode = parseInterface();
            if (interfaceNode.isPresent()) {
                top.Interfaces.add(interfaceNode.get());
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
             if( tokens.peek(0).get().getType() != Token.TokenTypes.DEDENT)
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
            throw new SyntaxErrorException("Expected Method", tokens.getCurrentLine(), tokens.getCurrentColumnNumber());
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
//            if(tokens.peek(0).get().getType() != Token.TokenTypes.WORD)
//            {
//                throw new SyntaxErrorException("Expected Name", tokens.getCurrentLine(), tokens.getCurrentColumnNumber());
//            }
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
            throw new SyntaxErrorException("Expected Variable type", tokens.getCurrentLine(), tokens.getCurrentColumnNumber());
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