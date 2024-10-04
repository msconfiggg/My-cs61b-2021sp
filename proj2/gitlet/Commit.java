package gitlet;



import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

/** Represents a gitlet commit object.
 *
 *  does at a high level.
 *
 *  @author msconfig
 */
public class Commit implements Serializable {
    /**
     *
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */
    private String message;
    /** The date of this Commit. */
    private Date timeStamp;
    /** The parent of this Commit. */
    private Commit parent;
    /** The merge parent of this Commit. */
    private Commit mergeParent;
    /** The blobs of this Commit. */
    private HashMap<String, String> blobs;


    public Commit(String message, Commit parent) {
        this.message = message;
        if (message.equals("initial commit")) {
            this.timeStamp = new Date(0);
        } else {
            this.timeStamp = new Date();
        }

        this.parent = parent;
        this.blobs = new HashMap<>();
    }

    /*用于merge*/
    public Commit(String message, Commit parent, Commit mergeParent) {
        this.message = message;
        if (message.equals("initial commit")) {
            this.timeStamp = new Date(0);
        } else {
            this.timeStamp = new Date();
        }

        this.parent = parent;
        this.parent = mergeParent;
        this.blobs = new HashMap<>();
    }

    public String getMessage() {
        return message;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public String getHash() {
        return Utils.sha1(Utils.serialize(this));
    }

    public Commit getParent() {
        return parent;
    }

    public Commit getMergeParent() {
        return mergeParent;
    }

    public HashMap<String, String> getBlobs() {
        return blobs;
    }
}
