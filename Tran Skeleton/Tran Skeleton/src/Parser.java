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

        }

    }

    /*
    Looks at the current token, if there is supposed to be a newline there and there is not it will return a error,
    if there is a newline there nothing will happen and the parser will continue to parse
     */
    public void RequireNewLine() throws SyntaxErrorException {
        if(tokens.peek(0).get().getType() != Token.TokenTypes.NEWLINE)
        {
            throw new SyntaxErrorException("expected a newline", tokens.getCurrentLine(), tokens.getCurrentColumnNumber());
        }
    }
}