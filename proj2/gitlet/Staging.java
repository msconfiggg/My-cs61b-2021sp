package gitlet;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;

public class Staging implements Serializable {
    private HashMap<String, String> add;
    private HashSet<String> remove;

    public Staging() {
        add = new HashMap<>();
        remove = new HashSet<>();
    }

    public void clear() {
        add = new HashMap<>();
        remove = new HashSet<>();
    }

    public void add(String fileName, String blobHash) {
        add.put(fileName, blobHash);
    }

    public void remove(String fileName) {
        remove.add(fileName);
    }

    public HashMap<String, String> getAdd() {
        return add;
    }

    public HashSet<String> getRemove() {
        return remove;
    }

    public void unAdd(String filename) {
        add.remove(filename);
    }

    public void unRemove(String filename) {
        remove.remove(filename);
    }
}
