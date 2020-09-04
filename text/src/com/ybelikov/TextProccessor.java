package com.ybelikov;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.logging.Logger;

import java.util.stream.Collectors;


public class TextProccessor {

    private String pathToTextFile;
    private List<Set<Character>> listOfSetOfCharactersInEveryWord;
    private static final Logger logger = Logger.getLogger(TextProccessor.class.getName());

    public TextProccessor(String pathToTextFile) {
        this.pathToTextFile = pathToTextFile;
    }

    public int countWordsWithMaxDifferentLetters() {
        int maxNumberOfDifferentLetters = 0;
        try {
            File file = new File(pathToTextFile);
            Scanner sc = new Scanner(file);
            while (sc.hasNext()) {
                var line = sc.nextLine();
                String[] words = line.split("[!._,'@;? ]");
                Integer currentMaxNumberOfDifferentLetters = findMaxNumberOfDifferentLetters(words);
                if (currentMaxNumberOfDifferentLetters > maxNumberOfDifferentLetters) {
                    maxNumberOfDifferentLetters = currentMaxNumberOfDifferentLetters;
                }
            }
            sc.close();
        } catch (FileNotFoundException ex) {
            logger.info("Can't find the requested file!");
        } catch (Exception ex) {
            logger.info("Something went wrong!");
        }   finally {
            return makeCalc(maxNumberOfDifferentLetters);
        }
    }

    private int makeCalc(Integer maxNumberOfDifferentLetters) {
        var listWithMaxLetters = listOfSetOfCharactersInEveryWord
                                    .stream()
                                    .filter(s -> s.size() == maxNumberOfDifferentLetters)
                                    .collect(Collectors.toList());
        return listWithMaxLetters.size();
    }

    private Integer findMaxNumberOfDifferentLetters(String[] words) {

        var resizedWords = trimWords(words);
        listOfSetOfCharactersInEveryWord = Arrays.stream(resizedWords)
                            .map(word -> word.chars()
                            .mapToObj(ch -> (char)ch)
                            .collect(Collectors.toSet()))
                            .collect(Collectors.toList());
        return listOfSetOfCharactersInEveryWord.stream().mapToInt(set -> set.size()).max().getAsInt();
    }

    private StringBuilder[] trimWords(String[] words) {
        StringBuilder[] resizedWords = new StringBuilder[words.length];
        for (int i = 0; i < words.length; ++i) {
            resizedWords[i] = new StringBuilder(words[i]);
            if(resizedWords[i].length() > 30) resizedWords[i].delete(29, words[i].length() - 1);
        }
        return resizedWords;
    }
}
