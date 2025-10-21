package realcolin.continental.world.densityfunction;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.DensityFunction;
import org.jetbrains.annotations.NotNull;
import realcolin.continental.world.continent.Continents;

import java.awt.*;
import java.util.concurrent.ConcurrentHashMap;

public class ContinentSampler implements DensityFunction.SimpleFunction {

    public static final MapCodec<ContinentSampler> CODEC =
            RecordCodecBuilder.mapCodec(instance -> instance.group(
                    Continents.CODEC.fieldOf("continents").forGetter(src -> src.continentsHolder),
                    DensityFunction.HOLDER_HELPER_CODEC.fieldOf("base").forGetter(src -> src.base)
            ).apply(instance, ContinentSampler::new));

    private final Holder<Continents> continentsHolder;
    private final DensityFunction base;
    private final ConcurrentHashMap<FunctionContext, Double> cache = new ConcurrentHashMap<>(4096);

    public ContinentSampler(Holder<Continents> continentsHolder, DensityFunction base) {
        this.continentsHolder = continentsHolder;
        this.base = base;
    }

    @Override
    public double compute(@NotNull FunctionContext functionContext) {
        if (cache.containsKey(functionContext)) {
            var a = cache.get(functionContext);
            if (a != null) {
                return a;
            }
        }

        var val = continentsHolder.value().compute(new Point(functionContext.blockX(), functionContext.blockZ()));

        if (cache.size() >= 1000) {
            cache.clear();
        }

        cache.put(functionContext, val);
        return val;
    }

    @Override
    public double minValue() {
        return -1.2;
    }

    @Override
    public double maxValue() {
        return 1;
    }

    @Override
    public @NotNull KeyDispatchDataCodec<? extends DensityFunction> codec() {
        return new KeyDispatchDataCodec<>(CODEC);
    }
}
