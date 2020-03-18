import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Nonogram {

    private static final int SQUARES_PER_SECTION = 5;

    private static final char FILLED = 'O'; // square
    private static final char BLANK = '.'; // dot
    private static final char UNKNOWN = ' '; // space
    private static final char VERT_DIV = '|';
    private static final char HOR_DIV = '-';

    private ArrayList<Integer>[] rowClues;
    private ArrayList<Integer>[] colClues;
    private char[][] grid;

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

    private void updateRow(int row) {
        ArrayList<Integer> unknownSquares = new ArrayList<>(); // list of indices of unknown squares in row.
        // populate unknownSquares
        for (int c = 0; c < grid[row].length; c++) {
            if (grid[row][c] == UNKNOWN) unknownSquares.add(c);
        }

        int k = sumChunks(row) - numFilledSquares(row); // how many squares still need to be filled

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
        }
        
        // remove invalid row guesses
        for (int i = rowGuesses.size() - 1; i >= 0; i--) {
            if (!rowClues[row].equals(generateCluesForLine(rowGuesses.get(i)))) rowGuesses.remove(i);
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

    // returns sum of all needed filled squares in rom. 
    // Ex: clue is (2, 1), returns 3
    private int sumChunks(int row) {
        int sum = 0;
        for (int i = 0; i < rowClues[row].size(); i++) {
            sum += rowClues[row].get(i);
        }
        return sum;
    }

    // returns number of filled squares in row. 
    private int numFilledSquares(int row) {
        int sum = 0;
        for (int c = 0; c < grid[row].length; c++) {
            if (grid[row][c] == FILLED) sum++;
        }
        return sum;
    }

    // returns a list of sets of k elements from data. 
    public ArrayList<int[]> combin(int k, ArrayList<Integer> data) {
        ArrayList<int[]> combinations = new ArrayList<>();
        int[] combination = new int[k];
        data.add(data.get(data.size() - 1) + 1);
        // initialize with first lexicographic combination
        for (int i = 0; i < k; i++) {
            combination[i] = data.get(i);
        }
        System.out.println(Arrays.toString(combination));
        int lastVal = data.get(data.size() - 2);
        System.out.println("lastVal  = " + lastVal);
        while (combination[k - 1] < lastVal + 1) {
            combinations.add(combination.clone());

            // generate next combination in lexicographic order
            int t = k - 1;
            System.out.println("t = " + t);
            while (t != 0 && combination[t] == lastVal) {
                t--;
            }
            combination[t] = data.get(data.indexOf(combination[t]) + 1);
            for (int i = t + 1; i < k; i++) {
                combination[i] = data.get(data.indexOf(combination[i - 1]) + 1);
            }
            System.out.println(Arrays.toString(combination));
        }
        return combinations;
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

    public void rowGuesses(int rowNum) {
        int rows = grid.length;
        for (int r = 0; r < rows; r++) {
            int[] leftGuess = new int[rows];
            int searchCol = 0;
            for (int clue = 0; clue < rowClues[rowNum].size(); clue++) {
                int chunkLen = rowClues[rowNum].get(clue);
                if (isSpaceInRowForChunk(rowNum, chunkLen, searchCol)) {
                    searchCol += chunkLen + 1;
                }
            }
        }
    }

    private boolean isSpaceInRowForChunk(int rowNum, int chunkLen, int searchCol) {
        int cols = grid[rowNum].length;
        boolean isSpace = false;
        int blankSpace = 0;
        int c = searchCol;
        // is there enough empty space
        while (!isSpace && c < cols) {
            if (grid[rowNum][c] != BLANK) blankSpace++;
            isSpace = blankSpace >= chunkLen;
            c++;
        }
        // square to right must be empty or the wall
        isSpace = isSpace && (c >= cols - 1 || grid[rowNum][c + 1] != FILLED);
        return isSpace;
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

    public void printRowClues() {
        System.out.println("ROW CLUES:");
        printClues(rowClues);
        System.out.println();
    }

    public void printColClues() {
        System.out.println("COL CLUES:");
        printClues(colClues);
        System.out.println();
    }

    private void printClues(ArrayList<Integer>[] clues) {
        for (int i = 0; i < clues.length; i++) {
            for (int j = 0; j < clues[i].size(); j++) {
                System.out.print(clues[i].get(j) + ", ");
            }
            System.out.println();
        }
    }

    private ArrayList<Integer> generateCluesForLine(char[] line) {
        ArrayList<Integer> clues = new ArrayList<>();
        boolean isChunk = false;
        int chunkLen = 0;
        int square = 0;
        for (int i = 0; i < line.length; i++) {
            square = line[i];
            if (isChunk) { // prev block is filled
                if (square == BLANK) { // O .
                    clues.add(chunkLen);
                    isChunk = false;
                    chunkLen = 0;
                } else { // O O
                    chunkLen++;
                }
            } else if (square == BLANK) { // O .
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
}