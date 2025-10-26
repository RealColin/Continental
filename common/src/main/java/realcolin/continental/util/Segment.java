package realcolin.continental.util;

import java.awt.geom.Point2D;

public record Segment(Point2D first, Point2D second) {

    public double getSlope() {
        if (first.getX() == second.getX())
            return Double.NaN;
        else if (first.getY() == second.getY())
            return 0.0;
        else
            return (second.getY() - first.getY()) / (second.getX() - first.getX());
    }

    public double distTo(Point2D p) {
        var a = first;
        var b = second;

        var abx = b.getX() - a.getX();
        var aby = b.getY() - a.getY();
        var apx = p.getX() - a.getX();
        var apy = p.getY() - a.getY();

        var proj = abx * apx + aby * apy;
        var absq = abx * abx + aby * aby;

        var d = proj / absq;

        if (d <= 0.0)
            return a.distance(p);
        else if (d >= 1.0)
            return b.distance(p);

        var closest = new Point2D.Double(a.getX() + abx * d, a.getY() + aby * d);

        return closest.distance(p);
    }

    public Line toLine() {
        var slope = getSlope();
        var intercept = this.second().getY() - slope * this.second().getX();
        double A, B, C;
        if (Double.isNaN(slope)) {
            A = 1;
            B = 0;
            C = -this.second().getX();
        } else {
            A = slope;
            B = -1;
            C = intercept;
        }
        return new Line(A, B, C);
    }
}
