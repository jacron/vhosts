package nl.jcroonen.vhosts.model;

import java.util.Dictionary;

public class VHost {
    private final Dictionary<String, String> dictionary;

    public Dictionary<String, String> getDictionary() {
        return dictionary;
    }

    public VHost(Dictionary<String, String> dictionary) {
        this.dictionary = dictionary;
    }
}
