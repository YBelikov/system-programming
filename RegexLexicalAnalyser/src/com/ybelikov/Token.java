package com.ybelikov;

public class Token {

    public Token(LexemType type, String token) {
        this.type = type;
        this.token = token;
    }

    public LexemType type() {
        return this.type;
    }

    @Override
    public String toString() {
        return type.toString() + "\t\t\t\t" + token + "\n";
    }


    private LexemType type;
    private String token;
}
