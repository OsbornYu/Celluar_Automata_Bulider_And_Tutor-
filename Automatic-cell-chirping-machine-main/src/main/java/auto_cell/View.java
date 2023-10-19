package auto_cell;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import auto_cell.basic.Board.Board;
import auto_cell.basic.Cell.Cell;
import auto_cell.basic.Enum.CellState;

/**
 * For render the view (UI) we need
 */
public class View {

    private int rows;
    private int cols;
    private int initUnitSize = 0;
    private JFrame frame = null;
    private JMenuBar menuBar;
    private JMenu settingsMenu, saveMenu, loadMenu;
    // private JMenuItem boardMenuItem;
    private JMenuItem rulesMenuItem, saveItem, loadItem;
    private JButton startButton;
    private JButton pauseResumeButton;
    private JButton ResetButton;
    private JLabel roundsLabel;
    private JTextField roundsTextField;
    private JLabel[][] board;
    private JPanel boardPanel;

    public View(int width, int height, int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        board = new JLabel[rows][cols];
        createWindow(width, height, rows, cols);
    }

    public void setRoundsText(int rounds) {
        this.roundsTextField.setText(rounds + "");
    }

    private void updateSize(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        // if (initUnitSize == 0) {
        // initUnitSize = Math.min(frame.getWidth(), frame.getHeight()) / Math.min(40,
        // Math.max(rows, cols));
        // }
        for (JLabel[] row : board) {
            for (JLabel cell : row) {
                cell.setPreferredSize(new Dimension(initUnitSize, initUnitSize));
            }
        }
    }

    public void setPanelSlotColor(int row, int col, String color) {
        Color cellColor;
        try {
            cellColor = Color.decode(color);
        } catch (NumberFormatException e) {
            cellColor = Color.BLACK;
        }
        board[row][col].setBackground(cellColor);
    }

    public void createWindow(int width, int height, int rows, int cols) {
        frame = new JFrame("Cell");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(width, height);
        frame.setLocationRelativeTo(null);

        // Create menu bar, menus, and menu items
        menuBar = new JMenuBar();
        // todo: add save and load to menu bar
        // todo: click save, pop a window to allow user select where to save and call
        // saveToFile to save.
        // todo: click load, pop a window to allow user select which file need to load
        // and call loadFromFile to reload and rerender board.
        JMenu fileMenu = new JMenu("File");
        saveItem = new JMenuItem("Save");
        loadItem = new JMenuItem("Load");
        fileMenu.add(saveItem);
        fileMenu.add(loadItem);
        settingsMenu = new JMenu("Settings");
        // boardMenuItem = new JMenuItem("Board");
        rulesMenuItem = new JMenuItem("Rules");
        // settingsMenu.add(boardMenuItem);
        settingsMenu.add(rulesMenuItem);
        //
        menuBar.add(fileMenu);
        menuBar.add(settingsMenu);
        frame.setJMenuBar(menuBar);

        // Create start, pause, and Reset buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());

        roundsLabel = new JLabel("Rounds (-1 for infinite):");
        roundsTextField = new JTextField(10);
        roundsTextField.setText("-1");
        startButton = new JButton("Start");
        pauseResumeButton = new JButton("Pause");
        pauseResumeButton.setEnabled(false);
        ResetButton = new JButton("Reset");
        buttonPanel.add(roundsLabel);
        buttonPanel.add(roundsTextField);
        buttonPanel.add(startButton);
        buttonPanel.add(pauseResumeButton);
        buttonPanel.add(ResetButton);
        // add buttonPanel to the frame
        frame.setLayout(new BorderLayout());
        frame.add(buttonPanel, BorderLayout.NORTH);

        // Create a JPanel to hold the board cells
        JPanel boardPanel = new JPanel(new GridLayout(rows, cols));

        // Create a JScrollPane and add the boardPanel to it
        JScrollPane scrollPane = new JScrollPane(boardPanel);

        // Set the preferred size of the scrollPane
        scrollPane.setPreferredSize(new Dimension(width, height));

        if (initUnitSize == 0) {
            initUnitSize = Math.min(frame.getWidth(), frame.getHeight()) / Math.min(40, Math.max(rows, cols));
        }

        // boardPanel = new JPanel();
        // boardPanel.setLayout(new GridLayout(rows, cols));
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                JLabel l = new JLabel("");
                l.setOpaque(true);
                l.setPreferredSize(new Dimension(initUnitSize, initUnitSize));
                l.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
                boardPanel.add(l);
                board[r][c] = l;
            }
        }
        // frame.add(boardPanel, BorderLayout.CENTER);

        // Add the scrollPane to the frame
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    public void setPauseButtonText(String text) {
        this.pauseResumeButton.setText(text);
    }

    /**
     * todo: displayBoard should have a offset to render dynamicly
     * 
     * @param board
     * @throws InterruptedException
     */
    public void displayBoard(Board board) throws InterruptedException {
        int rows = board.getM();
        int cols = board.getN();
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                Cell cell = board.getCell(row, col);
                String color = getCellColor(cell.getState());
                setPanelSlotColor(row, col, color);
            }
        }
    }

    public String getCellColor(CellState state) {
        return state == CellState.ALIVE ? "#5c5c5c" : "#ffffff";
    }

    public void bindEvents(Controller controller) {
        // frame
        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                updateSize(rows, cols);
            }
        });
        // settings
        rulesMenuItem.addActionListener(e -> controller.showSettingsRuleView());
        // files
        saveItem.addActionListener(e -> {
            controller.saveBoardToFile();
        });
        loadItem.addActionListener(e -> {
            controller.loadBoardFromFile();
        });
        // control panels
        startButton.addActionListener(e -> {
            int rounds = Integer.valueOf(roundsTextField.getText());
            controller.start(rounds);
            startButton.setEnabled(false);
            pauseResumeButton.setEnabled(true);
        });
        // pause resume button
        pauseResumeButton.addActionListener(e -> {
            if (pauseResumeButton.getText().equals("Pause")) {
                controller.pause();
                pauseResumeButton.setText("Resume");
            } else {
                controller.resume();
                pauseResumeButton.setText("Pause");
            }
        });
        // reset button
        ResetButton.addActionListener(e -> {
            controller.reset();
            startButton.setEnabled(true);
            pauseResumeButton.setText("Pause");
            pauseResumeButton.setEnabled(false);
        });
        // board
        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[0].length; col++) {
                final int r = row;
                final int c = col;
                board[row][col].addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        controller.triggleCell(r, c);
                    }
                });
            }
        }
    }
}
