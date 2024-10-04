package gitlet;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;



/** Represents a gitlet repository.
 *
 *  does at a high level.
 *
 *  @author TODO
 */
public class Repository {
    /**
     *
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    private File CWD;
    /** The .gitlet directory. */
    private File GITLET;
    /** The blobs directory. */
    private File BLOBS;
    /** The branches directory. */
    private File BRANCHES;

    /** The commits file. */
    private File COMMITS;
    /** The staging area file. */
    private File STAGING;
    /** The master file. */
    private File MASTER;
    /** The head file. */
    private File HEAD;

    private Staging staging;
    private TreeMap<String, Commit> commits;


    public Repository() {
        /* directories */
        CWD = new File(System.getProperty("user.dir"));
        GITLET = Utils.join(CWD, ".gitlet");
        BLOBS = Utils.join(GITLET, "blobs");
        BRANCHES = Utils.join(GITLET, "branches");

        /* files */
        COMMITS = Utils.join(GITLET, "commits");
        STAGING = Utils.join(GITLET, "staging.txt");
        MASTER = Utils.join(BRANCHES, "master");
        HEAD = Utils.join(GITLET, "HEAD");
        staging = new Staging();
        commits = new TreeMap<>();
    }

    public void init() {
        if (Utils.join(System.getProperty("user.dir"), ".gitlet").exists()) {
            throw new GitletException("A Gitlet version-control system"
                    + "already exists in the current directory.");
        }

        GITLET.mkdir();
        BLOBS.mkdir();
        BRANCHES.mkdir();

        Utils.writeObject(STAGING, staging);
        Commit initCommit = new Commit("initial commit", null);
        commits.put(initCommit.getHash(), initCommit);
        Utils.writeObject(COMMITS, commits);
        Utils.writeContents(MASTER, initCommit.getHash());
        Utils.writeContents(HEAD, "master");
    }

    public void add(String fileName) {
        if (!Utils.join(CWD, fileName).exists()) {
            throw new GitletException("File does not exist.");
        }

        File file = Utils.join(CWD, fileName);
        Blob blob = new Blob(file);
        Commit headCommit = getHead();
        HashMap<String, String> headCommitBlobs = headCommit.getBlobs();
        File blobFile = Utils.join(BLOBS, blob.getHash());
        Utils.writeContents(blobFile, blob.getContent());

        /*如果待删除区存在改文件，则将其移除*/
        staging = getStaging();
        if (staging.getRemove().contains(blob.getName())) {
            staging.unRemove(blob.getName());
            Utils.writeObject(STAGING, staging);
        }

        /*如果文件版本和当前提交版本一致，则将其移出暂存区（如果暂存区内存在），并退出*/
        if (headCommitBlobs.containsKey(blob.getName())
                && headCommitBlobs.get(blob.getName()).equals(blob.getHash())) {
            staging = getStaging();
            staging.unAdd(blob.getName());
            Utils.writeObject(STAGING, staging);
            return;
        }

        /*添加到暂存区*/
        staging = getStaging();
        staging.add(blob.getName(), blob.getHash());
        Utils.writeObject(STAGING, staging);
    }

    public void commit(String commitMessage) {
        if (commitMessage.isBlank()) {
            throw new GitletException("Please enter a commit message.");
        }

        staging = getStaging();
        if (staging.getAdd().isEmpty() && staging.getRemove().isEmpty()) {
            throw new GitletException("No changes added to the commit.");
        }

        Commit headCommit = getHead();
        Commit commit = new Commit(commitMessage, headCommit);
        HashMap<String, String> blobs = commit.getBlobs();

        /*如果当前提交中存在某文件，而在暂存区不存在，并且也不位于待删除区，则直接将当前提交中版本的该文件添加到新提交中*/
        for (String key: headCommit.getBlobs().keySet()) {
            if (!staging.getAdd().containsKey(key) && !staging.getRemove().contains(key)) {
                blobs.put(key, headCommit.getBlobs().get(key));
            }
        }

        /*将暂存区的文件添加到新提交中*/
        for (String key: staging.getAdd().keySet()) {
            blobs.put(key, staging.getAdd().get(key));
        }

        staging.clear();
        Utils.writeObject(STAGING, staging);
        commits = getCommits();
        commits.put(commit.getHash(), commit);
        Utils.writeObject(COMMITS, commits);
        updateHeadBranch(commit);
    }

