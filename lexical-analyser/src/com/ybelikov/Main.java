package com.ybelikov;

import com.sun.source.tree.LambdaExpressionTree;

import java.io.*;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        String path = "D:/system_programming/lexical-analyser/sample.txt";
        String filePath = "D:/system_programming/lexical-analyser/output.txt";
        try (Scanner sc = new Scanner(new File(path)); FileWriter output = new FileWriter(new File(filePath))) {
            LexicalAnalyser analyser = new LexicalAnalyser();
            while (sc.hasNext()) {
                var line = sc.nextLine();
                if (line.length() == 0) continue;
                analyser.setLine(line);
                while (!analyser.reachEndOfTheLine()) {
                    var token = analyser.scan();
                    if (token.type == LexemType.ONE_LINE_COMMENT || token.type == LexemType.DOCUMENT_COMMENT) {
                        continue;
                    }
                    output.write(token.toString());
                    output.write("\n");
                }
            }
        } catch (FileNotFoundException ex) {
            System.out.println("Requested file not found");
        } catch (LexicalError ex) {
            System.out.println(ex.info());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
