import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class Lexer {

    private TextManager input;
    private int col_number; //Var to keep track of the column
    private int row_number; //Var to keep track of the row

    public Lexer(String input) {
        this.col_number = 1;
        this.row_number = 1;
        this.input = new TextManager(input);
    }

    public ArrayList<Character> fill_punct() {
        ArrayList<Character> punct = new ArrayList<Character>();
        punct.add('='); //ASSIGN
        punct.add('('); //LPAREN
        punct.add(')'); //RPAREN
        punct.add(':'); //COLON
        punct.add('+'); //PLUS
        punct.add('-'); //MINUS
        punct.add('*'); //TIMES
        punct.add('/'); //DIVIDE
        punct.add('%'); //MODULO
        punct.add(','); //COMMA
        punct.add('<'); //LESSTHAN
        punct.add('>'); //GREATERTHAN
        punct.add('!'); //used for notequal
        return punct;
    }

    public HashMap<String, Token.TokenTypes> fill_keywords() {
        HashMap<String, Token.TokenTypes> keywords = new HashMap<>();
        keywords.put("if", Token.TokenTypes.IF); //IF
        keywords.put("else", Token.TokenTypes.ELSE); //ELSE
        keywords.put("accessor", Token.TokenTypes.ACCESSOR); //ACCESSOR
        keywords.put("mutator", Token.TokenTypes.MUTATOR); //MUTATOR
        keywords.put("loop", Token.TokenTypes.LOOP); //LOOP
        keywords.put("class", Token.TokenTypes.CLASS); //CLASS
        keywords.put("interface", Token.TokenTypes.INTERFACE); //INTERFACE
        keywords.put("implements", Token.TokenTypes.IMPLEMENTS); //IMPLEMENTS
        keywords.put("false", Token.TokenTypes.FALSE); //FALSE
        keywords.put("true", Token.TokenTypes.TRUE); //TRUE
        keywords.put("new", Token.TokenTypes.NEW); //NEW
        keywords.put("private", Token.TokenTypes.PRIVATE); //PRIVATE
        keywords.put("shared", Token.TokenTypes.SHARED); //SHARED
        keywords.put("construct", Token.TokenTypes.CONSTRUCT); //CONSTRUCT
        keywords.put("not", Token.TokenTypes.NOT); //NOT
        keywords.put("and", Token.TokenTypes.AND); //AND
        keywords.put("or", Token.TokenTypes.OR); //OR
        return keywords;
    }



    public List<Token> Lex() throws Exception {
        var retVal = new LinkedList<Token>();
        ArrayList<Character> punct = fill_punct();
        int indentationlevel = 0;
        int prev_indent_level = 0;

        //While loop to loop until the end of the input
        while (!input.isAtEnd()) {
            //Getting a new character
            char current_character = input.peekCharacter();

            //Checking if the current character is a space
            if (current_character == ' ') {
                //If it is "consume" the space in order to iterate the position to the next character
                input.getCharacter();
                col_number++;
            }
            if(current_character == '\t')
            {
                col_number += 4;
                input.getCharacter();
            }

            //Checking if the current character is a newLine
            if (current_character == '\n') {
                indentationlevel = 0;
                //Creating a newLine token
                Token newLine = new Token(Token.TokenTypes.NEWLINE, row_number, col_number);
                //Adding newLine token to the linked list
                retVal.add(newLine);
                //Using getCharacter to "consume" the character and iterate the position
                input.getCharacter();
                row_number++;
                col_number = 1;
                while (!input.isAtEnd() && (input.peekCharacter() == ' ' || input.peekCharacter() == '\t')) {
                    if (input.peekCharacter() == '\t')
                    {
                        input.getCharacter();
                        col_number += 4;
                    }
                    if(input.peekCharacter() == ' ')
                    {
                        input.getCharacter();
                        col_number++;
                    }

                    indentationlevel =(int) ((col_number -1 ) / 4);

                }
                if(((col_number - 1) % 4 != 0) && col_number != 1)
                {
                    throw new SyntaxErrorException("indents must be 4 spaces", row_number, col_number);
                }


                if(prev_indent_level < indentationlevel)
                {
                    for(int i = 0; i < indentationlevel - prev_indent_level; i++)
                    {
                        Token indent = new Token(Token.TokenTypes.INDENT, row_number, col_number);
                        retVal.add(indent);
                    }
                    prev_indent_level = indentationlevel;
                }
                else if(prev_indent_level > indentationlevel)
                {
                    for(int i = 0; i < prev_indent_level - indentationlevel; i++)
                    {
                        Token dedent = new Token(Token.TokenTypes.DEDENT, row_number, col_number);
                        retVal.add(dedent);
                    }
                    prev_indent_level = indentationlevel;
                }

            }

            //Checking if the current character is a letter
            if (Character.isLetter(current_character)) {
                //If the current character is a letter then call parseword
                Token word = parseWord();
                //Adding the word token to the linked list
                retVal.add(word);

            }

            //Checking if the current character is a number(digit)
            if (Character.isDigit(current_character)) {
                Token number = parseNumber();
                retVal.add(number);
            }

            /*
            this is a special circumstance where the dot can either be part of a decimal (.23) or a punctuation
            (.)
             */
            if (current_character == '.') {
                //If the character following the dot is a digit we know its part of a number
                if (Character.isDigit(input.peekCharacter(1))) {
                    Token number = parseNumber();
                    retVal.add(number);
                }
                //otherwise we know its punctuation
                else {
                    Token dot = parsePunctuation();
                    retVal.add(dot);
                }

            }

            //If the current character is in the arraylist filled in the fill_punct method we use parse punctuation
            if (punct.contains(current_character)) {
                Token punctuation = parsePunctuation();
                retVal.add(punctuation);
            }

            //Quoted Strings
            if(current_character == '\"')
            {
                Token QS = parseQS();
                retVal.add(QS);
            }

            //Quoted character
            if(current_character == '\'')
            {
                int start_col = col_number;
                input.getCharacter(); //Consuming the first \'
                col_number++;
                char c  = input.getCharacter(); //Getting the character
                col_number++;
                if (input.getCharacter() != '\'') //Saying if there isnt a \' following the character is it not a quoted character
                    throw new SyntaxErrorException("error", row_number, start_col);
                String buffer ="";
                buffer += c;
                Token QC = new Token(Token.TokenTypes.QUOTEDCHARACTER, row_number, start_col, buffer);
                retVal.add(QC);
            }

            //Comments
            if(current_character == '{')
            {
                int check = 1;
                int start_col = col_number;
                while(check != 0)
                {
                    if(input.peekCharacter() == '}')
                    {
                        check--;
                    }
                    if(input.peekCharacter(1) == '{')
                    {
                        check++;
                    }


                    if(input.isAtEnd())
                    {
                        throw new SyntaxErrorException("error, the comment is not closed", row_number, start_col);
                    }
                    if (input.peekCharacter() == '\n')
                    {
                        row_number++;
                        col_number = 1;
                    }
                    if(check != 0)
                    {
                        input.getCharacter();
                        col_number++;
                    }

                }
                input.getCharacter();
                col_number++;
            }

        }
        indentationlevel = 0;
        if(prev_indent_level > indentationlevel)
        {
            for(int i = 0; i < prev_indent_level - indentationlevel; i++)
            {
                Token dedent = new Token(Token.TokenTypes.DEDENT, row_number, col_number);
                retVal.add(dedent);
            }
        }
        return retVal;
    }

    private Token parseWord() throws SyntaxErrorException {
        //Declaring a buffer that will hold the word as its being parsed
        String buffer = "";
        HashMap<String, Token.TokenTypes> keywords = fill_keywords();
        int start_col = col_number;

        //Looping until the end of the input and if the next character is still a letter
        while (!input.isAtEnd() && Character.isLetter(input.peekCharacter())) {
            //Getting the current character(also incrementing position)
            char current_character = input.getCharacter();
            col_number++;
            //we Add the letter to the buffer and loop again
            buffer += current_character;
        }

        if (!buffer.isEmpty()) {
            if (keywords.containsKey(buffer)) {
                Token keyword = new Token(keywords.get(buffer), row_number, start_col);
                return keyword;
            } else {
                Token word = new Token(Token.TokenTypes.WORD, row_number, start_col, buffer);
                return word;
            }
        }
        throw new SyntaxErrorException("Something went wrong parsing the word", row_number, start_col);
    }

    private Token parseNumber() throws SyntaxErrorException {
        String buffer = "";
        int start_col = col_number;
        Boolean has_decimal = false;
        //Checking if we are not at the end of the input and that the next character is a digit or a DOT
        while (!input.isAtEnd() && Character.isDigit(input.peekCharacter()) || input.peekCharacter() == '.' && !has_decimal) {
            //Getting the current character
            char current_character = input.getCharacter();
            col_number++;

            //If the character isn't a digit or a dot
            if (!Character.isDigit(current_character) && current_character != '.') {
                //if the buffer isn't empty
                if (!buffer.isEmpty()) {
                    //Make a new number token and return
                    Token number = new Token(Token.TokenTypes.NUMBER, row_number, start_col, buffer);
                    return number;
                }
            } else {
                if (current_character == '.') {
                    if (has_decimal) {
                        Token number = new Token(Token.TokenTypes.NUMBER, row_number, start_col, buffer);
                        return number;
                    } else {
                        has_decimal = true;
                    }
                }
                //Add the current character to the buffer
                buffer += current_character;
            }
        }
        /*
        in the circumstance that we reach the end of the input, and we still have characters in the buffer
        we make a new number token and return it
         */
        if (!buffer.isEmpty()) {
            Token number = new Token(Token.TokenTypes.NUMBER, row_number, start_col, buffer);
            return number;
        }

        throw new SyntaxErrorException("Something went wrong parsing the Number", row_number, col_number);
    }

    private Token parsePunctuation() throws Exception {
        int start_col = col_number;
        char next_character = input.peekCharacter(1);
        char current_character = input.getCharacter();
        col_number++;

        switch (current_character) {
            case '=':
                if (next_character == '=') {
                    Token equals = new Token(Token.TokenTypes.EQUAL, row_number, start_col);
                    input.getCharacter();
                    col_number++;
                    return equals;
                } else {
                    Token assign = new Token(Token.TokenTypes.ASSIGN, row_number, start_col);
                    return assign;
                }
            case '(':
                Token Lparen = new Token(Token.TokenTypes.LPAREN, row_number, start_col);
                return Lparen;
            case ')':
                Token Rparen = new Token(Token.TokenTypes.RPAREN, row_number, start_col);
                return Rparen;
            case ':':
                Token colon = new Token(Token.TokenTypes.COLON, row_number, start_col);
                return colon;
            case '+':
                Token plus = new Token(Token.TokenTypes.PLUS, row_number, start_col);
                return plus;
            case '-':
                Token minus = new Token(Token.TokenTypes.MINUS, row_number, start_col);
                return minus;
            case '*':
                Token times = new Token(Token.TokenTypes.TIMES, row_number, start_col);
                return times;
            case '/':
                Token divide = new Token(Token.TokenTypes.DIVIDE, row_number, start_col);
                return divide;
            case '%':
                Token modulo = new Token(Token.TokenTypes.MODULO, row_number, start_col);
                return modulo;
            case ',':
                Token comma = new Token(Token.TokenTypes.COMMA, row_number, start_col);
                return comma;
            case '<':
                if (next_character == '=') {
                    Token lessThanEqual = new Token(Token.TokenTypes.LESSTHANEQUAL, row_number, start_col);
                    input.getCharacter();
                    col_number++;
                    return lessThanEqual;
                } else {
                    Token lessThan = new Token(Token.TokenTypes.LESSTHAN, row_number, start_col);
                    return lessThan;
                }
            case '>':
                if (next_character == '=') {
                    Token greaterThanEqual = new Token(Token.TokenTypes.GREATERTHANEQUAL, row_number, start_col);
                    input.getCharacter();
                    col_number++;
                    return greaterThanEqual;
                } else {
                    Token greaterThan = new Token(Token.TokenTypes.GREATERTHAN, row_number, start_col);
                    return greaterThan;
                }
            case '!':
                if (next_character == '=') {
                    Token notEqual = new Token(Token.TokenTypes.NOTEQUAL, row_number, start_col);
                    input.getCharacter();
                    col_number++;
                    return notEqual;
                }
                else
                {
                    throw new SyntaxErrorException("! not recognized", row_number, start_col);
                }
            case '.':
                Token dot = new Token(Token.TokenTypes.DOT, row_number, start_col);
                return dot;

            default:
                throw new SyntaxErrorException("something went wrong parsing punctuation",row_number,col_number);
        }
    }

    private Token parseQS() throws Exception {

        int start_col = col_number;
        //Declared a empty string
        String buffer = "";

        //declaring a current variable to keep track of the current character, this also serves
        //the purpose of consuming the \" character so we dont add it to the string
        char current = input.getCharacter();
        //Iterating column number
        col_number++;

        //Checking if the next character is not a escape quote
        while(input.peekCharacter() != '\"' && !input.isAtEnd())
        {
            //If it is not we will update current to make it that non quoted character as well as increment position
            current = input.getCharacter();

            if(current == '\n')
            {
                row_number++;
                col_number = 1;
            }
            //increment column number
            col_number++;
            //adding the non quote to the buffer
            buffer += current;
        }
        //Consuming the final \" character
        current = input.getCharacter();
        if (current != '\"'){
            throw new SyntaxErrorException("Error, did not close quoted string", row_number, start_col);
        }
        //iterating column
        col_number++;
        //Checking if the buffer is empty

            //If it isnt then we will add the quoted string token
        Token QS = new Token(Token.TokenTypes.QUOTEDSTRING, row_number, start_col, buffer);
        return QS;

    }

}


