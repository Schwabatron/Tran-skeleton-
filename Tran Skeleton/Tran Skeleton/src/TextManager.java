import java.util.PrimitiveIterator;

public class TextManager {


    private String input;
    private int position;

    public TextManager(String input) {
        this.input = input;
        this.position = 0;
    }

    public boolean isAtEnd() {
        //Check if the position variable is greater than or equal to the
        //length of the input
        if(position >= input.length()) {
            //If true then return true because we are not at the end of the input
            return true;
        }
        else
        {
            //if false then return false because we are not at the end of input
            return false;
        }
    }

    public char peekCharacter() {
        if(isAtEnd()) {
            return '~';
        }
        else
        {
            return input.charAt(position);
        }

    }

    public char peekCharacter(int distance) {
        //Check if you are peeking past the length of the input
       if(position + distance >= input.length())
       {
           //If true then return a ~ (a character not recognized in tran~
           return '~';
       }
       else
       {
           //If false return the character at the peeked position
           return input.charAt(position + distance);
       }
    }

    public char getCharacter() {
        if(isAtEnd()) {
            return '~';
        }
        //save the character at the current position
        char c = input.charAt(position);
        //Increment the position
        position++;
        //return the character at the initial position
        return c;
    }
}
