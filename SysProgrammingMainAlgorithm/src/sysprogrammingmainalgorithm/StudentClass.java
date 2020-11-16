package sysprogrammingmainalgorithm;
import JavaTeacherLib.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

public class StudentClass extends MyLang {
    private LinkedList<Node> language;


    public StudentClass(String fileLang, int llk1) {
        super(fileLang, llk1);
        language = super.getLanguarge();
    }

    public void strongLL1() {
        this.printTerminals();
        this.createNonProdRools();
        this.createNonDosNeterminals();
        var epsilon = this.createEpsilonNonterminals();
        this.setEpsilonNonterminals(epsilon);
        var ffirstContext = this.firstK();
        this.setFirstK(ffirstContext);
        LlkContext [] followContext = this.followK();
        this.setFollowK(followContext);
        this.firstFollowK();
        boolean isStrong = true;
        for (int i = 0; i < language.size(); ++i) {
            Node current = language.get(i);
            int[] rule = current.getRoole();
            LlkContext context = current.getFirstFollowK();
            Node current1 = null;
            int[] rule1;
            int j = 0;
            boolean executeNext = true;
            do {

                if (j == language.size()) {
                    executeNext = false;
                    break;
                }
                current1 = language.get(j);
                if (current == current1) {
                    executeNext = false;
                    break;
                }
                rule1 = current1.getRoole();
                ++j;
            } while(rule1[0] != rule[0]);
            if (executeNext && current1 != null) {
               LlkContext context1 = current1.getFirstFollowK();
               for (int k = 0; k < context.calcWords(); ++k) {
                   if (context1.wordInContext(context.getWord(k))) {
                       isStrong = false;
                       break;
                   }
               }
            }
        }
        if (isStrong) {
            System.out.println("Граматика є LL(1)");
        } else {
            System.out.println("Граматика не є LL(1)");
        }
    }
}
   