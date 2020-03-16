import java.util.ArrayList;

public class Nonogram {

    private ArrayList<Integer>[] rowClues;
    private ArrayList<Integer>[] colClues;

    public Nonogram(int[][] grid) {
        rowClues = generateClues(true, grid);
        colClues = generateClues(false, grid);
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