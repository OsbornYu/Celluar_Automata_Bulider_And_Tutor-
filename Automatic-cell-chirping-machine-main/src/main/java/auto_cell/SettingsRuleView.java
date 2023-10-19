package auto_cell;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.*;

import auto_cell.basic.Enum.CellState;
import auto_cell.basic.Enum.RuleCondition;
import auto_cell.basic.Rule.CustomRule;
import auto_cell.basic.Rule.RuleItem;

import java.util.List;
import java.util.Map;

class Util {
    public static <T extends Enum<T>> String[] enumToStrings(Class<T> enumClass) {
        T[] enumValues = enumClass.getEnumConstants();
        List<String> enumNames = new ArrayList<>();
        for (T value : enumValues) {
            String name = value.name();
            if (name.equals("REMAIN")) {
                continue;
            }
            enumNames.add(name);
        }
        return enumNames.toArray(new String[0]);
    }

    public static <T extends Enum<T>> JComboBox<String> enumToSelectBox(Class<T> enuClass, String selected) {
        JComboBox<String> selectBox = new JComboBox<>(Util.enumToStrings(enuClass));
        selectBox.setSelectedItem(selected);
        return selectBox;
    }
}

public class SettingsRuleView {

    private JFrame frame;
    private JPanel appliedRulesPanel;
    private JPanel customWeights;
    private JPanel newRulePanel;
    private JButton saveNewRuleBtn;
    private JScrollPane scrollPane;
    private JButton saveNewCustomButton;
    //
    private Map<RuleItem, Map<String, Component>> ruleComponent;
    private Map<JButton, RuleItem> removeRuleButtons;
    private List<JTextField> customWieghtInputs;

    public SettingsRuleView() {
        ruleComponent = new HashMap<>();
        removeRuleButtons = new HashMap<>();
        frame = new JFrame();
        frame.setTitle("Rules Settings");
        frame.setSize(1000, 600);

        appliedRulesPanel = new JPanel();
        appliedRulesPanel.setLayout(new BoxLayout(appliedRulesPanel, BoxLayout.Y_AXIS));
        appliedRulesPanel.setBorder(BorderFactory.createTitledBorder("Applied Custom Rules"));
        // custom weights panel has 2 cols. left col is a 3 * 3 input boxes, and the
        // right col is a button named "save";
        customWeights = createCustomWeightsPanel();

        scrollPane = new JScrollPane(appliedRulesPanel);

        newRulePanel = createNewRuleUIComponent();
        newRulePanel.setBorder(BorderFactory.createTitledBorder("New Custom Rules"));

        // Create a main panel and add newRulePanel and scrollPane to it
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        // newRulePanel.setPreferredSize(new Dimension(1000, 100));
        // scrollPane.setPreferredSize(new Dimension(1000, 300));
        // customWeights.setPreferredSize(new Dimension(1000, 100));
        newRulePanel.setPreferredSize(new Dimension(1000, 80));
        newRulePanel.setMinimumSize(new Dimension(1000, 80));
        newRulePanel.setMaximumSize(new Dimension(1000, 80));

        customWeights.setPreferredSize(new Dimension(1000, 120));
        customWeights.setMinimumSize(new Dimension(1000, 120));
        customWeights.setMaximumSize(new Dimension(1000, 120));

        scrollPane.setMaximumSize(new Dimension(1000, Integer.MAX_VALUE));

        mainPanel.add(newRulePanel);
        mainPanel.add(scrollPane);
        mainPanel.add(customWeights);

        // Add main panel to the frame
        frame.add(mainPanel);

        frame.setVisible(false);
    }

    public void show() {
        frame.setVisible(true);
    }

    private JPanel createCustomWeightsPanel() {
        // todo: add BorderFactory with name "Weights (0.0-1.0)"
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Weights (0.0-1.0)"));

        // create 3x3 input boxes in a panel
        JPanel inputPanel = new JPanel(new GridLayout(3, 3));
        customWieghtInputs = new ArrayList<>(9);
        for (int i = 0; i < 9; i++) {
            JTextField text = new JTextField(5);
            Dimension size = new Dimension(5, 1);
            text.setHorizontalAlignment(JTextField.CENTER);
            text.setText("1");
            text.setMinimumSize(size);
            text.setMaximumSize(size);
            text.setPreferredSize(size);
            customWieghtInputs.add(text);
            inputPanel.add(text); // adjust the field size as per your requirement
        }

        // create save button
        saveNewCustomButton = new JButton("Save");

        // add input panel and save button to the main panel
        panel.add(inputPanel, BorderLayout.CENTER);
        panel.add(saveNewCustomButton, BorderLayout.EAST);

        return panel;
    }

    public void rerenderAppliedRules(CustomRule customRule, Controller controller) {
        appliedRulesPanel.removeAll();
        removeRuleButtons.clear();
        for (RuleItem ruleItem : customRule.getRuleItems()) {
            appliedRulesPanel.add(createRowPanel(ruleItem));
        }
        bindRemoveRuleButtons(controller);
        appliedRulesPanel.revalidate();
        appliedRulesPanel.repaint();
    }

