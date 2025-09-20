package realcolin.continental.world.continent;

import net.minecraft.world.level.levelgen.DensityFunction;

public class Continent {
    private final int x;
    private final int z;
    private final int radius;

    public Continent(int x, int z, int radius) {
        this.x = x;
        this.z = z;
        this.radius = radius;
    }

    private double distTo(DensityFunction.FunctionContext context) {
        return Math.sqrt(Math.pow(context.blockX() - x, 2) + Math.pow(context.blockZ() - z, 2));
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
