package com.ybelikov;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum LexemType {
    IDENTIFIER("[A-Za-z]([A-Za-z]|[0-9])*"),
    OR("\\|\\|"),
    AND("&&"),
    LESS("<"),
    OPEN_PARENTHESIS("\\("),
    CLOSE_PARENTHESIS("\\)"),
    OPEN_CURLY_BRACE("\\{"),
    CLOSING_CURLY_BRACE("\\}"),
    OPEN_SQUARE_BRACE("\\["),
    CLOSE_SQUARE_BRACE("\\]"),
    LESS_OR_EQUAL("<="),
    GREATER(">"),
    GREATER_OR_EQUAL(">="),
    NOT_EQUAL("!="),
    EQUAL("=="),
    ASSIGNMENT("="),
    ASSIGNMENT_WITH_DEDUCTION(":="),
    FLOATING_POINT_NUMBER("[0-9]*\\.[0-9]*"),
    INCREMENT("\\+\\+"),
    DECREMENT("--"),
    PLUS("\\+"),
    MINUS("-"),
    MULTIPLY("\\*"),
    STRING_LITERAL("([\"'`])(.*?)([\"'`])"),
    ONE_LINE_COMMENT("\\/\\/.*"),
    DOCUMENT_COMMENT("/(.*?)\\*\\/"),
    DIVIDE("/"),
    COLON(":"),
    DOT("\\."),
    COMA(","),
    SEMICOLON(";"),

    INTEGER("\\d"),
    PREDECLARED_IDENTIFIER,
    ERROR,
    KEY_WORD;

    private final Pattern pattern;
    LexemType(String s) {
        this.pattern = Pattern.compile("^" + s);
    }
    LexemType() {
        this.pattern = Pattern.compile(" ");
    }

    int match(String s) {
        Matcher matcher = pattern.matcher(s);
        if (matcher.find()) {
            return matcher.end();
        }
        return -1;
    }

}
