package realcolin.continental.mixin;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.world.level.DataPackConfig;
import net.minecraft.world.level.WorldDataConfiguration;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.WorldData;
import org.lwjgl.system.CallbackI;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import realcolin.continental.data.DataGeneration;
import realcolin.continental.world.continent.ContinentSettings;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Mixin(MinecraftServer.class)
abstract class MixinMinecraftServer {

    @Shadow @Final private LevelStorageSource.LevelStorageAccess storageSource;
    @Shadow public abstract WorldData getWorldData();
    @Shadow @Final private PackRepository packRepository;
    @Shadow protected abstract CompletableFuture<Void> reloadResources(Collection<String> packs);

    @Inject(method = "loadLevel", at = @At("HEAD"))
    private void loadPack(CallbackInfo ci) {
//        var access = storageSource;
//        if (access == null) return;
//
//        Path worldRoot = access.getLevelPath(LevelResource.ROOT);
//        long seed = getWorldData().worldGenOptions().seed();
//        var tempSettings = new ContinentSettings(5, 7, 5000, 0.25);
//
//        try {
//            DataGeneration.createPack(worldRoot, tempSettings, seed);
//            packRepository.reload();
//
//            final String id = "file/continental_generated";
//
//            if (packRepository.getPack(id) == null) {
//                System.out.println("SKIBIDI NOT GOOD PACK MISSING");
//            }
//
//            WorldDataConfiguration cfg = getWorldData().getDataConfiguration();
//            DataPackConfig dpc = cfg.dataPacks();
//
//            ArrayList<String> enabled = new ArrayList<>(dpc.getEnabled());
//            ArrayList<String> disabled = new ArrayList<>(dpc.getDisabled());
//            enabled.remove(id);
//            disabled.remove(id);
//            enabled.addFirst(id);
//
//            DataPackConfig newDpc = new DataPackConfig(enabled, disabled);
//            getWorldData().setDataConfiguration(new WorldDataConfiguration(newDpc, cfg.enabledFeatures()));
//
//            if (packRepository.getPack(id) == null) {
//                System.out.println("JOE BIDEN NOT GOOD PACK MISSING");
//            }
//
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
    }
}
