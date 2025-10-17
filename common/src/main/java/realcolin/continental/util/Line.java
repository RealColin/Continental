package realcolin.continental.util;

import java.awt.geom.Point2D;

// Ax + By + C = 0
public record Line(double A, double B, double C) {

    public double distTo(Point2D point) {
        var num = Math.abs(A * point.getX() + B * point.getY() + C);
        var den = Math.sqrt(Math.pow(A, 2) + Math.pow(B, 2));

        return num / den;
    }

    public Point2D getIntersectingPoint(Line line) {
        double det = A * line.B() - line.A() * B;

        if (Math.abs(det) < 1e-12) {
            return new Point2D.Double(Double.NaN, Double.NaN);
        }

        double x = (B * line.C() - line.B() * C) / det;
        double y = (C * line.A() - line.C * A) / det;

        return new Point2D.Double(x, y);
    }
}
