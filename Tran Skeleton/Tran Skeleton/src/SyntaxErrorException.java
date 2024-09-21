public class SyntaxErrorException extends Exception{
    private int lineNumber, characterPosition;

    public SyntaxErrorException (String message, int lineNumber, int characterPosition) {
        super(message);
        this.lineNumber = lineNumber;
        this.characterPosition = characterPosition;
    }

    @Override
    public String toString() {
        return "Error at line " + lineNumber + " at character " + characterPosition + " at " + super.toString();
    }
}
