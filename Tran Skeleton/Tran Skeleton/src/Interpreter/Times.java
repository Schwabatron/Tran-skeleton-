package Interpreter;

import AST.BuiltInMethodDeclarationNode;

import java.util.List;

public class Times extends BuiltInMethodDeclarationNode {
    @Override
    public List<InterpreterDataType> Execute(List<InterpreterDataType> params) {
        return (List<InterpreterDataType>) params.get(0);
    }
}
