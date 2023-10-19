package app;

import javax.swing.JOptionPane;

import auto_cell.Controller;
import auto_cell.basic.Enum.CellState;
import auto_cell.basic.Enum.RuleCondition;
import auto_cell.basic.Rule.RuleItem;

public class App {
        public static void main(String[] args) {
                String rowInput = JOptionPane.showInputDialog(null, "Enter number of rows:");
                int rows = Integer.parseInt(rowInput);

                String colInput = JOptionPane.showInputDialog(null, "Enter number of columns:");
                int cols = Integer.parseInt(colInput);

                // String roundInput = JOptionPane.showInputDialog(null, "Enter number of rounds
                // (-1 for infinite):");
                // int rounds = Integer.parseInt(roundInput);

                Controller autoCell = new Controller(rows, cols, -1);

                // autoCell.triggleCell(10, 11);
                // autoCell.triggleCell(11, 12);
                // autoCell.triggleCell(12, 10);
                // autoCell.triggleCell(12, 11);
                // autoCell.triggleCell(12, 12);

                // autoCell.triggleCell(20, 11);
                // autoCell.triggleCell(21, 12);
                // autoCell.triggleCell(22, 10);
                // autoCell.triggleCell(22, 11);
                // autoCell.triggleCell(22, 12);

                // autoCell.triggleCell(10, 21);
                // autoCell.triggleCell(11, 22);
                // autoCell.triggleCell(12, 20);
                // autoCell.triggleCell(12, 21);
                // autoCell.triggleCell(12, 22);

                // autoCell.triggleCell(20, 21);
                // autoCell.triggleCell(21, 22);
                // autoCell.triggleCell(22, 20);
                // autoCell.triggleCell(22, 21);
                // autoCell.triggleCell(22, 22);

                // https://zh.wikipedia.org/wiki/%E5%BA%B7%E5%A8%81%E7%94%9F%E5%91%BD%E6%B8%B8%E6%88%8F
                // https://playgameoflife.com/ a online demo for testing

                // Rule 1
                // When the current cell is alive, the cell becomes dead when there are less
                // than 2 surviving cells around it (not including 2). (Simulating the scarcity
                // of life)
                autoCell.addNewRule(
                                new RuleItem(CellState.ALIVE, RuleCondition.LESS, CellState.ALIVE, 2,
                                                CellState.DEAD));
                // Rule 2
                // When the current cell is alive, the cell remains as it is when surrounded by
                // 2 or 3 surviving cells.

                // Rule 3
                // When the current cell is alive, the cell becomes dead when there are more
                // than 3 surviving cells around it. (Simulating too many lives)
                autoCell.addNewRule(
                                new RuleItem(CellState.ALIVE, RuleCondition.MORE, CellState.ALIVE, 3,
                                                CellState.DEAD));
                // Rule 4
                // When the current cell is dead, the cell becomes alive when surrounded by 3
                // surviving cells. (Simulation of reproduction)
                autoCell.addNewRule(new RuleItem(CellState.DEAD, RuleCondition.EQUAL,
                                CellState.ALIVE, 3, CellState.ALIVE));
        }
}
