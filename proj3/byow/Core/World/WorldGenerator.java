package byow.Core.World;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import org.apache.commons.math3.util.Pair;

import java.util.*;

public class WorldGenerator {
    private final int width;
    private final int height;
    private final Random rand;
    private World world;
    private TETile[][] map;
    private final HashSet<Pair<Integer, Integer>> points;
    private final HashSet<Pair<Integer, Integer>> visited;

    public WorldGenerator(int width, int height, Random rand) {
        this.width = width;
        this.height = height;
        this.rand = rand;
        this.points = new HashSet<>();
        this.visited = new HashSet<>();
    }

    public void generateWorld() {
        this.world = new World(width, height, Tileset.NOTHING);
        this.map = world.getMap();
        addRooms();
        addPaths();
        connect();
        addWalls();
    }

    public void addRooms() {
        for (int i = 0; i < 50; i++) {
            int x = rand.nextInt(width);
            while (x % 2 == 0) {
                x = rand.nextInt(width);
            }
            int y = rand.nextInt(height);
            while (y % 2 == 0) {
                y = rand.nextInt(height);
            }
            int halfWidth = (rand.nextInt(3) + 1) * 2;//确保是偶数
            int halfHeight = (rand.nextInt(3) + 1) * 2;
            Room room = new Room(x, y, halfWidth, halfHeight, world);
            if (room.isAvail()) {
                room.addRoom();
            }
        }
    }

    public void addPaths() {
        for (int i = 1; i < width; i += 2) {
            for (int j = 1; j < height; j += 2) {
                if (map[i][j].equals(Tileset.NOTHING)) {
                    map[i][j] = Tileset.WALL;
                }
            }
        }

        for (int i = 1; i < width; i += 2) {
            for (int j = 1; j < height; j += 2) {
                if (map[i][j].equals(Tileset.WALL)) {
                    addPath(i, j);
                }
            }
        }
    }

    public void addPath(int x, int y) {
        map[x][y] = Tileset.FLOOR;

        // 随机方向数组
        Integer[] directions = {0, 1, 2, 3};  // 0:右, 1:下, 2:左, 3:上
        Collections.shuffle(Arrays.asList(directions), rand);

        for (int direction : directions) {
            switch (direction) {
                case 0: // 右
                    if (x + 2 < width - 1 && map[x + 2][y].equals(Tileset.WALL)) {
                        map[x + 1][y] = Tileset.FLOOR;
                        map[x + 2][y] = Tileset.FLOOR;
                        addPath(x + 2, y);
                    }
                    break;
                case 1: // 下
                    if (y + 2 < height - 1 && map[x][y + 2].equals(Tileset.WALL)) {
                        map[x][y + 1] = Tileset.FLOOR;
                        map[x][y + 2] = Tileset.FLOOR;
                        addPath(x, y + 2);
                    }
                    break;
                case 2: // 左
                    if (x - 2 > 0 && map[x - 2][y].equals(Tileset.WALL)) {
                        map[x - 1][y] = Tileset.FLOOR;
                        map[x - 2][y] = Tileset.FLOOR;
                        addPath(x - 2, y);
                    }
                    break;
                case 3: // 上
                    if (y - 2 > 0 && map[x][y - 2].equals(Tileset.WALL)) {
                        map[x][y - 1] = Tileset.FLOOR;
                        map[x][y - 2] = Tileset.FLOOR;
                        addPath(x, y - 2);
                    }
                    break;
            }
        }
    }

    public void addWalls() {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (!map[i][j].equals(Tileset.NOTHING)) {
                    addWall(i, j);
                }
            }
        }
    }

    public void addWall(int x, int y) {
        if (x - 1 >= 0) {
            if (map[x - 1][y].equals(Tileset.NOTHING)) {
                map[x - 1][y] = Tileset.WALL;
            }
            if (y - 1 >= 0 && map[x - 1][y - 1].equals(Tileset.NOTHING)) {
                map[x - 1][y - 1] = Tileset.WALL;
            }
            if (y + 1 < height && map[x - 1][y + 1].equals(Tileset.NOTHING)) {
                map[x - 1][y + 1] = Tileset.WALL;
            }
        }

        if (x + 1 < width) {
            if (map[x + 1][y].equals(Tileset.NOTHING)) {
                map[x + 1][y] = Tileset.WALL;
            }
            if (y - 1 >= 0 && map[x + 1][y - 1].equals(Tileset.NOTHING)) {
                map[x + 1][y - 1] = Tileset.WALL;
            }
            if (y + 1 < height && map[x + 1][y + 1].equals(Tileset.NOTHING)) {
                map[x + 1][y + 1] = Tileset.WALL;
            }
        }

        if (y - 1 >= 0 && map[x][y - 1].equals(Tileset.NOTHING)) {
            map[x][y - 1] = Tileset.WALL;
        }

        if (y + 1 < height && map[x][y + 1].equals(Tileset.NOTHING)) {
            map[x][y + 1] = Tileset.WALL;
        }
    }

    public void connect() {
        addToMap(1, 1, visited);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (!map[i][j].equals(Tileset.NOTHING)) {
                    continue;
                }

                int count = 0;//大于等于2
                boolean flag1 = false;//周围有在set中的FLOOR块
                boolean flag2 = false;//周围有不在set中的FLOOR块

                if (i - 1 >= 0 && map[i - 1][j].equals(Tileset.FLOOR)) {
                    count += 1;
                    Pair<Integer, Integer> pair = new Pair<>(i - 1, j);
                    if (points.contains(pair)) {
                        flag1 = true;
                    } else {
                        flag2 = true;
                    }
                }

                if (i + 1 < width && map[i + 1][j].equals(Tileset.FLOOR)) {
                    count += 1;
                    Pair<Integer, Integer> pair = new Pair<>(i + 1, j);
                    if (points.contains(pair)) {
                        flag1 = true;
                    } else {
                        flag2 = true;
                    }
                }

                if (j - 1 >= 0 && map[i][j - 1].equals(Tileset.FLOOR)) {
                    count += 1;
                    Pair<Integer, Integer> pair = new Pair<>(i, j - 1);
                    if (points.contains(pair)) {
                        flag1 = true;
                    } else {
                        flag2 = true;
                    }
                }

                if (j + 1 < height && map[i][j + 1].equals(Tileset.FLOOR)) {
                    count += 1;
                    Pair<Integer, Integer> pair = new Pair<>(i, j + 1);
                    if (points.contains(pair)) {
                        flag1 = true;
                    } else {
                        flag2 = true;
                    }
                }

                if (count >= 2 && flag1 && flag2) {
                    map[i][j] = Tileset.FLOOR;
                    addToMap(i, j, visited);
                }
            }
        }
    }

    public void addToMap(int x, int y, HashSet<Pair<Integer, Integer>> visited) {
        Pair<Integer, Integer> point = new Pair<>(x, y);
        if (visited.contains(point)) {
            return;
        }

        points.add(point);
        visited.add(point);
        if (x - 1 >= 0 && map[x - 1][y].equals(Tileset.FLOOR)) {
            addToMap(x - 1, y, visited);
        }

        if (x + 1 < width && map[x + 1][y].equals(Tileset.FLOOR)) {
            addToMap(x + 1, y, visited);
        }

        if (y - 1 >= 0 && map[x][y - 1].equals(Tileset.FLOOR)) {
            addToMap(x, y - 1, visited);
        }

        if (y + 1 < height && map[x][y + 1].equals(Tileset.FLOOR)) {
            addToMap(x, y + 1, visited);
        }
    }

    public World getWorld() {
        return world;
    }
}
