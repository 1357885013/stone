package main.stateMachine;

import java.util.*;

public class Pattern {
    //state change table : state - > chat - > set(state)
    private TransformTable trans;

    private int stateIndex = 0;
    private String regex;

    public static void main(String[] args) {
        Pattern pattern;

//        pattern = Pattern.compile("((//.*?(\\n|$))|(/\\*.*?\\*/))|([a-zA-Z_][a-zA-Z0-9_]*)|(\\d+)|(\"((\\\\\")|.)*?\")|(\\+\\+|--|\\+=|-\\+|\\*=|/=|&&|\\|\\||!=|==|>=|<=)|(\\{|\\}|\\[|\\]|\\(|\\)|\\+|\\-|\\*|/|=|&|\\||!|:|;|,|<|>|'|\\\"|\\.)|(\\b)");
//        pattern = Pattern.compile("(\\+\\+|--|\\+=|-\\+|\\*=|/=|&&|\\|\\||!=|==|>=|<=)|(\\{|\\}|\\[|\\]|\\(|\\)|\\+|\\-|\\*|/|=|&|\\||!|:|;|,|<|>|'|\\\"|\\.)");
//        pattern = Pattern.compile("--|\\+=|-\\+|\\*=|/=|&&|\\|\\||!=|==|>=|<=|\\{|\\}|\\[|\\]|\\(|\\)|\\+|\\-|\\*|/|=|&|\\||!|:|;|,|<|>|'|\\\"|\\.");
//        pattern = Pattern.compile("a|b|c|aA|bB|cC");
//        pattern = Pattern.compile("(\\+\\+|--)|(\\+=|-\\+|\\*=)");
//        pattern = Pattern.compile("([ab][AB]*)");  // end上有自旋
        // todo: 有错误
//        pattern = Pattern.compile("//.*?(\\n|$)");
//        pattern = Pattern.compile("([az][az09]*)|(d+)");
//        pattern = Pattern.compile("b*?ca");
//        pattern = Pattern.compile("ab*?c");
//        pattern = Pattern.compile("ab*(abc)|(ade)");
//        pattern = Pattern.compile("cab*?");
        // todo: 有错误
//        pattern = Pattern.compile("ca(.|a)*");
//        pattern = Pattern.compile("(ca.b)|(.d.s)");
//        pattern = Pattern.compile("(a|b|[ccc][as]|d)");


//        testResolveMBrace();

    }

    public static void print(TransformTable trans, String stage) {
        System.out.println("-----------  " + stage);
        for (State s : trans.keySet()) {
            System.out.println(s);
            for (String s1 : trans.get(s).keySet()) {
                System.out.println("   " + s1 + "     " + Arrays.toString(trans.get(s).get(s1).toArray()));
            }
        }
    }

    public static void print(TransformTable trans) {
        print(trans, "end");
    }

    public Matcher matcher(CharSequence input) {
        Matcher matcher = new Matcher(trans, input);
        return matcher;
    }

    public static Pattern compile(String regex) {
        Pattern pattern = new Pattern();
        pattern.regex = regex;
        pattern.parse(regex);
//        print(pattern.trans, "DFA");
        // 先去空边
        pattern.deleteEmptyInput();
//        print(pattern.trans, "去空边后");
        // 先去空边
        // 顺序处理 . 和 ^[  (所有输入都处理了,再处理自己)
        pattern.NFA2DFA1();
//        print(pattern.trans, ". and [^ 后");
        // 合并同input边, 不用考虑 . 和 ^[
        pattern.NFA2DFA2();
//        print(pattern.trans, "合并状态后");
        return pattern;
    }

    private boolean isIntersect(String left, String right) {
        if (left.equals(right) || left.equals("_.") || right.equals("_.")) return true;
        if (left.charAt(0) == '^' && right.charAt(0) == '^') return false;
        if (left.charAt(0) == '^' && right.length() == 1 && right.charAt(0) == left.charAt(1)) return false;
        if (right.charAt(0) == '^' && left.length() == 1 && left.charAt(0) == right.charAt(1)) return false;
        System.out.println("error in compare for intersect:  " + left + "  " + right);
        return false;
    }

