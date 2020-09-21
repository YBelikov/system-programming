package com.ybelikov;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.logging.Logger;


public class TextProccessor {

    private String pathToTextFile;
    private Map<String, Integer> listOfSetOfCharactersInEveryWord;
    private static final Logger logger = Logger.getLogger(TextProccessor.class.getName());
    private Integer max;
    private final Integer MAXLENGTHOFWORD = 30;

    public TextProccessor(String pathToTextFile) {
        this.max = 0;
        this.pathToTextFile = pathToTextFile;
        this.listOfSetOfCharactersInEveryWord = new HashMap<>();
    }

    public void countWordsWithMaxDifferentLetters() {

        try {
            File file = new File(pathToTextFile);
            Scanner sc = new Scanner(file, "UTF-8");
            while (sc.hasNext()) {
                var line = sc.nextLine();
                String[] words = line.split("[^a-zA-zа-яА-яёЁЇїІіЄєґҐ]+");
                findMaxNumberOfDifferentLetters(words);
            }
            sc.close();
        } catch (FileNotFoundException ex) {
            logger.info("Can't find the requested file!");
        } catch (Exception ex) {
            logger.info(ex.getMessage());
        } 
    }

    public void printWords() {
        for (Map.Entry<String, Integer> entry : listOfSetOfCharactersInEveryWord.entrySet()) {
            if(entry.getValue() == this.max) {
                System.out.println(entry.getKey());
            }
        }
    }

    private void findMaxNumberOfDifferentLetters(String[] words) {
        for (var word : words) {
            if (word.length() > MAXLENGTHOFWORD) {
                word = word.substring(0, MAXLENGTHOFWORD - 1);
            }
            String notSorted = String.valueOf(word);
            var chars = word.toCharArray();

            Arrays.sort(chars);
            word = new String(chars);
            int diffCharCounter = 1;
            word = word.toLowerCase();
            for (int i = 1; i < word.length(); ++i) {
                if (word.charAt(i - 1) != word.charAt(i)) {
                    ++diffCharCounter;
                }
            }
            notSorted = notSorted.toLowerCase();
            listOfSetOfCharactersInEveryWord.put(notSorted, diffCharCounter);
            if (diffCharCounter > this.max) {
                this.max = diffCharCounter;
            }
        }
    }
}
