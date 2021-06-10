package main;


import java.util.*;
import java.util.stream.Collectors;

public class GrammerTree {
    //开始符号
    String startSymbol = "";
    //文法集
    Set<Formula> formulas = new HashSet<>();
    //终结符集
    List<String> terSymbols = new ArrayList<>();
    //非终结符集
    List<String> noSymbols = new ArrayList<>();
    //所有符号集
    List<String> allSymbol = new ArrayList<>();
    //first集的判断
    Map<String, Boolean> judgefirst = new HashMap<>();
    //first集
    Map<String, List<String>> firstSet = new HashMap<>();
    //右部first集
    Map<Formula, List<String>> rightFirst = new HashMap<>();
    //follow集判断
    Map<String, Boolean> judgefollow = new HashMap<>();
    //follow集
    Map<String, List<String>> followSet = new HashMap<>();
    //预测分析表
    Map<String, Formula> M = new HashMap<>();
    //分析结构
    Map<String, Object> result = new HashMap<>();

    // 根据 文法 构造语法树
    GrammerTree(String[] wenfa) {

        //readFormula
        for (int i = 0; i < wenfa.length; i++) {
            if (0 == i) {
                // 分离左右部
                startSymbol = wenfa[0].split("->")[0];
            }
            // 分解 文法 右部的或部分
            if (wenfa[i].contains("|")) {
                //添加复杂产生式
                formulas.addAll(Formula.splitFormula(wenfa[i]));
            } else {
                formulas.add(new Formula(wenfa[i]));
            }
        }
        init();
    }


    //初始化集合
    private void init() {
        //查找并记录 所有 非终结符noSymbols ( 文法中左部的集合 )
        for (Formula formula : formulas) {
            noSymbols.add(formula.getLeft());
        }

        //查找 所有 终结符terSymbols ( 非终结符 的 补集 )
        for (Formula formula : formulas) {
            for (int j = 0; j < formula.getRigthSize(); j++) {
                if (!noSymbols.contains(formula.getRightIndex(j))) {
                    terSymbols.add(formula.getRightIndex(j));
                }
            }
        }
        //将结束标记放入终结符
        terSymbols.add("#");

        allSymbol.addAll(terSymbols);
        allSymbol.addAll(noSymbols);

        //初始化FIRST集和FOLLOW集(仅初始化集合的key:符号)
        for (String key : allSymbol) {
            firstSet.put(key, null);
            judgefirst.put(key, false);
            // 如果是非终结符
            if (noSymbols.contains(key)) {
                followSet.put(key, null);
                judgefollow.put(key, false);
            }
        }
        // 去掉文法中的循环, 例如: A -> Ab
        // 当前行的右部只包含排序在下面的非终结符,不包含排序在上面的非终极符 和 本身
        // todo:

        //初始化每个符号的FIRST集
        for (String key : allSymbol) {
            if (!judgefirst.get(key)) {
                //求出并保存 FIRST集
                firstSet.put(key, getFirst(key));
            }
        }

        //初始化每个非终结符号的follow集
        for (String sym : noSymbols) {
            if (!judgefollow.get(sym)) {
                followSet.put(sym, getFollow(sym));
                judgefollow.put(sym, true);
            }
        }
        //初始化每个formula的follow集
        for (Formula f : formulas) {
            rightFirst.put(f, getFormulaFirst(f));
        }
        //初始化预测分析表
        initM();
    }

