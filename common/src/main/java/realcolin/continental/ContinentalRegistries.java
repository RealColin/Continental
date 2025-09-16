package realcolin.continental;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import realcolin.continental.world.continent.Continents;

public class ContinentalRegistries {
    public static final ResourceKey<Registry<Continents>> CONTINENTS = ResourceKey.createRegistryKey(ResourceLocation.parse("worldgen/continents"));
}