    private void deleteEmptyInput() {
        boolean end;
        do {
            end = true;
            Set<State> transKeys = new HashSet<>(trans.keySet());
            out:
            for (State inState : transKeys) {
                Map<String, Set<State>> inputToStates = trans.get(inState);
                Set<String> inputToStatesKeys = new HashSet<>(inputToStates.keySet());
                for (String input : inputToStatesKeys) {
                    Set<State> toState = inputToStates.get(input);
                    if (input.equals("_@")) {
                        // 把左右两个状态合成个新的状态
                        // todo: 左右groupIndex 合成到一个state里的情况
                        State newState = State.build(stateIndex++, inState, toState);

                        State leftState = inState;
                        Set<State> rightStateSet = toState;

                        // left : inState
                        // right : toState

                        // 左右状态输入
                        Set<State> toStates11;
                        for (State state11 : trans.keySet()) {
                            for (String input11 : trans.get(state11).keySet()) {
                                toStates11 = trans.get(state11).get(input11);
                                // 左状态输入
                                if (toStates11.contains(leftState)) {
                                    trans.add(state11, input11, newState);
                                    trans.delete(state11, input11, leftState);
                                }
                                // 右状态输入
                                if (toStates11.contains(leftState)) {
                                    trans.add(state11, input11, newState);
                                    trans.delete(state11, input11, leftState);
                                }
                            }
                        }

                        // 左状态输出
                        for (String leftInput : trans.get(leftState).keySet()) {
                            Set<State> leftOutStates = trans.get(leftState).get(leftInput);
                            for (State leftOutState : leftOutStates) {
                                // 空自旋不用转移
                                if (!((rightStateSet.contains(leftOutState) || leftOutState.equals(leftState)) && leftInput.equals("_@")))
                                    trans.add(newState, leftInput, leftOutState);
                            }
                        }


                        // 右状态输出
                        for (State rightState : rightStateSet) {
                            if (trans.get(rightState) != null)
                                for (String rightInput : trans.get(rightState).keySet()) {
                                    Set<State> rightOutStateSet = trans.get(rightState).get(rightInput);
                                    if (rightOutStateSet != null)
                                        for (State rightOutState : rightOutStateSet) {
                                            // 空自旋不用转移
                                            if (!((rightStateSet.contains(rightOutState) || rightOutState.equals(leftState)) && rightInput.equals("_@")))
                                                trans.add(newState, rightInput, rightOutState);
                                        }
                                }
                        }

                        // 删除
                        trans.delete(leftState);
                        for (State state : rightStateSet) {
                            trans.delete(state);
                        }
                        end = false;
                        break out;
                    }
//                    print(trans, inState + "  NFA to DFA  " + input);
                }
            }
        } while (!end);
    }

    private void NFA2DFA1() {
        Map<State, HashSet<State>> allInStates = new HashMap<>();
        Map<State, Boolean> resolved = new HashMap<>();
        State nowState = null;
        // 初始化
        for (State state : trans.keySet()) {
            if (state.isStart())
                nowState = state;
            else
                allInStates.put(state, new HashSet<State>());
            resolved.put(state, false);
        }

        // 统计指向某状态的边的数量
        for (State state : trans.keySet()) {
            for (String input : trans.get(state).keySet()) {
                for (State toState : trans.get(state, input)) {
                    HashSet<State> states = allInStates.get(toState);
                    // 结束状态只有输入没有输出
                    if (states != null)
                        states.add(state);
                }
            }
        }

        do {
            if (trans.get(nowState) != null)
                for (String input : trans.get(nowState).keySet()) {
                    // 是欠处理的边不?
                    if (input.equals("_.") || input.charAt(0) == '^') {
                        for (String rightInput : trans.get(nowState).keySet()) {
                            // 如果相交
                            if (isIntersect(input, rightInput))
                                // 全部挨个转移
                                for (State leftState : trans.get(nowState, input)) {
                                    // 是否转移成功
                                    trans.add(nowState, rightInput, leftState);
                                }
                        }
                    }
                }
            resolved.put(nowState, true);
            State temp = null;
            //删除allInStates的同时 寻找下一个所有边都处理的状态
            for (State state : allInStates.keySet()) {
                allInStates.get(state).remove(nowState);
                if (allInStates.get(state).isEmpty() && temp == null)
                    temp = state;
            }
            if (temp != null)
                allInStates.remove(temp);
            nowState = temp;

        } while (nowState != null);
        // 处理失败
        if (resolved.values().contains(false))
            System.out.println("有边没被处理, 在. 和 [^] 阶段");
    }

