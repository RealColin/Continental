package realcolin.continental.client;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import realcolin.continental.Constants;
import realcolin.continental.world.continent.ContinentSettings;
import realcolin.continental.world.continent.Continents;

public class PreviewScreen extends Screen {
    private CreateWorldScreen parent;

    private DynamicTexture previewTexture;
    private NativeImage previewImage;
    private ResourceLocation previewLocation;

    private ContinentSettings settings;
    private long seed;

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
        this.addRenderableWidget(
                Button.builder(Component.literal("Done"), btn -> this.onClose())
                        .bounds((this.width - 120) / 2, this.height - 28, 120, 20)
                        .build()
        );
        buildPreviewImage();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.render(graphics, mouseX, mouseY, partialTicks);

        if (previewLocation != null & previewTexture != null) {
            int imgWidth = previewImage.getWidth();
            int imgHeight = previewImage.getHeight();

            int maxWidth = this.width - 20;
            int maxHeight = this.height - 40;
            double scale = Math.min(maxWidth / (double)imgWidth, maxHeight / (double)imgHeight);
            int drawWidth = Math.max(1, (int)Math.round(imgWidth * scale));
            int drawHeight = Math.max(1, (int)Math.round(imgHeight * scale));
            int x0 = (this.width - drawWidth) / 2;
            int y0 = (this.height - drawHeight) / 2;
            int x1 = x0 + drawWidth;
            int y1 = y0 + drawHeight;

            graphics.blit(previewLocation, x0, y0, x1, y1, 0f, 1f, 0f, 1f);
        }
    }

    // TODO draw continents and oceans instead of rainbow gradient
    private void buildPreviewImage() {
        clean();

        var continents = Continents.generate(settings, seed);

        int imgWidth = (int)(this.width * 0.9);
        int imgHeight = (int)(this.height * 0.8);

        previewImage = new NativeImage(NativeImage.Format.RGBA, imgWidth, imgHeight, false);
        for (int y = 0; y < imgHeight; y++) {
            for (int x = 0; x < imgWidth; x++) {
                int r = (int)(255.0 * x / (double)(imgWidth - 1));
                int g = (int)(255.0 * y / (double)(imgHeight - 1));
                int b = 64;
                int a = 255;
                int argb = (a << 24) | (r << 16) | (g << 8) | b;
                previewImage.setPixelABGR(x, y, argb);
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
