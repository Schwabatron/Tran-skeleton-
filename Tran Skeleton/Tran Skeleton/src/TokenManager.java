import java.util.List;
import java.util.Optional;

public class TokenManager {
    //Declaring the Tokens that will be parsed
    List<Token> Tokens;
    //private int token_position;

    public TokenManager(List<Token> tokens) {
        //Initializing the Token list with the Tokens passed in through the constructor
        this.Tokens = tokens;
        //this.token_position = 0;
    }

    public boolean done() {
        return Tokens.isEmpty();
    }

    /*
    The match and remove function takes in a tokentype as input, the function will then check the next
    Token and see if it is the same as the token type passed in, if it is we will
    remove it and return it
     */
    public Optional<Token> matchAndRemove(Token.TokenTypes t) {
        if(done())
        {
            return Optional.empty();
        }

        Token ret_token = Tokens.getFirst(); //Token we will be checking
        if(ret_token.getType() == t) {
            //If the token is a match we will increment(remove) the token and return the token
            Tokens.removeFirst();
            return Optional.of(ret_token);
        }
        else
        {
            //Otherwise returning an empty that can then be handled in the parser
            return Optional.empty();
        }
    }

    public Optional<Token> peek(int i) {
        //Checking if my peek will peek past the end of the token stream
        if(done())
        {
            return Optional.empty();
        }
        if(i > Tokens.size())
        {
            //If it does then return empty and handle it
            return Optional.empty();
        }
        else
        {
            //otherwise return the token at peeked position
            return Optional.of(Tokens.get(i));
        }
    }

    public boolean nextTwoTokensMatch(Token.TokenTypes first, Token.TokenTypes second) {
         Optional<Token> firstToken = peek(0);
         Optional<Token> secondToken = peek(1);
         //Checking if the optionals returns empty
         if(!firstToken.isPresent() && !secondToken.isPresent()) {
             return false;
         }
         //checking if the tokens are of the correct type
         if(firstToken.get().getType() == first && secondToken.get().getType() == second) {
             return true; //If they are return true
         }
         else
         {
             return false; //If they are not return false
         }
    }

    /*
    Accessors for the line number and column number
    Used to throw syntax exceptions
     */
    public int getCurrentLine()
    {
        return Tokens.getFirst().getLineNumber();
    }

    public int getCurrentColumnNumber()
    {
        return Tokens.getFirst().getColumnNumber();
    }
}
