package realcolin.continental.densityfunction;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.DensityFunction;

public class GradientDist implements DensityFunction.SimpleFunction {
    public static final MapCodec<GradientDist> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.fieldOf("point_x").forGetter(src -> src.point_x),
            Codec.INT.fieldOf("point_z").forGetter(src -> src.point_z),
            Codec.DOUBLE.fieldOf("min_val").forGetter(src -> src.min_val),
            Codec.DOUBLE.fieldOf("max_val").forGetter(src -> src.max_val),
            DensityFunction.HOLDER_HELPER_CODEC.fieldOf("base").forGetter(src -> src.base),
            Codec.DOUBLE.fieldOf("dropoff").forGetter(src -> src.dropoff)
    ).apply(instance, GradientDist::new));

    private final int point_x;
    private final int point_z;
    private final double min_val;
    private final double max_val;
    private final DensityFunction base;
    private final double dropoff;

    public GradientDist(int point_x, int point_z, double min_val, double max_val, DensityFunction base, double dropoff) {
        this.point_x = point_x;
        this.point_z = point_z;
        this.min_val = min_val;
        this.max_val = max_val;
        this.base = base;
        this.dropoff = dropoff;
    }

    @Override
    public double compute(FunctionContext functionContext) {
        var dist = calcEuclidianDist(functionContext.blockX(), functionContext.blockZ());
        var base_val = base.compute(functionContext);

        var res = Math.clamp(base_val - (dropoff * dist), min_val, max_val);

        return res;
    }

    private double calcEuclidianDist(int x, int z) {
        return Math.sqrt(Math.pow(point_x - x, 2) + Math.pow(point_z - z, 2));
    }

    @Override
    public double minValue() {
        return this.min_val;
    }

    @Override
    public double maxValue() {
        return this.max_val;
    }

    @Override
    public DensityFunction mapAll(Visitor visitor) {
        return new GradientDist(point_x, point_z, min_val, max_val, base.mapAll(visitor), dropoff);
    }

    @Override
    public KeyDispatchDataCodec<? extends DensityFunction> codec() {
        return new KeyDispatchDataCodec<>(CODEC);
    }
}
