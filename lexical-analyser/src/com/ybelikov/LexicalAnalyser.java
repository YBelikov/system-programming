package com.ybelikov;

import java.awt.image.ImagingOpException;
import java.io.*;
import java.util.*;
import java.util.logging.Logger;

public class LexicalAnalyser {
    private static final Logger logger = Logger.getLogger(LexicalAnalyser.class.getName());
    private static final List<String> keywords = Collections.unmodifiableList(Arrays.asList("break", "default", "func", "interface", "select", "case", "defer", "go", "map", "struct", "chan", "else", "goto",
        "package", "switch", "const", "fallthrough", "if", "range", "type", "continue", "for", "import", "return", "var"));

    private static final List<String> types = Collections.unmodifiableList(Arrays.asList("int", "const", "string", "float", "bool"));

    private String line;
    private static Integer lineNumber;
    private static boolean inDocumentComment;
    private Integer currentIndex;
    private char peek;

    public LexicalAnalyser() {
        this.peek = ' ';
        lineNumber = 0;
        inDocumentComment = false;
    }

    public void setLine(String lineOfCode) {
        this.currentIndex = 0;
        this.line = lineOfCode;
        this.peek = ' ';
        line += "\n";
        ++lineNumber;
    }

    public Token scan() throws LexicalError {
        if (inDocumentComment) {
          return processDocumentComment();
        }
        for (; ; getNextChar()) {
            if (this.peek == ' ' || this.peek == '\t')  continue;
            else if (this.peek == '\n')  {
                ++lineNumber;
            }
            else break;
        }
        Token token;

        switch (this.peek) {
           case '&' :
            if (getNextChar('&')) {
                token = new Token(LexemType.AND, "&&");
                move();
                return token;
            }
            throw new LexicalError(lineNumber.toString(), "Can't recognize indentifier");
           case '|':
               if (getNextChar('|')) {
                   token = new Token(LexemType.OR, "||");
                   move();
                   return token;
               }
               throw new LexicalError(lineNumber.toString(), "Can't recognize indentifier");
           case '{':
               token = new Token(LexemType.OPEN_CURLY_BRACE, peek);
               move();
               return token;
           case '}':
               token = new Token(LexemType.CLOSE_CURLY_BRACE, peek);
               move();
               return token;
           case '[':
               token = new Token(LexemType.OPEN_SQUARE_BRACE, peek);
               move();
               return token;
           case ']':
               token = new Token(LexemType.CLOSE_SQUARE_BRACE, peek);
               move();
               return token;
           case '(':
               token = new Token(LexemType.OPEN_PARENTHESIS, peek);
               move();
               return token;
           case ')':
               token = new Token(LexemType.CLOSE_PARENTHESIS, peek);
               move();
               return token;
           case '<':
               if (getNextChar('=')) {
                   token = new Token(LexemType.LESS_OR_EQUAL, "<=");
                   move();
               } else {
                   token = new Token(LexemType.LESS, '<');
                   move();
               }
               return token;
           case '>':
               if (getNextChar('=')) {
                   token = new Token(LexemType.GREATER_OR_EQUAL, ">=");
                   move();
               } else {
                   token = new Token(LexemType.GREATER, '>');
                   move();
               }
               return token;
           case '=':
               if (getNextChar('=')) {
                   token = new Token(LexemType.EQUAL, "==");
                   move();
               } else {
                   token = new Token(LexemType.ASSIGNMENT, '=');
                   move();
               }
               return token;
           case ':':
               if (getNextChar('=')) {
                   token = new Token(LexemType.ASSIGNMENT_WITH_DEDUCTION, ":=");
                   move();
               } else {
                   token = new Token(LexemType.COLON, ':');
                   move();
               }
               return token;
           case ';':
               token  = new Token(LexemType.SEMICOLON, peek);
               move();
               return token;
           case ',':
               token = new Token(LexemType.COMA, peek);
               move();
               return token;
           case '.':
               token = new Token(LexemType.DOT, peek);
               move();
               return token;
           case '+':
               token = new Token(LexemType.PLUS, peek);
               move();
               return token;
           case '-':
               token = new Token(LexemType.MINUS, peek);
               move();
               return token;
           case '*':
               token = new Token(LexemType.MULTIPLICATION, peek);
               move();
               return token;
            case '/':
               if (getNextChar('/')) {
                   token = new Token(LexemType.ONE_LINE_COMMENT, "//");
                   currentIndex = line.length() + 1;
               } else if(this.peek == '*') {
                   inDocumentComment = true;
                   token = processDocumentComment();
               } else {
                   token = new Token(LexemType.DIVISION, '/');
               }
               return token;
            case '\"':
                token = processStringLiteral();
                move();
                return token;
            case '!':
                if (getNextChar('=')) {
                    token = new Token(LexemType.NOT_EQUAL, "!=");
                    move();
                } else {
                    token = new Token(LexemType.NOT, '!');
                    move();
                }
                return token;
       }
        if (Character.isDigit(peek)) {
           return processNumber();
        }
        if (Character.isLetter(peek)) {
            return processWord();
        }
        return processError("");
    }

