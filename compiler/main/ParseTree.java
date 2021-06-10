package main;

import java.util.ArrayList;
import java.util.List;

public class ParseTree {
    public String name;
    public ParseTree parent = null;
    public List<ParseTree> children = new ArrayList<>();
    public boolean open = true;

    public ParseTree(ParseTree parent, String name, List<ParseTree> children, boolean open) {
        this.name = name;
        this.parent = parent;
        if (children != null)
            this.children = children;
        this.open = open;
    }

    public ParseTree() {
    }
    public String toString() {
        return name+"<"+children.size()+">";
    }
    public String toStringT(int deep){
        String str = ""+this.name+"\n";
        if (this.children==null || this.children.size() < 1){
            return str;
        }
        StringBuilder deepPrefix = new StringBuilder();
        for (int i = 0; i < deep; i++) {
            deepPrefix.append("___ ");
        }
        for (ParseTree child : this.children) {
            str = str + deepPrefix + "|___ " + child.toStringT(deep + 1);
        }
        return str;
    }
    public String toStringH(){
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("label:'").append(this.name).append("',");

        if (this.children!=null && this.children.size() > 0) {
            sb.append("children:").append("[");
            for (ParseTree child : this.children) {
                sb.append(child.toStringH()).append(",");
            }
            sb.append("]");
        }
        sb.append("}");
        return sb.toString();
    }

}
