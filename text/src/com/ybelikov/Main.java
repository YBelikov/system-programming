package com.ybelikov;

import java.util.Scanner;


public class Main {

    public static void main(String[] args) {
	Scanner sc = new Scanner(System.in);
	
	String filePath = sc.nextLine();

        TextProccessor processor = new TextProccessor(filePath);
        System.out.println(processor.countWordsWithMaxDifferentLetters());
    }

}
