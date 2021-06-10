package main;

import java.util.*;

public class StateTransform {

    // 所有的状态
    private final String[] states;
    // 所有的输入
    private final String[][] input;
    // 结束状态
    private final Set<Integer> endState;
    // 转换表
    private int[][] transition;

    int current, start;

    public StateTransform(Map<String, Object> stateList, String method) {
        this.states = (String[]) stateList.get("S");//[*,...]
        this.input = (String[][]) stateList.get("I");//[[*,...],...]
        this.start = (int) stateList.get("SS");//number
        List<Integer> fs = Arrays.asList((Integer[]) stateList.get("FS"));
        this.endState = new HashSet(fs);//[number,...]

        this.current = this.start;//number

        if (method.equals("DFA"))
            this.transition = (int[][]) stateList.get("T");//{current:{input:result,...},...}
        else if (method.equals("NFA")) {
            System.out.println("not support");
        } else
            System.out.println("method is not DFA | NFA");
    }

    // 状态转换
    public String transform(String i) {
        for (int I = 0; I < this.input.length; I++) {
            for (int ii = 0; ii < this.input[I].length; ii++) {
                // 遍历每一个输入里的每一个字符


                if (this.input[I][ii].equals(i) && this.transition[this.current][I] != -1) {
                    this.current = this.transition[this.current][I];
                    return "W";
                }
            }
        }
        // 可以结束
        if (endState.contains(this.current)) {
            return this.states[this.current];
        }

        // 出错
        System.out.print(i);
        System.out.println("  " +this.current);
        return "U";
    }

    public void reset() {
        this.current = this.start;
    }
}