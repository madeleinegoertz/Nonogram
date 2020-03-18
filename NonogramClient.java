import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class NonogramClient {
    public static void main(String[] args) {
        // this is the answer we're trying to generate
        int[][] grid = {{0, 1, 0, 1, 0},
                        {1, 0, 1, 0, 1},
                        {1, 0, 0, 0, 1},
                        {0, 1, 0, 1, 0},
                        {0, 0, 1, 0, 0}};
        Nonogram nonogram = new Nonogram(grid);

        ArrayList<Integer> set = new ArrayList<Integer>();
        for (int i = 0; i < 5; i++) {
            set.add(i);
        }
        ArrayList<int[]> sets = nonogram.combin(2, set);
        for (int i = 0; i < sets.size(); i++) {
            System.out.println(Arrays.toString(sets.get(i)));
        }
        
        // Scanner s = new Scanner(System.in);
        // while (true) {
        //     System.out.println(nonogram);
        //     System.out.print("Enter a move [r][c][f/b]: ");
        //     int r = Integer.parseInt(s.next());
        //     int c = Integer.parseInt(s.next());
        //     boolean fill = Character.toLowerCase(s.next().charAt(0)) != 'b';
        //     if (fill) nonogram.fill(r, c);
        //     else nonogram.blank(r, c);
        // }
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