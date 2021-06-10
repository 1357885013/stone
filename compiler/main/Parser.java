package main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Parser {
    private static StateTransform tagStateTransform;
    private static List<String[]> wordResult;
    private static GrammerTree grammerTree;

    // 启动函数  main
    public static void main(String[] args) {
        String source = "public int test(boolean a,int b=1){" +
                "if(a=false)" +
                "b++;" +
                "else" +
                "b--;" +
                "}";
        source = "public int test ( boolean a , int b = 1 ) {" +
                "if(a = false)" +
                "b++;" +
                "else" +
                "b--;" +
                "}";
        source = "public int test ( int a , int b ) {" +
                "b=b+1;" +
                "}";
        parseCi(source);
    }

    public static void parseCi(String source) {
        // 词法分析 的 状态机
        tagStateTransform = new StateTransform(Option.token, "DFA");
        wordResult = new ArrayList<>();

        for (int i = 0, j = 0; i < source.length(); i++) {
            String r = tagStateTransform.transform(String.valueOf(source.charAt(i)));
            if (r.equals("W")) continue;
            else if (r.equals("U")) {
                String value = source.substring(j, i);
                System.out.println("词法解析器报错: " + value);
            } else {
                String value = source.substring(j, i);
                j = i--;

                if (r.equals("id") && Option.keywords.contains(value))
                    r = value;
                wordResult.add(new String[]{r, value});
                tagStateTransform.reset();
            }
        }
        System.out.println("词法分析结果");
        for (String[] strings : wordResult) {
            if (!strings[0].equals("SPACE"))
                System.out.print(Arrays.toString(strings) + "   ");
        }
        System.out.println();
        System.out.println("文法分析结果");

        ParseTree p = new ParseTree();
        parseWen(p);

        // 打印js树,在网页里能看
        System.out.println(p.toStringH());
        System.out.println();
        // 打印文本树
//        System.out.println(p.toStringT(0));

    }

    static void parseWen(ParseTree p) {
        grammerTree = new GrammerTree(Option.wenfa);
        grammerTree.startAnalyse(Parser.wordResult, p);

    }

//    static void  printFirst() {
//        let result = [];
//        for (let i in this.first) {
//            let e = [];
//            e[0] = i;
//            e[1] = '(';
//            for (let j of this.first[i]) {
//                e[1] += j + ',';
//            }
//            e[1] += ')';
//            result.push(e);
//        }
//    }
//
//    static printFollow() {
//        let result = [];
//        for (let i in this.follow) {
//            let e = [];
//            e[0] = i;
//            e[1] = '(';
//            for (let j of this.follow[i]) {
//                e[1] += j + ',';
//            }
//            e[1] += ')';
//            result.push(e);
//        }
//    }
//
//    static printPredictMap() {
//        let result = [];
//        for (let i in this.predictMap) {
//            let e = [];
//            e[0] = i;
//            e[1] = this.predictMap[i].formula;
//            result.push(e);
//        }
//    }
//
//    static printGrammerResult() {
//    }
//

}



