package realcolin.continental.densityfunction;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.DensityFunction;

import java.util.List;
import java.util.stream.Collectors;

public class MultiMax implements DensityFunction.SimpleFunction {

    private static final Codec<List<DensityFunction>> LIST = DensityFunction.HOLDER_HELPER_CODEC.listOf();

    public static final MapCodec<MultiMax> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.DOUBLE.fieldOf("min_val").forGetter(src -> src.minValue),
            Codec.DOUBLE.fieldOf("max_val").forGetter(src -> src.maxValue),
            LIST.fieldOf("functions").forGetter(src -> src.functions)
    ).apply(instance, MultiMax::new));

    private final double minValue;
    private final double maxValue;
    private final List<DensityFunction> functions;

    public MultiMax(double minValue, double maxValue, List<DensityFunction> functions) {
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.functions = functions;
    }

    @Override
    public double compute(FunctionContext functionContext) {
        var max = minValue();

        for (var func : functions) {
            double x = func.compute(functionContext);
            if (x > max)
                max = x;
        }

        return Math.min(max, maxValue());
    }

    @Override
    public double minValue() {
        return minValue;
    }

    @Override
    public double maxValue() {
        return maxValue;
    }

    @Override
    public DensityFunction mapAll(Visitor visitor) {
        return new MultiMax(minValue, maxValue, functions.stream().map(x -> x.mapAll(visitor)).toList());
    }

    @Override
    public KeyDispatchDataCodec<? extends DensityFunction> codec() {
        return new KeyDispatchDataCodec<>(CODEC);
    }
}
