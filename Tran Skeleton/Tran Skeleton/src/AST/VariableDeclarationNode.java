package AST;

public class VariableDeclarationNode implements Node {
    public String type;
    public String name;

    @Override
    public String toString() {
        return type + " " + name;
    }
}
