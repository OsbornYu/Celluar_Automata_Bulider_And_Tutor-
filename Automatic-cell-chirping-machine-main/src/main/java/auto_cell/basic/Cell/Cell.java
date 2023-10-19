package auto_cell.basic.Cell;

import auto_cell.basic.Enum.CellState;

public class Cell {

    private CellState state;
    private double weight = 1.0;

    public Cell(CellState iniState) {
        this.state = iniState;
    }

    public CellState getState() {
        return this.state;
    }

    public double getWeight() {
        return this.weight;
    }

    public void setState(CellState state) {
        this.state = state;
    }
}