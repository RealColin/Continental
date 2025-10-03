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

    // TODO REDO continent placement and clean up
    public static Continents generate(ContinentSettings settings, long seed) {
        var list = new ArrayList<Continent>();

        var stdv = settings.variation() * settings.meanSize();
        System.out.println("STD DEV: " + stdv); // TODO delete this once verified

        var seeds = getSeeds(seed);

        var rand = new Random(seeds[0]);
        var numContinents = rand.nextInt((int)settings.minContinents(), (int)settings.maxContinents() + 1); // TODO change longs to ints in ContinentSettings

        var easedSpacing = Math.pow(settings.spacing(), Constants.EASING_EXP);
        var coverage = ((1 - easedSpacing) * Constants.LAND_COVERAGE_MAX) + (easedSpacing * Constants.LAND_COVERAGE_MIN);
        var area = (numContinents * Math.PI * settings.meanSize() * settings.meanSize()) / coverage;
        int sideLength = (int)Math.round(Math.sqrt(area));

        // TODO delete these after testing
        System.out.println("Number of Continents: " + numContinents);
        System.out.println("Side length: " + sideLength);

        var points = new ArrayList<Point2D>();
        rand = new Random(seeds[1]);
        int half = sideLength / 2;
        int lower = -half;
        int upper = sideLength - half;

        for (int i = 0; i < numContinents; i++) {
            int x = rand.nextInt(lower, upper);
            int z = rand.nextInt(lower, upper);
            points.add(new Point2D.Double(x, z));
        }

        // TODO delete this after testing
        for (var p : points) {
            System.out.println(p);
        }

        // TODO do this better somehow
        var bounds = new ArrayList<Point2D>();
        bounds.add(new Point2D.Double(lower, lower));
        bounds.add(new Point2D.Double(lower, upper));
        bounds.add(new Point2D.Double(upper, upper));
        bounds.add(new Point2D.Double(upper, lower));

        // TODO figure out how to pick numberOfIterations
        var continentCenters = Voronoi.runLloydRelaxation(points, bounds, 3);

        // TODO delete this
        System.out.println("CONTINENT CENTERS:");
        for (var p : continentCenters) {
            System.out.println(p);
        }

        rand = new Random(seeds[2]);
        for (var p : continentCenters) {
            var radius = rand.nextGaussian(settings.meanSize(), stdv);
            var con = new Continent((int)p.getX(), (int)p.getY(), (int) Math.round(radius));
            list.add(con);
        }

        return new Continents(list);
    }

    public List<Continent> get() {
        return continents;
    }


    private static final long GAMMA = 0x9E3779B97F4A7C15L;

    private static long[] getSeeds(long seed) {
        long seed1 = mix64(seed += GAMMA);
        long seed2 = mix64(seed += GAMMA);
        long seed3 = mix64(seed += GAMMA);
        long seed4 = mix64(seed += GAMMA);
        return new long[]{seed1, seed2, seed3, seed4};
    }

    private static long mix64(long z) {
        z = (z ^ (z >>> 30)) * 0xBF58476D1CE4E5B9L;
        z = (z ^ (z >>> 27)) * 0x94D049BB133111EBL;
        return z ^ (z >>> 31);
    }
}
