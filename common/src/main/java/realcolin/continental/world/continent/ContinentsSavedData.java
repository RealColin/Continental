package realcolin.continental.world.continent;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.List;

// TODO don't need this anymore
public class ContinentsSavedData extends SavedData {

    private static final Codec<List<Continent>> CODEC =
            RecordCodecBuilder.<Continent>create(instance -> instance.group(
                    Codec.INT.fieldOf("x").forGetter(Continent::getX),
                    Codec.INT.fieldOf("z").forGetter(Continent::getZ),
                    Codec.INT.fieldOf("radius").forGetter(Continent::getRadius)
            ).apply(instance, Continent::new)).listOf();

    public static final SavedDataType<ContinentsSavedData> ID = new SavedDataType<>(
            "continental",
            ContinentsSavedData::new,
            RecordCodecBuilder.create(instance -> instance.group(
                CODEC.fieldOf("continents").forGetter(ContinentsSavedData::getContinents)
            ).apply(instance, ContinentsSavedData::new)),
            null
    );

    private List<Continent> continents = new ArrayList<>();

    public ContinentsSavedData() {}

    public ContinentsSavedData(List<Continent> continents) {
        this.continents = continents;
    }

    public List<Continent> getContinents() {
        return continents;
    }

    public static ContinentsSavedData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(ID);
    }

    public boolean isEmpty() {
        return continents.isEmpty();
    }

    public void generate(long seed) {
        continents.clear();
        System.out.println("GENERATED! Seed is " + seed);
        setDirty();
    }
}
