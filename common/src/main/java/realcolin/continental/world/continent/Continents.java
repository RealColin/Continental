package realcolin.continental.world.continent;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.resources.RegistryFileCodec;
import realcolin.continental.Constants;
import realcolin.continental.ContinentalRegistries;
import realcolin.continental.util.Voronoi;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Continents {
    private static final Codec<List<Point2D.Double>> P_CODEC =
            RecordCodecBuilder.<Point2D.Double>create(instance -> instance.group(
                    Codec.DOUBLE.fieldOf("x").forGetter(Point2D.Double::getX),
                    Codec.DOUBLE.fieldOf("z").forGetter(Point2D.Double::getY)
            ).apply(instance, Point2D.Double::new)).listOf();

    private static final Codec<Continent> C_CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    Codec.INT.fieldOf("x").forGetter(Continent::getX),
                    Codec.INT.fieldOf("z").forGetter(Continent::getZ),
                    Codec.INT.fieldOf("radius").forGetter(Continent::getRadius),
                    P_CODEC.fieldOf("points").forGetter(Continent::getShape)
            ).apply(instance, Continent::new));

    public static final Codec<Continents> DIRECT_CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                C_CODEC.listOf().fieldOf("continents").forGetter(src -> src.continents)
            ).apply(instance, Continents::new));

    public static final Codec<Holder<Continents>> CODEC = RegistryFileCodec.create(ContinentalRegistries.CONTINENTS, DIRECT_CODEC);

    private final List<Continent> continents;
    public Continents(List<Continent> continents) {
        this.continents = continents;
    }

    public List<Continent> get() {
        return continents;
    }

    public double compute(Point point) {
        double maxVal = Double.NEGATIVE_INFINITY;

        for (var c : continents) {
            if (c.isPointInside(point))
                maxVal = 1.0;
        }

        return Math.max(-1.0, maxVal);
    }

    public static Continents generate(ContinentSettings settings, long seed) {
        /* Get seeds for the generators based off the world seed */
        var seeds = getSeeds(seed);
        var numConsSeed = seeds[0];
        var randPointsSeed = seeds[1];
        var randRadiiSeed = seeds[2];
        var continentSeeder = seeds[3];

        /* Get the Continent positions */
        var positions = getContinentPositions(settings, numConsSeed, randPointsSeed);

        /* Get the radius for each Continent */

        var rand = new Random(randRadiiSeed);
        var conRand = new Random(continentSeeder);
        var stddev = settings.variation() * settings.meanSize();
        var minRadius = 0.2 * settings.meanSize(); // TODO put the 0.2 in Constants maybe?
        var maxRadius = 1.8 * settings.meanSize(); // TODO put the 1.8 in Constants maybe?

        var list = new ArrayList<Continent>();

        for (var p : positions) {
            int radius = (int)Math.round(Math.clamp(rand.nextGaussian(settings.meanSize(), stddev), minRadius, maxRadius));
            var shape = getContinentShape(p, radius, conRand.nextLong());
            var con = new Continent((int)p.getX(), (int)p.getY(), radius, shape);
            list.add(con);
        }

        return new Continents(list);
    }


    private static List<Point2D.Double> getContinentShape(Point2D center, int radius, long seed) {
        var ret = getBasicContinentShape(center, radius, seed);
        for (int i = 0; i < 4; i++) {
            ret = chaikinStep(ret);
        }
        return ret;
    }

    private static List<Point2D.Double> chaikinStep(List<Point2D.Double> shape) {
        var n = shape.size();
        var t = 0.25; // default 0.25
        var out = new ArrayList<Point2D.Double>();
        for (int i = 0; i < n; i++) {
            var a = shape.get(i);
            var b = shape.get((i+1) % n);
            var qx = (1 - t) * a.getX() + t * b.getX();
            var qy = (1 - t)* a.getY() + t * b.getY();
            var rx = t * a.getX() + (1 - t) * b.getX();
            var ry = t * a.getY() + (1 - t) * b.getY();
            out.add(new Point2D.Double(qx, qy));
            out.add(new Point2D.Double(rx, ry));
        }
        return out;
    }

    private static List<Point2D.Double> getBasicContinentShape(Point2D center, int radius, long seed) {
        int N = 36; // number of points to shape the continent
        var roughness = 0.9; // variance between radii, increase to have less circular shaped continents
        var minFrac = 0.25; // the smallest possible radius

        var random = new Random(seed);

        // get the angles in a randomly distributed way
        var rotation = random.nextDouble() * Math.PI * 2.0; // between 0 and 2 PI
        var theta = new double[N]; // angles
        for (int i = 0; i < N; i++) {
            theta[i] = rotation + (2.0 * Math.PI * i) / N;
        }

        var maxJitter = (Math.PI * 2.0 / N) * 0.25;
        for (int i = 0; i < N; i++) {
            var jitter = (random.nextDouble() * 2.0 - 1.0) * maxJitter;
            theta[i] += jitter;
        }

        // get the radii using radial noise
        var radii = new double[N];

        int maxHarmonics = 6;
        var baseAmp = 0.45 * roughness;
        double decay = 0.55;

        var phaseCos = new double[maxHarmonics];
        var phaseSin = new double[maxHarmonics];
        for (int k = 0; k < maxHarmonics; k++) {
            phaseCos[k] = random.nextDouble() * Math.PI * 2.0;
            phaseSin[k] = random.nextDouble() * Math.PI * 2.0;
        }

        for (int i = 0; i < N; i++) {
            var t = theta[i];
            var signal = 0.0;
            var amp = baseAmp;
            for (int k = 1; k <= maxHarmonics; k++) {
                var ac = amp * (random.nextDouble() * 0.8 + 0.2);
                var as = amp * (random.nextDouble() * 0.8 + 0.2);
                signal += ac * Math.cos(k * t + phaseCos[k - 1]);
                signal += as * Math.sin(k * t + phaseSin[k - 1]);
                amp *= decay;
            }

            signal += (random.nextDouble() * 2.0 - 1.0) * 0.05 * roughness;
            double r = radius * (1.0 + signal);
            radii[i] = Math.max(minFrac * radius, r);
        }

        normalizeMeanRadius(radii, radius);

        var poly = new ArrayList<Point2D.Double>();

        for (int i = 0; i < N; i++) {
            double x = center.getX() + radii[i] * Math.cos(theta[i]);
            double y = center.getY() + radii[i] * Math.sin(theta[i]);
            poly.add(new Point2D.Double(x, y));
        }

        return poly;
    }

    private static void normalizeMeanRadius(double[] r, double M) {
        var sum = 0.0;
        for (var v : r) sum += v;
        var target = r.length * M;
        var scale = target / sum;
        for (int i = 0; i < r.length; i++) r[i] *= scale;
    }

    private static List<Point2D> getContinentPositions(ContinentSettings settings, long firstSeed, long secondSeed) {
        var rand = new Random(firstSeed);
        var numContinents = rand.nextInt(settings.minContinents(), settings.maxContinents() + 1);

        var stddev = settings.variation() * settings.meanSize();
        var easedSpacing = Math.pow(settings.spacing(), Constants.EASING_EXP);
        var coverage = ((1 - easedSpacing) * Constants.LAND_COVERAGE_MAX) + (easedSpacing * Constants.LAND_COVERAGE_MIN);
        var area = (numContinents * Math.PI * (settings.meanSize() * settings.meanSize() + stddev * stddev)) / coverage;
        int sideLength = (int)Math.round(Math.sqrt(area));

        int half = sideLength / 2;
        int lower = -half;
        int upper = sideLength - half;

        rand = new Random(secondSeed);
        var points = new ArrayList<Point2D>();

        for (int i = 0; i < numContinents; i++) {
            var x = rand.nextDouble(0, 101);
            var z = rand.nextDouble(0, 101);
            var xi = lower + (x / 100.0) * (upper - lower);
            var zi = lower + (z / 100.0) * (upper - lower);
            points.add(new Point2D.Double(xi, zi));
        }

        var bounds = new ArrayList<Point2D>();
        bounds.add(new Point2D.Double(lower, lower));
        bounds.add(new Point2D.Double(lower, upper));
        bounds.add(new Point2D.Double(upper, upper));
        bounds.add(new Point2D.Double(upper, lower));

        return Voronoi.runLloydRelaxation(points, bounds, 3); // TODO use the uniformity setting to control num iters
    }

    private static final long GAMMA = 0x9E3779B97F4A7C15L;

    private static long[] getSeeds(long seed) {
        long seed1 = mix64(seed += GAMMA);
        long seed2 = mix64(seed += GAMMA);
        long seed3 = mix64(seed += GAMMA);
        long seed4 = mix64(seed + GAMMA);
        return new long[]{seed1, seed2, seed3, seed4};
    }

    private static long mix64(long z) {
        z = (z ^ (z >>> 30)) * 0xBF58476D1CE4E5B9L;
        z = (z ^ (z >>> 27)) * 0x94D049BB133111EBL;
        return z ^ (z >>> 31);
    }
}
