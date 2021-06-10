package main.stateMachine;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Matcher {

    private CharSequence input;
    private TransformTable trans;
    private Map<Integer, int[]> groups;
    private int start = 0;
    private int end = -1;
    private int endLoc = 0;
    private State nowState;

    // \b \w \d 这些


    public Matcher(TransformTable trans, CharSequence input) {
        this.groups = new HashMap<>();
        this.input = input;
        this.trans = trans;
    }

    public String group(int index) {
        int[] locs = groups.get(index);
        if (locs == null) return null;
        return (String) input.subSequence(locs[0], locs[1] + 1);
    }

    Matcher region(int start) {
        this.start = start;
        this.end = -1;
        return this;
    }

    Matcher region(int start, int end) {
        this.start = start;
        this.end = end;

        return this;
    }


    // 查找下一个
    boolean lookingAt() {
        this.groups.clear();
        nowState = trans.getStartState();
        int loc = this.start;
        if (this.end == -1)
            this.end = input.length() - 1;
        boolean crossEnd = false;
        Set<State> temp;

        char input;
        while (loc <= this.end) {
            temp = null;
            input = this.input.charAt(loc);
            Set<State> nextStateSet = trans.get(nowState, String.valueOf(input));
            if (trans.get(nowState) != null) {
                if (nextStateSet == null)
                    //找  .   [^..]  ^ $
                    for (String in : trans.get(nowState).keySet()) {
                        if (in.equals("_."))
                            temp = trans.get(nowState, in);
                        if (in.charAt(0) == '^' && in.charAt(1) != input) {
                            nextStateSet = trans.get(nowState, in);
                            break;
                        }
                    }
                if (nextStateSet == null) nextStateSet = temp;
            }

            // does not have next state
            if (nextStateSet == null || nextStateSet.size() == 0) {
                return crossEnd;
            }
            State nextState = nextStateSet.toArray(new State[0])[0];
            // 记录结束状态 和 group(0)
            if (nextState.isEnd()) {
                this.groups.put(0, new int[]{this.start, loc});
                crossEnd = true;
                endLoc = loc;
            }
            // 记录 group
            if (nextState.getGroupIndex().size() > 0) {
                for (Integer groupIndex : nextState.getGroupIndex()) {
                    int[] gins = this.groups.get(groupIndex);
                    if (gins == null)
                        this.groups.put(groupIndex, new int[]{loc, -1});
                    else
                        gins[1] = loc;
                }
            }
            nowState = nextState;
            loc++;
        }
        for (Integer integer : new HashSet<>(this.groups.keySet())) {
            if (groups.get(integer)[1] == -1)
                groups.remove(integer);
        }

        return true;
    }

    // 获取当前识别的结尾
    public int end() {
        return endLoc;
    }
}
