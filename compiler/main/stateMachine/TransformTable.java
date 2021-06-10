package main.stateMachine;

import java.util.*;

public class TransformTable {
    public Map<State, Map<String, Set<State>>> trans = new HashMap<>();
    private List<String[]> waitForDelete;
    private State startState;

    public TransformTable(String regex) {
        startState = State.start();
        trans.put(startState, new HashMap<>());
        trans.put(State.end(), new HashMap<>());
        add(State.start(), regex, State.end());
        waitForDelete = new ArrayList<>();
    }

    public Map<String, Set<State>> add(State inState) {
        return trans.computeIfAbsent(inState, v -> new HashMap<>());
    }

    public Set<State> set(State inState, String input, Set<State> toStates) {
        Map<String, Set<State>> inputs = trans.computeIfAbsent(inState, v -> new HashMap<>());
        return inputs.put(input, toStates);
    }

    public Set<State> add(State inState, String input, Set<State> toStates) {
        Map<String, Set<State>> inputs = trans.computeIfAbsent(inState, v -> new HashMap<>());
        inputs.computeIfAbsent(input, v -> new HashSet<>()).addAll(toStates);
        return inputs.get(input);
    }


    public boolean add(State inState, String input, State toState) {
        Map<String, Set<State>> inputs = trans.computeIfAbsent(inState, v -> new HashMap<>());
        Set<State> toStates = inputs.computeIfAbsent(input, v -> new HashSet<>());
        if (toStates.contains(toState))
            return false;
        else
            toStates.add(toState);
        return true;
    }

    public Set<State> keySet() {
        return trans.keySet();
    }

    public Map<String, Set<State>> get(State inState) {
        return trans.get(inState);
    }

    public Set<State> get(State inState, String input) {
        if (!trans.containsKey(inState)) return null;
        return trans.get(inState).get(input);
    }

    public Set<State> delete(State inState, String input) {
        if (!trans.containsKey(inState)) return null;
        return trans.get(inState).remove(input);
    }

    public Map<String, Set<State>> delete(State inState) {
        return trans.remove(inState);
    }

//    public void deleteDelayedClear() {
//        waitForDelete.clear();
//    }
//
//    public void deleteDelayed(State inState, String input) {
//        waitForDelete.add(new String[]{inState, input});
//    }
//
//    public void deleteDelayedNow() {
//        for (String[] strings : waitForDelete) {
//            this.delete(strings[0], strings[1]);
//        }
//        waitForDelete.clear();
//    }


    public boolean delete(State inState, String input, State toState) {
        if (!trans.containsKey(inState)) return false;
        Set<State> toStates = trans.get(inState).get(input);
        if (toStates == null) return false;
        return toStates.remove(toState);
    }

    public State getStartState() {
        return startState;
    }

    public void setStartState(State startState) {
        this.startState = startState;
    }

    public void add(State state, Map<String, Set<State>> inputToStates) {
        Map<String, Set<State>> inputs = trans.get(state);
        if (inputs == null)
            trans.put(state, inputToStates);
        else
            trans.get(state).putAll(inputs);
    }
}
