package Interpreter;

import AST.BuiltInMethodDeclarationNode;

import java.util.LinkedList;
import java.util.List;

public class ConsoleWrite extends BuiltInMethodDeclarationNode {
    public LinkedList<String> console = new LinkedList<>();
    @Override
    public List<InterpreterDataType> Execute(List<InterpreterDataType> params) {
        StringBuilder sb = new StringBuilder();
        for (var i : params) {
            sb.append(i.toString());
            System.out.print(i.toString());
        }
        System.out.println();
        console.add(sb.toString());
        return List.of();
    }
}

