package main.stateMachine;

import stone.Token;

import java.util.Arrays;

public class Test {
    public static String regexPat = "((//.*?(\\n|$))|(/\\*.*?\\*/))|([a-zA-Z_][a-zA-Z0-9_]*)|" +
            "(\\d+)|(\"((\\\\\")|.)*?\")|" +
            "(\\+\\+|--|\\+=|-\\+|\\*=|/=|&&|\\|\\||!=|==|>=|<=)|" +
            "(\\{|\\}|\\[|\\]|\\(|\\)|\\+|\\-|\\*|/|=|&|\\||!|:|;|,|<|>|'|\\\"|\\.)|(\\s)";    //匹配结果会存进group里，并用标号区分，（1（2）（3 num）（（4 string））token ）  具体在addToken()里
    private static Pattern pattern = Pattern.compile(regexPat);

    public static void main(String[] args) {
        String input = "+= ++ -- = * 1000 sadf123 /*123123*/";
        Matcher matcher = pattern.matcher(input);
//        matcher.useTransparentBounds(true).useAnchoringBounds(false);
        int pos = 0;
        int endPos = input.length()-1;
        while (pos < endPos) {      //不是一次把一行里的所有结果都匹配出来，而是一个一个的匹配
            matcher.region(pos, endPos);  //核心语句, 设置搜索区域
            if (matcher.lookingAt()) { // 从区域的头部开始查找匹配,不用匹配全部
                // 识别到了
                if (matcher.group(0) != null)
                    System.out.println(matcher.group(0));
                pos = matcher.end()+1;
            } else
                System.out.println("bad token at line ");
        }
    }

    // 把正则匹配到的 token 添加到queue
    protected String[] getToken(int lineNo, Matcher matcher) {
        String m = matcher.group(1);
        if (m != null) // if not a space
            if (matcher.group(2) == null) { // if not a comment
                Token token;
                // 区分 识别到的是什么token
                if (matcher.group(3) != null)
                    return new String[]{"id", matcher.group(3)};
                else if (matcher.group(4) != null)
                    return new String[]{"id", matcher.group(3)};
                else
                    return new String[]{"id", matcher.group(3)};
            }
        return null;
    }
}