    public void bindSaveNewButton(Controller controller) {
        saveNewRuleBtn.addActionListener(e -> {
            JComboBox cellStateBox = (JComboBox) newRulePanel.getComponent(1);
            JComboBox conditionBox = (JComboBox) newRulePanel.getComponent(7);
            JComboBox neighborStateBox = (JComboBox) newRulePanel.getComponent(5);
            JTextField neighborNumberField = (JTextField) newRulePanel.getComponent(9);
            JComboBox resultBox = (JComboBox) newRulePanel.getComponent(3);
            CellState cellState = CellState.valueOf((String) cellStateBox.getSelectedItem());
            RuleCondition condition = RuleCondition.valueOf((String) conditionBox.getSelectedItem());
            CellState neighborState = CellState.valueOf((String) neighborStateBox.getSelectedItem());
            double neighborNumber = Double.parseDouble(neighborNumberField.getText());
            CellState result = CellState.valueOf((String) resultBox.getSelectedItem());
            RuleItem ruleItem = new RuleItem(cellState, condition, neighborState, neighborNumber, result);
            controller.addNewRule(ruleItem);
        });
        //
        saveNewCustomButton.addActionListener(e -> {
            float[] weights = new float[9];
            for (int i = 0; i < customWieghtInputs.size(); i++) {
                weights[i] = Float.parseFloat(customWieghtInputs.get(i).getText());
            }
            controller.setCustomWeights(weights);
        });
    }

    public void bindRemoveRuleButtons(Controller controller) {
        removeRuleButtons.forEach((btn, rule) -> {
            // btn.removeActionListener(btn.getActionListeners()[0]); // Remove existing
            btn.addActionListener(e -> {
                removeRuleButtons.remove(btn);
                controller.removeRule(rule);
            });
        });
    }

    private JPanel createNewRuleUIComponent() {
        JPanel panel = new JPanel(new FlowLayout());
        // add components to the panel
        panel.add(new JLabel("Current")); // 0
        panel.add(new JComboBox<>(Util.enumToStrings(CellState.class))); // 1
        panel.add(new JLabel("cell will")); // 2
        panel.add(new JComboBox<>(Util.enumToStrings(CellState.class))); // 3
        panel.add(new JLabel("Condition: (Neighbor")); // 4
        panel.add(new JComboBox<>(Util.enumToStrings(CellState.class))); // 5
        panel.add(new JLabel("cells")); // 6
        panel.add(new JComboBox<>(Util.enumToStrings(RuleCondition.class))); // 7
        panel.add(new JLabel("than")); // 8
        panel.add(new JTextField(5)); // 9
        panel.add(new JLabel(")")); // 10
        saveNewRuleBtn = new JButton("Add Rule"); // 11
        panel.add(saveNewRuleBtn); // 12
        // Setting preferred size for the newRulePanel
        panel.setPreferredSize(new Dimension(1000, 30));
        return panel;
    }

    private void addLabelAndComboBox(JPanel appliedRulesPanel, String labelText, Class<? extends Enum> enumClass,
            String selectedItem, RuleItem ruleItem) {
        // appliedRulesPanel.add(new JLabel(labelText));
        JComboBox<String> comboBox = Util.enumToSelectBox(enumClass, selectedItem);
        appliedRulesPanel.add(comboBox);
        ruleComponent.get(ruleItem).put(labelText, comboBox);
    }

    private JPanel createRowPanel(RuleItem ruleItem) {
        //
        ruleComponent.put(ruleItem, new HashMap<>());
        //
        JPanel appliedRulesPanel = new JPanel(new FlowLayout());
        // add components to the appliedRulesPanel, map
        appliedRulesPanel.add(new JLabel("Current"));
        addLabelAndComboBox(appliedRulesPanel, "Cell State", CellState.class, ruleItem.getSelf().toString(), ruleItem);
        appliedRulesPanel.add(new JLabel("cell will"));
        addLabelAndComboBox(appliedRulesPanel, "Result", CellState.class, ruleItem.getResult().toString(), ruleItem);
        appliedRulesPanel.add(new JLabel("Condition: (Neighbor"));
        addLabelAndComboBox(appliedRulesPanel, "Neighbor State", CellState.class,
                ruleItem.getNeighborState().toString(), ruleItem);
        appliedRulesPanel.add(new JLabel("cells"));
        addLabelAndComboBox(appliedRulesPanel, "Condition", RuleCondition.class, ruleItem.getCondition().toString(),
                ruleItem);
        appliedRulesPanel.add(new JLabel("than"));
        JTextField neighborNumber = new JTextField(5);
        neighborNumber.setText(Double.toString(ruleItem.getNumber()));
        ruleComponent.get(ruleItem).put("Neighbor Number", neighborNumber);
        appliedRulesPanel.add(neighborNumber); // InputBox
        appliedRulesPanel.add(new JLabel(")"));

        JButton removeRuleBtn = new JButton("Remove Rule");
        removeRuleButtons.put(removeRuleBtn, ruleItem);
        appliedRulesPanel.add(removeRuleBtn);

        return appliedRulesPanel;
    }

}
