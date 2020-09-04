package com.ybelikov;



public class Main {

    public static void main(String[] args) {
        TextProccessor processor = new TextProccessor(args[0]);
        System.out.println(processor.countWordsWithMaxDifferentLetters());
    }
}
