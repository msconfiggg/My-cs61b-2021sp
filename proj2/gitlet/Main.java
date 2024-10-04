package gitlet;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author TODO
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        try {
            Main.helper(args);
            return;
        } catch (GitletException e) {
            System.err.println(e.getMessage());
        }

        System.exit(0);
    }

    public static void helper(String[] args) {
        // TODO: what if args is empty?
        Repository repo = new Repository();
        if (args.length == 0) {
            throw new GitletException("Please enter a command.");
        }

        String firstArg = args[0];
        if (!firstArg.equals("init") && !repo.getRepo().exists()) {
            throw new GitletException("Not in an initialized Gitlet directory.");
        }

        switch(firstArg) {
            case "init":
                // TODO: handle the `init` command
                if (args.length == 1) {
                    repo.init();
                } else {
                    throw new GitletException("Incorrect operands.");
                }
                break;
            case "add":
                // TODO: handle the `add [filename]` command
                if (args.length == 2) {
                    repo.add(args[1]);
                } else {
                    throw new GitletException("Incorrect operands.");
                }
                break;
            // TODO: FILL THE REST IN
            case "commit":
                if (args.length == 2) {
                    repo.commit(args[1]);
                } else {
                    throw new GitletException("Incorrect operands.");
                }
                break;
            case "rm":
                if (args.length == 2) {
                    repo.rm(args[1]);
                } else {
                    throw new GitletException("Incorrect operands.");
                }
                break;
            case "log":
                if (args.length == 1) {
                    repo.log();
                } else {
                    throw new GitletException("Incorrect operands.");
                }
                break;
            case "global-log":
                if (args.length == 1) {
                    repo.globalLog();
                } else {
                    throw new GitletException("Incorrect operands.");
                }
                break;
            case "find":
                if (args.length == 2) {
                    repo.find(args[1]);
                } else {
                    throw new GitletException("Incorrect operands.");
                }
                break;
            case "status":
                if (args.length == 1) {
                    repo.status();
                } else {
                    throw new GitletException("Incorrect operands.");
                }
                break;
            case "checkout":
                if (args.length == 2 || args.length == 3 || args.length ==4) {
                    repo.checkout(args);
                } else {
                    throw new GitletException("Incorrect operands.");
                }
                break;
            case "branch":
                if (args.length == 2) {
                    repo.branch(args[1]);
                } else {
                    throw new GitletException("Incorrect operands.");
                }
                break;
            case "rm-branch":
                if (args.length == 2) {
                    repo.rmBranch(args[1]);
                } else {
                    throw new GitletException("Incorrect operands.");
                }
                break;
            case "reset":
                if (args.length == 2) {
                    repo.reset(args[1]);
                } else {
                    throw new GitletException("Incorrect operands.");
                }
                break;
            case "merge":
                if (args.length == 2) {
                    repo.merge(args[1]);
                } else {
                    throw new GitletException("Incorrect operands.");
                }
                break;
            default:
                throw new GitletException("No command with that name exists.");
        }
    }
}
