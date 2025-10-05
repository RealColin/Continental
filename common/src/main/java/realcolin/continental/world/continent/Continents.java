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
    private static final Codec<Continent> C_CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    Codec.INT.fieldOf("x").forGetter(Continent::getX),
                    Codec.INT.fieldOf("z").forGetter(Continent::getZ),
                    Codec.INT.fieldOf("radius").forGetter(Continent::getRadius)
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
            var dist = c.distTo(point);
            var val = 1.0 - ((1.2 / c.getRadius()) * dist);

            if (val > maxVal)
                maxVal = val;
        }

        return Math.max(-1.0, maxVal);
    }

    public static Continents generate(ContinentSettings settings, long seed) {
        /* Get seeds for the generators based off the world seed */
        var seeds = getSeeds(seed);
        var numConsSeed = seeds[0];
        var randPointsSeed = seeds[1];
        var randRadiiSeed = seeds[2];

        /* Get the Continent positions */
        var positions = getContinentPositions(settings, numConsSeed, randPointsSeed);

        /* Get the radius for each Continent */

        var rand = new Random(randRadiiSeed);
        var stddev = settings.variation() * settings.meanSize();
        var minRadius = 0.2 * settings.meanSize(); // TODO put the 0.2 in Constants maybe?
        var maxRadius = 1.8 * settings.meanSize(); // TODO put the 1.8 in Constants maybe?

        var list = new ArrayList<Continent>();

        for (var p : positions) {
            var radius = Math.clamp(rand.nextGaussian(settings.meanSize(), stddev), minRadius, maxRadius);
            var con = new Continent((int)p.getX(), (int)p.getY(), (int) Math.round(radius));
            list.add(con);
        }

        return new Continents(list);
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

        // TODO change this to generate between 0-100 and then interpolate to fit the lower-upper range
        // TODO that way, changing the "spacing" value doesn't affect the position of the continents
//        for (int i = 0; i < numContinents; i++) {
//            int x = rand.nextInt(lower, upper);
//            int z = rand.nextInt(lower, upper);
//            points.add(new Point2D.Double(x, z));
//        }
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
        long seed3 = mix64(seed + GAMMA);
        return new long[]{seed1, seed2, seed3};
    }

    private static long mix64(long z) {
        z = (z ^ (z >>> 30)) * 0xBF58476D1CE4E5B9L;
        z = (z ^ (z >>> 27)) * 0x94D049BB133111EBL;
        return z ^ (z >>> 31);
    }
}
