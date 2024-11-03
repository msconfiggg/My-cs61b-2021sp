package byow.Core.World;

import byow.TileEngine.TETile;

import java.io.Serializable;

public class World implements Serializable {
    private final int width;
    private final int height;
    private final TETile[][] map;

    public World(int width, int height, TETile tile) {
        this.width = width;
        this.height = height;
        this.map = new TETile[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                map[i][j] = tile;
            }
        }
    }

    public TETile[][] getMap() {
        return map;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public TETile getTile(int x, int y) {
        return map[x][y];
    }

    public boolean isPointValid(int x, int y) {
        return x >= 0  && x < width && y >= 0 && y < height;
    }
}
