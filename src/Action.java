package src;

import java.util.ArrayList;

public class Action {
    
    private boolean isRow;
    private ArrayList<Integer> indices;

    public Action(boolean isRow) {
        this(isRow, new ArrayList<Integer>());
    }

    public Action(boolean isRow, ArrayList<Integer> indices) {
        this.isRow = isRow;
        this.indices = indices;
    }

    public boolean isRow() {
        return isRow;
    }

    public int numIndices() {
        return indices.size();
    }

    public int getIndex(int i) {
        return indices.get(i);
    }

    public void addIndex(int i) {
        indices.add(i);
    }

    public int remove(int i) {
        return indices.remove(i);
    }

    public String toString() {
        String str = isRow ? "ROW:" : "COL:";
        for (int i = 0; i < indices.size(); i++) {
            str += " " + indices.get(i);
        }
        return str;
    }
}