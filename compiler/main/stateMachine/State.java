package main.stateMachine;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class State {
    private int index;
    private Set<Integer> groupIndex;
    private boolean isEnd=false;
    private boolean isStart=false;

    public State(int index, State state) {
        this.index = index;
        if (state.groupIndex != null)
            this.groupIndex = new HashSet<>(state.groupIndex);
        this.isEnd = state.isEnd;
        this.isStart = state.isStart;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        State state = (State) o;
        return index == state.index;
    }

    @Override
    public int hashCode() {
        return Objects.hash(index);
    }

    @Override
    public String toString() {
        return (isStart ? "Start-" : "") + (isEnd ? "End-" : "") + "S" + index;
    }

    public State() {
    }

    public State(int index) {
        this.index = index;
    }

    public static State build(int index, int groupIndex) {
        State state = new State();
        state.setIndex(index);
        state.addGroupIndex(groupIndex);
        return state;
    }


    // 多个状态合并
    public static State build(int index, Set<State> states) {
        State state = new State();
        state.setIndex(index);
        for (State s : states) {
            state.getGroupIndex().addAll(s.getGroupIndex());
            if (s.isStart)
                state.isStart = true;
            if (s.isEnd)
                state.isEnd = true;
        }
        return state;
    }

    public static State build(int index, State inState, Set<State> states) {
        State state = new State();
        state.setIndex(index);
        states.add(inState);
//        state.getGroupIndex().addAll(inState.getGroupIndex());
        for (State s : states) {
            state.getGroupIndex().addAll(s.getGroupIndex());
            if (s.isStart)
                state.isStart = true;
            if (s.isEnd)
                state.isEnd = true;
        }
        return state;
    }

    public static State build(int index) {
        State state = new State();
        state.setIndex(index);
        return state;
    }

    public static State start() {
        State state = new State();
        state.setStart(true);
        return state;
    }

    public static State end() {
        State state = new State();
        state.setEnd(true);
        return state;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public boolean isEmptyGroupIndex() {
        return this.groupIndex == null || this.groupIndex.isEmpty();
    }

    public boolean containsGroupIndex(int groupIndex) {
        return this.groupIndex != null && this.groupIndex.contains(groupIndex);
    }

    public Set<Integer> getGroupIndex() {
        if (groupIndex == null) groupIndex = new HashSet<>();
        return groupIndex;
    }

    public void addGroupIndex(int groupIndex) {
        if (this.groupIndex == null) this.groupIndex = new HashSet<>();
        this.groupIndex.add(groupIndex);
    }

    public boolean isEnd() {
        return isEnd;
    }

    public boolean isStart() {
        return isStart;
    }

    public State setStart(boolean start) {
        isStart = start;
        index = -1;
        return this;
    }

    public State setEnd(boolean end) {
        isEnd = end;
        index = -2;
        return this;
    }
}
