import java.util.ArrayList;

import org.omg.CORBA.UNKNOWN;

public class Nonogram {

    private static final int SQUARES_PER_SECTION = 5;

    private static final char FILLED = 254; // square
    private static final char BLANK = 250; // dot
    private static final char UNKNOWN = 32; // space
    private static final char VERT_DIV = '|';
    private static final char HOR_DIV = '-';

    private ArrayList<Integer>[] rowClues;
    private ArrayList<Integer>[] colClues;
    private char[][] board;

    public Nonogram(int[][] grid) {
        rowClues = generateClues(true, grid);
        colClues = generateClues(false, grid);
        board = new char[grid.length][grid[0].length];
        for (int r = 0; r < board.length; r++) {
            for (int c = 0; c < board[r].length; c++) {
                board[r][c] = UNKNOWN;
            }
        }
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
            for (int c = 0; c < board[r].length; c++) {
                if (c % SQUARES_PER_SECTION == 0) str += String.format("%" + chunkClueSpace + "c", VERT_DIV);
                str += String.format("%" + chunkClueSpace + "c", board[r][c]);
            }
            // add end line
            str += String.format("%" + chunkClueSpace + "c\n", VERT_DIV);
        }
        // add bottom row
        str += rowClueSpacing(HOR_DIV, rowClueSpace + gridColsSpaced + 1) + "\n";
        return str;
    }

    private int chunkVal(int numClue, int numChunk, int maxNumChunks, ArrayList<Integer>[] clues) {
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