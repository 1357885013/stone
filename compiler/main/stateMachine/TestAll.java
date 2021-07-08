package main.stateMachine;

public class TestAll {
    private static int allCount = 0;
    private static int successCount = 0;
    private static int failureCount = 0;

    public static void main(String[] args) {
        Pattern pattern;
        pattern = Pattern.compile("abc");
        testEach(pattern,
                new String[]{"abc", "abc|"},
                new String[]{"abcabc", "abc|abc|"},
                new String[]{"", ""},
                new String[]{"ab", ""}
        );

        pattern = Pattern.compile("a|b|c");
        testEach(pattern,
                new String[]{"abc", "a|b|c|"},
                new String[]{"abda", "a|b|"},
                new String[]{"", ""},
                new String[]{"d", ""},
                new String[]{"a", "a|"}
        );

        pattern = Pattern.compile("a*");
        testEach(pattern,
                new String[]{"aaa", "aaa|"},
                new String[]{"aaab", "aaa|"},
                new String[]{"bbbbbb", ""},
                new String[]{"", ""},
                new String[]{"a\nb", "a|"}
        );

        pattern = Pattern.compile(".*");
        testEach(pattern,
                new String[]{"abc", "abc|"},
                new String[]{"abcabc", "abcabc|"},
                new String[]{"", ""},
                new String[]{"a\nb", "a\nb|"}
        );

        pattern = Pattern.compile(".bc|def");
        testEach(pattern,
                new String[]{"dbc", "dbc|"},
                new String[]{"abc", "abc|"},
                new String[]{"cbc", "cbc|"},
                new String[]{"def", "def|"},
                new String[]{"", ""},
                new String[]{"a\nb", ""}
        );

        pattern = Pattern.compile("a+");
        testEach(pattern,
                new String[]{"aaa", "aaa|"},
                new String[]{"aa", "aa|"},
                new String[]{"a", "a|"},
                new String[]{"bc", ""},
                new String[]{"", ""}
        );

        pattern = Pattern.compile(".+");
        testEach(pattern,
                new String[]{"abc", "abc|"},
                new String[]{"abcabc", "abcabc|"},
                new String[]{"", ""},
                new String[]{"a\nb", "a\nb|"}
        );

        pattern = Pattern.compile(".+bc");
        testEach(pattern,
                new String[]{"abc", "abc|"},
                new String[]{"abcabc", "abcabc|"},
                new String[]{"bc", ""},
                new String[]{"asfds", ""},
                new String[]{"a\nbc", "a\nbc|"}
        );

        pattern = Pattern.compile(".*bc");
        testEach(pattern,
                new String[]{"abc", "abc|"},
                new String[]{"abcabc", "abcabc|"},
                new String[]{"bc", "bc|"},
                new String[]{"asfds", ""},
                new String[]{"a\nbc", "a\nbc|"}
        );

        pattern = Pattern.compile("[abc]");
        testEach(pattern,
                new String[]{"abc", "a|b|c|"},
                new String[]{"abda", "a|b|"},
                new String[]{"", ""},
                new String[]{"d", ""},
                new String[]{"a", "a|"}
        );

        pattern = Pattern.compile("[^abc]");
        testEach(pattern,
                new String[]{"a", ""},
                new String[]{"b", ""},
                new String[]{"c", ""},
                new String[]{"d", "d|"},
                new String[]{"\n", "\n|"},
                new String[]{"f", "f|"}
        );

        pattern = Pattern.compile("([^abc]1)|([^ade]2)|([^fg]3)|([^ghijklmn]4)");
        testEach(pattern,
                new String[]{"a1", ""},
                new String[]{"b1", ""},
                new String[]{"c1", ""},
                new String[]{"d1", "d1|"},
                new String[]{"e1", "e1|"},
                new String[]{"f1", "f1|"},
                new String[]{"g1", "g1|"},
                new String[]{"h1", "h1|"},
                new String[]{"i1", "i1|"},
                new String[]{"j1", "j1|"},
                new String[]{"k1", "k1|"},
                new String[]{"l1", "l1|"},
                new String[]{"m1", "m1|"},
                new String[]{"n1", "n1|"},

                new String[]{"a1", ""},
                new String[]{"b2", "b2|"},
                new String[]{"c2", "c2|"},
                new String[]{"d2", ""},
                new String[]{"e2", ""},
                new String[]{"f2", "f2|"},
                new String[]{"g2", "g2|"},
                new String[]{"h2", "h2|"},
                new String[]{"i2", "i2|"},
                new String[]{"j2", "j2|"},
                new String[]{"k2", "k2|"},
                new String[]{"l2", "l2|"},
                new String[]{"m2", "m2|"},
                new String[]{"n2", "n2|"},

                new String[]{"a3", "a3|"},
                new String[]{"b3", "b3|"},
                new String[]{"c3", "c3|"},
                new String[]{"d3", "d3|"},
                new String[]{"e3", "e3|"},
                new String[]{"f3", ""},
                new String[]{"g3", ""},
                new String[]{"h3", "h3|"},
                new String[]{"i3", "i3|"},
                new String[]{"j3", "j3|"},
                new String[]{"k3", "k3|"},
                new String[]{"l3", "l3|"},
                new String[]{"m3", "m3|"},
                new String[]{"n3", "n3|"},

                new String[]{"a4", "a4|"},
                new String[]{"b4", "b4|"},
                new String[]{"c4", "c4|"},
                new String[]{"d4", "d4|"},
                new String[]{"e4", "e4|"},
                new String[]{"f4", "f4|"},
                new String[]{"g4", ""},
                new String[]{"h4", ""},
                new String[]{"i4", ""},
                new String[]{"j4", ""},
                new String[]{"k4", ""},
                new String[]{"l4", ""},
                new String[]{"m4", ""},
                new String[]{"n4", ""}

        );
        pattern = Pattern.compile("((//.*?(\\n|$))|(/\\*.*?\\*/))|([a-zA-Z_][a-zA-Z0-9_]*)|(\\d+)|(\"((\\\\\")|.)*?\")|(\\+\\+|--|\\+=|-\\+|\\*=|/=|&&|\\|\\||!=|==|>=|<=)|(\\{|\\}|\\[|\\]|\\(|\\)|\\+|\\-|\\*|/|=|&|\\||!|:|;|,|<|>|'|\\\"|\\.)|(\\b)");
        testEach(pattern,
                new String[]{"public int a=10", "public| |int| |a|=|10|"},
                new String[]{"    private static void testEach(Pattern pattern, String[]... inputs) {\n" +
                        "        System.out.println(\"--------------------------------\");\n" +
                        "        System.out.println(\"\\033[30;46;1m\" + pattern.getRegex() + \"\\033[0m\");\n" +
                        "        if (inputs != null)\n" +
                        "            for (String[] input : inputs) {\n" +
                        "                allCount++;\n" +
                        "                String result = search(pattern, input[0]);\n" +
                        "                if (result.equals(input[1])) {\n" +
                        "                    System.out.println(\"\\033[32;2m  success    \" + input[0].replace(\"\\n\", \"\\\\n\").replace(\"\\t\", \"\\\\t\") + \"\\033[0m\");\n" +
                        "                    successCount++;\n" +
                        "                } else {\n" +
                        "                    System.out.println(\"\\033[31;2m  error    \" + input[0].replace(\"\\n\", \"\\\\n\").replace(\"\\t\", \"\\\\t\") + \"\\033[0m\");\n" +
                        "                    failureCount++;\n" +
                        "                }\n" +
                        "            }\n" +
                        "    }", "++|"}
        );

        // todo: {0,4}  // 要加上匹配次数么, 匹配几次进入下一状态
        // : [^ ]
        // todo: $ ^
        // todo: 贪婪模式
        // todo: 反向预查

        System.out.println();
        System.out.println();
//        System.out.println("==================================================");
        System.out.print("\033[32;2m  " + successCount + "\033[0m");
        System.out.print("/");
        System.out.print("\033[37;2m" + allCount + "\033[0m");

        System.out.println("      \033[31;2m" + failureCount + "\033[0m");
    }

