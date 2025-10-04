package realcolin.continental.world.continent;

import java.awt.*;

public class Continent {
    private final int x;
    private final int z;
    private final int radius;

    public Continent(int x, int z, int radius) {
        this.x = x;
        this.z = z;
        this.radius = radius;
    }

    public double distTo(Point point) {
        return Math.sqrt(Math.pow(point.getX() - x, 2) + Math.pow(point.getY() - z, 2));
    }

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }

    public int getRadius() {
        return radius;
    }
}
