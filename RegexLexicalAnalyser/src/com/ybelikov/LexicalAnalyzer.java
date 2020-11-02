package com.ybelikov;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LexicalAnalyzer {
    private final StringBuilder inputText = new StringBuilder();
    private final Set<Character> whitespaceCharacters = new HashSet<>();
    private Pattern keyWordPattern;
    private Pattern indentifierPattern;
    private Matcher keyWordMatcher;
    private Matcher indentifierMatcher;
    private String errorMessage = "";
    private Token token;
    private boolean end = false;

    LexicalAnalyzer(String path) {
        keyWordPattern = Pattern.compile("var|func|interface|select|case|defer|go|map|struct|chan|else|goto|package|const|switch" +
                        "|const|fallthrough|if|range|type|continue|for|import|return");
        indentifierPattern = Pattern.compile("append|bool|cap|close|complex|complex64|complex128|uint16|copy|false|float32|" +
                        "float64|imag|int|int8|int16|uint32|int32|int64|iota|len|make|new|nil|panic|uint64|print|println|real|recover|" +
                        "string|true|uint|uint8|uintptr");
        readFile(path);
        createWhitespaceSet();
        analyze();

    }

    private void readFile(String path) {
        try (Scanner sc = new Scanner(new File(path))) {
            while (sc.hasNext()) {
                inputText.append(sc.next()).append("\n");

            }
        } catch (FileNotFoundException ex) {
            this.end = true;
            this.errorMessage = ex.getMessage();
        }
    }

    private void createWhitespaceSet() {
        this.whitespaceCharacters.add('\r');
        this.whitespaceCharacters.add('\n');
        this.whitespaceCharacters.add((char)8);
        this.whitespaceCharacters.add((char)9);
        this.whitespaceCharacters.add((char)11);
        this.whitespaceCharacters.add((char)12);
        this.whitespaceCharacters.add((char)32);
    }


    public void analyze() {
        if (end) {
            return;
        }
        if (inputText.length() == 0) {
            end = true;
            return;
        }
        removeWhitespaces();
        if (recognizeToken()) {
            return;
        }
        //end = true;
        if (inputText.length() > 0) {
            processError();
            //errorMessage = "Unexpected symbol: " + inputText.charAt(0);
        }
    }

    private void processError() {
        int count = 0;
        String errorTokenString = new String();
        do {
            errorTokenString += inputText.charAt(count);
            ++count;
        } while (!Character.isWhitespace(inputText.charAt(count)));
        token = new Token(LexemType.ERROR, errorTokenString);
        inputText.delete(0, count);
    }
    private void removeWhitespaces() {
        int count = 0;
        while (whitespaceCharacters.contains(inputText.charAt(count))) {
            ++count;
            if (inputText.length() == 1) {
                break;
            }
        }
        if (count > 0) {
            inputText.delete(0, count);
        }
    }

    private boolean recognizeToken() {
        for (var lexem : LexemType.values()) {
            int match = lexem.match(inputText.toString());
            if (match != -1) {
                var tokenString = inputText.substring(0, match);
                if (lexem == LexemType.IDENTIFIER) {
                    keyWordMatcher = keyWordPattern.matcher(tokenString);
                    if (keyWordMatcher.matches()) lexem = LexemType.KEY_WORD;
                    indentifierMatcher = indentifierPattern.matcher(tokenString);
                    if (indentifierMatcher.matches()) lexem = LexemType.PREDECLARED_IDENTIFIER;
                }
                token = new Token(lexem, tokenString);
                inputText.delete(0, match);
                return true;
            }
        }
        return false;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public boolean success() {
        return errorMessage.isEmpty();
    }

    public Token token() {
        return token;
    }

    public boolean end() {
        return end;
    }

}