    private Token processError(String initialError) {
        String error = initialError;
        while(!Character.isWhitespace(peek) || peek != '\n') {
            error += peek;
            getNextChar();
        }
        return new Token(LexemType.LEXICAL_ERROR, error);
    }

    private void getNextChar() {
        if (currentIndex < line.length()) {
            this.peek = line.charAt(currentIndex);
        }
        ++currentIndex;
    }

    private boolean getNextChar(char c) {
        getNextChar();
        return this.peek == c;
    }

    private Token processNumber() throws LexicalError {
        Integer integerLiteral = 0;
        String hexOrByLiteral = new String();
        do {
            if (peek == 'x' || peek == 'b') {
                hexOrByLiteral = hexOrByLiteral + integerLiteral.toString();
                do {
                    hexOrByLiteral += String.valueOf(peek);
                    getNextChar();
                } while (Character.isLetterOrDigit(peek));
                return new Token(LexemType.INTEGER_LITERAL, hexOrByLiteral);
            } else if ((peek != 'x' | peek != 'b') && Character.isLetter(peek)) {
                return processError(integerLiteral.toString());
            }
                integerLiteral = 10 * integerLiteral + Character.digit(peek, 10);
                getNextChar();
        } while (Character.isLetterOrDigit(peek));
        if (peek != '.') return new Token(LexemType.INTEGER_LITERAL, integerLiteral);
        Double floatingPointLiteral = integerLiteral.doubleValue();
        Integer divisor = 10;
        getNextChar();
        while (true) {
            if(!Character.isDigit(peek)) break;
            floatingPointLiteral += Double.valueOf(String.valueOf(peek)) / divisor;
            divisor *= 10;
            getNextChar();
        }
        return new Token(LexemType.FLOATING_POINT_LITERAL, floatingPointLiteral);
    }

    private Token processWord() {
        StringBuilder word = new StringBuilder();
        do {
            word.append(peek);
            getNextChar();
        } while (Character.isLetterOrDigit(peek));
        if (keywords.contains(word.toString())) {
            return new Token(LexemType.KEYWORD, word.toString());
        } else if (types.contains(word.toString())) {
            return new Token(LexemType.DATA_TYPE, word.toString());
        } else {
            return  new Token(LexemType.IDENTIFIER, word.toString());
        }
   }

 public boolean reachEndOfTheLine() {
        return currentIndex >= line.length();
   }

   private void move() {
       getNextChar();
   }

   private Token processStringLiteral() {
        StringBuilder literal = new StringBuilder();
        literal.append('\"');
        do {
            getNextChar();
            literal.append(this.peek);
        } while (Character.isLetterOrDigit(this.peek));
        getNextChar();
        return new Token(LexemType.STRING_LITERAL, literal.toString());
   }

   private Token processDocumentComment() {
        line += "\n";
        StringBuilder builder = new StringBuilder();
        do {
           getNextChar();
           builder.append(this.peek);
           if (this.peek == '*' && getNextChar('/')) {
               inDocumentComment = false;
               break;
           }
        } while (this.peek != '\n');
        currentIndex = line.length() + 1;
        return new Token(LexemType.DOCUMENT_COMMENT, builder.toString());
   }

}