    /*用于merge*/
    public void commit(String commitMessage, Commit mergeParent) {
        if (commitMessage.isBlank()) {
            throw new GitletException("Please enter a commit message.");
        }

        staging = getStaging();
        if (staging.getAdd().isEmpty() && staging.getRemove().isEmpty()) {
            throw new GitletException("No changes added to the commit.");
        }

        Commit headCommit = getHead();
        Commit commit = new Commit(commitMessage, headCommit, mergeParent);
        HashMap<String, String> blobs = commit.getBlobs();

        /*如果当前提交中存在某文件，而在暂存区不存在，并且也不位于待删除区，则直接将当前提交中版本的该文件添加到新提交中*/
        for (String key: headCommit.getBlobs().keySet()) {
            if (!staging.getAdd().containsKey(key) && !staging.getRemove().contains(key)) {
                blobs.put(key, headCommit.getBlobs().get(key));
            }
        }

        /*将暂存区的文件添加到新提交中*/
        for (String key: staging.getAdd().keySet()) {
            blobs.put(key, staging.getAdd().get(key));
        }

        staging.clear();
        Utils.writeObject(STAGING, staging);
        commits = getCommits();
        commits.put(commit.getHash(), commit);
        Utils.writeObject(COMMITS, commits);
        updateHeadBranch(commit);
    }

    public void rm(String fileName) {
        staging = getStaging();
        Commit headCommit = getHead();
        if (!staging.getAdd().containsKey(fileName)
                && !headCommit.getBlobs().containsKey(fileName)) {
            throw new GitletException("No reason to remove the file.");
        }

        staging.unAdd(fileName);
        if (headCommit.getBlobs().containsKey(fileName)) {
            staging.remove(fileName);
            Utils.restrictedDelete(fileName);
        }

        Utils.writeObject(STAGING, staging);
    }

    public void log() {
        Commit headCommit = getHead();
        while (headCommit != null) {
            printLog(headCommit);
            headCommit = headCommit.getParent();
        }
    }

    public void globalLog() {
        commits = getCommits();
        Collection<Commit> cs = commits.values();
        for (Commit commit: cs) {
            printLog(commit);
        }
    }

    public void find(String commitMessage) {
        boolean flag = true;
        commits = getCommits();
        Collection<Commit> cs = commits.values();
        for (Commit commit: cs) {
            if (commit.getMessage().equals(commitMessage)) {
                flag = false;
                System.out.println(commit.getHash());
            }
        }

        if (flag) {
            throw new GitletException("Found no commit with that message.");
        }
    }