    private static void testEach(Pattern pattern, String[]... inputs) {
        System.out.println("--------------------------------");
        System.out.println("\033[30;46;1m" + pattern.getRegex() + "\033[0m");
        if (inputs != null)
            for (String[] input : inputs) {
                allCount++;
                String result = search(pattern, input[0]);
                if (result.equals(input[1])) {
                    System.out.println("\033[32;2m  success    " + input[0].replace("\n", "\\n").replace("\t", "\\t") + "\033[0m");
                    successCount++;
                } else {
                    System.out.println("\033[31;2m  error    " + input[0].replace("\n", "\\n").replace("\t", "\\t") + "\033[0m");
                    System.out.println("          expect : " + input[1].replace("\n", "\\n").replace("\t", "\\t"));
                    System.out.println("          actual : " + result.replace("\n", "\\n").replace("\t", "\\t"));
                    failureCount++;
                }
            }
    }

    private static String search(Pattern pattern, String input) {
        Matcher matcher = pattern.matcher(input);
        StringBuilder sb = new StringBuilder();
        int pos = 0;
        int endPos = input.length() - 1;
        while (pos <= endPos) {      //不是一次把一行里的所有结果都匹配出来，而是一个一个的匹配
            matcher.region(pos, endPos);  //核心语句, 设置搜索区域
            if (matcher.lookingAt()) { // 从区域的头部开始查找匹配,不用匹配全部
                // 识别到了
                if (matcher.group(0) != null)
                    if (!matcher.group(0).equals(" "))
                        sb.append(matcher.group(0)).append("|");
                pos = matcher.end() + 1;
            } else
                break;
//                System.out.println("bad token at line ");
        }
        return sb.toString();
    }

}
