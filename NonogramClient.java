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
        System.out.println(nonogram);
        int[][] bigGrid = bigGrid(grid);
        Nonogram bigNonogram = new Nonogram(bigGrid);
        System.out.println(bigNonogram);
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