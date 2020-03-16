import java.util.ArrayList;

public class NonogramClient {
    public static void main(String[] args) {
        // this is the answer we're trying to generate
        int[][] grid = {{0, 1, 0, 1, 0},
                        {1, 0, 1, 0, 1},
                        {1, 0, 0, 0, 1},
                        {0, 1, 0, 1, 0},
                        {0, 0, 1, 0, 0}};
        Nonogram nonogram = new Nonogram(grid);
        nonogram.printRowClues();
        nonogram.printColClues();
    }    
} 