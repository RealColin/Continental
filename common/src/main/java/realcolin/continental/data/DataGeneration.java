package realcolin.continental.data;

import net.minecraft.SharedConstants;
import net.minecraft.server.packs.PackType;
import realcolin.continental.Constants;
import realcolin.continental.world.continent.ContinentSettings;
import realcolin.continental.world.continent.Continents;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class DataGeneration {
    private static final String PACK_FOLDER = "continental_generated";

    public static void createPack(Path path, ContinentSettings settings, long seed) throws IOException {
        Path packRoot = path.resolve(PACK_FOLDER);
        if (Files.exists(packRoot)) {
            Constants.LOG.info("Generated Continental datapack already exists.");
            return;
        }

        try {
            Files.createDirectories(packRoot);

            writeMCMeta(packRoot);
            writeOverworldDensityFunction(packRoot);
            var continents = Continents.generate(settings, seed);
            writeContinents(packRoot, continents);

        } catch (IOException e) {
            Constants.LOG.error("Failed to generate datapack at {}", packRoot, e);
        }
    }

    private static void writeMCMeta(Path path) throws IOException {
        int packFormat = SharedConstants.getCurrentVersion().packVersion(PackType.SERVER_DATA);
        String str = """
                {
                    "pack": {
                        "pack_format": %d,
                        "description": "Continental (generated)"
                    }
                }
                """.formatted(packFormat);
        Files.writeString(path.resolve("pack.mcmeta"), str);
    }

    private static void writeOverworldDensityFunction(Path path) throws IOException {
        String func = """
                {
                    "type": "continental:continent_sampler",
                    "continents": "continental:overworld",
                    "base": {
                        "type": "minecraft:shifted_noise",
                        "noise": "minecraft:continentalness",
                        "shift_x": "minecraft:shift_x",
                        "shift_y": 0,
                        "shift_z": "minecraft:shift_z",
                        "xz_scale": 0.25,
                        "y_scale": 0
                    }
                }
                """;
        var filePath = path.resolve("data/minecraft/worldgen/density_function/overworld");
        Files.createDirectories(filePath);
        Files.writeString(filePath.resolve("continents.json"), func);
    }

    private static void writeContinents(Path path, Continents continents) throws IOException {
        StringBuilder continentsString = new StringBuilder();

        continentsString.append(
                """
                {
                    "continents": [
                """);

        for (var continent : continents.get()) {
            continentsString.append(
                    """
                            {
                                "x": %d,
                                "z": %d,
                                "radius": %d,
                                "points": [
                    """.formatted(continent.getX(), continent.getZ(), continent.getRadius()));
            for (var point : continent.getBoundaryPoints()) {
                continentsString.append(
                        """
                                        {
                                            "x": %f,
                                            "z": %f
                                        },
                        """.formatted(point.getX(), point.getY()));
                if (point.equals(continent.getBoundaryPoints().getLast()))
                    continentsString.deleteCharAt(continentsString.length() - 2);
            }
            continentsString.append(
                    """
                                ]
                            },
                    """);
            if (continent.equals(continents.get().getLast()))
                continentsString.deleteCharAt(continentsString.length() - 2);
        }
//        continentsString.append("\t]\n}");
        var t = continents.getTransitions();
        continentsString.append("\t],\n");
        continentsString.append(
                """
                    "transitions": {
                        "coast": %d,
                        "nearInland": %d,
                        "midInland": %d,
                        "farInland": %d,
                        "ocean": %d,
                        "deepOcean": %d
                    }
                """.formatted(t.coast(), t.nearInland(), t.midInland(), t.farInland(), t.ocean(), t.deepOcean()));
        continentsString.append("\n}");


        var str = continentsString.toString();
        var filePath = path.resolve("data/" + Constants.MOD_ID + "/worldgen/continents");
        Files.createDirectories(filePath);
        Files.writeString(filePath.resolve("overworld.json"), str);
    }
}
