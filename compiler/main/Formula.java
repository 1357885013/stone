package main;

import java.util.ArrayList;
import java.util.List;

public class Formula {
    private String formulaText;
    private String[] tokens;

    Formula(String wenfa) {
        this.formulaText = wenfa;
        if (!this.judgement()) {
            System.out.println("exit");
        }
    }

    boolean judgement() {
        //判断文法中是不是含有"->"，若没有则报错
        if (!this.formulaText.contains("->")) {
            System.out.println("错误：文法" + formulaText + "中不包含\"->\"");
            return false;
        } else {
            //如果"->"前或后存在空，报错
            if (0 == this.formulaText.split("->")[0].length() || 0 == this.formulaText.split("->")[1].length()) {
                System.out.println("错误：文法"+this.formulaText+"中\"->\"前或后为空");
                return false;
            }
            //存在多个->时报错
            if (this.formulaText.split("->").length > 2) {
                System.out.println("错误：文法"+this.formulaText+"中\"->\"存在大于一个");
                return false;
            }
        }
        return true;
    }

    //把 "|" 分离开
    public static List<Formula> splitFormula(String formula_str) {
        List<Formula> formulas = new ArrayList<>();

       Formula fma = new Formula(formula_str);
        String[] strs = fma.getRight().split("\\|");
        //console.log(strs.length);
        for (String str : strs) {
            String newfam = fma.getLeft() + "->" + str;
            formulas.add(new Formula(newfam));
        }
        return formulas;
    }

    //获取左部
    public String getLeft() {
        return this.formulaText.split("->")[0];
    }

    //获取右部
    public String getRight() {
        return this.formulaText.split("->")[1];
    }

    //重写toString
    public String toString() {
        return this.formulaText;
    }

    //获取右部的候选词数量
    public int getRigthSize() {
        return this.getRight().split(" ").length;
    }

    //通过索引检索右部
    public String getRightIndex(int index) {
        return this.getRight().split(" ")[index];
    }
}
