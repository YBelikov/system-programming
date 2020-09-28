import java.util.List;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.logging.Logger;

public class DFA {
    
    private static final Logger logger = Logger.getLogger(DFA.class.getName());
    private List<String> alphabet;
    private List<String> states;
    private String initialState;
    private List<String> finalStates;
    private String[][] transitions;
    
    public void readFromFile(String path) {
        try(Scanner sc = new Scanner(new File(path))) {
            
        } catch(FileNotFoundException ex) {
            logger.info("Can't open requested file!");
        }
    }

    public DFA() {

    }   
}
