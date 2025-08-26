package realcolin.continental;


import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.DeferredRegister;
import realcolin.continental.densityfunction.GradientDist;
import realcolin.continental.densityfunction.MultiMax;

@Mod(Constants.MOD_ID)
public class ContinentalNeoForge {

    private static final DeferredRegister<MapCodec<? extends DensityFunction>> DENSITY_FUNCTIONS =
            DeferredRegister.create(BuiltInRegistries.DENSITY_FUNCTION_TYPE.key(), Constants.MOD_ID);

    public ContinentalNeoForge(IEventBus eventBus) {
        Continental.init();

        DENSITY_FUNCTIONS.register("multimax", () -> MultiMax.CODEC);
        DENSITY_FUNCTIONS.register("gradient_dist", () -> GradientDist.CODEC);
        DENSITY_FUNCTIONS.register(eventBus);
    }
}