    private String getKeyOfStates(Set<State> states) {
        return Arrays.toString(states.stream().map(State::getIndex).sorted().toArray(Integer[]::new));
    }

    private void NFA2DFA2() {
        boolean end;
        Map<String, State> cache = new HashMap<>();
        do {
            end = true;
            Set<State> transKeys = new HashSet<>(trans.keySet());
            out:
            for (State inState : transKeys) {
                Map<String, Set<State>> inputToStates = trans.get(inState);
                Set<String> inputToStatesKeys = new HashSet<>(inputToStates.keySet());
                for (String input : inputToStatesKeys) {
                    Set<State> oldStates = inputToStates.get(input);
                    // 如果一个状态一个输入有多个输出
                    if (oldStates.size() > 1) {
                        // 合成个新的状态
                        // todo: 可能已经有相同的集合转过了
                        State newState;
                        String oldStateKey = getKeyOfStates(oldStates);
                        if (cache.containsKey(oldStateKey))
                            newState = cache.get(oldStateKey);
                        else {
                            newState = State.build(stateIndex++, oldStates);
                            cache.put(oldStateKey, newState);
                        }
                        // 将这多个状态替换成新状态
                        trans.delete(inState, input);
                        trans.add(inState, input, newState);

                        // 复制旧状态上的输出到新状态上
                        for (State oldState : new HashSet<>(oldStates)) {
                            if (trans.get(oldState) != null)
                                // 转移输出
                                for (String in : trans.get(oldState).keySet()) {
                                    trans.add(newState, in, trans.get(oldState, in));
                                }
                        }
                        //  不用转移输入,  会死循环, 不删除原状态, 该去原来还去原来
                        // 删除
//                        for (State state : oldStates) {
//                            trans.delete(state);
//                        }
                        end = false;
                        break out;
                    }
//                    print(trans, inState + "  NFA to DFA  " + input);
                }
            }
        } while (!end);
    }

