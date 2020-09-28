package com.ybelikov;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        DFA automata = new DFA();
        //System.out.println("Type a path to file with automata representation");
        //Scanner in = new Scanner(System.in);
        //String path = in.next();
        automata.readFromFile("D:/system_programming/dfa-minimization/src/com/ybelikov/dfa.txt");
        System.out.println("Before minimization:");
        automata.printInitialTransitions();
        automata.minimize();
        System.out.println("After minimization:");
        automata.printMinimizedTransitions();
        automata.printTransitionTable();

    }
}
