package auto_cell.basic.Board;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;

import auto_cell.basic.Point;
import org.json.JSONArray;
import org.json.JSONObject;

import auto_cell.basic.Cell.Cell;
import auto_cell.basic.Enum.CellState;

public class Board {

    private Map<Integer, Cell> cells;
    private int m;
    private int n;

    /**
     * This is a infinity board.
     */
    public Board(int m, int n) {
        cells = new HashMap<>();
        this.m = m;
        this.n = n;
        initializeCells();
    }

    public synchronized void resize(int newM, int newN) {
        Map<Integer, Cell> oldCells = new HashMap<>(cells);

        this.m = newM;
        this.n = newN;
        cells.clear();
        initializeCells();

        for (int i = 0; i < newM; i++) {
            for (int j = 0; j < newN; j++) {
                int newIndex = getIndex(i, j);
                if (oldCells.containsKey(newIndex)) {
                    cells.put(newIndex, oldCells.get(newIndex));
                }
            }
        }
    }

    private void initializeCells() {
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                int cellIndex = getIndex(i, j);
                cells.put(cellIndex, new Cell(CellState.DEAD));
            }
        }
    }

    private int getIndex(int i, int j) {
        return i * n + j;
    }

    /**
     * 
     * @param i virtual coor i
     * @param j virtual coor j
     * @return
     */
    public synchronized Cell getCell(int i, int j) {
        int cellIndex = getIndex(i, j);
        return cells.getOrDefault(cellIndex, null);
    }

    /**
     * This is a public
     * 
     * @param i virtual coor i
     * @param j virtual coor j
     * @return
     */
    public synchronized List<Cell> getNeighbors(int i, int j) {
        List<Cell> neighbors = new ArrayList<>(8);
        for (int row = i - 1; row <= i + 1; row++) {
            for (int col = j - 1; col <= j + 1; col++) {
                if (row >= 0 && row < m && col >= 0 && col < n && !(row == i && col == j)) {
                    int neighborIndex = getIndex(row, col);
                    neighbors.add(cells.getOrDefault(neighborIndex, null));
                }
            }
        }
        return neighbors;
    }

    public int getM() {
        return m;
    }

    public int getN() {
        return n;
    }

    public void setAllDead() {
        for (int cellIndex : cells.keySet()) {
            cells.get(cellIndex).setState(CellState.DEAD);
        }
    }

    public Set<Point> getAliveCellsAndNeighbors() {
        Set<Point> activeCells = new HashSet<>();
        for (int row = 0; row < m; row++) {
            for (int col = 0; col < n; col++) {
                Cell cur = getCell(row, col);
                if (cur.getState() == CellState.ALIVE) {
                    activeCells.add(new Point(row, col));
                    activeCells.addAll(getNeighborPoints(row, col));
                }
            }
        }
        return activeCells;
    }

    private Set<Point> getNeighborPoints(int row, int col) {
        Set<Point> neighbors = new HashSet<>();
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) continue;
                if (row + i >= 0 && row + i < m && col + j >= 0 && col + j < n) {
                    neighbors.add(new Point(row + i, col + j));
                }
            }
        }
        return neighbors;
    }

    public void saveToFile(String filename) throws IOException {
        JSONObject boardState = new JSONObject();
        JSONArray cellsArray = new JSONArray();

        for (int row = 0; row < this.getM(); row++) {
            JSONArray cellRow = new JSONArray();
            for (int col = 0; col < this.getN(); col++) {
                cellRow.put(this.getCell(row, col).getState().toString());
            }
            cellsArray.put(cellRow);
        }
        boardState.put("cells", cellsArray);

        Files.write(Paths.get(filename), boardState.toString().getBytes());
    }

    public void loadFromFile(String filename) throws IOException {
        String content = new String(Files.readAllBytes(Paths.get(filename)));
        JSONObject boardState = new JSONObject(content);
        JSONArray cellsArray = boardState.getJSONArray("cells");

        int rowsFromFile = cellsArray.length();
        int colsFromFile = rowsFromFile > 0 ? cellsArray.getJSONArray(0).length() : 0;

        int validRows = Math.min(rowsFromFile, this.getM());
        int validCols = Math.min(colsFromFile, this.getN());

        for (int row = 0; row < validRows; row++) {
            JSONArray cellRow = cellsArray.getJSONArray(row);
            for (int col = 0; col < validCols; col++) {
                String state = cellRow.getString(col);
                this.getCell(row, col).setState(CellState.valueOf(state));
            }
        }
    }
}
