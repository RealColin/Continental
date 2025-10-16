package realcolin.continental.util;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class Voronoi {
    private final List<Cell> cells;

    public Voronoi(List<Point2D> points, List<Point2D> initialBounds) {
        cells = new ArrayList<>();

        for (var p : points) {
            var bounds = new ArrayList<>(initialBounds);

            for (var pi : points) {
                if (p.equals(pi))
                    continue;

                var line = getPerpendicularBisector(p, pi);
                bounds = clip(bounds, line, p, pi);
            }

            var cell = new Cell(bounds);
            cells.add(cell);
        }
    }

    public static List<Point2D> runLloydRelaxation(List<Point2D> initialPoints, List<Point2D> bounds, int numberOfIterations) {
        var points = initialPoints;
        for (int i = 0; i < numberOfIterations; i++) {
            var vor = new Voronoi(points, bounds);
            points = vor.getCentroids();
        }

        // I want them rounded because block positions are integers not decimals
        for (int i = 0; i < points.size(); i++) {
            var point = points.get(i);
            point = new Point2D.Double(Math.round(point.getX()), Math.round(point.getY()));
            points.set(i, point);
        }

        return points;
    }

    private List<Point2D> getCentroids() {
        var ret = new ArrayList<Point2D>();

        for (var cell : cells) {
            var points = cell.bounds();
            int n = points.size();
            if (n == 0) {
                ret.add(new Point2D.Double(Double.NaN, Double.NaN));
                continue;
            } else if (n == 1) {
                ret.add(new Point2D.Double(points.getFirst().getX(), points.getFirst().getY()));
                continue;
            } else if (n == 2) {
                var mid = midpoint(points.get(0), points.get(1));
                ret.add(mid);
                continue;
            }

            // shoelace to get area
            var area = 0.0;
            var cx = 0.0;
            var cy = 0.0;

            for (int i = 0; i < n; i++) {
                var a = points.get(i);
                var b = points.get((i + 1) % n);
                var cross = a.getX() * b.getY() - b.getX() * a.getY();
                area += cross;
                cx += (a.getX() + b.getX()) * cross;
                cy += (a.getY() + b.getY()) * cross;
            }
            area /= 2.0;
            cx /= (6 * area);
            cy /= (6 * area);

            ret.add(new Point2D.Double(cx, cy));
        }

        return ret;
    }

    private ArrayList<Point2D> clip(ArrayList<Point2D> initial, Line clipLine, Point2D p, Point2D pi) {
        var output = new ArrayList<Point2D>();

        for (int i = 0; i < initial.size(); i++) {
            var cur = initial.get(i);
            int prevIn = (i - 1 + initial.size()) % initial.size();
            var prev = initial.get(prevIn);

            var inter = getIntersecting(clipLine, new Segment(cur, prev));

            if (closerToFirst(cur, p, pi)) {
                if (!closerToFirst(prev, p, pi)) {
                    output.add(inter);
                }
                output.add(cur);
            } else if (closerToFirst(prev, p, pi)) {
                output.add(inter);
            }

        }

        return output;
    }

    private boolean closerToFirst(Point2D test, Point2D first, Point2D second) {
        return test.distance(first) <= test.distance(second);
    }


    // using Cramer's rule!!
    private Point2D getIntersecting(Line line, Segment seg) {
        Line segLine = seg.toLine();

        double A1 = line.A(), B1 = line.B(), C1 = line.C();
        double A2 = segLine.A(), B2 = segLine.B(), C2 = segLine.C();

        double det = A1 * B2 - A2 * B1;

        if (Math.abs(det) < 1e-12) {
            return new Point2D.Double(Double.NaN, Double.NaN);
        }

        double x = (B1 * C2 - B2 * C1) / det;
        double y = (C1 * A2 - C2 * A1) / det;

        return new Point2D.Double(x, y);
    }

    private Point2D midpoint(Point2D p1, Point2D p2) {
        return new Point2D.Double((p1.getX() + p2.getX()) / 2.0, (p1.getY() + p2.getY()) / 2.0);
    }

    private Line getPerpendicularBisector(Point2D p1, Point2D p2) {
        var midpoint = midpoint(p1, p2);
        var seg = new Segment(p1, p2);
        var slope = seg.getSlope();
        double A, B, C;
        if (slope == 0.0) {
            A = 1;
            B = 0;
            C = -midpoint.getX();
        } else if (Double.isNaN(slope)) {
            A = 0;
            B = -1;
            C = midpoint.getY();
        } else {
            A = -1.0 / slope;
            B = -1.0;
            C = (1.0 / slope) * midpoint.getX() + midpoint.getY();
        }
        return new Line(A, B, C);
    }

    record Cell(List<Point2D> bounds) {}
}