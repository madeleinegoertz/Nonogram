import java.io.*;
import java.util.*;

public class NonogramPuzzle {

    private static final int SQUARES_PER_SECTION = 5;

    private static final char VERT_DIV = '|';
    private static final char HOR_DIV = '-';

    private ArrayList<Integer>[] rowClues;
    private ArrayList<Integer>[] colClues;
    private Square[][] grid;
    private PrintStream toFile;

    // pass in a file of ones and zeros, space separated
    public NonogramPuzzle(String name, File answersFile) throws IOException {
        Scanner fileScanner = new Scanner(answersFile);
        ArrayList<ArrayList<Integer>> input = new ArrayList<ArrayList<Integer>>();
        while (fileScanner.hasNextLine()) {
            Scanner lineScanner = new Scanner (fileScanner.nextLine());
            ArrayList<Integer> row = new ArrayList<Integer>();
            while (lineScanner.hasNextInt()) {
                row.add(lineScanner.nextInt());
            }
            input.add(row);
        }
        fileScanner.close();
        int[][] answers = new int[input.size()][input.get(0).size()];
        for (int r = 0; r < answers.length; r++) {
            for (int c = 0; c < answers[r].length; c++) {
                answers[r][c] = input.get(r).get(c);
            }
        }
        init(name, generateClues(true, answers), generateClues(false, answers)); 
    }

    // pass in two files, one with the row clues, one with the col clues, each space separated
    public NonogramPuzzle(String name, File rowClues, File colClues) throws FileNotFoundException {
        init(name, getCluesFromFile(rowClues), getCluesFromFile(colClues));
    }

    private void init(String name, ArrayList<Integer>[] rowClues, ArrayList<Integer>[] colClues)
            throws FileNotFoundException {
        this.rowClues = rowClues;
        this.colClues = colClues;
        grid = new Square[rowClues.length][colClues.length];
        for (int r = 0; r < grid.length; r++) {
            for (int c = 0; c < grid[r].length; c++) {
                grid[r][c] = new Square();
            }
        }
        toFile = new PrintStream(new File("out\\" + name + ".txt"));
    }

    private ArrayList<Integer>[] getCluesFromFile(File f) throws FileNotFoundException {
        ArrayList<ArrayList<Integer>> input = new ArrayList<>();
        Scanner fileScanner = new Scanner(f);
        while (fileScanner.hasNextLine()) {
            Scanner lineScanner = new Scanner(fileScanner.nextLine());
            ArrayList<Integer> newClue = new ArrayList<Integer>();
            while (lineScanner.hasNextInt()) {
                newClue.add(lineScanner.nextInt());
            }
            input.add(newClue);
        }
        fileScanner.close();
        ArrayList<Integer>[] clues = input.toArray(new ArrayList[input.size()]);
        return clues;
    }

    public boolean isLineCorrect(int lineNum, boolean isRow) {
        ArrayList<Integer>[] clues = colClues;
        Square[] line = getCol(lineNum);
        if (isRow) {
            clues = rowClues;
            line = getRow(lineNum);
        }
        return areAllSquaresKnown(lineNum, isRow) && clues[lineNum].equals(generateCluesForLine(line));
    }

    private boolean areAllSquaresKnown(int lineNum, boolean isRow) {
        Square[] line = isRow ? getRow(lineNum) : getCol(lineNum);
        boolean allFilled = true;
        int i = 0;
        while (allFilled && i < line.length) {
            allFilled = !line[i].isUnknown();
            i++;
        }
        return allFilled;
    }

    public boolean isCorrect() {
        boolean isCorrect = true;
        int r = 0;
        while (isCorrect && r < rows()) {
            isCorrect = isLineCorrect(r, true);
            r++;
        }
        int c = 0;
        while (isCorrect && c < cols()) {
            isCorrect = isLineCorrect(c, false);
            c++;
        }
        return isCorrect;
    }

    public boolean manualFill(int r, int c) {
        boolean isValid = isValidSquare(r, c);
        if (isValid) {
            if (grid[r][c].isFilled()) grid[r][c].setUnknown();
            else grid[r][c].setFilled();
        }
        return isValid;
    }

    public boolean manualBlank(int r, int c) {
        boolean isValid = isValidSquare(r, c);
        if (isValid) {
            if (grid[r][c].isBlank()) grid[r][c].setUnknown();
            else grid[r][c].setBlank();
        }
        return isValid;
    }

    private boolean isValidSquare(int r, int c) {
        return isValidRow(r) && isValidCol(c);
    }

    private boolean isValidRow(int r) {
        return r >= 0 && r < rows();
    }

    private boolean isValidCol(int c) {
        return c >= 0 && c < cols();
    }

    public boolean isUnknown(int r, int c) {
        return isValidSquare(r, c) && grid[r][c].isUnknown();
    }

    public boolean isFilled(int r, int c) {
        return isValidSquare(r, c) && grid[r][c].isFilled();
    }

