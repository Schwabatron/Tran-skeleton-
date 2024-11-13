package Interpreter;

import AST.*;

import java.util.*;

public class Interpreter {
    private TranNode top;

    /** Constructor - get the interpreter ready to run. Set members from parameters and "prepare" the class.
     *
     * Store the tran node.
     * Add any built-in methods to the AST
     * @param top - the head of the AST
     */
    public Interpreter(TranNode top) {
        this.top = top;
       //starting point for the interpreter
    }

    /**
     * This is the public interface to the interpreter. After parsing, we will create an interpreter and call start to
     * start interpreting the code.
     *
     * Search the classes in Tran for a method that is "isShared", named "start", that is not private and has no parameters
     * Call "InterpretMethodCall" on that method, then return.
     * Throw an exception if no such method exists.
     */
    public void start() throws ClassNotFoundException{
        Interpreter interpreter = new Interpreter(top);
        for(var class_name : interpreter.top.Classes) //Looping through all the classes
        {
            for(var method : class_name.methods)//Looping through all the methods in each class
            {
                /*
                    If a method has the name start, is shared, has no params, and is not private
                 */
                if(method.isShared && method.name.equals("start") && method.parameters.isEmpty() && !method.isPrivate)
                {
                    /*
                        - the start method has no parameters so we can make an empty list
                        - Since the shared means it is not with a specific object, call with empty the method itself, and the list of params
                     */
                    ObjectIDT object= new ObjectIDT(class_name);
                    List<InterpreterDataType> list = new ArrayList<>();
                    interpretMethodCall(Optional.of(object), method, list);
                    return;
                }
            }
        }

        throw new ClassNotFoundException("no start class found or start class declared incorrectly");
    }

    //              Running Methods

    /**
     * Find the method (local to this class, shared (like Java's system.out.print), or a method on another class)
     * Evaluate the parameters to have a list of values
     * Use interpretMethodCall() to actually run the method.
     *
     * Call GetParameters() to get the parameter value list
     * Find the method. This is tricky - there are several cases:
     * someLocalMethod() - has NO object name. Look in "object"
     * console.write() - the objectName is a CLASS and the method is shared
     * bestStudent.getGPA() - the objectName is a local or a member
     *
     * Once you find the method, call InterpretMethodCall() on it. Return the list that it returns.
     * Throw an exception if we can't find a match.
     * @param object - the object we are inside right now (might be empty)
     * @param locals - the current local variables
     * @param mc - the method call
     * @return - the return values
     */
    private List<InterpreterDataType> findMethodForMethodCallAndRunIt(Optional<ObjectIDT> object, HashMap<String, InterpreterDataType> locals, MethodCallStatementNode mc) {
        List<InterpreterDataType> result = null;

        List<InterpreterDataType> parameters = getParameters(object, locals, mc); // Call to the getparameters() function

        /*
            Finding the method
         */

        return result;
    }

    /**
     * Run a "prepared" method (found, parameters evaluated)
     * This is split from findMethodForMethodCallAndRunIt() because there are a few cases where we don't need to do the finding:
     * in start() and dealing with loops with iterator objects, for example.
     *
     * Check to see if "m" is a built-in. If so, call Execute() on it and return
     * Make local variables, per "m"
     * If the number of passed in values doesn't match m's "expectations", throw
     * Add the parameters by name to locals.
     * Call InterpretStatementBlock
     * Build the return list - find the names from "m", then get the values for those names and add them to the list.
     * @param object - The object this method is being called on (might be empty for shared)
     * @param m - Which method is being called
     * @param values - The values to be passed in
     * @return the returned values from the method
     */
    private List<InterpreterDataType> interpretMethodCall(Optional<ObjectIDT> object, MethodDeclarationNode m, List<InterpreterDataType> values) {
        var retVal = new LinkedList<InterpreterDataType>();


        /*
            Note for later: I still have not implemented the built-in functions. this will need to be done and accounted for at a later time.
         */

        if(m.parameters.size() != values.size())
        {
            throw new IllegalArgumentException("the count and the number of parameters are mismatched");
        }

        /*
            - Stores the method's local variables, including parameters and any variables created within the method.
            - This acts as a symbol table for the current method call, allowing variables to be accessed by name.
         */
        HashMap<String, InterpreterDataType> locals = new HashMap<>();
        //Adding all the parameters to the hashmap
        for(int i = 0; i < m.parameters.size(); i++)
        {
            locals.put(m.parameters.get(i).name, values.get(i));
        }
        //Adding all the local variables to the hashmap
        for(int i = 0; i < m.locals.size(); i++)
        {
            InterpreterDataType local = instantiate(m.locals.get(i).type);
            locals.put(m.locals.get(i).name, local);
        }



        /*
            interprets the statements located in the body of the method(Assignments, if, loop, method calls)
        */
        interpretStatementBlock(object, m.statements, locals);

        /*
            - Creating the return values
            - these new values could've possibly been edited in locals in the statement block
         */
        for(var return_values : m.returns)
        {
            InterpreterDataType return_value = locals.get(return_values.name);
            if(return_value != null)
            {
                retVal.add(return_value);
            }
        }
        return retVal;
    }

