package realcolin.continental.mixin;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.tabs.Tab;
import net.minecraft.client.gui.components.tabs.TabManager;
import net.minecraft.client.gui.components.tabs.TabNavigationBar;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.WorldLoader;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.world.level.DataPackConfig;
import net.minecraft.world.level.WorldDataConfiguration;
import net.minecraft.world.level.levelgen.*;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import realcolin.continental.Constants;
import realcolin.continental.client.ContinentalTab;
import realcolin.continental.data.DataGeneration;
import realcolin.continental.world.continent.ContinentSettings;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@Mixin(CreateWorldScreen.class)
public abstract class MixinCreateWorldScreen extends Screen {

    @Unique private boolean isReentry = false;

    @Shadow @Final private TabManager tabManager;
    @Shadow @Final private HeaderAndFooterLayout layout;
    @Shadow private TabNavigationBar tabNavigationBar;
    @Shadow @Final WorldCreationUiState uiState;

    @Shadow abstract void onCreate();

    @Shadow abstract Pair<Path, PackRepository> getDataPackSelectionSettings(WorldDataConfiguration worldDataConfiguration);

    @Invoker("createDefaultLoadConfig")
    static WorldLoader.InitConfig createDefaultLoadConfig(PackRepository ignoredRepo, WorldDataConfiguration ignoredCfg) {
        throw new AssertionError(); // Mixin rewrites this
    }

    protected MixinCreateWorldScreen(Component title) {
        super(title);
    }

    // Maybe see if I can use mixins to modify the args of
    // the original construction of the nav bar instead of making
    // it for a second time

    @Inject(method = "init", at = @At("RETURN"))
    private void addContinentsTab(CallbackInfo ci) {
        var tabs = new ArrayList<>(this.tabNavigationBar.getTabs());
        tabs.add(new ContinentalTab(this, layout));

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

    @Inject(method = "repositionElements", at = @At("RETURN"))
    private void onResize(CallbackInfo ci) {
        if (tabNavigationBar.getTabs().size() == 3)
            return;

        var tab = tabNavigationBar.getTabs().get(3);

        if (tab instanceof ContinentalTab ctab) {
            ctab.resize(this.width);
        }
    }

    @Inject(method = "onCreate", at = @At("HEAD"), cancellable = true)
    private void onCreateHead(CallbackInfo ci) {
        if (isReentry) return;
        ci.cancel();

        Constants.LOG.info("Beginning pack creation");

        var pair = getDataPackSelectionSettings(uiState.getSettings().dataConfiguration());
        var dir = pair.getFirst();

        /* Get the seed/settings and generate the files */
        long seed = uiState.getSettings().options().seed();

        // TODO do this better
        var settings = new ContinentSettings(5, 7, 5000, 0.250, 0.500, 0.500);
        for (var tab : tabNavigationBar.getTabs()) {
            if (tab instanceof ContinentalTab ct) {
                Constants.LOG.debug("Got settings from Continental tab");
                settings = ct.getSettings();
                break;
            }
        }

        try {
            DataGeneration.createPack(dir, settings, seed);
        } catch (IOException e) {
            Constants.LOG.error("Failed to generate continents pack in temp directory", e);
        }

        /* Make sure the files are properly loaded as a datapack*/
        var repo = pair.getSecond();
        repo.reload();

        if (repo.addPack("file/continental_generated")) {
            var selected = repo.getSelectedIds().stream().toList();
            var unSelected = repo.getAvailableIds().stream().filter((x) -> !selected.contains(x)).collect(ImmutableList.toImmutableList());
            var wdc = new WorldDataConfiguration(new DataPackConfig(selected, unSelected), this.uiState.getSettings().dataConfiguration().enabledFeatures());
            applyPacks(repo, wdc, this::recall, (Throwable err) -> Constants.LOG.error(String.valueOf(err)));

        } else {
            recall();
        }
    }

    private void recall() {
        isReentry = true;
        onCreate();
        isReentry = false;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void applyPacks(PackRepository repo, WorldDataConfiguration wdc, Runnable onSuccess, Consumer<Throwable> onFail) {
        var initConfig = createDefaultLoadConfig(repo, wdc);
        CompletableFuture future = WorldLoader.load(initConfig, (context) -> {
            if (context.datapackWorldgen().lookupOrThrow(Registries.WORLD_PRESET).listElements().findAny().isEmpty()) {
                throw new IllegalStateException("Needs at least one world preset to continue");
            } else if (context.datapackWorldgen().lookupOrThrow(Registries.BIOME).listElements().findAny().isEmpty()) {
                throw new IllegalStateException("Needs at least one biome continue");
            } else {
                WorldCreationContext worldcreationcontext = this.uiState.getSettings();
                DynamicOps<JsonElement> dynamicops = worldcreationcontext.worldgenLoadContext().createSerializationContext(JsonOps.INSTANCE);
                DataResult<JsonElement> dataresult = WorldGenSettings.encode(dynamicops, worldcreationcontext.options(), worldcreationcontext.selectedDimensions()).setLifecycle(Lifecycle.stable());
                DynamicOps<JsonElement> dynamicops1 = context.datapackWorldgen().createSerializationContext(JsonOps.INSTANCE);
                WorldGenSettings worldgensettings = dataresult.flatMap((p_232895_) -> WorldGenSettings.CODEC.parse(dynamicops1, p_232895_)).getOrThrow((p_337413_) -> new IllegalStateException("Error parsing worldgen settings after loading data packs: " + p_337413_));
                return new WorldLoader.DataLoadOutput(new DataPackReloadCookie(worldgensettings, context.dataConfiguration()), context.datapackDimensions());
            }
        }, (resManager, reloadableRes, regAccess, cookie) -> {
            resManager.close();
            return new WorldCreationContext(((DataPackReloadCookie)cookie).worldGenSettings(), regAccess, reloadableRes, ((DataPackReloadCookie)cookie).dataConfiguration());
        }, Util.backgroundExecutor(), Minecraft.getInstance()).thenApply(ctx -> { ((WorldCreationContext)ctx).validate(); return ctx; });
        future.thenAcceptAsync(ctx -> {
            this.uiState.setSettings((WorldCreationContext)ctx);
            onSuccess.run();
        }, Minecraft.getInstance()).exceptionally(err -> {
            onFail.accept((Throwable) err);
            return null;
        });

    }
}
