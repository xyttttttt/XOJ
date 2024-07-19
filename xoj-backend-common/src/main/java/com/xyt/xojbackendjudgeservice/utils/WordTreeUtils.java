package com.xyt.xojbackendjudgeservice.utils;

import cn.hutool.dfa.WordTree;

public class WordTreeUtils {
    private static WordTree wordTree = new WordTree();

    public static void init() {
        wordTree.addWords("你妈");
        wordTree.addWords("妈");
        wordTree.addWords("逼");
        wordTree.addWords("爹");
        wordTree.addWords("母亲");
        wordTree.addWords("fuck");
        wordTree.addWords("屎");
        wordTree.addWords("贱逼");
        wordTree.addWords("脑残");
        wordTree.addWords("傻");
        wordTree.addWords("操");
        wordTree.addWords("草");
        wordTree.addWords("艹");
        wordTree.addWords("父亲");
        wordTree.addWords("垃圾");
        wordTree.addWords("滚蛋");
        wordTree.addWords("脑子");
        wordTree.addWords("鸡");
        wordTree.addWords("生殖器");
        wordTree.addWords("奶");
        wordTree.addWords("骚");
        wordTree.addWords("死人");
    }

    public static boolean isMatchInText(String text){
        boolean match = wordTree.isMatch(text);
        return  match;
    }
}
