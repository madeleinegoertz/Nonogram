package src;

public class Square {

    private static final String FILLED = "O";
    private static final String BLANK = ".";
    private static final String UNKNOWN = " ";

    private enum State {
        FILLED, // know to be filled in
        BLANK, // know to be blank
        UNKNOWN // do not know
    }

    private State state;

    public Square() {
        setUnknown();
    }

    public Square(State state) {
        this.state = state;
    }

    public boolean isFilled() {
        return getState() == State.FILLED;
    }

    public boolean isBlank() {
        return getState() == State.BLANK;
    }

    public boolean isUnknown() {
        return getState() == State.UNKNOWN;
    }

    public State getState() {
        return state;
    }

    public void setFilled() {
        setState(State.FILLED);
    }

    public void setUnknown() {
        setState(State.UNKNOWN);
    }

    public void setBlank() {
        setState(State.BLANK);
    }

    public void setState(State state) {
        this.state = state;
    }

    public Square clone() {
        return new Square(this.getState());
    }

    public String toString() {
        String str = "";
        if (isFilled()) str = FILLED;
        else if (isBlank()) str = BLANK;
        else str = UNKNOWN;
        return str;
    }
}