    //              Running Constructors

    /**
     * This is a special case of the code for methods. Just different enough to make it worthwhile to split it out.
     *
     * Call GetParameters() to populate a list of IDT's
     * Call GetClassByName() to find the class for the constructor
     * If we didn't find the class, throw an exception
     * Find a constructor that is a good match - use DoesConstructorMatch()
     * Call InterpretConstructorCall() on the good match
     * @param callerObj - the object that we are inside when we called the constructor
     * @param locals - the current local variables (used to fill parameters)
     * @param mc  - the method call for this construction
     * @param newOne - the object that we just created that we are calling the constructor for
     */
    private void findConstructorAndRunIt(Optional<ObjectIDT> callerObj, HashMap<String, InterpreterDataType> locals, MethodCallStatementNode mc, ObjectIDT newOne) {
    }

    /**
     * Similar to interpretMethodCall, but "just different enough" - for example, constructors don't return anything.
     *
     * Creates local variables (as defined by the ConstructorNode), calls Instantiate() to do the creation
     * Checks to ensure that the right number of parameters were passed in, if not throw.
     * Adds the parameters (with the names from the ConstructorNode) to the locals.
     * Calls InterpretStatementBlock
     * @param object - the object that we allocated
     * @param c - which constructor is being called
     * @param values - the parameter values being passed to the constructor
     */
    private void interpretConstructorCall(ObjectIDT object, ConstructorNode c, List<InterpreterDataType> values) {
    }

    //              Running Instructions

    /**
     * Given a block (which could be from a method or an "if" or "loop" block, run each statement.
     * Blocks, by definition, do ever statement, so iterating over the statements makes sense.
     *
     * For each statement in statements:
     * check the type:
     *      For AssignmentNode, FindVariable() to get the target. Evaluate() the expression. Call Assign() on the target with the result of Evaluate()
     *      For MethodCallStatementNode, call doMethodCall(). Loop over the returned values and copy the into our local variables
     *      For LoopNode - there are 2 kinds.
     *          Setup:
     *          If this is a Loop over an iterator (an Object node whose class has "iterator" as an interface)
     *              Find the "getNext()" method; throw an exception if there isn't one
     *          Loop:
     *          While we are not done:
     *              if this is a boolean loop, Evaluate() to get true or false.
     *              if this is an iterator, call "getNext()" - it has 2 return values. The first is a boolean (was there another?), the second is a value
     *              If the loop has an assignment variable, populate it: for boolean loops, the true/false. For iterators, the "second value"
     *              If our answer from above is "true", InterpretStatementBlock() on the body of the loop.
     *       For If - Evaluate() the condition. If true, InterpretStatementBlock() on the if's statements. If not AND there is an else, InterpretStatementBlock on the else body.
     * @param object - the object that this statement block belongs to (used to get member variables and any members without an object)
     * @param statements - the statements to run
     * @param locals - the local variables
     */
    private void interpretStatementBlock(Optional<ObjectIDT> object, List<StatementNode> statements, HashMap<String, InterpreterDataType> locals) {
        //Setting up code for iterating over each statement in the method, if, or loop
        for(var statement: statements)
        {
            /*
                - If the statement is an instance of an assignment node we get the target, the expression, and evaluate it
                - Assign the value from evaluate to the target
             */
            if(statement instanceof AssignmentNode) {
                InterpreterDataType target =  findVariable(((AssignmentNode) statement).target.name,locals, object);
                InterpreterDataType evaluate_assignment = evaluate(locals, object, ((AssignmentNode) statement).expression);
                target.Assign(evaluate_assignment);
            }
            /*
                -For method call statement node we call findMethodForMethodCallAndRunIt() and loop over the returned values
             */
            else if(statement instanceof MethodCallStatementNode) {
                //Getting a list of the returned values from findMethodForMethodCallAndRunIt()
                List<InterpreterDataType> returned_values = findMethodForMethodCallAndRunIt(object, locals, ((MethodCallStatementNode) statement));
                for(var returned_value : returned_values)
                {
                   /*
                   Need to get the name of the returned value and then put the value where the string matches in local hashmap
                    */
                    locals.put(returned_value.toString(), returned_value); //maybe tostring (come back later)
                }
            }
        }

    }