    public void parse(String regex) {
//        System.out.println("------ " + regex);
        // 初始化
        trans = new TransformTable(regex);
        int stateGroupIndex = 1;
        // 循环找能展开的正则分式
        boolean end;
        do {
            end = true;
            Set<State> transKeys = new HashSet<>(trans.keySet());
            for (State inState : transKeys) {
                Map<String, Set<State>> inputToStates = trans.get(inState);
                Set<String> inputToStatesKeys = new HashSet<>(inputToStates.keySet());
                input:
                for (String input : inputToStatesKeys) {
                    Set<State> toState = inputToStates.get(input);
                    String inputO = input;
                    boolean handled = false;
                    if (canSplit(input)) {
                        // []*  |  ()
//                        input = input.trim();
                        Stack<String> stack = new Stack<>();
                        boolean or = false;
                        boolean isMBrace = false;
                        for (int i = 0; i < input.length(); i++) {
                            char c = input.charAt(i);

                            if (c == '(' && ((i > 0 && input.charAt(i - 1) != '\\') || i == 0)) {
                                stack.push("(");
                            } else if (input.charAt(i) == ')' && i > 0 && input.charAt(i - 1) != '\\') {

                                if (stack.size() > 0 && stack.peek().equals("("))
                                    stack.pop();
                                else {
                                    System.out.println(inputO);
                                    System.out.println(input);
                                    System.out.println("第" + (i - 1) + "位这里的右括号没有左括号匹配");
                                    return;
                                }
                                // 去最外层括号 , 同时给这个input的左右状态都加上相同的group编号
                                if (!handled && i == input.length() - 1 && input.charAt(0) == '(') {
                                    trans.add(inState, input.substring(1, input.length() - 1), new HashSet<>(trans.get(inState, inputO)));
                                    inState.addGroupIndex(stateGroupIndex);
                                    for (State state : trans.get(inState, inputO)) {
                                        state.addGroupIndex(stateGroupIndex);
                                    }
                                    stateGroupIndex++;

                                    trans.delete(inState, inputO);
                                    end = false;
                                    continue input;
                                }
                                continue;
                            }
                            // 刚匹配到括号还能进一次循环, 之后就不能了
                            if (!((stack.size() == 0) || (stack.size() == 1 && c == '(' && i != 0))) continue;

                            // [] 内众生平等
                            if (c == '[' && ((i > 0 && input.charAt(i - 1) != '\\') || i == 0)) {
                                isMBrace = true;
                                continue;
                            } else if (isMBrace) {
                                if (input.charAt(i) == ']') {
                                    isMBrace = false;
                                }
                                // 有 [ 没被匹配就直接跳过
                                continue;
                            }
                            // or 结构
                            if (stack.empty() && input.charAt(i) == '|' && i > 0 && input.charAt(i - 1) != '\\') {
                                or = true;
//                                inputToStates.put(input.substring(0, i), new HashSet<>(toState));
                                // 如果toState里有 endState且endState没有任何转换路径
                                for (State state : toState) {
                                    if (state.isEnd() && (trans.get(state) == null || trans.get(state).size() == 0)) {
                                        // 把endState 换成一个新的
                                        trans.add(inState, input.substring(0, i), new State(stateIndex++, state));
                                    } else
                                        // 直接添加
                                        trans.add(inState, input.substring(0, i), state);
                                }
                                input = input.substring(i + 1);
                                i = -1;
                                handled = true;
                            }
                        }
                        // and 结构
                        String inputOO = input;
                        if (!or) {
                            boolean leftBrace = false;
                            stack.clear();
                            Set<Character> endChar = new HashSet<>(Arrays.asList('?', '*', '+', '{'));
                            State leftState = inState;
                            isMBrace = false;
                            for (int i = 0; i < input.length(); i++) {
                                char c = input.charAt(i);
                                if (c == '(' && ((i > 0 && input.charAt(i - 1) != '\\') || i == 0)) {
                                    stack.push("(");
                                } else if (c == ')' && i > 0 && input.charAt(i - 1) != '\\') {
                                    if (stack.peek().equals("("))
                                        stack.pop();
                                    else {
                                        System.out.println(inputO);
                                        System.out.println(input);
                                        System.out.println("第" + (i - 1) + "位这里的右括号没有左括号匹配");
                                        return;
                                    }
                                    // 括号栈为空 , 说明匹配成功
                                    if (stack.size() == 0) {
                                        //  (da){0,3}  这种情况 和 . * +
                                        if (i + 1 < input.length() && endChar.contains(input.charAt(i + 1))) {
                                            int endIndex = getSuffixEndIndex(input, i + 1);
                                            trans.add(leftState, input.substring(0, endIndex + 1), State.build(stateIndex));
                                            leftState = State.build(stateIndex++);
                                            input = input.substring(endIndex + 1);
                                            i = -1;
                                            handled = true;
                                        } else {
                                            // 光秃秃括号情况
                                            trans.add(leftState, input.substring(1, i), State.build(stateIndex));
                                            leftState = State.build(stateIndex++);
                                            input = input.substring(i + 1);
                                            i = -1;
                                            handled = true;
                                        }
                                    }
                                }
                                // 还在括号里就继续
                                if (!((stack.size() == 0) || (stack.size() == 1 && c == '(' && i != 0))) continue;

                                // 中括号
                                if (c == '[' && ((i > 0 && input.charAt(i - 1) != '\\') || i == 0)) {
                                    isMBrace = true;
                                    continue;
                                } else if (isMBrace) {
                                    if (input.charAt(i) == ']') {
                                        isMBrace = false;
                                        // 后面有 限定符
                                        if (i + 1 < input.length() && endChar.contains(input.charAt(i + 1))) {
                                            int endIndex = getSuffixEndIndex(input, i + 1);
                                            // 判断是不是占一整行 , 不是的话进入
                                            if (handled || endIndex != input.length() - 1) {
                                                if (endIndex == input.length() - 1)
                                                    trans.add(leftState, input.substring(0, endIndex + 1), new HashSet<>(inputToStates.get(inputOO)));
                                                else
                                                    trans.add(leftState, input.substring(0, endIndex + 1), State.build(stateIndex));
                                                leftState = State.build(stateIndex++);
                                                input = input.substring(endIndex + 1);
                                                i = -1;
                                                handled = true;
                                            }
                                        }
                                        // 光秃秃括号情况
                                        // 判断是不是占一整行 , 不是的话进入
                                        else if (handled || i != input.length() - 1) {
                                            // 判断是不是结束
                                            if (i == input.length() - 1)
                                                trans.add(leftState, input.substring(0, i + 1), new HashSet<>(inputToStates.get(inputOO)));
                                            else
                                                trans.add(leftState, input.substring(0, i + 1), State.build(stateIndex));
                                            leftState = State.build(stateIndex++);
                                            input = input.substring(i + 1);
                                            i = -1;
                                            handled = true;
                                        }
                                    }
                                    continue;
                                }

                                // 找到了限定符 {} * + .
                                if (endChar.contains(c) && i > 0 && input.charAt(i - 1) != '\\') {
                                    int endIndex = getSuffixEndIndex(input, i);
                                    if (handled || endIndex != input.length() - 1) {
                                        if (endIndex == input.length() - 1)
                                            trans.add(leftState, input.substring(0, endIndex + 1), new HashSet<>(inputToStates.get(inputOO)));
                                        else
                                            trans.add(leftState, input.substring(0, endIndex + 1), State.build(stateIndex));
                                        leftState = State.build(stateIndex++);
                                        input = input.substring(endIndex + 1);
                                        i = -1;
                                        handled = true;
                                    } else {
                                        break;
                                    }
                                }

                                // 单字符 与,
                                // 如果是最后一位  或者是  \.
                                if ((i == 0 && input.length() == 1) || (i == 1 && input.length() == 2 && input.charAt(0) == '\\')) {
                                    trans.add(leftState, input, new HashSet<>(inputToStates.get(inputOO)));
                                    input = "";
                                    i = 0;
                                    handled = true;
                                }
                                // 如果不是最后一位
                                else if ((i > 0) && input.charAt(i - 1) != '\\') {
                                    trans.add(leftState, input.substring(0, i), State.build(stateIndex));
                                    leftState = State.build(stateIndex++);
                                    input = input.substring(i);
                                    i = (input.length() == 1) ? -1 : 0;
                                    handled = true;
                                }
                                // 输出中间步骤
//                                print(trans,input);
                            }
                        } else {
                            // 处理 or 结构的最后一部分

                            // 如果toState里有 endState且endState没有任何转换路径
                            for (State state : toState) {
                                if (state.isEnd() && (trans.get(state) == null || trans.get(state).size() == 0)) {
                                    // 把endState 换成一个新的
                                    trans.add(inState, input, new State(stateIndex++, state));
                                } else
                                    // 直接添加
                                    trans.add(inState, input, state);
                            }
                            // 删除原有的转换路径
                            trans.delete(inState, inputO);
                        }

                        // 不是 or 也 不是 and
                        if (!handled) {
                            // 最外层有一层括号

                            if ((input.endsWith("*") && input.charAt(input.length() - 2) != '\\')) {
                                trans.add(inState, input.substring(0, input.length() - 1), inState);
                                trans.add(inState, "_@", new HashSet<>(trans.get(inState, input)));
                                trans.delete(inState, input);
                                end = false;
                            } else if ((input.endsWith("*?") && input.charAt(input.length() - 3) != '\\')) {
                                trans.add(inState, input.substring(0, input.length() - 2), inState);
                                trans.add(inState, "_@", new HashSet<>(trans.get(inState, input)));
                                trans.delete(inState, input);
                                end = false;
                            } else if ((input.endsWith("?") && input.charAt(input.length() - 2) != '\\')) {
                                trans.add(inState, input.substring(0, input.length() - 1), new HashSet<>(trans.get(inState, input)));
                                trans.add(inState, "_@", new HashSet<>(trans.get(inState, input)));
                                trans.delete(inState, input);
                                end = false;
                            } else if (input.endsWith("+?") && input.charAt(input.length() - 3) != '\\') {
                                trans.add(inState, input.substring(0, input.length() - 2), new HashSet<>(trans.get(inState, input)));
                                // 结束状态 自旋
                                for (State s : trans.get(inState, input))
                                    trans.add(s, input.substring(0, input.length() - 2), s);
                                trans.delete(inState, input);
                                end = false;
                            } else if (input.endsWith("+") && input.charAt(input.length() - 2) != '\\') {
                                trans.add(inState, input.substring(0, input.length() - 1), new HashSet<>(trans.get(inState, input)));
                                // 结束状态 自旋
                                for (State s : trans.get(inState, input))
                                    trans.add(s, input.substring(0, input.length() - 1), s);
                                trans.delete(inState, input);
                                end = false;
                            } else if (input.charAt(0) == '[' && input.charAt(input.length() - 1) == ']') {
                                boolean except = input.charAt(1) == '^';
                                Set<String> each = resolveMBrace(input.substring(except ? 2 : 1, input.length() - 1));
                                for (String s : each) {
                                    trans.add(inState, except ? "^" + s : s, new HashSet<>(trans.get(inState, input)));
                                }
                                trans.delete(inState, input);
                                end = false;
                            }
                        } else {
                            // 删除原来的
                            trans.delete(inState, inputO);
                            end = false;
                        }
                    } else {
                        if (expendInput(inState, input))
                            end = false;
                    }
                }
//                trans.deleteDelayedNow();
            }
        } while (!end);
    }

