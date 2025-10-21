package realcolin.continental.world.continent;

import net.minecraft.world.phys.AABB;
import realcolin.continental.util.Line;
import realcolin.continental.util.Segment;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class Continent {
    private final int x;
    private final int z;
    private final int radius;
    private final List<Point2D.Double> boundaryPoints;
    private final List<Segment> boundarySegments;
    private final AABB boundingBox;

    public Continent(int x, int z, int radius, List<Point2D.Double> shape) {
        this.x = x;
        this.z = z;
        this.radius = radius;
        this.boundaryPoints = shape;
        this.boundarySegments = segmentsToLines(shape);
        this.boundingBox = calculateAABB(shape);

//        System.out.println("Continent: ");
//        for (var p : boundaryPoints) {
//            System.out.println("(" + p.getX() + ", " + p.getY() + ")");
//        }
//        for (var s : boundarySegments) {
//            System.out.println("polygon((" + s.first().getX() + ", " + s.first().getY() + "), (" + s.second().getX() + ", " + s.second().getY() + "))");
//        }
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

    public List<Point2D.Double> getBoundaryPoints() {
        return boundaryPoints;
    }

    public List<Segment> intersectsWith(Line line) {
        var ret = new ArrayList<Segment>();

        for (var s : boundarySegments) {

        }

        return ret;
    }

    public Segment getClosestFrom(Point point) {
        Segment closest = null;
        var closestDist = Double.POSITIVE_INFINITY;

        for (var s : boundarySegments) {
            var dist = s.distTo(point);
            if (dist < closestDist) {
                closestDist = dist;
                closest = s;
            }
        }

        return closest;
    }

    public Point2D getClosestPointFrom(Point point) {
        Point2D closest = null;
        var closestDist = Double.POSITIVE_INFINITY;

        for (var p : boundaryPoints) {
            var dist = p.distance(point);
            if (dist < closestDist) {
                closestDist = dist;
                closest = p;
            }
        }

        return closest;
    }

    public boolean isPointInside(Point point) {
        if (!boundingBox.contains(point.getX(), point.getY(),0))
            return false;

        var inside = false;

        for (int i = 0, j = boundaryPoints.size() - 1; i < boundaryPoints.size(); j = i++) {
            var xi = boundaryPoints.get(i).getX();
            var yi = boundaryPoints.get(i).getY();
            var xj = boundaryPoints.get(j).getX();
            var yj = boundaryPoints.get(j).getY();

            boolean intersect = ((yi > point.getY()) != (yj > point.getY())) &&
                    (point.getX() < (xj - xi) * (point.getY() - yi) / (yj - yi + 1e-12) + xi);

            if (intersect)
                inside = !inside;
        }

        return inside;
    }

    private List<Segment> segmentsToLines(List<Point2D.Double> points) {
        var segments = new ArrayList<Segment>();

        for (int i = 0, j = points.size() - 1; i < points.size(); j = i++) {
            var s = new Segment(points.get(i), points.get(j));
            segments.add(s);
        }

        return segments;
    }

    private AABB calculateAABB(List<Point2D.Double> points) {
        double minX = Double.POSITIVE_INFINITY, minY = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY, maxY = Double.NEGATIVE_INFINITY;
        for (var p : points) {
            if (p.getX() < minX) minX = p.getX();
            if (p.getY() < minY) minY = p.getY();
            if (p.getX() > maxX) maxX = p.getX();
            if (p.getY() > maxY) maxY = p.getY();
        }
        return new AABB(minX, minY, Double.NEGATIVE_INFINITY, maxX, maxY, Double.POSITIVE_INFINITY);
    }
}
