import java.util.ArrayList;
import java.util.List;

public class Nonogram {

    private static final int SQUARES_PER_SECTION = 5;

    private static final char FILLED = 'O';
    private static final char BLANK = '.';
    private static final char UNKNOWN = ' ';
    private static final char VERT_DIV = '|';
    private static final char HOR_DIV = '-';

    private ArrayList<Integer>[] rowClues;
    private ArrayList<Integer>[] colClues;
    public char[][] grid;

    public Nonogram(int[][] answers) {
        rowClues = generateClues(true, answers);
        colClues = generateClues(false, answers);
        grid = new char[answers.length][answers[0].length];
        for (int r = 0; r < grid.length; r++) {
            for (int c = 0; c < grid[r].length; c++) {
                grid[r][c] = UNKNOWN;
            }
        }
    }

    public void updateCol(int col) {
        ArrayList<Integer> unknownSquares = new ArrayList<>(); // list of indices of unknown squares in col.
        // populate unknownSquares
        for (int r = 0; r < grid.length; r++) {
            if (grid[r][col] == UNKNOWN) unknownSquares.add(r);
        }
        int sumChunks = sumChunks(col, false);
        int numFilledSquares = numFilledSquares(col, false);
        int k = sumChunks - numFilledSquares; // how many squares still need to be filled

        // list of sets of indices that could be filled (as arrays)
        ArrayList<int[]> filledSquareGuess = combin(k, unknownSquares);
        // list of all possible cols as char arrays. All possible, not all valid cols. 
        ArrayList<char[]> colGuesses = new ArrayList<>();
        // populate all possible cols as char arrays
        for (int i = 0; i < filledSquareGuess.size(); i++) { // loop through every col combination
            colGuesses.add(getCol(col)); // add in already known squares
            // add in new info from guesses
            for (int j = 0; j < filledSquareGuess.get(i).length; j++) { // loop through each index in the guess
                colGuesses.get(i)[filledSquareGuess.get(i)[j]] = FILLED;
            }
            // make any remaining unknown squares blank
            for (int r = 0; r < colGuesses.get(i).length; r++) {
                if (colGuesses.get(i)[r] == UNKNOWN) colGuesses.get(i)[r] = BLANK;
            }
        }
        
        // remove invalid col guesses
        for (int i = colGuesses.size() - 1; i >= 0; i--) {
            List<Integer> realClues = colClues[col];
            List<Integer> guessClues = generateCluesForLine(colGuesses.get(i)); 
            boolean isEqual = realClues.equals(guessClues);
            if (!isEqual) colGuesses.remove(i);
        }

        // if square is the same across all possible valid cols, update the grid.
        if (colGuesses.size() > 0) {
            for (int r = 0; r < colGuesses.get(0).length; r++) {
                if (squareSameAcrossGuesses(r, colGuesses)) grid[r][col] = colGuesses.get(0)[r];
            }
        }
    }

    public boolean isCorrect() {
        boolean isCorrect = true;
        int r = 0;
        while (isCorrect && r < rows()) {
            isCorrect = rowClues[r].equals(generateCluesForLine(grid[r]));
            r++;
        }
        int c = 0;
        while (isCorrect && c < cols()) {
            isCorrect = colClues[c].equals(generateCluesForLine(getCol(c)));
            c++;
        }
        return isCorrect;
    }

    public void updateRow(int row) {
        ArrayList<Integer> unknownSquares = new ArrayList<>(); // list of indices of unknown squares in row.
        // populate unknownSquares
        for (int c = 0; c < grid[row].length; c++) {
            if (grid[row][c] == UNKNOWN) unknownSquares.add(c);
        }
        int sumChunks = sumChunks(row, true);
        int numFilledSquares = numFilledSquares(row, true);
        int k = sumChunks - numFilledSquares; // how many squares still need to be filled

        // list of sets of indices that could be filled (as arrays)
        ArrayList<int[]> filledSquareGuess = combin(k, unknownSquares);
        // list of all possible rows as char arrays. All possible, not all valid rows. 
        ArrayList<char[]> rowGuesses = new ArrayList<>();
        // populate all possible rows as char arrays
        for (int i = 0; i < filledSquareGuess.size(); i++) { // loop through every row combination
            rowGuesses.add(grid[row].clone()); // add in already known squares
            // add in new info from guesses
            for (int j = 0; j < filledSquareGuess.get(i).length; j++) { // loop through each index in the guess
                rowGuesses.get(i)[filledSquareGuess.get(i)[j]] = FILLED;
            }
            // make any remaining unknown squares blank
            for (int c = 0; c < rowGuesses.get(i).length; c++) {
                if (rowGuesses.get(i)[c] == UNKNOWN) rowGuesses.get(i)[c] = BLANK;
            }
        }
        
        // remove invalid row guesses
        for (int i = rowGuesses.size() - 1; i >= 0; i--) {
            List<Integer> realClues = rowClues[row];
            List<Integer> guessClues = generateCluesForLine(rowGuesses.get(i)); 
            boolean isEqual = realClues.equals(guessClues);
            if (!isEqual) rowGuesses.remove(i);
        }

        // if square is the same across all possible valid rows, update the grid.
        if (rowGuesses.size() > 0) {
            for (int c = 0; c < rowGuesses.get(0).length; c++) {
                if (squareSameAcrossGuesses(c, rowGuesses)) grid[row][c] = rowGuesses.get(0)[c];
            }
        }
    }

