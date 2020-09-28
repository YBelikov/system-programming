package com.ybelikov;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.logging.Logger;

enum Color {
    BLACK,
    WHITE
};


public class DFA {
    private static final Logger logger = Logger.getLogger(DFA.class.getName());
    private List<List<String>> equivalenceClasses;
    private List<String> alphabet;
    private List<String> states;
    private Integer initial;
    private List<String> finals;
    private ArrayList<String> reachableStates;
    private HashMap<Integer, String[]> transitionsTable;
    private HashMap<Integer, String[]> transitionsTableAfterMinimization;
    private Color[] marked;


    public void readFromFile(String path) {
        try (Scanner sc = new Scanner(new File(path))) {
            this.alphabet = Arrays.asList(sc.next().split(","));
            this.states = Arrays.asList(sc.next().split(","));
            this.initial = sc.nextInt();
            this.finals = Arrays.asList(sc.next().split(","));
            this.marked = new Color[states.size()];
            Arrays.fill(marked, Color.WHITE);
            this.equivalenceClasses = new ArrayList<>();
            this.reachableStates = new ArrayList<>();
            readTransitions(sc);
        } catch (FileNotFoundException ex) {
            logger.info("Can't find requested file!");
        } catch(Exception ex) {
            logger.info("Something completely went wrong!");
        }
    }

    public void minimize() {
        findNonReachableStates(initial);
        HashMap<Integer, String[]> finalStatesTransitionTable = getTransitionsForFinalStates();
        HashMap<Integer, String[]> nonFinalStatesTransitionTable = getTransitionsForNonFinalStates();
        finalStatesTransitionTable = removeNonReachableStates(finalStatesTransitionTable);
        nonFinalStatesTransitionTable = removeNonReachableStates(nonFinalStatesTransitionTable);
        finalStatesTransitionTable = eliminateSimilarRows(finalStatesTransitionTable);
        nonFinalStatesTransitionTable = eliminateSimilarRows(nonFinalStatesTransitionTable);
        this.transitionsTableAfterMinimization = uniteTables(finalStatesTransitionTable, nonFinalStatesTransitionTable);
    }

    private void readTransitions(Scanner sc) {
        fillTransitionTable();
        while (sc.hasNext()) {
            String transition = sc.next();
            int from = transition.charAt(0) - '0';
            int edge = transition.charAt(1) - 'a';
            String to = transition.substring(2);
            if (this.transitionsTable.get(from)[edge].equals("$")) {
                this.transitionsTable.get(from)[edge] = to;
            } else if (this.transitionsTable.get(from)[edge].length() >= 1) {
                this.transitionsTable.get(from)[edge] = transitionsTable.get(from)[edge] + to;
            }
        }



    }

    private void fillTransitionTable() {
        this.transitionsTable = new HashMap<>();
        for (var state : states) {
            String[] to = new String[alphabet.size()];
            Arrays.fill(to, "$");
            this.transitionsTable.put(state.charAt(0) - '0', to);
        }
    }

    public void printTransitionTable() {
        printTable(this.transitionsTableAfterMinimization);
    }

    private void findNonReachableStates(Integer currentState) {
        marked[currentState] = Color.BLACK;
        var adj = transitionsTable.get(currentState);
        for (int i = 0; i < adj.length; ++i) {
            if (adj[i].equals("$")) {
                continue;
            }
            var nextStates = adj[i];
            for (int j = 0; j < nextStates.length(); ++j) {
                var ch = nextStates.charAt(j);
                int next = ch - '0';
                if (marked[next] != Color.BLACK) {
                    findNonReachableStates(next);
                }
            }
        }
    }

    private HashMap<Integer, String[]> removeNonReachableStates(HashMap<Integer, String[]> transitions) {

        for (int i = 0; i < marked.length; ++i) {
            if (marked[i] == Color.WHITE) {
                transitions.remove(i);
            } else {
                if (!reachableStates.contains(String.valueOf(i))) {
                    reachableStates.add(String.valueOf(i));
                }
             }
        }
        return transitions;
    }

