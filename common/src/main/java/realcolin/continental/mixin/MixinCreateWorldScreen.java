package realcolin.continental.mixin;

import net.minecraft.client.gui.components.tabs.Tab;
import net.minecraft.client.gui.components.tabs.TabManager;
import net.minecraft.client.gui.components.tabs.TabNavigationBar;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import realcolin.continental.client.ContinentalTab;

import java.util.ArrayList;

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
}
