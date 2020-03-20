import java.util.ArrayList;

public class NonogramSolver {

    private NonogramPuzzle puzzle;

    public NonogramSolver(NonogramPuzzle puzzle) {
        this.puzzle = puzzle;
    }

    public void solve() {
        int round = 1;
        while (!puzzle.isCorrect()) {
            int r = 0;
            while (!puzzle.isCorrect() && r < puzzle.rows()) {
                puzzle.println("r = " + r);
                updateRow(r);
                puzzle.filePrintln(this.toString());
                round++;
                r++;
            }
            int c = 0;
            while (!puzzle.isCorrect() && c < puzzle.cols()) {
                puzzle.println("c = " + c);
                puzzle.filePrintln(this.toString());
                updateCol(c);
                round++;
                c++;
            }
        }
        puzzle.println("round = " + round);
        puzzle.println(this.toString());
    }

    private void updateLine(int lineNum, boolean isRow) {
        if (!puzzle.isLineCorrect(lineNum, isRow)) {
            ArrayList<Integer> unknownSquares = new ArrayList<>(); // list of indices of unknown squares in line.
            // populate unknownSquares
            for (int i = 0; i < (isRow ? puzzle.cols() : puzzle.rows()); i++) {
                if (puzzle.isUnknown(isRow ? lineNum : i, isRow ? i : lineNum)) unknownSquares.add(i);
            }
            int sumChunks = sumChunks(lineNum, isRow);
            int numFilledSquares = numFilledSquares(lineNum, isRow);
            int k = sumChunks - numFilledSquares; // how many squares still need to be filled

            // list of sets of indices that could be filled (as arrays)
            ArrayList<int[]> filledSquareGuess = combin(k, unknownSquares);
            // list of all possible lines as Square arrays. All possible, not all valid lines.
            ArrayList<Square[]> lineGuesses = new ArrayList<>();
            // populate all possible lines as Square arrays
            for (int i = 0; i < filledSquareGuess.size(); i++) { // loop through every line combination
                lineGuesses.add(puzzle.getLine(lineNum, isRow)); // add in already known squares
                // add in new info from guesses
                for (int j = 0; j < filledSquareGuess.get(i).length; j++) { // loop through each index in the guess
                    lineGuesses.get(i)[filledSquareGuess.get(i)[j]].setFilled();
                }
                // make any remaining unknown squares blank
                for (int j = 0; j < lineGuesses.get(i).length; j++) {
                    if (lineGuesses.get(i)[j].isUnknown()) lineGuesses.get(i)[j].setBlank();
                }
            }

            // remove invalid line guesses
            for (int i = lineGuesses.size() - 1; i >= 0; i--) {
                ArrayList<Integer> realClues = puzzle.getClues(lineNum, isRow);
                ArrayList<Integer> guessClues = puzzle.generateCluesForLine(lineGuesses.get(i));
                boolean isEqual = realClues.equals(guessClues);
                if (!isEqual) lineGuesses.remove(i);
            }

            // if square is the same across all possible valid lines, update the grid.
            if (lineGuesses.size() > 0) {
                for (int i = 0; i < lineGuesses.get(0).length; i++) {
                    boolean isSquareSame = squareSameAcrossGuesses(i, lineGuesses);
                    if (isSquareSame) {
                        boolean isFilled = lineGuesses.get(0)[i].isFilled();
                        int first = isRow ? lineNum : i;
                        int second = isRow ? i : lineNum;
                        if (isFilled) puzzle.setFilled(first, second);
                        else puzzle.setBlank(first, second);
                    }
                }
            }
        }
    }

    private void updateCol(int col) {
        updateLine(col, false);
    }

    private void updateRow(int row) {
       updateLine(row, true);
    }

    private boolean squareSameAcrossGuesses(int c, ArrayList<Square[]> rowGuesses) {
        boolean squaresSame = true;
        int i = 1;
        while (squaresSame && i < rowGuesses.size()) {
            squaresSame = rowGuesses.get(i)[c].getState() == rowGuesses.get(i - 1)[c].getState();
            i++;
        }
        return squaresSame;
    }

    // returns sum of all needed filled squares in line. 
    // Ex: clue is (2, 1), returns 3
    private int sumChunks(int line, boolean isRow) {
        ArrayList<Integer> clues = puzzle.getColClues(line);
        if (isRow) clues = puzzle.getRowClues(line);
        int sum = 0;
        for (int i = 0; i < clues.size(); i++) {
            sum += clues.get(i);
        }
        return sum;
    }

    // returns number of filled squares in line. 
    private int numFilledSquares(int line, boolean isRow) {
        int sum = 0;
        for (int i = 0; i < (isRow ? puzzle.cols() : puzzle.rows()); i++) {
            if (puzzle.isFilled(isRow ? line : i, isRow ? i : line)) sum++;
        }
        return sum;
    }

    // returns a list of sets of k elements from data. 
    // note: data must be sorted lowest to highest
    private ArrayList<int[]> combin(int k, ArrayList<Integer> data) {
        ArrayList<int[]> combinations = new ArrayList<>();
        int[] combination = new int[k];
        // initialize with first lexicographic combination
        for (int i = 0; i < k; i++) {
            combination[i] = data.get(i);
        }

        while (sum(combination) < sumlastCombin(k, data)) {
            combinations.add(combination.clone());

            // generate next combination in lexicographic order
            int t = k - 1;
            while (t != 0 && combination[t] == data.get(data.size() - k + t)) {
                t--;
            }
            combination[t] = data.get(data.indexOf(combination[t]) + 1);
            for (int i = t + 1; i < k; i++) {
                combination[i] = data.get(data.indexOf(combination[i - 1]) + 1);
            }
        }
        combinations.add(combination.clone());
        return combinations;
    }

    private int sum(int[] set) {
        int sum = 0;
        for (int i = 0; i < set.length; i++) {
            sum += set[i];
        }
        return sum;
    }

    // returns the last combination (in lexicogrpahical order) of length k from set data
    private int sumlastCombin(int k, ArrayList<Integer> data) {
        int sum = 0;
        for (int i = 0; i < k; i++) {
            sum += data.get(data.size() - 1 - i);
        }
        return sum;
    }

    public String toString() {
        return puzzle.toString();
    }
}