    private HashMap<Integer, String[]> getTransitionsForFinalStates() {
        HashMap<Integer, String[]> finalTransitions = new HashMap<>();
        for (var state : finals) {
            finalTransitions.put(state.charAt(0) - '0', transitionsTable.get(state.charAt(0) - '0').clone());
        }
        return finalTransitions;
    }

    private HashMap<Integer, String[]> getTransitionsForNonFinalStates() {
        HashMap<Integer, String[]> nonFinalTransitions = new HashMap<>();
        var allStates = new HashSet<>(states);
        var finalStates = new HashSet<>(finals);
        allStates.removeAll(finalStates);
        for (var state : allStates) {
            nonFinalTransitions.put(state.charAt(0) - '0', transitionsTable.get(state.charAt(0) - '0').clone());
        }
        return nonFinalTransitions;
    }

    private HashMap<Integer, String[]> eliminateSimilarRows(HashMap<Integer, String[]> table) {
        var states = table.keySet().toArray();
        for (int i = 0; i < states.length - 1; ++i ) {
            List<String> eqClass = new ArrayList<>();
            for (int j = i + 1; j < states.length; ++j ) {
                var tr1 = table.get(states[i]);
                var tr2 = table.get(states[j]);
                if(Arrays.equals(tr1, tr2)) {
                    eqClass.add(states[i].toString() + "-" + states[j].toString() + " ");
                    table.remove(states[i]);
                    if (finals.contains(states[i].toString())) {
                        for (int k = 0; k < tr2.length; ++k) {
                            tr2[k] = states[j].toString();
                        }
                    }
                }
            }
            if (!eqClass.isEmpty()) {
                equivalenceClasses.add(eqClass);
            }
        }
        return table;
    }

    private HashMap<Integer, String[]> uniteTables(HashMap<Integer, String[]> first, HashMap<Integer, String[]> second) {
        HashMap<Integer, String[]> unionTable = new HashMap<>();
        for(var row : first.entrySet()) {
            unionTable.put(row.getKey(), row.getValue());
        }
        for(var row : second.entrySet()) {
            unionTable.put(row.getKey(), row.getValue());
        }
        return unionTable;
    }

    private void printTable(HashMap<Integer, String[]> table) {
       for(var row : table.entrySet()) {
           var stringArray = row.getValue();
           for(int i = 0; i < stringArray.length; ++i) {
               if (stringArray[i].equals("$")) {
                  continue;
               }
               var characters = stringArray[i].toCharArray();
               for(int k = 0; k < characters.length; ++k) {
                   char ch = (char) ('a' + i);
                   String transition = String.format("%d%c%d", row.getKey(), ch, characters[k] - '0');
                   System.out.println(transition);
               }
           }
       }
       System.out.println("Equivalence classes: ");
       for (var eqClass : equivalenceClasses) {
           for (var str : eqClass) {
               System.out.println(eqClass);
           }
       }
    }

    public void printInitialTransitions() {
        System.out.print("  ");
        for (var letter : alphabet) {
            System.out.print(letter + " ");
        }
        System.out.println();
        for (var row : transitionsTable.entrySet()) {
            System.out.print(row.getKey() + " ");
            var trans = row.getValue();
            for (var tr : trans) {
                System.out.print(tr + " ");
            }
            System.out.println();
        }
    }

    public void printMinimizedTransitions() {
        System.out.print("  ");
        for (var letter : alphabet) {
            System.out.print(letter + " ");
        }
        System.out.println();
        for (var row : transitionsTableAfterMinimization.entrySet()) {
            System.out.print(row.getKey() + " ");
            var trans = row.getValue();
            for (var tr : trans) {
                System.out.print(tr + " ");
            }
            System.out.println();
        }
    }
}