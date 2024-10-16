package byow.lab12;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Random;

/**
 * Draws a world consisting of hexagonal regions.
 */
public class HexWorld {
    private static final int WIDTH = 80;
    private static final int HEIGHT = 60;

    private static final long SEED = 2873123;
    private static final Random RANDOM = new Random(SEED);

    public static void addTessHexagen(TETile tile, int outSize, int inSize, int x, int y, TETile[][] world) {
        drawLeftHalf(tile, outSize, inSize, x, y, world);
        drawRightHalf(tile, outSize, inSize, x, y, world);
    }

    public static void drawLeftHalf(TETile tile, int outSize, int inSize, int x, int y, TETile[][] world) {
        for (int i = 0; i < outSize; i++) {
            int num = outSize + i;
            int startX = x + (inSize * 2 - 1) * i;
            int startY = y + (outSize - 1 - i) * inSize;
            drawOneCol(tile, num, inSize, startX, startY, world);
        }
    }

    public static void drawRightHalf(TETile tile, int outSize, int inSize, int x, int y, TETile[][] world) {
        for (int i = 1; i < outSize; i++) {
            int num = 2 * outSize - 1 - i;
            int startX = x + (inSize * 2 - 1) * (outSize - 1 + i);
            int startY = y + i * inSize;
            drawOneCol(tile, num, inSize, startX, startY, world);
        }
    }

    public static void drawOneCol(TETile tile, int num, int inSize, int x, int y, TETile[][] world) {
        int startX = x;
        for (int i = 0; i < num; i++) {
            int startY = y + inSize * 2 * i;
            addHexagon(tile, inSize, startX, startY, world);
        }
    }

    public static void addRandomHexagon(int size, int x, int y, TETile[][] world) {
        TETile tile = randomTile();
        addHexagon(tile, size, x, y, world);
    }

    public static void addHexagon(TETile tile, int size, int x, int y, TETile[][] world) {
        drawUpHalf(tile, size, x, y, world);
        drawDownHalf(tile, size, x, y, world);
    }

    public static void drawUpHalf(TETile tile, int size, int x, int y, TETile[][] world) {
        int startX = x;
        int maxNum = 3 * size - 2;
        for (int i = 0; i < size; i++) {
            int num = maxNum - 2 * i;
            int startY = y + size + i;
            drawOneRow(tile, num, maxNum, startX, startY, world);
        }
    }

    public static void drawDownHalf(TETile tile, int size, int x, int y, TETile[][] world) {
        int startX = x;
        int maxNum = 3 * size - 2;
        for (int i = 0; i < size; i++) {
            int num = size + 2 * i;
            int startY = y + i;
            drawOneRow(tile, num, maxNum, startX, startY, world);
        }
    }

    public static void drawOneRow(TETile tile, int num, int maxNum, int x, int y, TETile[][] world) {
        int j = y;
        int start = x + (maxNum - num) / 2;
        int end = x + (maxNum + num) / 2;
        for (int i = start; i < end; i++) {
            world[i][j] = tile;
        }
    }

    public static void initWorld(TETile[][] world) {
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                world[i][j] = Tileset.NOTHING;
            }
        }
    }

    private static TETile randomTile() {
        int tileNum = RANDOM.nextInt(6);
        return switch (tileNum) {
            case 0 -> Tileset.GRASS;
            case 1 -> Tileset.FLOWER;
            case 2 -> Tileset.SAND;
            case 3 -> Tileset.WATER;
            case 4 -> Tileset.MOUNTAIN;
            case 5 -> Tileset.TREE;
            default -> Tileset.NOTHING;
        };
    }

    public static void main(String[] args) {
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);

        TETile[][] hexWorld = new TETile[WIDTH][HEIGHT];
        initWorld(hexWorld);

        addTessHexagen(randomTile(), 3, 3, 0, 0, hexWorld);

        ter.renderFrame(hexWorld);
    }
}