    private boolean canSplit(String regex) {
        if (regex.length() > 2) return true;
        if (regex.length() == 1) return false;
        return !((regex.charAt(0) == '_' || regex.charAt(0) == '^' || regex.charAt(0) == '\\'));
    }

    private boolean expendInput(State inState, String input) {
        if (input.charAt(0) == '.') {
            trans.add(inState, "_.", trans.get(inState, input));
            trans.delete(inState, input);
            return false;
        }
        if (input.charAt(0) == '\\' && input.length() > 1)
            switch (input.charAt(1)) {
                case 'w':
                    trans.add(inState, "[a-zA-Z0-9]", trans.get(inState, input));
                    trans.delete(inState, input);
                    return true;
                case 'd':
                    trans.add(inState, "[0-9]", trans.get(inState, input));
                    trans.delete(inState, input);
                    return true;
                case 's':
                    trans.add(inState, "[ \r\f\t\n]", trans.get(inState, input));
                    trans.delete(inState, input);
                    return true;
                case 'S':
                    trans.add(inState, "[ \r\f\t]", trans.get(inState, input));
                    trans.delete(inState, input);
                    return true;
                default:
                    trans.add(inState, String.valueOf(input.charAt(1)), trans.get(inState, input));
                    trans.delete(inState, input);
                    return false;
            }
        return false;
    }

