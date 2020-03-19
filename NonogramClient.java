import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import com.opencsv.exceptions.CsvException;

public class NonogramClient {
    public static void main(String[] args) throws IOException, CsvException {
        // this is the answer we're trying to generate
        Nonogram heart = new Nonogram("heart", new File("lib\\heartRowClues.txt"), new File("lib\\heartColClues.txt"));
        // heart.solve();
        Nonogram tenByTen = new Nonogram("10by10", new File("lib\\10by10RowClues.txt"), new File("lib\\10by10ColClues.txt"));
        // tenByTen.solve();
        Nonogram fifteenByFifteen = new Nonogram("15by15", new File("lib\\15by15RowClues.txt"), new File("lib\\15by15ColClues.txt"));
        // fifteenByFifteen.solve();
        Nonogram twentyByTwenty = new Nonogram("20by20", new File("lib\\20by20RowClues.txt"), new File("lib\\20by20ColClues.txt"));
        // twentyByTwenty.solve();
        Nonogram twentyFiveByTwentyFive = new Nonogram("25by25", new File("lib\\25by25RowClues.txt"), new File("lib\\25by25ColClues.txt"));
        twentyFiveByTwentyFive.solve();
    }

    private static void manualTurn(Nonogram nonogram) {
        Scanner s = new Scanner(System.in);
        while (true) {
            System.out.println(nonogram);
            System.out.print("Enter a move [r][c][f/b]: ");
            int r = Integer.parseInt(s.next());
            int c = Integer.parseInt(s.next());
            boolean fill = Character.toLowerCase(s.next().charAt(0)) != 'b';
            if (fill) nonogram.fill(r, c);
            else nonogram.blank(r, c);
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