    private boolean squareSameAcrossGuesses(int c, List<char[]> rowGuesses) {
        boolean squaresSame = true;
        int i = 1;
        while (squaresSame && i < rowGuesses.size()) {
            squaresSame = rowGuesses.get(i)[c] == rowGuesses.get(i - 1)[c];
            i++;
        }
        return squaresSame;
    }

    // returns sum of all needed filled squares in line. 
    // Ex: clue is (2, 1), returns 3
    private int sumChunks(int line, boolean isRow) {
        ArrayList<Integer>[] clues = colClues;
        if (isRow) clues = rowClues;
        int sum = 0;
        for (int i = 0; i < clues[line].size(); i++) {
            sum += clues[line].get(i);
        }
        return sum;
    }

    // returns number of filled squares in line. 
    private int numFilledSquares(int line, boolean isRow) {
        int sum = 0;
        for (int i = 0; i < grid[line].length; i++) {
            if (grid[isRow ? line : i][isRow ? i : line] == FILLED) sum++;
        }
        return sum;
    }

    // returns a list of sets of k elements from data. 
    // note: data must be sorted lowest to highest
    public ArrayList<int[]> combin(int k, ArrayList<Integer> data) {
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

    public boolean fill(int r, int c) {
        boolean isValid = isValidSquare(r, c);
        if (isValid) {
            if (grid[r][c] == FILLED) grid[r][c] = UNKNOWN;
            else grid[r][c] = FILLED;
        }
        return isValid;
    }

    public boolean blank(int r, int c) {
        boolean isValid = isValidSquare(r, c);
        if (isValid) {
            if (grid[r][c] == BLANK) grid[r][c] = UNKNOWN;
            else grid[r][c] = BLANK;
        }
        return isValid;
    }

    private boolean isValidSquare(int r, int c) {
        return r >= 0 && r < grid.length && c >= 0 && c < grid[r].length;
    }

    public String toString() {
        String str = "";
        int rowClueSpace = maxNumChunks(rowClues) * (maxDigits(rowClues) + 1);
        // add all the col clue labels
        int maxNumColChunks = maxNumChunks(colClues);
        int chunkClueSpace = maxDigits(colClues) + 1;
        int gridColsSpaced = (colClues.length + 1 + colClues.length / SQUARES_PER_SECTION) * chunkClueSpace;
        for (int r = 0; r < maxNumColChunks; r++) {
            str += rowClueSpacing(' ', rowClueSpace); // blank space for row clues
            for (int c = 0; c < colClues.length; c++) { // loop through each col
                int chunkVal = chunkVal(c, r, maxNumColChunks, colClues);
                if (c % SQUARES_PER_SECTION == 0) str += String.format("%" + chunkClueSpace + "c", VERT_DIV);
                str += String.format("%" + chunkClueSpace + "s", chunkVal == 0 ? " " : chunkVal);
            }
            str += String.format("%" + chunkClueSpace + "c\n", VERT_DIV);
        }
        
        int maxNumRowChunks = maxNumChunks(rowClues);
        for (int r = 0; r < rowClues.length; r++) { // loop through each row clue
            // add dividing line if at end of section.
            if (r % SQUARES_PER_SECTION == 0) str += rowClueSpacing(HOR_DIV, rowClueSpace + gridColsSpaced + 1) + "\n";
            // add all the row clue labels
            for (int c = 0; c < maxNumRowChunks; c++) { // loop through each row clue chunk
                int chunkVal = chunkVal(r, c, maxNumRowChunks, rowClues);
                str += String.format("%" + (maxDigits(colClues) + 1) + "s", chunkVal == 0 ? " " : chunkVal);
            }
            // add all the values from the grid
            for (int c = 0; c < grid[r].length; c++) {
                if (c % SQUARES_PER_SECTION == 0) str += String.format("%" + chunkClueSpace + "c", VERT_DIV);
                str += String.format("%" + chunkClueSpace + "c", grid[r][c]);
            }
            // add end line
            str += String.format("%" + chunkClueSpace + "c\n", VERT_DIV);
        }
        // add bottom row
        str += rowClueSpacing(HOR_DIV, rowClueSpace + gridColsSpaced + 1) + "\n";
        return str;
    }

    private int chunkVal(int numClue, int numChunk, int maxNumChunks, List<Integer>[] clues) {
        int val = 0;
        int offset = maxNumChunks - clues[numClue].size();
        if (numChunk >= offset ) val = clues[numClue].get(numChunk - offset);
        return val;
    }

    private String rowClueSpacing(char symbol, int rowClueSpace) {
        String str = "";
        for (int i = 0; i < rowClueSpace; i++) {
            str += symbol;
        }
        return str;
    }

    /**
     * Used for toString()
     * @param clues, either rows or cols
     * @return greatest number of digits in any chunk clue
     */
    private int maxDigits(ArrayList<Integer>[] clues) {
        int maxDigits = 0;
        for (int i = 0; i < clues.length; i++) {
            for (int j = 0; j < clues[i].size(); j++) {
                int numDigits = (int)(Math.log10(clues[i].get(j)) + 1);
                if (numDigits > maxDigits) maxDigits = numDigits;
            }
        }
        return maxDigits;
    }

    /**
     * Used for toString()
     * @param clues, either for rows or cols
     * @return the greatest number of chunks in either rows or cols
     */
    private int maxNumChunks(ArrayList<Integer>[] clues) {
        int maxChunks = 0;
        for (int i = 0; i < clues.length; i++) {
            int numChunks = clues[i].size();
            if (numChunks > maxChunks) maxChunks = numChunks;
        }
        return maxChunks;
    }

    public ArrayList<Integer> generateCluesForLine(char[] line) {
        ArrayList<Integer> clues = new ArrayList<>();
        boolean isChunk = false;
        int chunkLen = 0;
        int square = 0;
        for (int i = 0; i < line.length; i++) {
            square = line[i];
            if (isChunk) { // prev block is filled
                if (square == BLANK) { // O _
                    clues.add(chunkLen);
                    isChunk = false;
                    chunkLen = 0;
                } else { // O O
                    chunkLen++;
                }
            } else if (square == FILLED) { // . O
                isChunk = true;
                chunkLen++;
            }
        }
        // fence-posting for last square
        if (isChunk && square == FILLED) {
            clues.add(chunkLen);
        }
        return clues;
    }

    private ArrayList<Integer>[] generateClues(boolean isRow, int[][] grid) {
        int rows = grid.length;
        int cols = grid[0].length;
        int primaryLoopEnd = isRow ? rows : cols;
        int secondaryLoopEnd = isRow ? cols : rows;
        // create array of clues
        ArrayList<Integer>[] clues = new ArrayList[grid.length];
        for (int i = 0; i < clues.length; i++) {
            clues[i] = new ArrayList<Integer>();
        }
        // loop through sections (row or col)
        for (int i = 0; i < primaryLoopEnd; i++) {
            boolean isChunk = false;
            int chunkLen = 0;
            int square = 0;
            for (int j = 0; j < secondaryLoopEnd; j++) {
                square = grid[isRow ? i : j][isRow ? j : i];
                if (isChunk) { // prev block is filled
                    if (square == 0) { // 1 0
                        clues[i].add(chunkLen);
                        isChunk = false;
                        chunkLen = 0;
                    } else { // 1 1
                        chunkLen++;
                    }
                } else if (square == 1) { // 0 1 
                    isChunk = true;
                    chunkLen++;
                }
            }
            // fence-posting for last square
            if (isChunk && square == 1) {
                clues[i].add(chunkLen);
            }
        }
        return clues;
    }

    private char[] getCol(int colNum) {
        char[] col = new char[cols()];
        for (int r = 0; r < rows(); r++) {
            col[r] = grid[r][colNum];
        }
        return col;
    }

    public int rows() {
        return grid.length;
    }

    public int cols() {
        return grid[0].length;
    }
}