    /**
     *  evaluate() processes everything that is an expression - math, variables, boolean expressions.
     *  There is a good bit of recursion in here, since math and comparisons have left and right sides that need to be evaluated.
     *
     * See the How To Write an Interpreter document for examples
     * For each possible ExpressionNode, do the work to resolve it:
     * BooleanLiteralNode - create a new BooleanLiteralNode with the same value
     *      - Same for all of the basic data types
     * BooleanOpNode - Evaluate() left and right, then perform either and/or on the results.
     * CompareNode - Evaluate() both sides. Do good comparison for each data type
     * MathOpNode - Evaluate() both sides. If they are both numbers, do the math using the built-in operators. Also handle String + String as concatenation (like Java)
     * MethodCallExpression - call doMethodCall() and return the first value
     * VariableReferenceNode - call findVariable()
     * @param locals the local variables
     * @param object - the current object we are running
     * @param expression - some expression to evaluate
     * @return a value
     */
    private InterpreterDataType evaluate(HashMap<String, InterpreterDataType> locals, Optional<ObjectIDT> object, ExpressionNode expression) {
        if(expression instanceof BooleanLiteralNode) { //basic data type
            return new BooleanIDT(((BooleanLiteralNode) expression).value);
        }
        else if(expression instanceof StringLiteralNode) { //basic data type
            return new StringIDT(((StringLiteralNode) expression).value);
        }
        else if(expression instanceof NumericLiteralNode) { //basic data type
            return new NumberIDT(((NumericLiteralNode) expression).value);
        }
        else if(expression instanceof BooleanOpNode){
            InterpreterDataType left = evaluate(locals, object, ((BooleanOpNode) expression).left);
            InterpreterDataType right = evaluate(locals, object, ((BooleanOpNode) expression).right);
            if (left instanceof BooleanIDT && right instanceof BooleanIDT) { // if the left and right are now boolean literals we can evaluate them
                boolean leftValue = ((BooleanIDT) left).Value;
                boolean rightValue = ((BooleanIDT) right).Value;
                BooleanOpNode.BooleanOperations op = ((BooleanOpNode) expression).op;

                switch (op) {
                    case and:
                        return new BooleanIDT(leftValue && rightValue);
                    case or:
                        return new BooleanIDT(leftValue || rightValue);
                    default:
                        throw new IllegalArgumentException("Unsupported boolean operation");
                }
            }
        }
        else if(expression instanceof CompareNode) {
            InterpreterDataType left = evaluate(locals, object, ((CompareNode) expression).left);
            InterpreterDataType right = evaluate(locals, object, ((CompareNode) expression).right);

            if (left instanceof NumberIDT && right instanceof NumberIDT) { // if the left and right are now boolean literals we can evaluate them
                float leftValue = ((NumberIDT) left).Value;
                float rightValue = ((NumberIDT) right).Value;
                CompareNode.CompareOperations op = ((CompareNode) expression).op;
                switch (op) {
                    case lt:
                        return new BooleanIDT(leftValue < rightValue);
                    case le:
                        return new BooleanIDT(leftValue <= rightValue);
                    case gt:
                        return new BooleanIDT(leftValue > rightValue);
                    case ge:
                        return new BooleanIDT(leftValue >= rightValue);
                    case eq:
                        return new BooleanIDT(leftValue == rightValue);
                    case ne:
                        return new BooleanIDT(leftValue != rightValue);
                    default:
                        throw new IllegalArgumentException("Unsupported comparison operation");
                }
            }
        }
        else if(expression instanceof MathOpNode) {
            InterpreterDataType left = evaluate(locals, object, ((MathOpNode) expression).left);
            InterpreterDataType right = evaluate(locals, object, ((MathOpNode) expression).right);

            MathOpNode.MathOperations op = ((MathOpNode) expression).op;

            // Check if both operands are numbers
            if (left instanceof NumberIDT && right instanceof NumberIDT) {
                float leftValue = ((NumberIDT) left).Value;
                float rightValue = ((NumberIDT) right).Value;

                switch (op) {
                    case add:
                        return new NumberIDT(leftValue + rightValue);
                    case subtract:
                        return new NumberIDT(leftValue - rightValue);
                    case multiply:
                        return new NumberIDT(leftValue * rightValue);
                    case divide:
                        if (rightValue == 0) {
                            throw new ArithmeticException("Division by zero");
                        }
                        return new NumberIDT(leftValue / rightValue);
                    default:
                        throw new IllegalArgumentException("Unsupported math operation for numbers");
                }
            }
            // Check if both operands are strings (only supports concatenation)
            else if (left instanceof StringIDT && right instanceof StringIDT) {
                if (op == MathOpNode.MathOperations.add) {
                    String leftValue = ((StringIDT) left).Value;
                    String rightValue = ((StringIDT) right).Value;
                    return new StringIDT(leftValue + rightValue);
                } else {
                    throw new IllegalArgumentException("Only addition (+) is supported for strings");
                }
            }
            else {//mixed / unsupported add types
                throw new IllegalArgumentException("Incompatible types for math operation: " +
                        left.getClass().getSimpleName() + " and " + right.getClass().getSimpleName());
            }
        }
        else if(expression instanceof MethodCallExpressionNode){
            List<InterpreterDataType> methodexp = findMethodForMethodCallAndRunIt(object, locals, ((MethodCallStatementNode) expression)); //Confusing rule here in the cast
            return methodexp.get(0);
        }
        else if(expression instanceof VariableReferenceNode) {
            InterpreterDataType variable = findVariable(((VariableReferenceNode) expression).name, locals, object);

            if (variable instanceof NumberIDT) {
                return new NumberIDT(((NumberIDT) variable).Value);
            }
            if(variable instanceof BooleanIDT) {
                return new BooleanIDT(((BooleanIDT) variable).Value);
            }
            if(variable instanceof StringIDT) {
                return new StringIDT(((StringIDT) variable).Value);
            }
        }
        else
        {
            throw new IllegalArgumentException();
        }

        return null;//idk why it needs this
    }