    //获取first集
    List getFirst(String key) {
        List<String> first = new ArrayList<>();
        List<String> tempHash = new ArrayList<>();
        // 如果是终结符
        if (terSymbols.contains(key)) {
            // 模拟 set 的 add 方法
            first.add(key);
            return first;
        }

        for (Formula formula : formulas) {
            if (formula.getLeft().equals(key)) {
                // 如果右部第一个是终极符就加入到 first集里
                if (terSymbols.contains(formula.getRightIndex(0))) {
                    first.add(formula.getRightIndex(0));
                    continue;
                }
                // 如果是空就加入 first集
                if (formula.getRight().equals("@")) {
                    first.add("@");
                    continue;
                }

                int index = 1; //用来判断是不是所有公式都包含@空的情况

                for (int j = 0; j < formula.getRigthSize(); j++) {
                    String symbol = formula.getRightIndex(j);

                    // symbol是否已求过first集
                    if (!judgefirst.get(symbol)) {
                        // 递归
                        firstSet.put(symbol, getFirst(symbol));
                        judgefirst.put(symbol, true);
                    }

                    // 把symbol的fist集并上
                    tempHash.addAll(firstSet.get(symbol));

                    // 删除 tempHash 里 为 空 的 项
                    int indexofkong = tempHash.indexOf("@");
                    while (indexofkong != -1) {
                        tempHash.remove(indexofkong);
                        indexofkong = tempHash.indexOf("@");
                    }

                    first.addAll(tempHash);

                    // 如果 右部的第一个非终极符有可能为空, 那么第二个终极符的first集也需要并上
                    if (firstSet.get(symbol).contains("@")) {
                        index++;
                    } else {
                        break;
                    }
                }
                // 如果右部的全部非终结符都有可能为空, 那么first集里也有空
                if (index == formula.getRigthSize()) {
                    first.add("@");
                }
            }
        }
        return first.stream().distinct().collect(Collectors.toList());
    }

    List getFollow(String key) {
        //定义用来返回的follow集
        List<String> follow = new ArrayList<>();
        List<String> tempHash = new ArrayList<>();

        //把空字符#放入开始符号的FOLLOW集中
        if (startSymbol.equals(key)) {
            follow.add("#");
        }
        String right, temps;
        //遍历所有文法
        for (Formula formula : formulas) {
            //获得右部
            right = formula.getRight();
            //判断右部是否包含key
            if (right.contains(key)) {
                for (int j = 0; j < formula.getRigthSize(); j++) {
                    temps = formula.getRightIndex(j);
                    // 在右部中找到 当前符号及位置
                    if (temps.equals(key)) {
                        // 如果是在最后一位 , 那么 key 的 follow == 当前文法左部的follow
                        if (j == formula.getRigthSize() - 1) {
                            if (formula.getLeft().equals(key)) {
                                break;
                            }
                            // 最后一位 递归求follow集
                            if (!judgefollow.get(formula.getLeft())) {
                                followSet.put(formula.getLeft(), getFollow(formula.getLeft()));
                                judgefollow.put(formula.getLeft(), true);
                            }
                            //addall
                            follow.addAll(followSet.get(formula.getLeft()));
                        } else {
                            // 最后一位的 first集
                            List<String> tmp_firstset = firstSet.get(formula.getRightIndex(formula.getRigthSize() - 1));
                            // 如果key的位置是倒数第二位 且 最后一位的first集里包含空
                            // todo: first 里包含空又不是整个为空, 空后面跟什么都没有意义, 所以有空就是空
                            if ((j == formula.getRigthSize() - 2) && (tmp_firstset.contains("@"))) {
                                // if (formula.getLeft() === key) break;
                                // 求出左部的 follow集
                                if (!judgefollow.get(formula.getLeft())) {
                                    followSet.put(formula.getLeft(), getFollow(formula.getLeft()));
                                    judgefollow.put(formula.getLeft(), true);
                                }
                                //add all
                                follow.addAll(followSet.get(formula.getLeft()));
                            }
                            // 如果 下一位 是终极符
                            if (terSymbols.contains(formula.getRightIndex(j + 1))) {
                                follow.add(formula.getRightIndex(j + 1));
                            } else {
                                // 下一位的first集
                                List<String> first_temp = firstSet.get(formula.getRightIndex(j + 1));
                                //add all
                                follow.addAll(first_temp.stream().filter(a -> !a.equals("@")).collect(Collectors.toList()));
                            }
                        }
                    }
                }
            }
        }
        return follow.stream().distinct().collect(Collectors.toList());
    }

    //获取一个firmula的first集
    List getFormulaFirst(Formula formula) {
        //定义用来返回的FIRST集
        List<String> firsts = new ArrayList<>();
        List temp = new ArrayList();
        boolean bool = false;
        int flag = 0;
        if (formula.getRight().equals("@")) {
            firsts.add("@");
            return firsts;
        }
        for (int i = 0; i < formula.getRigthSize(); i++) {
            List<String> firstSet_temp = firstSet.get(formula.getRightIndex(i));
            firsts.addAll(firstSet_temp.stream().filter(a -> !a.equals("@")).collect(Collectors.toList()));
            flag++;
            // 找不到空 就退出
            if (firsts.size() == firstSet_temp.size()) {
                break;
            }
        }
        if (flag == formula.getRigthSize() && flag > 1) {
            firsts.add("@");
        }
        return firsts.stream().distinct().collect(Collectors.toList());
    }

