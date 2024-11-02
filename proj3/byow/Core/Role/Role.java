package byow.Core.Role;

import byow.Core.World.Side;
import byow.Core.World.World;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.io.Serializable;

public abstract class Role implements Serializable {
    private int x;
    private int y;
    protected TETile symbol;
    private final World world;
    protected TETile[][] map;

    public Role(int x, int y, World world) {
        this.x = x;
        this.y = y;
        this.world = world;
        map = world.getMap();
    }

    protected void move(Side side) {
        int newX = x;
        int newY = y;
        switch (side) {
            case UP -> newY += 1;
            case DOWN -> newY -= 1;
            case LEFT -> newX -= 1;
            case RIGHT -> newX += 1;
        }

        if (RoleUtils.validLoc(newX, newY, world)) {
            map[x][y] = Tileset.FLOOR;
            x = newX;
            y = newY;
            map[x][y] = symbol;
        }
    }


    public int locX() {
        return x;
    }

    public int locY() {
        return y;
    }
}
