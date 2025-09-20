package realcolin.continental.world.densityfunction;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.DensityFunction;
import org.jetbrains.annotations.NotNull;
import realcolin.continental.world.continent.Continents;

public class ContinentSampler implements DensityFunction.SimpleFunction {

    public static final MapCodec<ContinentSampler> CODEC =
            Continents.CODEC.fieldOf("continents")
                    .xmap(ContinentSampler::new, src -> src.continents);


    private final Holder<Continents> continents;


    public ContinentSampler(Holder<Continents> continents) {
        this.continents = continents;
    }

    @Override
    public double compute(@NotNull FunctionContext functionContext) {

        return -0.7;
    }

    @Override
    public double minValue() {
        return -100.2;
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
