package byow.Core.Role;

import byow.Core.World.World;
import byow.TileEngine.Tileset;
import org.apache.commons.math3.util.Pair;

import java.util.Random;

public class RoleUtils {

    public static Pair<Integer, Integer> randomLoc(World world, Random rand) {
        int x = rand.nextInt(world.getWidth());
        int y = rand.nextInt(world.getHeight());
        while (!validLoc(x, y, world)) {
            x = rand.nextInt(world.getWidth());
            y = rand.nextInt(world.getHeight());
        }

        return new Pair<>(x, y);
    }

    public static boolean validLoc(int x, int y, World world) {
        if (x < 0 || x >= world.getWidth()) {
            return false;
        }

        if (y < 0 || y >= world.getHeight()) {
            return false;
        }

        return world.getTile(x, y).character() == Tileset.FLOOR.character(); //用equals会出问题
    }
}
