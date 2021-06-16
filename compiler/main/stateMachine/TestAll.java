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
                    sb.append(matcher.group(0)).append("|");
                pos = matcher.end() + 1;
            } else
                break;
//                System.out.println("bad token at line ");
        }
        return sb.toString();
    }

}
