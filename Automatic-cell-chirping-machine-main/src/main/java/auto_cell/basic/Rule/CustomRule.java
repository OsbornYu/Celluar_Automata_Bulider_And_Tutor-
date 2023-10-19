package auto_cell.basic.Rule;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import auto_cell.basic.Cell.Cell;
import auto_cell.basic.Enum.CellState;
import auto_cell.basic.Enum.RuleCondition;

/**
 * CustomRule Allow basic custom rules.
 * For each rule,
 * condition: MORE, MORE_EQUAL, EQUAL, LESS_EQUAL, LESS
 * neighbors: int
 * neighborState: ALIVE, DEAD
 * Operator: AND, OR
 * result: ALIVE, DEAD
 * 
 * For example,
 * ALIVE, MORE 3 ALIVE result ALIVE
 * ALIVE, LESS_EQUAL 5 ALIVE result ALIVE
 * ALIVE, MORE 5 ALIVE result DEAD
 * ALIVE, LESS_EQUAL 3 result DEAD
 * 
 * what't the final result?
 */
public class CustomRule {

    private Map<CellState, LinkedList<RuleItem>> rules;

    public CustomRule() {
        this.rules = new HashMap<>();
    }

    public CellState apply(Cell cell, List<Cell> neighbors, float[] customWeights) {
        CellState selfState = cell.getState();
        Map<CellState, Double> metStateNeighbors = new HashMap<>();
        // check each rule for the current situation. return the first true CellState
        if (this.rules.containsKey(selfState)) {
            for (RuleItem rule : this.rules.get(selfState)) {
                double n = 0;
                CellState requiredState = rule.getNeighborState();
                if (metStateNeighbors.containsKey(requiredState))
                    n = metStateNeighbors.get(requiredState);
                else {
                    int i = 0;
                    for (Cell neighbor : neighbors) {
                        if (neighbor.getState() == requiredState)
                            n += neighbor.getWeight() * customWeights[i];
                        i++;
                    }
                    metStateNeighbors.put(requiredState, n);
                }

                if (rule.apply(n)) {
                    return rule.getResult();
                }
            }
        }
        return CellState.REMAIN;
    }

    public void add(RuleItem item) {
        CellState selfState = item.getSelf();
        if (this.rules.containsKey(selfState)) {
            this.rules.get(selfState).push(item);
        } else {
            LinkedList<RuleItem> ul = new LinkedList<>();
            ul.push(item);
            this.rules.put(selfState, ul);
        }
    }

    public void remove(RuleItem item) {
        CellState selfState = item.getSelf();
        if (this.rules.containsKey(selfState)) {
            this.rules.get(selfState).remove(item);
        }
    }

    public void update(RuleItem pre, RuleItem cur) {
        this.remove(pre);
        this.add(cur);
    }

    public RuleItem[] getRuleItems() {
        List<RuleItem> items = new ArrayList<>();
        for (LinkedList<RuleItem> ruleItems : rules.values()) {
            items.addAll(ruleItems);
        }
        return items.toArray(new RuleItem[0]);
    }

    public void saveRulesToFile(String filename) throws IOException {
        JSONObject rulesObject = new JSONObject();
        JSONArray rulesArray = new JSONArray();

        for (RuleItem item : getRuleItems()) {
            JSONObject ruleItem = new JSONObject();
            ruleItem.put("self", item.getSelf().toString());
            ruleItem.put("condition", item.getCondition().toString());
            ruleItem.put("neighborState", item.getNeighborState().toString());
            ruleItem.put("number", item.getNumber());
            ruleItem.put("result", item.getResult().toString());
            rulesArray.put(ruleItem);
        }

        rulesObject.put("rules", rulesArray);
        Files.write(Paths.get(filename + "rules"), rulesObject.toString().getBytes());
    }

    public void loadRulesFromFile(String filename) throws IOException {
        String content = new String(Files.readAllBytes(Paths.get(filename + "rules")));
        JSONObject rulesObject = new JSONObject(content);
        JSONArray rulesArray = rulesObject.getJSONArray("rules");
        rules.clear();

        for (int i = 0; i < rulesArray.length(); i++) {
            JSONObject ruleItem = rulesArray.getJSONObject(i);
            CellState self = CellState.valueOf(ruleItem.getString("self"));
            RuleCondition condition = RuleCondition.valueOf(ruleItem.getString("condition"));
            CellState neighborState = CellState.valueOf(ruleItem.getString("neighborState"));
            double number = ruleItem.getDouble("number");
            CellState result = CellState.valueOf(ruleItem.getString("result"));

            RuleItem item = new RuleItem(self, condition, neighborState, number, result);
            add(item);
        }
    }
}
