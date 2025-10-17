package realcolin.continental.client;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import realcolin.continental.Constants;
import realcolin.continental.world.continent.ContinentSettings;
import realcolin.continental.world.continent.Continents;

import java.awt.*;

public class PreviewScreen extends Screen {
    private final CreateWorldScreen parent;
    private final ContinentSettings settings;
    private final long seed;

    private DynamicTexture previewTexture;
    private NativeImage previewImage;
    private ResourceLocation previewLocation;

    protected PreviewScreen(CreateWorldScreen screen, ContinentSettings settings, long seed) {
        super(Component.empty());
        Constants.LOG.info("Constructing Preview Screen");
        this.parent = screen;
        this.settings = settings;
        this.seed = seed;
    }

    @Override
    public void onClose() {
        super.onClose();
        this.minecraft.setScreen(parent);
    }

    @Override
    protected void init() {
        super.init();
//        Constants.LOG.info("Initializing Preview Screen.");
        var title = new StringWidget(Component.literal("World Preview"), parent.getFont());
        title.setPosition(10, 10);

        this.addRenderableWidget(title);
        this.addRenderableWidget(
                Button.builder(Component.literal("Done"), btn -> this.onClose())
                        .bounds((this.width - 120) / 2, this.height - 24, 120, 20)
                        .build()
        );
        this.addRenderableWidget(
                Button.builder(Component.literal("Hi"), btn -> {})
                        .bounds(20, this.height - 24, 120, 20)
                        .build()
        );
        // TODO add functionality for this please
        this.addRenderableWidget(
                Button.builder(Component.literal("Randomize Seed"), btn -> {})
                        .bounds(this.width - 140, this.height - 24, 120, 20)
                        .build()
        );
        buildPreviewImage();
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.render(graphics, mouseX, mouseY, partialTicks);

        if (previewLocation != null & previewTexture != null) {
            int imgWidth = previewImage.getWidth();
            int imgHeight = previewImage.getHeight();

            int x0 = 0;
            int y0 = 28;
            int x1 = x0 + imgWidth;
            int y1 = y0 + imgHeight;

            graphics.blit(previewLocation, x0, y0, x1, y1, 0f, 1f, 0f, 1f);
        }
    }

    // TODO draw continents and oceans instead of rainbow gradient
    private void buildPreviewImage() {
        clean();

        var continents = Continents.generate(settings, seed);
        int minX = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxY = Integer.MIN_VALUE;

        for (var c : continents.get()) {
            int smallX = c.getX() - c.getRadius();
            int bigX = c.getX() + c.getRadius();
            int smallY = c.getZ() - c.getRadius();
            int bigY = c.getZ() + c.getRadius();

            if (smallX < minX)
                minX = smallX;
            if (bigX > maxX)
                maxX = bigX;
            if (smallY < minY)
                minY = smallY;
            if (bigY > maxY)
                maxY = bigY;
        }

        var max = Math.max(maxX, maxY);
        var min = Math.min(minX, minY);

        int imgWidth = this.width;
        int imgHeight = this.height - 56;

        double ratio = (double)imgWidth / (double)imgHeight;

        var rmax = ratio * max;
        var rmin = ratio * min;

        // TODO select color based on distance to ocean
        previewImage = new NativeImage(NativeImage.Format.RGBA, imgWidth, imgHeight, false);
        for (int y = 0; y < imgHeight; y++) {
            for (int x = 0; x < imgWidth; x++) {
                var xi = rmin + (x / (float)imgWidth) * (rmax - rmin);
                var yi = min + (y / (float)imgHeight) * (max - min);
                var val = continents.compute(new Point((int)Math.round(xi), Math.round(yi)));

                int g, b, r = 0;
                if (val <= -0.19) {
                    b = 255;
                    g = 0;
                } else if (val <= -0.11) {
                    b = 0;
                    g = 50;
                } else if (val <= 0.03) {
                    b = 0;
                    g = 150;
                } else if (val <= 0.3) {
                    b = 0;
                    g = 200;
                } else {
                    b = 0;
                    g = 250;
                }

//                if (val >= -0.20) {
//                    g = (int)(100 + (val - -0.20) / (1.0 - -0.20) * (255 - 100));
//                    b = 0;
//                } else {
//                    g = 0;
//                    b = 255;
//                }

                int a = 255;
                int abgr = (a << 24) | (b << 16) | (g << 8) | r;
                previewImage.setPixelABGR(x, y, abgr);
            }
        }

        previewTexture = new DynamicTexture(() -> "Test Label", previewImage);

        TextureManager tm = Minecraft.getInstance().getTextureManager();
        previewLocation = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "preview_screen/preview");
        tm.register(previewLocation, previewTexture);
    }

    private void clean() {
        if (previewTexture != null) {
            TextureManager tm = Minecraft.getInstance().getTextureManager();
            if (previewLocation != null) {
                tm.release(previewLocation);
            }
            previewTexture.close();
            previewTexture = null;
            previewLocation = null;
            previewImage = null;
        }
    }
}