    public void status() {
        /*因为要按照字典顺序输出，所以采用list作为数据结构方便排序*/
        /*Branches*/
        ArrayList<String> branches = new ArrayList<>(Utils.plainFilenamesIn(BRANCHES));

        /*Staged Files*/
        staging = getStaging();
        ArrayList<String> staged = new ArrayList<>(staging.getAdd().keySet());

        /*Removed Files*/
        ArrayList<String> removed = new ArrayList<>(staging.getRemove());

        /*Modifications Not Staged For Commit*/
        ArrayList<String> unstaged = new ArrayList<>();
        Commit headCommit = getHead();
        HashMap<String, String> headCommitBlobs = headCommit.getBlobs();
        for (String fileName: Utils.plainFilenamesIn(CWD)) {
            File file = Utils.join(CWD, fileName);
            Blob blob = new Blob(file);
            /*在当前提交中被跟踪，在工作目录中已更改，但未暂存*/
            if (headCommitBlobs.containsKey(blob.getName())
                    && !headCommitBlobs.get(blob.getName()).equals(blob.getHash())
                    && !staging.getAdd().containsKey(blob.getName())) {
                unstaged.add(blob.getName() + " (modified)");
            }

            /*被暂存以添加，但与工作目录中的内容不同*/
            if (staging.getAdd().containsKey(blob.getName())
                    && !staging.getAdd().get(blob.getName()).equals(blob.getHash())) {
                unstaged.add(blob.getName() + " (modified)");
            }
        }

        /*被暂存以添加，但在工作目录中已删除*/
        for (String fileName: staging.getAdd().keySet()) {
            if (!Utils.join(CWD, fileName).exists()) {
                unstaged.add(fileName + " (deleted)");
            }
        }

        /*在工作目录中被删除，但在当前提交中被跟踪且未暂存以移除*/
        for (String fileName: headCommitBlobs.keySet()) {
            if (!Utils.join(CWD, fileName).exists()
                    && !staging.getRemove().contains(fileName)) {
                unstaged.add(fileName + " (deleted)");
            }
        }

        /*Untracked Files*/
        ArrayList<String> untracked = new ArrayList<>();
        for (String fileName: Utils.plainFilenamesIn(CWD)) {
            if (!headCommitBlobs.containsKey(fileName)
                    && !staging.getAdd().containsKey(fileName)) {
                untracked.add(fileName);
            }
        }

        printStatus(branches, staged, removed, unstaged, untracked);
    }

    public void checkout(String... args) {
        if (args.length == 3) {
            checkout1(args[2]);
        } else if (args.length == 4) {
            checkout2(args[1], args[3]);
        } else if (args.length == 2) {
            checkout3(args[1]);
        }
    }

    public void checkout1(String fileName) {
        Commit headCommit = getHead();
        if (!headCommit.getBlobs().containsKey(fileName)) {
            throw new GitletException("File does not exist in that commit.");
        }

        coverFile(fileName, headCommit);
    }

    public void checkout2(String commitHash, String fileName) {
        commitHash = abbrHash(commitHash);
        if (commitHash.equals("No commit with that id exists.")) {
            throw new GitletException("No commit with that id exists.");
        }

        commits = getCommits();
        if (!commits.containsKey(commitHash)) {
            throw new GitletException("No commit with that id exists.");
        }

        Commit commit = commits.get(commitHash);
        if (!commit.getBlobs().containsKey(fileName)) {
            throw new GitletException("File does not exist in that commit.");
        }

        coverFile(fileName, commit);
    }

    public void checkout3(String branchName) {
        File branch = Utils.join(BRANCHES, branchName);
        if (!branch.exists()) {
            throw new GitletException("No such branch exists.");
        }

        if (Utils.readContentsAsString(HEAD).equals(branchName)) {
            throw new GitletException("No need to checkout the current branch.");
        }

        commits = getCommits();
        Commit headCommit = getHead();
        Commit branchCommit = commits.get(Utils.readContentsAsString(branch));
        for (String fileName: Utils.plainFilenamesIn(CWD)) {
            if (!headCommit.getBlobs().containsKey(fileName)) {
                if (branchCommit.getBlobs().containsKey(fileName)) {
                    byte[] content = Utils.readContents(Utils.join(CWD, fileName));
                    byte[] branchContent = Utils.readContents(Utils.join(BLOBS,
                            branchCommit.getBlobs().get(fileName)));
                    if (!content.equals(branchContent)) {
                        throw new GitletException("There is an untracked file in the way;"
                                + " delete it, or add and commit it first.");
                    }
                }
            }
        }

        for (String fileName: branchCommit.getBlobs().keySet()) {
            coverFile(fileName, branchCommit);
        }

        for (String fileName: headCommit.getBlobs().keySet()) {
            if (!branchCommit.getBlobs().containsKey(fileName)) {
                Utils.restrictedDelete(fileName);
            }
        }

        staging = getStaging();
        staging.clear();
        Utils.writeObject(STAGING, staging);
        Utils.writeContents(HEAD, branchName);
    }

