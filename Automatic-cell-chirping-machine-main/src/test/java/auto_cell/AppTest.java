package auto_cell;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import auto_cell.basic.Cell.Cell;
import auto_cell.basic.Enum.CellState;
import auto_cell.basic.Enum.RuleCondition;
import auto_cell.basic.Rule.CustomRule;
import auto_cell.basic.Rule.RuleItem;

public class AppTest {

    // ========== Cell Tests ==========

    @Test
    public void testCellInitialization() {
        Cell cell = new Cell(CellState.ALIVE);
        assertEquals(CellState.ALIVE, cell.getState());
    }

    @Test
    public void testCellStateChange() {
        Cell cell = new Cell(CellState.DEAD);
        cell.setState(CellState.ALIVE);
        assertEquals(CellState.ALIVE, cell.getState());
    }

    // ========== RuleItem Tests ==========

    @Test
    public void testRuleItemMoreThanCondition() {
        RuleItem rule = new RuleItem(CellState.ALIVE, RuleCondition.MORE, CellState.ALIVE, 3, CellState.DEAD);
        assertTrue(rule.apply(4)); // More than 3 neighbors are ALIVE
        assertFalse(rule.apply(3)); // Only 3 neighbors are ALIVE
    }

    @Test
    public void testRuleItemLessThanCondition() {
        RuleItem rule = new RuleItem(CellState.DEAD, RuleCondition.LESS, CellState.ALIVE, 2, CellState.ALIVE);
        assertTrue(rule.apply(1)); // Less than 2 neighbors are ALIVE
        assertFalse(rule.apply(2)); // 2 neighbors are ALIVE
    }

    @Test
    public void testRuleItemEqualsCondition() {
        RuleItem rule = new RuleItem(CellState.ALIVE, RuleCondition.EQUAL, CellState.ALIVE, 2, CellState.DEAD);
        assertTrue(rule.apply(2)); // 2 neighbors are ALIVE
        assertFalse(rule.apply(3)); // 3 neighbors are ALIVE
    }

    // ========== CustomRule Tests ==========

    @Test
    public void testCustomRuleWithMultipleRuleItems() {
        RuleItem rule1 = new RuleItem(CellState.ALIVE, RuleCondition.MORE, CellState.ALIVE, 3, CellState.DEAD);
        RuleItem rule2 = new RuleItem(CellState.ALIVE, RuleCondition.LESS_EQUAL, CellState.ALIVE, 2, CellState.DEAD);
        CustomRule customRule = new CustomRule();
        customRule.add(rule1);
        customRule.add(rule2);

        Cell cell = new Cell(CellState.ALIVE);

        List<Cell> neighbors = generateNeighbors(4, CellState.ALIVE); // 4 ALIVE neighbors

        // Test with 4 alive neighbors (expecting DEAD because it's MORE than 3 ALIVE
        // neighbors)
        assertEquals(CellState.DEAD, customRule.apply(cell, neighbors, new float[] { 1, 1, 1, 1, 1, 1, 1, 1, 1 }));

        neighbors = generateNeighbors(2, CellState.ALIVE); // 2 ALIVE neighbors

        // Test with 2 alive neighbors (expecting DEAD because it's LESS_EQUAL than 2
        // ALIVE neighbors)
        assertEquals(CellState.DEAD, customRule.apply(cell, neighbors, new float[] { 1, 1, 1, 1, 1, 1, 1, 1, 1 }));
    }

    // Helper method to generate a list of neighbors
    private List<Cell> generateNeighbors(int count, CellState state) {
        List<Cell> neighbors = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            neighbors.add(new Cell(state));
        }
        return neighbors;
    }
}
