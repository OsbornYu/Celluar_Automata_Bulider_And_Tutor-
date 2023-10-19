package auto_cell.basic.Rule;

import auto_cell.basic.Enum.CellState;
import auto_cell.basic.Enum.RuleCondition;

public class RuleItem {

    private CellState self;
    private RuleCondition condition;
    private CellState neighborState;
    private double number;
    private CellState result;

    public RuleItem(CellState self, RuleCondition condition, CellState neighborState, double number, CellState result) {
        this.self = self;
        this.condition = condition;
        this.neighborState = neighborState;
        this.number = number;
        this.result = result;
    }

    public boolean apply(double metStateNeighbors) {
        switch (this.condition) {
            case MORE:
                return metStateNeighbors > this.number;
            case MORE_EQUAL:
                return metStateNeighbors >= this.number;
            case EQUAL:
                return metStateNeighbors == this.number;
            case LESS_EQUAL:
                return metStateNeighbors <= this.number;
            case LESS:
                return metStateNeighbors < this.number;
            default:
                throw new TypeNotPresentException("invalid condition", null);
        }
    }

    public CellState getSelf() {
        return self;
    }

    public CellState getResult() {
        return result;
    }

    public CellState getNeighborState() {
        return neighborState;
    }

    public RuleCondition getCondition() {
        return condition;
    }

    public double getNumber() {
        return number;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("RuleItem{");
        sb.append("self=").append(self);
        sb.append(", condition=").append(condition);
        sb.append(", neighborState=").append(neighborState);
        sb.append(", number=").append(number);
        sb.append(", result=").append(result);
        sb.append("}");
        return sb.toString();
    }
}