    //生成预测分析表
    void initM() {
        String key;
        for (Formula formula : formulas) {
            for (String terSym : terSymbols) {
                if (terSym.equals("@")) continue;
                // key 是 文法左部 + 终结符
                key = formula.getLeft() + terSym;
                if (rightFirst.get(formula).contains(terSym)) {
                    M.put(key, formula);
                }
            }
            // 如果文法 的first集包含空, follow集也添加到预测表里
            if (rightFirst.get(formula).contains("@")) {
                for (String ter : terSymbols) {
                    if (ter.equals("@")) continue;
                    key = formula.getLeft() + ter;
                    if (followSet.get(formula.getLeft()).contains(ter)) {
                        M.put(key, formula);
                    }
                }
            }
        }
    }

    //开始分析
    boolean startAnalyse(List<String[]> source,ParseTree p) {
        if (source.size() == 0) return true;
        Stack stack = new Stack();
        List keys = new ArrayList();
        keys.addAll(M.keySet());

        String X;
        int index = 0, row = 1, col = 1;
        stack.push("#");
        stack.push(startSymbol);
//        result = {name: startSymbol, children: [], open: true};
        boolean flag = true;
        boolean exit = false;
        Formula f;
        while (flag) {
            // 去除行首空白
            if (source.get(index)[0].equals("SPACE")) {
                index++;
                if (index < source.size()) continue;
                else break;
            } else if (source.get(index)[0].equals("ENTER")) {
                index++;
                row++;
                col = 1;
                if (index < source.size()) continue;
                else break;
            }
            // 取出一个符号
            X = (String) stack.pop();

            // 如果是终结符
            if (terSymbols.contains(X)) {
                // 如果类型相等, 则匹配成功
                if (X.equals(source.get(index)[0])) {
                    p.name = "<" + source.get(index)[0] + "," + source.get(index)[1] + ">";
                    p.open = false;

                    // 获取树的下一个待匹配节点
                    while (!p.open) {
                        if (p.parent != null) {
                            // 获取父亲的下一个孩子 如果有
                            int rindex = p.parent.children.indexOf(p);
                            if (rindex != p.parent.children.size() - 1) p = p.parent.children.get(rindex + 1);
                            else {
                                p = p.parent;
                                p.open = false;
                            }
                        } else break;
                    }
                    index++;
                    col++;
                    if (index == source.size()) break;
                } else {
                    System.out.println("error   " + "文法解析器报错" + "Error at Line " + ((col == 1) ? row - 1 : row) + "Require " + X + " But " + source.get(index)[1] + source.get(index)[0]);
                    return false;
                }
            } else if (X.equals("#")) {
                if (X.equals(source.get(index)[0])) {
                    flag = false;
                } else {
                    System.out.println("error   " + "文法解析器报错" + "Error at Line " + ((col == 1) ? row - 1 : row) + "Not Expected Stopping");
                    return false;
                }
            } else {
                if (M.get(X + source.get(index)[0])!=null) {
                    f = M.get(X + source.get(index)[0]);
                    if (f.getRight().equals("@")) {
                        p.open = false;
                        while (!p.open) {
                            if (p.parent!=null) {
                                int rindex = p.parent.children.indexOf(p);
                                if (rindex != p.parent.children.size() - 1) p = p.parent.children.get(rindex + 1);
                                else {
                                    p = p.parent;
                                    p.open = false;
                                }
                            } else break;
                        }
                        continue;
                    }
                    for (int j = f.getRigthSize() - 1; j >= 0; j--) {
                        stack.push(f.getRightIndex(j));
                        // 添加 分析树 节点
                        p.children.add(new ParseTree(p,f.getRightIndex(f.getRigthSize() - 1 - j),null,true));
                    }
                    if (p.children.size() != 0)
                        p = p.children.get(0);
                } else {
                    System.out.println("error   " + "文法解析器报错" + "Error at Line " + ((col == 1) ? row - 1 : row) + "Not Expected "+source.get(index)[1]);
                    return false;
                }
            }
        }
        System.out.println("success!");
        return true;
    }
}