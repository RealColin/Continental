package realcolin.continental.world.continent;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

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

    private final List<Continent> continents;
    public Continents(List<Continent> continents) {
        this.continents = continents;
    }
}
