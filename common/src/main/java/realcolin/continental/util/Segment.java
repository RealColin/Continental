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
