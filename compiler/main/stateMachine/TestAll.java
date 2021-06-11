package main.stateMachine;

public class TestAll {

    public static void main(String[] args) {
        Pattern pattern;
        pattern = Pattern.compile("abc");
        test1(pattern,
                new String[]{"abc", "abc|"},
                new String[]{"abcabc", "abc|abc|"},
                new String[]{"", ""},
                new String[]{"ab",""});
        pattern = Pattern.compile(".*");
        test1(pattern,
                new String[]{"abc", "abc|"},
                new String[]{"abcabc", "abcabc|"},
                new String[]{"", ""},
                new String[]{"a\nb","a\nb"});

    }

    private static void test1(Pattern pattern, String[]... inputs) {
        System.out.println(pattern.getRegex());
        if (inputs != null)
            for (String[] input : inputs) {
                String result = test(pattern, input[0]);
                if (result.equals(input[1])) {
                    System.out.println("  success    " + input[0]);
                } else {
                    System.out.println("  error    " + input[0]);
                    System.out.println("          expect" + input[1]);
                    System.out.println("          actual" + result);
                }
            }
    }

    private static String test(Pattern pattern, String input) {
        Matcher matcher = pattern.matcher(input);
        StringBuilder sb = new StringBuilder();
        int pos = 0;
        int endPos = input.length() - 1;
        while (pos < endPos) {      //不是一次把一行里的所有结果都匹配出来，而是一个一个的匹配
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
