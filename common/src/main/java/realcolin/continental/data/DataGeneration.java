package realcolin.continental.data;

import net.minecraft.SharedConstants;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataGenerator.PackGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.server.packs.PackType;
import realcolin.continental.Constants;
import realcolin.continental.world.continent.ContinentSettings;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class DataGeneration {
    private static final String PACK_FOLDER = "continental_generated";

    public static void createPack(Path path, ContinentSettings settings, long seed) throws IOException {
        // TODO TEST PRINTING DELETE LATER
        System.out.println("Path: " + path);
        System.out.println("Min Continents: " + settings.minContinents());
        System.out.println("Max Continents: " + settings.maxContinents());
        System.out.println("Seed: " + seed);

        Path packRoot = path.resolve(PACK_FOLDER);
        if (Files.exists(packRoot)) {
            Constants.LOG.debug("Generated Continental datapack already exists");
            return;
        }

        try {
            Files.createDirectories(packRoot);

            int packFormat = SharedConstants.getCurrentVersion().packVersion(PackType.SERVER_DATA);
            String mcmeta = """
            { "pack": { "pack_format": %d, "description": "Continental (generated)" } }
            """.formatted(packFormat);
            Files.writeString(packRoot.resolve("pack.mcmeta"), mcmeta);

            // TODO remove temp continents
            String continents = """
                    {
                        "continents": [
                            {
                                "x": 0,
                                "z": 0,
                                "radius": 1000
                            }
                        ]
                    }
                    """;

            Path dir = packRoot.resolve("data/" + Constants.MOD_ID + "/worldgen/continents");
            Files.createDirectories(dir);
            Files.writeString(dir.resolve("overworld.json"), continents);

            String densityFunc = """
                    {
                      "type": "continental:continent_sampler",
                      "continents": "continental:overworld"
                    }
                    """;

            Path dir2 = packRoot.resolve("data/minecraft/worldgen/density_function/overworld");
            Files.createDirectories(dir2);
            Files.writeString(dir2.resolve("continents.json"), densityFunc);

        } catch (IOException e) {
            Constants.LOG.error("Failed to generate datapack at {}", packRoot, e);
        }

    }

    public static DataGenerator makePack(Path dataGenPath, Path dataGenOutputPath, String name) {
        DataGenerator gen = new DataGenerator(dataGenPath, SharedConstants.getCurrentVersion(), true);
        //PackGenerator packGenerator = gen.new PackGenerator(true, name, new PackOutput(dataGenOutputPath));

        return gen;
    }
}
