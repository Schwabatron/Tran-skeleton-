package AST;

public class VariableReferenceNode implements Node,ExpressionNode {
    public String name;

    @Override
    public String toString() {
        return name;
    }
}
