package byow.Core.World;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

public class Room {
    private int x;
    private int y;
    private int halfWidth;
    private int halfHeight;
    private TETile[][] map;
    private World world;

    public Room(int x, int y, int halfWidth, int halfHeight, World world) {
        this.x = x;
        this.y = y;
        this.halfWidth = halfWidth;
        this.halfHeight = halfHeight;
        this.world = world;
        this.map = world.getMap();
    }

    public boolean isAvail() {
        if (x - halfWidth - 1 < 0) {
            return false;
        }

        if (x + halfWidth + 1 >= world.getWidth()) {
            return false;
        }

        if (y - halfHeight - 1 < 0) {
            return false;
        }

        if (y + halfHeight + 1 >= world.getHeight()) {
            return false;
        }

        for (int i = x - halfWidth - 1; i <= x + halfWidth + 1; i++) {
            for (int j = y - halfHeight - 1; j <= y + halfHeight +1; j++) {
                if (!map[i][j].equals(Tileset.NOTHING)) {
                    return false;
                }
            }
        }

        return true;
    }

    public void addRoom() {
        for (int i = x - halfWidth; i <= x + halfWidth; i++) {
            for (int j = y - halfHeight; j <= y + halfHeight; j++) {
                map[i][j] = Tileset.FLOOR;
            }
        }
    }
}
