package byow.lab13;

import byow.Core.RandomUtils;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.Color;
import java.awt.Font;
import java.util.Random;

public class MemoryGame {
    /** The width of the window of this game. */
    private int width;
    /** The height of the window of this game. */
    private int height;
    /** The current round the user is on. */
    private int round;
    /** The Random object used to randomly generate Strings. */
    private Random rand;
    /** Whether or not the game is over. */
    private boolean gameOver;
    /** Whether or not it is the player's turn. Used in the last section of the
     * spec, 'Helpful UI'. */
    private boolean playerTurn;
    /** The characters we generate random Strings from. */
    private static final char[] CHARACTERS = "abcdefghijklmnopqrstuvwxyz".toCharArray();
    /** Encouraging phrases. Used in the last section of the spec, 'Helpful UI'. */
    private static final String[] ENCOURAGEMENT = {"You can do this!", "I believe in you!",
                                                   "You got this!", "You're a star!", "Go Bears!",
                                                   "Too easy for you!", "Wow, so impressive!"};

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Please enter a seed");
            return;
        }

        long seed = Long.parseLong(args[0]);
        MemoryGame game = new MemoryGame(40, 40, seed);
        game.startGame();
    }

    public MemoryGame(int width, int height, long seed) {
        /* Sets up StdDraw so that it has a width by height grid of 16 by 16 squares as its canvas
         * Also sets up the scale so the top left is (0,0) and the bottom right is (width, height)
         */
        this.width = width;
        this.height = height;
        StdDraw.setCanvasSize(this.width * 16, this.height * 16);
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setPenColor(Color.white);
        StdDraw.setXscale(0, this.width);
        StdDraw.setYscale(0, this.height);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();

        //TODO: Initialize random number generator
        this.rand = new Random(seed);
    }

    public String generateRandomString(int n) {
        //TODO: Generate random string of letters of length n
        StringBuilder string = new StringBuilder();
        for (int i = 0; i < n; i++) {
            char letter = CHARACTERS[rand.nextInt(CHARACTERS.length)];
            string.append(letter);
        }
        return string.toString();
    }

    public void drawFrame(String s) {
        //TODO: Take the string and display it in the center of the screen
        StdDraw.clear(Color.black);
        StdDraw.line(0, height - 3, width, height - 3);
        StdDraw.text(5, height - 2, "Round: " + round);
        StdDraw.text(width / 2.0, height - 2, "Watch!");
        StdDraw.text(width - 8, height - 2, ENCOURAGEMENT[round % ENCOURAGEMENT.length]);
        StdDraw.text(width / 2.0, height / 2.0, s);
        StdDraw.show();
        //TODO: If game is not over, display relevant game information at the top of the screen
    }

    public void drawFrame(String s, int flag) {
        //TODO: Take the string and display it in the center of the screen
        StdDraw.clear(Color.black);
        StdDraw.line(0, height - 3, width, height - 3);
        StdDraw.text(5, height - 2, "Round: " + round);
        StdDraw.text(width / 2.0, height - 2, "Type!");
        StdDraw.text(width - 8, height - 2, ENCOURAGEMENT[round % ENCOURAGEMENT.length]);
        StdDraw.text(width / 2.0, height / 2.0, s);
        StdDraw.show();
        //TODO: If game is not over, display relevant game information at the top of the screen
    }

    public void flashSequence(String letters) {
        //TODO: Display each character in letters, making sure to blank the screen between letters
        for (int i = 0; i < letters.length(); i++) {
            drawFrame(letters.substring(i, i + 1));
            wait(1000);
            drawFrame("");
            wait(500);
        }

        drawFrame("", 0);
    }

    private void wait(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public String solicitNCharsInput(int n) {
        //TODO: Read n letters of player input
        StringBuilder string = new StringBuilder();
        while (string.length() < n) {
            if (StdDraw.hasNextKeyTyped()) {
                string.append(StdDraw.nextKeyTyped());
                drawFrame(string.toString(), 0);
            }
        }
        wait(1000);
        return string.toString();
    }

    public void startGame() {
        //TODO: Set any relevant variables before the game starts
        round = 1;
        //TODO: Establish Engine loop
        while (true) {
            drawFrame("Round: " + round);
            wait(1000);
            String target = generateRandomString(round);
            flashSequence(target);
            String input = solicitNCharsInput(round);
            if (!input.equals(target)) {
                drawFrame("Game Over! You made it to round: " + round);
                wait(1000);
                break;
            }

            round += 1;
        }
    }

}
