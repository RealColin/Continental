package realcolin.continental.client;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class ContinentalScreen extends Screen {
    private final CreateWorldScreen parent;

    public ContinentalScreen(CreateWorldScreen parent) {
        super(Component.literal("Continental"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, b -> {
            this.minecraft.setScreen(parent);
        }).pos(this.width/2 - 110, this.height - 28).size(100, 20).build());
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(parent);
    }
}
