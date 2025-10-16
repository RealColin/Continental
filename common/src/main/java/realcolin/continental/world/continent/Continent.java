package realcolin.continental.world.continent;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.List;

public class Continent {
    private final int x;
    private final int z;
    private final int radius;
    private final List<Point2D.Double> shape;

    public Continent(int x, int z, int radius, List<Point2D.Double> shape) {
        this.x = x;
        this.z = z;
        this.radius = radius;
        this.shape = shape;
    }

    // TODO need to take both distancce to center and distance from edges
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

    public List<Point2D.Double> getShape() {
        return shape;
    }

    public boolean isPointInside(Point point) {
        var inside = false;

        // TODO include basic rectangular bounds check (if it's outside the rectangle then it's outside the continent) to improve performance

        for (int i = 0, j = shape.size() - 1; i < shape.size(); j = i++) {
            var xi = shape.get(i).getX();
            var yi = shape.get(i).getY();
            var xj = shape.get(j).getX();
            var yj = shape.get(j).getY();

            boolean intersect = ((yi > point.getY()) != (yj > point.getY())) &&
                    (point.getX() < (xj - xi) * (point.getY() - yi) / (yj - yi + 1e-12) + xi);

            if (intersect)
                inside = !inside;
        }

        return inside;
    }
}
