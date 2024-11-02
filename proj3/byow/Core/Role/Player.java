package byow.Core.Role;

import byow.Core.World.Side;
import byow.Core.World.World;
import byow.TileEngine.Tileset;

import java.io.Serializable;

public class Player extends Role implements Serializable {

    public Player(int x, int y, World world) {
        super(x, y, world);
        symbol = Tileset.AVATAR;
        map[x][y] = symbol;
    }

    public void move(char key) {
        switch (key) {
            case 'W' -> move(Side.UP);
            case 'A' -> move(Side.LEFT);
            case 'S' -> move(Side.DOWN);
            case 'D' -> move(Side.RIGHT);
        }
    }
}
