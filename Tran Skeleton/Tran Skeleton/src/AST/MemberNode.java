package AST;

import java.util.List;
import java.util.Optional;

public class MemberNode implements Node {
    public VariableDeclarationNode declaration;
    public Optional<List<StatementNode>> accessor = Optional.empty();
    public Optional<List<StatementNode>> mutator = Optional.empty();


    @Override
    public String toString() {
        return  declaration +
                (accessor.map(statementNodes -> "accessor:\n" + Node.statementListToString(statementNodes)).orElse("")) +
                (mutator.map(statementNodes -> "mutator:\n" + Node.statementListToString(statementNodes)).orElse("")) +
                "\n" ;
    }
}