    public void branch(String branchName) {
        if (Utils.join(BRANCHES, branchName).exists()) {
            throw new GitletException("A branch with that name already exists.");
        }

        File branch = Utils.join(BRANCHES, branchName);
        Commit headCommit = getHead();
        Utils.writeContents(branch, headCommit.getHash());
    }

    public void rmBranch(String branchName) {
        if (!Utils.join(BRANCHES, branchName).exists()) {
            throw new GitletException("A branch with that name does not exist.");
        }

        if (Utils.readContentsAsString(HEAD).equals(branchName)) {
            throw new GitletException("Cannot remove the current branch.");
        }

        Utils.join(BRANCHES, branchName).delete();
    }

    public void reset(String commitHash) {
        commitHash = abbrHash(commitHash);
        if (commitHash.equals("No commit with that id exists.")) {
            throw new GitletException("No commit with that id exists.");
        }

        commits = getCommits();
        if (!commits.containsKey(commitHash)) {
            throw new GitletException("No commit with that id exists.");
        }

        Commit headCommit = getHead();
        Commit commit = commits.get(commitHash);
        for (String fileName: Utils.plainFilenamesIn(CWD)) {
            if (!headCommit.getBlobs().containsKey(fileName)
                    && commit.getBlobs().containsKey(fileName)) {
                throw new GitletException("There is an untracked file in the way;"
                        + " delete it, or add and commit it first.");
            }
        }

        for (String fileName: commit.getBlobs().keySet()) {
            coverFile(fileName, headCommit);
        }

        for (String fileName: Utils.plainFilenamesIn(CWD)) {
            if (!commit.getBlobs().containsKey(fileName)) {
                Utils.restrictedDelete(fileName);
            }
        }

        /*清空暂存区*/
        staging = getStaging();
        staging.clear();
        Utils.writeObject(STAGING, staging);

        /*将当前分支指向该提交*/
        String currentBranchName = Utils.readContentsAsString(HEAD);
        File currentBranch = Utils.join(BRANCHES, currentBranchName);
        Utils.writeContents(currentBranch, commit.getHash());
    }

    public void merge(String branchName) {
        /*给定分支不存在*/
        if (!Utils.join(BRANCHES, branchName).exists()) {
            throw new GitletException("A branch with that name does not exist.");
        }

        /*给定分支就是当前分支*/
        if (Utils.readContentsAsString(HEAD).equals(branchName)) {
            throw new GitletException("Cannot merge a branch with itself.");
        }

        /*暂存区不为空*/
        staging = getStaging();
        if (!staging.getAdd().isEmpty() || !staging.getRemove().isEmpty()) {
            throw new GitletException("You have uncommitted changes.");
        }

        /*当前提交中的未追踪文件会被合并覆盖或删除*/
        commits = getCommits();
        File branch = Utils.join(BRANCHES, branchName);
        Commit headCommit = getHead();
        Commit mergeCommit = commits.get(Utils.readContentsAsString(branch));
        for (String fileName: Utils.plainFilenamesIn(CWD)) {
            if (!headCommit.getBlobs().containsKey(fileName)
                    && mergeCommit.getBlobs().containsKey(fileName)) {
                throw new GitletException("There is an untracked file in the way;"
                        + " delete it, or add and commit it first.");
            }
        }

        Commit splitCommit = latestCommonAncestor(headCommit, mergeCommit);
        /*分叉点是给定分支*/
        if (splitCommit.getHash().equals(mergeCommit.getHash())) {
            System.out.println("Given branch is an ancestor of the current branch.");
            return;
        }

        /*分叉点是当前分支*/
        if (splitCommit.getHash().equals(headCommit.getHash())) {
            checkout("checkout", branchName);
            System.out.println("Current branch fast-forwarded.");
            return;
        }

        /*将splitCommit,headCommit和mergeCommit跟踪的所有文件添加到一起*/
        HashSet<String> files = new HashSet<>();
        files.addAll(splitCommit.getBlobs().keySet());
        files.addAll(headCommit.getBlobs().keySet());
        files.addAll(mergeCommit.getBlobs().keySet());

        int flag = 0;
        for (String fileName: files) {
            if (mergeHelper(fileName, splitCommit, headCommit, mergeCommit)) {
                flag += 1;
            }
        }

        boolean conflict = false;
        if (flag > 0) {
            conflict = true;
        }

        commit("Merged " + branchName + " into "
                + Utils.readContentsAsString(HEAD) + ".", mergeCommit);

        if (conflict) {
            System.out.println("Encountered a merge conflict.");
        }
    }

