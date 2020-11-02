package com.ybelikov;

import java.io.*;

public class Main {

    public static void main(String[] args) {
       try{
            LexicalAnalyzer analyzer = new LexicalAnalyzer("./sample.txt");
            FileWriter writer = new FileWriter("./output.txt");
            while (!analyzer.end()) {
                var token = analyzer.token();
                if (!token.type().equals(LexemType.ONE_LINE_COMMENT) && !token.type().equals(LexemType.DOCUMENT_COMMENT)) {
                    writer.write(token.toString());
                }
                analyzer.analyze();
            }
            writer.close();
            if (analyzer.success()) {
                System.out.println("OK!");
            } else {
                System.out.println(analyzer.getErrorMessage());
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
