package byow.Core.World;

import byow.TileEngine.TETile;

public class World {
    private int width;
    private int height;
    private TETile[][] map;

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

    public int getWidth() { return width; }

    public int getHeight() { return height; }
}
