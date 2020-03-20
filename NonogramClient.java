import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class NonogramClient {
    public static void main(String[] args) throws IOException {
        String[] testNames = {"heart",
                              "10by10",
                              "15by15",
                              "20by20",
                              "25by25"};
        NonogramSolver[] tests  = new NonogramSolver[testNames.length];
        for (int i = 0; i < tests.length; i++) {
            String name = testNames[i];
            NonogramPuzzle puzzle = new NonogramPuzzle(name, 
                                new File("lib\\" + name + "RowClues.txt"), 
                                new File("lib\\" + name + "ColClues.txt"));
            tests[i] = new NonogramSolver(puzzle);
            // tests[i].slowSolve();
            tests[i].queueSolve();
        }
    }

    private static void manualTurn(NonogramPuzzle nonogram) {
        Scanner s = new Scanner(System.in);
        while (true) {
            System.out.println(nonogram);
            System.out.print("Enter a move [r][c][f/b]: ");
            int r = Integer.parseInt(s.next());
            int c = Integer.parseInt(s.next());
            boolean fill = Character.toLowerCase(s.next().charAt(0)) != 'b';
            if (fill) nonogram.manualFill(r, c);
            else nonogram.manualBlank(r, c);
        }
    }
    
    // makes new grid with four grids.
    private static int[][] bigGrid(int[][] grid) {
        int[][] bigGrid = new int[grid.length * 2][grid[0].length * 2];
        for (int r = 0; r < bigGrid.length; r++) {
            for (int c = 0; c < bigGrid[r].length; c++) {
                bigGrid[r][c] = grid[r / 2][c / 2];
            }
        }
        return bigGrid;
    }
} 