    public Commit getHead() {
        String headBranchName = Utils.readContentsAsString(HEAD);
        File headBranch = Utils.join(BRANCHES, headBranchName);
        String headCommitHash = Utils.readContentsAsString(headBranch);
        commits = getCommits();
        return commits.get(headCommitHash);
    }

    public Staging getStaging() {
        return Utils.readObject(STAGING, Staging.class);
    }

    @SuppressWarnings("unchecked")
    public TreeMap<String, Commit> getCommits() {
        return Utils.readObject(COMMITS, TreeMap.class);
    }

    public void updateHeadBranch(Commit commit) {
        String fileName = Utils.readContentsAsString(HEAD);
        File headBranch = Utils.join(BRANCHES, fileName);
        Utils.writeContents(headBranch, commit.getHash());
    }

    public void printLog(Commit commit) {
        System.out.println("===");
        System.out.println("commit " + commit.getHash());
        /*merge信息*/
        if (commit.getMergeParent() != null) {
            System.out.println("Merge: " + commit.getParent().getHash().substring(0, 7)
                    + " " + commit.getMergeParent().getHash().substring(0, 7));
        }

        SimpleDateFormat timeStamp = new SimpleDateFormat("EEE MMM d HH:mm:ss y Z");
        System.out.println("Date: " + timeStamp.format(commit.getTimeStamp()));
        System.out.println(commit.getMessage());
        System.out.print("\n");
    }

    public void printStatus(ArrayList<String> branches, ArrayList<String> staged,
                            ArrayList<String> removed, ArrayList<String> unstaged,
                            ArrayList<String> untracked) {
        branches.sort(Comparator.naturalOrder());
        System.out.println("=== Branches ===");
        for (String fileName: branches) {
            if (Utils.readContentsAsString(HEAD).equals(fileName)) {
                System.out.println("*" + fileName);
            } else {
                System.out.println(fileName);
            }
        }
        System.out.print("\n");

        staged.sort(Comparator.naturalOrder());
        System.out.println("=== Staged Files ===");
        for (String fileName: staged) {
            System.out.println(fileName);
        }
        System.out.print("\n");

        removed.sort(Comparator.naturalOrder());
        System.out.println("=== Removed Files ===");
        for (String fileName: removed) {
            System.out.println(fileName);
        }
        System.out.print("\n");

        unstaged.sort(Comparator.naturalOrder());
        System.out.println("=== Modifications Not Staged For Commit ===");
        for (String fileName: unstaged) {
            System.out.println(fileName);
        }
        System.out.print("\n");

        untracked.sort(Comparator.naturalOrder());
        System.out.println("=== Untracked Files ===");
        for (String fileName: untracked) {
            System.out.println(fileName);
        }
        System.out.print("\n");
    }

