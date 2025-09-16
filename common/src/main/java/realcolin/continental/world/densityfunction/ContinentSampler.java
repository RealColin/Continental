package realcolin.continental.world.densityfunction;

import com.mojang.serialization.MapCodec;
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
import realcolin.continental.world.continent.ContinentsSavedData;

import java.util.List;
import java.util.function.Supplier;

public class ContinentSampler implements DensityFunction.SimpleFunction {

    public static final MapCodec<ContinentSampler> CODEC =
            ResourceKey.codec(Registries.DIMENSION).fieldOf("dimension")
                    .xmap(ContinentSampler::new, a -> a.levelKey);

    private final ResourceKey<Level> levelKey;
    private transient ContinentsSavedData cached;

    public ContinentSampler(ResourceKey<Level> levelKey) {
        this.levelKey = levelKey;
    }

    @Override
    public double compute(FunctionContext functionContext) {
//        var level = Services.PLATFORM.get(levelKey);
//        var saved = ContinentsSavedData.get(level);
        if (cached == null) {
//            System.out.println("UM THIS SHOULDN'T BE NULL");
        }


        return -0.7;
    }

    public void bind(ContinentsSavedData data) {
        System.out.println("BOUND");
        this.cached = data;
    }

    public ResourceKey<Level> level() {
        return levelKey;
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
    public KeyDispatchDataCodec<? extends DensityFunction> codec() {
        return new KeyDispatchDataCodec<>(CODEC);
    }
}
