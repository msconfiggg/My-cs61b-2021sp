package gitlet;

import java.io.File;
import java.io.Serializable;

public class Blob implements Serializable {
    /** The name of the file. */
    private String name;
    /** The content of the file. */
    private byte[] content;

    public Blob(File file) {
        name = file.getName();
        content = Utils.readContents(file);
    }

    public String getName() {
        return name;
    }

    public byte[] getContent() {
        return content;
    }

    public String getHash() {
        return Utils.sha1(Utils.serialize(this));
    }
}