    public void coverFile(String fileName, Commit sourceCommit) {
        String hash = sourceCommit.getBlobs().get(fileName);
        File file = Utils.join(BLOBS, hash);
        byte[] content = Utils.readContents(file);
        Utils.writeContents(Utils.join(CWD, fileName), content);
    }

    public void getAncestors(HashSet<String> mergeAncestors, Commit mergeCommit) {
        /*每次递归到初始提交都会添加一次，防止重复故用HashSet*/
        if (mergeCommit == null) {
            return;
        }

        mergeAncestors.add(mergeCommit.getHash());

        if (mergeCommit.getParent() != null) {
            getAncestors(mergeAncestors, mergeCommit.getParent());
        }

        if (mergeCommit.getMergeParent() != null) {
            getAncestors(mergeAncestors, mergeCommit.getMergeParent());
        }
    }

    public void ancestorsDepth(HashMap<Commit, Integer> anDepth,
                               HashSet<String> mergeAncestors,
                               Commit headCommit, Integer depth) {
        if (headCommit == null) {
            return;
        }

        if (mergeAncestors.contains(headCommit.getHash())) {
            anDepth.put(headCommit, depth);
        } else {
            if (headCommit.getParent() != null) {
                ancestorsDepth(anDepth, mergeAncestors, headCommit.getParent(), depth + 1);
            }

            if (headCommit.getMergeParent() != null) {
                ancestorsDepth(anDepth, mergeAncestors, headCommit.getMergeParent(), depth + 1);
            }
        }
    }

    public Commit latestCommonAncestor(Commit headCommit, Commit mergeCommit) {
        HashSet<String> mergeAncestors = new HashSet<>();
        getAncestors(mergeAncestors, mergeCommit);

        HashMap<Commit, Integer> anDepth = new HashMap<>();
        ancestorsDepth(anDepth, mergeAncestors, headCommit, 0);

        Commit split = null;
        Integer minDepth = -1;
        for (Map.Entry<Commit, Integer> entry: anDepth.entrySet()) {
            Commit commit = entry.getKey();
            Integer depth = entry.getValue();
            if (minDepth < 0) {
                split = commit;
                minDepth = depth;
            } else if (depth < minDepth) {
                split = commit;
                minDepth = depth;
            }
        }

        return split;
    }