    //              Utility Methods

    /**
     * Used when trying to find a match to a method call. Given a method declaration, does it match this methoc call?
     * We double check with the parameters, too, although in theory JUST checking the declaration to the call should be enough.
     *
     * Match names, parameter counts (both declared count vs method call and declared count vs value list), return counts.
     * If all of those match, consider the types (use TypeMatchToIDT).
     * If everything is OK, return true, else return false.
     * Note - if m is a built-in and isVariadic is true, skip all of the parameter validation.
     * @param m - the method declaration we are considering
     * @param mc - the method call we are trying to match
     * @param parameters - the parameter values for this method call
     * @return does this method match the method call?
     */
    private boolean doesMatch(MethodDeclarationNode m, MethodCallStatementNode mc, List<InterpreterDataType> parameters) {
        return true;
    }

    /**
     * Very similar to DoesMatch() except simpler - there are no return values, the name will always match.
     * @param c - a particular constructor
     * @param mc - the method call
     * @param parameters - the parameter values
     * @return does this constructor match the method call?
     */
    private boolean doesConstructorMatch(ConstructorNode c, MethodCallStatementNode mc, List<InterpreterDataType> parameters) {
        return true;
    }

    /**
     * Used when we call a method to get the list of values for the parameters.
     *
     * for each parameter in the method call, call Evaluate() on the parameter to get an IDT and add it to a list
     * @param object - the current object
     * @param locals - the local variables
     * @param mc - a method call
     * @return the list of method values
     */
    private List<InterpreterDataType> getParameters(Optional<ObjectIDT> object, HashMap<String,InterpreterDataType> locals, MethodCallStatementNode mc) {
        List<InterpreterDataType> parameterValues = new ArrayList<>();
        //Getting the value of each parameter and adding them to the list to be returned
        for (ExpressionNode paramExpr : mc.parameters) {
            InterpreterDataType evaluatedParam = evaluate(locals, object, paramExpr);
            parameterValues.add(evaluatedParam);
        }
        return parameterValues;
    }

