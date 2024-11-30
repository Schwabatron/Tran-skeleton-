package Interpreter;

import AST.BuiltInMethodDeclarationNode;

import java.util.List;

public class Clone extends BuiltInMethodDeclarationNode{
    @Override
    public List<InterpreterDataType> Execute(List<InterpreterDataType> params) {
        return List.of();
    }
}