    public boolean mergeHelper(String fileName, Commit splitCommit,
                            Commit headCommit, Commit mergeCommit) {
        staging = getStaging();
        HashMap<String, String> splitBlobs = splitCommit.getBlobs();
        HashMap<String, String> headBlobs = headCommit.getBlobs();
        HashMap<String, String> mergeBlobs = mergeCommit.getBlobs();
        /*分叉点存在该文件*/
        if (splitBlobs.containsKey(fileName)) {
            /*在给定分支自分叉点以来被修改但在当前分支未修改，检出给定分支的该文件，并添加到暂存区*/
            if (splitBlobs.get(fileName).equals(headBlobs.get(fileName))
                    && mergeBlobs.containsKey(fileName)
                    && !splitBlobs.get(fileName).equals(mergeBlobs.get(fileName))) {
                checkout("checkout", mergeCommit.getHash(), "--", fileName);
                staging.add(fileName, mergeBlobs.get(fileName));
                Utils.writeObject(STAGING, staging);
                return false;
            }
            /*当前分支中被修改但在给定分支未修改，不变*/
            if (headBlobs.containsKey(fileName)
                    && !splitBlobs.get(fileName).equals(headBlobs.get(fileName))
                    && splitBlobs.get(fileName).equals(mergeBlobs.get(fileName))) {
                return false;
            }
            /*当前分支和给定分支中均被相同修改，不变*/
            if (headBlobs.containsKey(fileName) && mergeBlobs.containsKey(fileName)
                    && headBlobs.get(fileName).equals(mergeBlobs.get(fileName))) {
                return false;
            }
            /*两个分支中都删除，不变*/
            if (!headBlobs.containsKey(fileName) && !mergeBlobs.containsKey(fileName)) {
                return false;
            }
            /*当前分支未修改且在给定分支缺失，删除该文件*/
            if (headBlobs.containsKey(fileName)
                    && headBlobs.get(fileName).equals(splitBlobs.get(fileName))
                    && !mergeBlobs.containsKey(fileName)) {
                rm(fileName);
                Utils.writeObject(STAGING, staging);
                return false;
            }
            /*给定分支未修改且在当前分支缺失，不变*/
            if (mergeBlobs.containsKey(fileName)
                    && mergeBlobs.get(fileName).equals(splitBlobs.get(fileName))
                    && !headBlobs.containsKey(fileName)) {
                return false;
            }
            /*两个分支文件都修改，且内容不同，冲突*/
            if (headBlobs.containsKey(fileName) && mergeBlobs.containsKey(fileName)
                    && !splitBlobs.get(fileName).equals(headBlobs.get(fileName))
                    && !splitBlobs.get(fileName).equals(mergeBlobs.get(fileName))
                    && !headBlobs.get(fileName).equals(mergeBlobs.get(fileName))) {
                return mergeConflict(fileName, headBlobs, mergeBlobs);
            }
            /*一个分支文件被修改另一个分支文件被删除，冲突*/
            if (headBlobs.containsKey(fileName) && !mergeBlobs.containsKey(fileName)
                    && !splitBlobs.get(fileName).equals(headBlobs.get(fileName))
                    || !headBlobs.containsKey(fileName) && mergeBlobs.containsKey(fileName)
                    && !splitBlobs.get(fileName).equals(mergeBlobs.get(fileName))) {
                return mergeConflict(fileName, headBlobs, mergeBlobs);
            }
        } else {
            /*仅在当前分支存在,不变*/
            if (headBlobs.containsKey(fileName) && !mergeBlobs.containsKey(fileName)) {
                return false;
            }
            /*仅在给定分支存在,被检出并暂存*/
            if (!headBlobs.containsKey(fileName) && mergeBlobs.containsKey(fileName)) {
                checkout("checkout", mergeCommit.getHash(), "--", fileName);
                staging.add(fileName, mergeBlobs.get(fileName));
                Utils.writeObject(STAGING, staging);
                return false;
            }
            /*两个分支文件内容不同，冲突*/
            if (headBlobs.containsKey(fileName) && mergeBlobs.containsKey(fileName)
                    && headBlobs.get(fileName).equals(mergeBlobs.get(fileName))) {
                return mergeConflict(fileName, headBlobs, mergeBlobs);
            }
        }
        Utils.writeObject(STAGING, staging);
        return false;
    }

    public boolean mergeConflict(String fileName, HashMap<String, String> headBlobs,
                                 HashMap<String, String> mergeBlobs) {
        String headContent = "";
        if (headBlobs.containsKey(fileName)) {
            headContent = Utils.readContentsAsString(Utils.join(BLOBS, headBlobs.get(fileName)));
        }

        String mergeContent = "";
        if (mergeBlobs.containsKey(fileName)) {
            mergeContent = Utils.readContentsAsString(Utils.join(BLOBS, mergeBlobs.get(fileName)));
        }

        File file = Utils.join(CWD, fileName);
        String content = "<<<<<<< HEAD\n" + headContent + "=======\n" + mergeContent + ">>>>>>>\n";
        Utils.writeContents(file, content);
        Blob blob = new Blob(file);
        staging = getStaging();
        staging.add(fileName, blob.getHash());
        Utils.writeObject(STAGING, staging);
        return true;
    }

    public String abbrHash(String hash) {
        final int len = 40;
        if (hash.length() == len) {
            return hash;
        }

        commits = getCommits();
        for (String key: commits.keySet()) {
            if (key.startsWith(hash)) {
                return key;
            }
        }

        return "No commit with that id exists.";
    }

    public File getRepo() {
        return GITLET;
    }
}
