package realcolin.continental.mixin;

import net.minecraft.client.gui.components.tabs.Tab;
import net.minecraft.client.gui.components.tabs.TabManager;
import net.minecraft.client.gui.components.tabs.TabNavigationBar;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.WorldCreationContext;
import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.server.RegistryLayer;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.WorldData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import realcolin.continental.Constants;
import realcolin.continental.client.ContinentalTab;
import realcolin.continental.data.DataGeneration;
import realcolin.continental.world.continent.ContinentSettings;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Optional;

@Mixin(CreateWorldScreen.class)
public class MixinCreateWorldScreen extends Screen {

    @Shadow @Final private TabManager tabManager;
    @Shadow private TabNavigationBar tabNavigationBar;
    @Shadow @Final WorldCreationUiState uiState;

    protected MixinCreateWorldScreen(Component title) {
        super(title);
    }

    // Maybe see if I can use mixins to modify the args of
    // the original construction of the nav bar instead of making
    // it for a second time

    @Inject(method = "init", at = @At("RETURN"))
    private void addContinentsTab(CallbackInfo ci) {
        var tabs = new ArrayList<>(this.tabNavigationBar.getTabs());
        tabs.add(new ContinentalTab(this));

        TabNavigationBar newBar = TabNavigationBar.builder(this.tabManager, this.width)
                .addTabs(tabs.toArray(new Tab[0]))
                .build();

        this.removeWidget(this.tabNavigationBar);
        this.tabNavigationBar = newBar;
        this.addRenderableWidget(this.tabNavigationBar);

        this.tabNavigationBar.selectTab(0, false);
        this.uiState.onChanged();
        this.repositionElements();
    }

    // THIS ONE PRINTS FIRST
    @Inject(method = "onCreate", at = @At("HEAD"))
    private void printJoe(CallbackInfo ci) {
        System.out.println("JOE BIDEN");
    }

    @Inject(
            method = "createNewWorld(Lnet/minecraft/core/LayeredRegistryAccess;Lnet/minecraft/world/level/storage/WorldData;)Z",
            at = @At("HEAD")
    )
    private void onCreateNewWorld(LayeredRegistryAccess<RegistryLayer> regs, WorldData worldData, CallbackInfoReturnable<Boolean> cir) {
        try {
            String saveName = uiState.getTargetFolder();
            LevelStorageSource storage = minecraft.getLevelSource();

            try (LevelStorageSource.LevelStorageAccess access = storage.createAccess(saveName)) {
                Path worldRoot = access.getLevelPath(LevelResource.ROOT);

                WorldCreationContext ctx = uiState.getSettings();
                long seed = ctx.options().seed();

                // TODO find a better way to do this because wtf
                var settings = new ContinentSettings(5, 7, 5000, 0.250);
                for (var tab : tabNavigationBar.getTabs()) {
                    if (tab instanceof ContinentalTab ct) {
                        settings = ct.getSettings();
                        break;
                    }
                }
                
                DataGeneration.createPack(worldRoot, settings, seed);
            }

        } catch (NullPointerException | IOException e) {
            Constants.LOG.error("Could not begin generation of Continental datapack");
        }
    }


}
