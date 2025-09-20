package realcolin.continental.world.densityfunction;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.DensityFunction;
import realcolin.continental.platform.Services;
import realcolin.continental.platform.services.IPlatformHelper;
import realcolin.continental.world.continent.Continent;
import realcolin.continental.world.continent.Continents;

import java.util.List;
import java.util.function.Supplier;

public class ContinentSampler implements DensityFunction.SimpleFunction {

    public static final MapCodec<ContinentSampler> CODEC =
            Continents.CODEC.fieldOf("continents")
                    .xmap(ContinentSampler::new, src -> src.continents);


    private final Holder<Continents> continents;


    public ContinentSampler(Holder<Continents> continents) {
        this.continents = continents;
    }

    @Override
    public double compute(FunctionContext functionContext) {
//        System.out.println("computed");

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
    public KeyDispatchDataCodec<? extends DensityFunction> codec() {
        return new KeyDispatchDataCodec<>(CODEC);
    }
}
