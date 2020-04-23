package alpt;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;

public class VocabularyManager {
    private HashMap<String, String> vocabulary = new HashMap<>();
    public VocabularyManager(File from) throws FileNotFoundException {
        try (Scanner scanner = new Scanner(from)) {
            while (scanner.hasNextLine()) {
                String[] s = scanner.nextLine().split("\t", 2);
                if (s.length < 2 || s[0].startsWith("#")) continue;
                vocabulary.put(s[0], s[1]);
            }
        }
    }

    public int size(){
        return vocabulary.size();
    }

    public Set<String> getWords(){
        return vocabulary.keySet();
    }

    public String getMeaning(String word){
        return vocabulary.get(word);
    }
}
