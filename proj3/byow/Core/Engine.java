package byow.Core;

import byow.Core.Role.Player;
import byow.Core.Role.RoleUtils;
import byow.InputDemo.KeyboardInputSource;
import byow.InputDemo.StringInputDevice;
import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.Core.World.*;
import org.apache.commons.math3.util.Pair;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.*;
import java.io.*;
import java.util.Random;

import static byow.Core.GameState.*;
import static edu.princeton.cs.introcs.StdDraw.mouseX;
import static edu.princeton.cs.introcs.StdDraw.mouseY;

public class Engine {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public static final int WIDTH = 81;
    public static final int HEIGHT = 51;
    private long seed;
    private Random rand;
    private World world;
    private Player player;
    private GameState gameState = MENU;
    private boolean active = true;

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
        displayMenu();
        KeyboardInputSource inputSource = new KeyboardInputSource();
        while (active) {
            evalMouse((int) mouseX(), (int) mouseY());

            // 检查键盘输入
            char key = inputSource.getNextKey();
            if (key != '\0') { // 如果有有效的键输入
                evalKey(key);
            }
        }
    }

    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     *
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     *
     * In other words, both of these calls:
     *   - interactWithInputString("n123sss:q")
     *   - interactWithInputString("lww")
     *
     * should yield the exact same world state as:
     *   - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] interactWithInputString(String input) {
        // TODO: Fill out this method so that it run the engine using the input
        // passed in as an argument, and return a 2D tile representation of the
        // world that would have been drawn if the same inputs had been given
        // to interactWithKeyboard().
        //
        // See proj3.byow.InputDemo for a demo of how you can make a nice clean interface
        // that works for many different input types.

        StringInputDevice inputDevice = new StringInputDevice(input);
        while (inputDevice.possibleNextInput()) {
            char key = inputDevice.getNextKey();
            evalKey(key);
        }

        return world.getMap();
    }

    private void evalKey(char key) {
        switch (gameState) {
            case MENU:
                if (key == 'N') {
                    gameState = SET_SEED;
                    displaySeedMenu();
                } else if (key == 'L') {
                    loadGame();
                    gameState = PLAY;
                } else if (key == 'Q') {
                    active = false;
                    System.exit(0);
                }
                break;
            case SET_SEED:
                if (key == 'S') {
                    gameState = PLAY;
                    newGame();
                } else {
                    seed = seed * 10 + Character.getNumericValue(key);
                    displaySeedMenu();
                }
                break;
            case PLAY:
                if (key == ':') {
                    gameState = COMMAND;
                } else {
                    playRound(key);
                }
                break;
            case COMMAND:
                if (key == 'Q') {
                    saveGame();
                    active = false;
                    System.exit(0);
                } else {
                    gameState = PLAY;
                }
        }
    }

    private void evalMouse(int x, int y) {
        if (gameState == PLAY) {
            if (!world.isPointValid(x, y)) {
                return;
            }

            TETile tile = world.getTile(x, y);
            String description = tile.description();
            ter.renderFrame(world.getMap(), description);
        }
    }

    private void displayMenu() {
        StdDraw.setCanvasSize(WIDTH * 16, HEIGHT * 16); // Each cell is 16x16 pixels
        Font font = new Font("Monaco", Font.BOLD, 60);
        StdDraw.setFont(font);
        StdDraw.setXscale(0, WIDTH);
        StdDraw.setYscale(0, HEIGHT);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();

        // Draw Title
        StdDraw.text(WIDTH / 2.0, HEIGHT / 1.5, "CS61B: THE GAME");

        // Draw Options
        Font subtitle = new Font("Monaco", Font.PLAIN, 30);
        StdDraw.setFont(subtitle);
        StdDraw.text(WIDTH / 2.0, HEIGHT * 0.40, "New Game (N)");
        StdDraw.text(WIDTH / 2.0, HEIGHT * 0.35, "Load Game (L)");
        StdDraw.text(WIDTH / 2.0, HEIGHT * 0.30, "Quit (Q)");

        StdDraw.show();
        StdDraw.pause(1);
    }

    private void displaySeedMenu() {
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.clear(Color.BLACK);

        // Draw Title
        StdDraw.text(WIDTH / 2.0, 40, "Enter a number: ");
        StdDraw.text(WIDTH / 2.0, 37, Long.toString(seed));
        StdDraw.text(WIDTH / 2.0, 34, "Then press 'S' to start");

        StdDraw.show();
        StdDraw.pause(1);
    }

    private void newGame() {
        rand = new Random(seed);
        WorldGenerator worldGen = new WorldGenerator(WIDTH, HEIGHT - 2, rand);
        worldGen.generateWorld();
        world = worldGen.getWorld();
        addPlayer();
        ter.initialize(WIDTH, HEIGHT);
        ter.renderFrame(world.getMap(), "");
    }

    private void loadGame() {
        File f = new File("./save_file");
        if (f.exists()) {
            try {
                FileInputStream fs = new FileInputStream(f);
                ObjectInputStream os = new ObjectInputStream(fs);
                seed = (long) os.readObject();
                rand = (Random) os.readObject();
                world = (World) os.readObject();
                player = (Player) os.readObject();
                ter.initialize(WIDTH, HEIGHT);
                ter = (TERenderer) os.readObject();
                ter.renderFrame(world.getMap(), "");
            } catch (FileNotFoundException e) {
                System.out.println("file not found");
                System.exit(0);
            } catch (IOException e) {
                System.out.println(e);
                System.exit(0);
            } catch (ClassNotFoundException e) {
                System.out.println("class not found");
                System.exit(0);
            }
        }
    }

    private void saveGame() {
        File f = new File("./save_file");
        try {
            if (!f.exists()) {
                f.createNewFile();
            }

            FileOutputStream fs = new FileOutputStream(f);
            ObjectOutputStream os = new ObjectOutputStream(fs);
            os.writeObject(seed);
            os.writeObject(rand);
            os.writeObject(world);
            os.writeObject(player);
            os.writeObject(ter);
            os.close();
        } catch (FileNotFoundException e) {
            System.out.println("file not found");
            System.exit(0);
        } catch (IOException e) {
            System.out.println(e);
            System.exit(0);
        }
    }

    private void addPlayer() {
        Pair<Integer, Integer> pair = RoleUtils.randomLoc(world, rand);
        int x = pair.getKey();
        int y = pair.getValue();
        player = new Player(x, y, world);
    }

    private void playRound(char key) {
        player.move(key);
        ter.renderFrame(world.getMap(), "");
    }
}
