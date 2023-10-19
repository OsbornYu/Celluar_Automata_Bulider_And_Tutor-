package auto_cell;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import auto_cell.basic.Board.Board;
import auto_cell.basic.Cell.Cell;
import auto_cell.basic.Enum.CellState;
import auto_cell.basic.Point;
import auto_cell.basic.Rule.CustomRule;
import auto_cell.basic.Rule.RuleItem;

/**
 * Main logics
 */
public class Controller {

    private int current = 0;
    private int rounds = 0;
    // modal
    private Board board = null;
    private CustomRule rules = null;
    private float[] customWeights = { 1, 1, 1, 1, 1, 1, 1, 1, 1 };
    //
    private final View view;
    private final SettingsRuleView settingsRuleView;
    //
    private final AtomicBoolean isRunning;
    private Thread simulationThread;

    public Controller(int m, int n, int rounds) {
        this.board = new Board(m, n);
        this.rounds = rounds;
        this.rules = new CustomRule();
        this.view = new View(800, 850, m, n);
        this.view.bindEvents(this);
        // this.view.setRoundsText(rounds);
        this.settingsRuleView = new SettingsRuleView();
        this.settingsRuleView.bindSaveNewButton(this);
        try {
            this.view.displayBoard(board);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        // states
        isRunning = new AtomicBoolean(true);
    }

    public void updateMapSize(int m, int n) {
        board.resize(m, n);
        try {
            view.displayBoard(board); // re-render the board
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void addNewRule(RuleItem ruleItem) {
        this.rules.add(ruleItem);
        this.settingsRuleView.rerenderAppliedRules(rules, this);
    }

    public void removeRule(RuleItem ruleItem) {
        this.rules.remove(ruleItem);
        this.settingsRuleView.rerenderAppliedRules(rules, this);
    }

    public void showSettingsRuleView() {
        this.settingsRuleView.show();
        if (isRunning.get())
            this.pause();
    }

    public void triggleCell(int row, int col) {
        CellState newState = board.getCell(row, col).getState() == CellState.ALIVE ? CellState.DEAD : CellState.ALIVE;
        board.getCell(row, col).setState(newState);
        view.setPanelSlotColor(row, col, view.getCellColor(newState));
    }

    public void setCustomWeights(float[] customWeights) {
        this.customWeights = customWeights;
    }

    public void run() {
        // start a logics thread to simulate the result.
        simulationThread = new Thread(() -> {
            try {
                // data
                int rows = board.getM();
                int cols = board.getN();
                // logics
                while (rounds == -1 ? isRunning.get() : current < rounds) {
                    if (!isRunning.get()) {
                        // Pause simulation if isRunning is false
                        Thread.sleep(100);
                        continue;
                    }
                    this.view.displayBoard(board);
                    Thread.sleep(100);

                    Map<Point, CellState> newStates = new HashMap<>();
                    Set<Point> activeCells = board.getAliveCellsAndNeighbors();
                    for (Point p : activeCells) {
                        Cell cur = this.board.getCell(p.x, p.y);
                        List<Cell> neighbors = this.board.getNeighbors(p.x, p.y);
                        CellState newState = this.rules.apply(cur, neighbors, customWeights);
                        newStates.put(p, newState == CellState.REMAIN ? cur.getState() : newState);
                    }

                    // Apply the changes
                    for (Map.Entry<Point, CellState> entry : newStates.entrySet()) {
                        Point p = entry.getKey();
                        this.board.getCell(p.x, p.y).setState(entry.getValue());
                    }

                    current += 1;
                }
            } catch (InterruptedException e) {
                // Handle the exception
                System.out.println(e.getMessage());
                Thread.currentThread().interrupt();
            }
        });
        simulationThread.start();
    }

    public void start(int rounds) {
        // init
        this.rounds = rounds;
        isRunning.set(true);
        //
        run();
    }

    public void pause() {
        isRunning.set(false);
        view.setPauseButtonText("Resume");
    }

    public void resume() {
        isRunning.set(true);
        view.setPauseButtonText("Pause");
        //
        run();
    }

    public void reset() {
        try {
            // stop thread
            isRunning.set(false);
            simulationThread.interrupt();
            // reset state.
            current = 0;
            int rows = board.getM();
            int cols = board.getN();
            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < cols; col++) {
                    board.getCell(row, col).setState(CellState.DEAD);
                }
            }
            view.displayBoard(board);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    public void saveBoardToFile() {
        // Use JFileChooser to get the file path
        String filePath = "";
        JFileChooser fileChooser = new JFileChooser();
        int returnValue = fileChooser.showSaveDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            filePath = fileChooser.getSelectedFile().getPath();
        } else {
            return; // User cancelled the operation
        }
        try {
            this.board.saveToFile(filePath);
            this.rules.saveRulesToFile(filePath);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "Error: The selected file path is not valid or the file format is incorrect.", "Loading Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public void loadBoardFromFile() {
        String filePath = "";
        // Use JFileChooser to get the file path
        JFileChooser fileChooser = new JFileChooser();
        int returnValue = fileChooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            filePath = fileChooser.getSelectedFile().getPath();
        } else {
            return; // User cancelled the operation
        }
        try {
            this.board.loadFromFile(filePath);
            this.rules.loadRulesFromFile(filePath);
            this.settingsRuleView.rerenderAppliedRules(rules, this);
            this.view.displayBoard(this.board);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "Error: The selected file path is not valid or the file format is incorrect.", "Loading Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