    /**
     * Used when we have an IDT and we want to see if it matches a type definition
     * Commonly, when someone is making a function call - do the parameter values match the method declaration?
     *
     * If the IDT is a simple type (boolean, number, etc) - does the string type match the name of that IDT ("boolean", etc)
     * If the IDT is an object, check to see if the name matches OR the class has an interface that matches
     * If the IDT is a reference, check the inner (refered to) type
     * @param type the name of a data type (parameter to a method)
     * @param idt the IDT someone is trying to pass to this method
     * @return is this OK?
     */
    private boolean typeMatchToIDT(String type, InterpreterDataType idt) {
        switch (type) {
            case "boolean":
                return idt instanceof BooleanIDT;
            case "number":
                return idt instanceof NumberIDT;
            case "string":
                return idt instanceof StringIDT;
            case "character":
                return idt instanceof CharIDT;
            default:
                if (idt instanceof ReferenceIDT) {
                    ReferenceIDT refIDT = (ReferenceIDT) idt;
                    return refIDT.refersTo.equals(type);
                }
                else if(idt instanceof ObjectIDT) {
                    ObjectIDT refIDT = (ObjectIDT) idt;
                    return refIDT.astNode.interfaces.contains(type) || refIDT.astNode.name.equals(type); //maybe???
                }
                else {
                    return false;
                }
        }
    }

    /**
     * Find a method in an object that is the right match for a method call (same name, parameters match, etc. Uses doesMatch() to do most of the work)
     *
     * Given a method call, we want to loop over the methods for that class, looking for a method that matches (use DoesMatch) or throw
     * @param object - an object that we want to find a method on
     * @param mc - the method call
     * @param parameters - the parameter value list
     * @return a method or throws an exception
     */
    private MethodDeclarationNode getMethodFromObject(ObjectIDT object, MethodCallStatementNode mc, List<InterpreterDataType> parameters) {
        throw new RuntimeException("Unable to resolve method call " + mc);
    }

    /**
     * Find a class, given the name. Just loops over the TranNode's classes member, matching by name.
     *
     * Loop over each class in the top node, comparing names to find a match.
     * @param name Name of the class to find
     * @return either a class node or empty if that class doesn't exist
     */
    private Optional<ClassNode> getClassByName(String name) {
        //loops over all the classes in the top node
        for (ClassNode classNode : top.Classes) {
            if (classNode.name.equals(name)) {
                return Optional.of(classNode);
            }
        }
        return Optional.empty();
    }

    /**
     * Given an execution environment (the current object, the current local variables), find a variable by name.
     *
     * @param name  - the variable that we are looking for
     * @param locals - the current method's local variables
     * @param object - the current object (so we can find members)
     * @return the IDT that we are looking for or throw an exception
     */
    private InterpreterDataType findVariable(String name, HashMap<String,InterpreterDataType> locals, Optional<ObjectIDT> object) {
        if (locals.containsKey(name)) { //Check locals
            return locals.get(name);
        }
        else if (object.isPresent() && object.get().members.containsKey(name)) { //check members
            return object.get().members.get(name);
        }
        else {
            throw new RuntimeException("Unable to find variable " + name);
        }
    }

    /**
     * Given a string (the type name), make an IDT for it.
     *
     * @param type The name of the type (string, number, boolean, character). Defaults to ReferenceIDT if not one of those.
     * @return an IDT with default values (0 for number, "" for string, false for boolean, ' ' for character)
     */
    private InterpreterDataType instantiate(String type) {
        if(type.equals("number")){ //default number
            return new NumberIDT(0);
        }
        else if(type.equals("string")){
            return new StringIDT("");
        }
        else if(type.equals("boolean")){
            return new BooleanIDT(false);
        }
        else if(type.equals("character")){
            return new CharIDT(' ');
        }
        else{
            return new ReferenceIDT();
        }
        //return null;
    }
}
