import AST.InterfaceNode;
import AST.MethodHeaderNode;
import AST.TranNode;

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
            Optional<InterfaceNode> interfaceNode = parseInterface();
            if (interfaceNode.isPresent()) {
                top.Interfaces.add(interfaceNode.get());
            }
            if(tokens.peek(0).isPresent() && tokens.peek(0).get().getType() == Token.TokenTypes.NEWLINE)
            {
                tokens.matchAndRemove(Token.TokenTypes.NEWLINE);
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
             if(tokens.peek(0).get().getType() == Token.TokenTypes.DEDENT)
             {
                 tokens.matchAndRemove(Token.TokenTypes.DEDENT);
                 break;
             }
             RequireNewLine();
             //Optional<MethodHeaderNode> methodNode = parseMethod();
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
        if(tokens.matchAndRemove(Token.TokenTypes.RPAREN).isEmpty())
        {
            throw new SyntaxErrorException("Expected Rparen", tokens.getCurrentLine(), tokens.getCurrentColumnNumber());
        }
        if(!tokens.matchAndRemove(Token.TokenTypes.COLON).isEmpty())
        {
            //Parsing a variable declaration
            if(tokens.matchAndRemove(Token.TokenTypes.WORD).isEmpty())
            {
                throw new SyntaxErrorException("Expected Return type", tokens.getCurrentLine(), tokens.getCurrentColumnNumber());
            }
            if(tokens.matchAndRemove(Token.TokenTypes.WORD).isEmpty())
            {
                throw new SyntaxErrorException("Expected return something", tokens.getCurrentLine(), tokens.getCurrentColumnNumber());
            }
        }
        return Optional.of(methodNode);
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