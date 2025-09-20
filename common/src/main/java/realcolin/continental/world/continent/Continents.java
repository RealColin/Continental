package realcolin.continental.world.continent;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.resources.RegistryFileCodec;
import realcolin.continental.ContinentalRegistries;

import java.util.List;

public class Continents {
    private static final Codec<Continent> C_CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    Codec.INT.fieldOf("x").forGetter(Continent::getX),
                    Codec.INT.fieldOf("z").forGetter(Continent::getZ),
                    Codec.INT.fieldOf("radius").forGetter(Continent::getRadius)
            ).apply(instance, Continent::new));

    public static final Codec<Continents> DIRECT_CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                C_CODEC.listOf().fieldOf("continents").forGetter(src -> src.continents)
            ).apply(instance, Continents::new));

    public static final Codec<Holder<Continents>> CODEC = RegistryFileCodec.create(ContinentalRegistries.CONTINENTS, DIRECT_CODEC);

    private final List<Continent> continents;
    public Continents(List<Continent> continents) {
        this.continents = continents;
    }
}
