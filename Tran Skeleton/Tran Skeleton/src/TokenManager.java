import java.util.List;
import java.util.Optional;

public class TokenManager {

    public TokenManager(List<Token> tokens) {
    }

    public boolean done() {
        return false;
    }

    public Optional<Token> matchAndRemove(Token.TokenTypes t) {
        return Optional.empty();
    }

    public Optional<Token> peek(int i) {
        return Optional.empty();
    }
}