    public boolean isBlank(int r, int c) {
        return isValidSquare(r, c) && grid[r][c].isBlank();
    }
 
    public void setFilled(int r, int c) {
        if (isValidSquare(r, c)) grid[r][c].setFilled();
    }

    public void setBlank(int r, int c) {
        if (isValidSquare(r, c)) grid[r][c].setBlank();
    }

    public String toString() {
        String str = "";
        int maxNumRowChunks = maxNumChunks(rowClues);
        int maxDigitsInRowClues = maxDigits(rowClues) + 1;
        int rowClueSpace =  maxNumRowChunks * maxDigitsInRowClues;
        // add all the col clue labels
        int maxNumColChunks = maxNumChunks(colClues);
        int chunkClueSpace = maxDigits(colClues) + 1;
        int gridColsSpaced = (rows() + 1 + rows() / SQUARES_PER_SECTION) * chunkClueSpace;
        for (int r = 0; r < maxNumColChunks; r++) {
            str += rowClueSpacing(' ', rowClueSpace); // blank space for row clues
            for (int c = 0; c < cols(); c++) { // loop through each col
                int chunkVal = chunkVal(c, r, maxNumColChunks, colClues);
                if (c % SQUARES_PER_SECTION == 0) str += String.format("%" + chunkClueSpace + "c", VERT_DIV);
                str += String.format("%" + chunkClueSpace + "s", chunkVal == 0 ? " " : chunkVal);
            }
            str += String.format("%" + chunkClueSpace + "c\n", VERT_DIV);
        }
        
        for (int r = 0; r < rows(); r++) { // loop through each row clue
            // add dividing line if at end of section.
            if (r % SQUARES_PER_SECTION == 0) str += rowClueSpacing(HOR_DIV, rowClueSpace + gridColsSpaced + 1) + "\n";
            // add all the row clue labels
            for (int c = 0; c < maxNumRowChunks; c++) { // loop through each row clue chunk
                int chunkVal = chunkVal(r, c, maxNumRowChunks, rowClues);
                str += String.format("%" + maxDigitsInRowClues + "s", chunkVal == 0 ? " " : chunkVal);
            }
            // add all the values from the grid
            for (int c = 0; c < cols(); c++) {
                if (c % SQUARES_PER_SECTION == 0) str += String.format("%" + chunkClueSpace + "c", VERT_DIV);
                str += String.format("%" + chunkClueSpace + "s", grid[r][c]);
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

    public ArrayList<Integer> generateCluesForLine(Square[] line) {
        ArrayList<Integer> clues = new ArrayList<>();
        boolean isChunk = false;
        int chunkLen = 0;
        Square square = null;
        for (int i = 0; i < line.length; i++) {
            square = line[i];
            if (isChunk) { // prev block is filled
                if (!square.isFilled()) { // O _
                    clues.add(chunkLen);
                    isChunk = false;
                    chunkLen = 0;
                } else { // O O
                    chunkLen++;
                }
            } else if (square.isFilled()) { // . O
                isChunk = true;
                chunkLen++;
            }
        }
        // fence-posting for last square
        if (isChunk && square.isFilled()) {
            clues.add(chunkLen);
        }
        return clues;
    }

    private ArrayList<Integer>[] generateClues(boolean isRow, int[][] grid) {
        int primaryLoopEnd = isRow ? rows() : cols();
        int secondaryLoopEnd = isRow ? cols() : rows();
        // create array of clues
        ArrayList<Integer>[] clues = new ArrayList[primaryLoopEnd];
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

    public Square[] getLine(int lineNum, boolean isRow) {
        Square[] line = new Square[isRow ? rows() : cols()];
        for (int i = 0; i < line.length; i++) {
            line[i] = grid[isRow ? lineNum : i][isRow ? i : lineNum].clone();
        }
        return line;
    }

    public Square[] getRow(int rowNum) {
        return getLine(rowNum, true);
    }

    public Square[] getCol(int colNum) {
        return getLine(colNum, false);
    }

    public int rows() {
        return grid.length;
    }

    public int cols() {
        return grid[0].length;
    }

    public ArrayList<Integer> getRowClues(int row) {
        return getClues(row, true);
    }

    public ArrayList<Integer> getColClues(int col) {
        return getClues(col, false);
    }

    public ArrayList<Integer> getClues(int lineNum, boolean isRow) {
        ArrayList<Integer> clues = new ArrayList<Integer>();
        if (isRow ? isValidRow(lineNum) : isValidCol(lineNum)) clues = isRow ? rowClues[lineNum] : colClues[lineNum];
        return clues;
    }

    public void consolePrintln(String text) {
        System.out.println(text);
    }

    public void filePrintln(String text) {
        toFile.println(text);
    }

    public void println(String text) {
        consolePrintln(text);
        filePrintln(text);
    }
}