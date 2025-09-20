package realcolin.continental;


import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import realcolin.continental.world.continent.Continents;
import realcolin.continental.world.densityfunction.ContinentSampler;
import realcolin.continental.world.densityfunction.GradientDist;
import realcolin.continental.world.densityfunction.MultiMax;

@Mod(Constants.MOD_ID)
public class ContinentalNeoForge {

    private static final DeferredRegister<MapCodec<? extends DensityFunction>> DENSITY_FUNCTIONS =
            DeferredRegister.create(BuiltInRegistries.DENSITY_FUNCTION_TYPE.key(), Constants.MOD_ID);

    public ContinentalNeoForge(IEventBus eventBus) {
        Continental.init();

        DENSITY_FUNCTIONS.register("multimax", () -> MultiMax.CODEC);
        DENSITY_FUNCTIONS.register("gradient_dist", () -> GradientDist.CODEC);
        DENSITY_FUNCTIONS.register("continent_sampler", () -> ContinentSampler.CODEC);
        DENSITY_FUNCTIONS.register(eventBus);

        eventBus.addListener(ContinentalNeoForge::registerData);
    }

    public static void registerData(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(ContinentalRegistries.CONTINENTS, Continents.DIRECT_CODEC);
    }
}