    private Set<String> resolveMBrace(String body) {
        Set<String> result = new HashSet<String>();
        for (int i = 0; i < body.length(); i++) {
            if (i < body.length() - 2 && body.charAt(i + 1) == '-') {
                result.addAll(resolveMBraceIn(body.substring(i, i + 3)));
                i += 2;
            }
            result.add(String.valueOf(body.charAt(i)));
        }
        return result;
    }

    private Set<String> resolveMBraceIn(String body) {
        Set<String> result = new HashSet<String>();
        if (body.charAt(1) != '-') {
            System.out.println("a-b 错误");
            return result;
        }
        if (body.charAt(0) >= body.charAt(2))
            return new HashSet<>(Arrays.asList(String.valueOf(body.charAt(0)), String.valueOf(body.charAt(0)), String.valueOf(body.charAt(0))));
        for (int i = body.charAt(0); i <= body.charAt(2); i++) {
            result.add(String.valueOf((char) i));
        }
        return result;
    }

    private int getSuffixEndIndex(String input, int i) {
        if (input.charAt(i) == '{') {
            while (input.charAt(i) != '}') i++;
        }
        if (i + 1 <= input.length() - 1 && input.charAt(i + 1) == '?')
            i++;
        return i;
    }


    private static void testResolveMBrace() {
        Pattern pattern = new Pattern();

        Set<String> strings = pattern.resolveMBrace("a-zA-Z0-9");
        strings.forEach(System.out::println);
        System.out.println("----");
        strings = pattern.resolveMBrace("z-a-0-");
        strings.forEach(System.out::println);
        System.out.println("----");
        strings = pattern.resolveMBrace("-z-a-0-");
        strings.forEach(System.out::println);
        System.out.println("----");
        strings = pattern.resolveMBrace("-a-c-0-");
        strings.forEach(System.out::println);
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }
}
