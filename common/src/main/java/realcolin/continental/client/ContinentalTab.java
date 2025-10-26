package realcolin.continental.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.tabs.GridLayoutTab;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import realcolin.continental.world.continent.ContinentSettings;

public class ContinentalTab extends GridLayoutTab {
    private final Slider minContinents;
    private final Slider maxContinents;
    private final Slider avgContinentSize;
    private final Slider continentSizeVariation;
    private final Slider continentSpacing;
    private final Slider spacingUniformity;
    private final ContinentOptionsList list;
    private final HeaderAndFooterLayout headerAndFooterLayout;

    public ContinentalTab(Screen parent, HeaderAndFooterLayout headerAndFooterLayout) {
        super(Component.literal("Continental"));
        this.headerAndFooterLayout = headerAndFooterLayout;

        GridLayout.RowHelper row = this.layout.columnSpacing(10).rowSpacing(8).createRowHelper(2);
        LayoutSettings cell = row.newCellSettings();

        /* labels */
        var continentSettingsLabel = new StringWidget(Component.literal("Continent Settings"), parent.getFont());
        continentSettingsLabel.setWidth(200);
        continentSettingsLabel.setHeight(20);
        continentSettingsLabel.alignLeft();
        var minLabel = new StringWidget(Component.literal("Min Continents"), parent.getFont());
        minLabel.setWidth(200);
        minLabel.setHeight(20);
        minLabel.alignLeft();
        var maxLabel = new StringWidget(Component.literal("Max Continents"), parent.getFont());
        maxLabel.setWidth(200);
        maxLabel.setHeight(20);
        maxLabel.alignLeft();
        var avgSizeLabel = new StringWidget(Component.literal("Mean Continent Size"), parent.getFont());
        avgSizeLabel.setWidth(200);
        avgSizeLabel.setHeight(20);
        avgSizeLabel.alignLeft();
        var variationLabel = new StringWidget(Component.literal("Continent Size Variance"), parent.getFont());
        variationLabel.setWidth(200);
        variationLabel.setHeight(20);
        variationLabel.alignLeft();
        var spacingLabel = new StringWidget(Component.literal("Continent Spacing"), parent.getFont());
        spacingLabel.setWidth(200);
        spacingLabel.setHeight(20);
        spacingLabel.alignLeft();
        var uniformityLabel = new StringWidget(Component.literal("Continent Spacing Uniformity"), parent.getFont());
        uniformityLabel.setWidth(200);
        uniformityLabel.setHeight(20);
        uniformityLabel.alignLeft();

        /* sliders */
        maxContinents = new Slider(200, 20,  7.0, 1, 20, (s, v) -> v, true);
        minContinents = new Slider(200, 20,  5.0, 1, 20, (s, v) -> {
            if (maxContinents.value() < v) {
                v = maxContinents.value();
            }
            return v;
        }, true);
        maxContinents.setCallback((s, v ) -> {
            if (minContinents.value() > v) {
                v = minContinents.value();
            }
            return v;
        });
        avgContinentSize = new Slider(200, 20, 5000, 2000, 20000, (s, v) -> v, true);
        continentSizeVariation = new Slider(200, 20, 0.25, 0, 0.8, (s, v) -> v, false);
        continentSpacing = new Slider(200, 20, 0.25, 0, 1.0, (s, v) -> v, false);
        spacingUniformity = new Slider(200, 20, 0.25, 0, 1.0, (s, v) -> v, false);


        /* tooltips */
        minContinents.setTooltip(Tooltip.create(Component.literal("Minimum number of continents to generate.")));
        maxContinents.setTooltip(Tooltip.create(Component.literal("Maximum number of continents to generate.")));
        avgContinentSize.setTooltip(Tooltip.create(Component.literal("Average radius of the generated continents.")));
        continentSizeVariation.setTooltip(Tooltip.create(Component.literal("How varied the continents are in size. The higher the value, the more variation.")));
        continentSpacing.setTooltip(Tooltip.create(Component.literal("How spread apart the continents are. Higher value means continents are further apart.")));
        spacingUniformity.setTooltip(Tooltip.create(Component.literal("How uniform the spacing between continents is. The higher the value, the more uniform the spacing.")));

        /* preview screen button */
        var button = new Button.Builder(Component.literal("Open Preview Screen"),
                (b) -> Minecraft.getInstance().setScreen(
                        new PreviewScreen((CreateWorldScreen)parent, getSettings(), ((CreateWorldScreen) parent).getUiState().getSettings().options().seed())));

        this.list = new ContinentOptionsList(parent.width - 20, parent.height -33 - 33);


        /* add everything to the screen */
        list.addSingleWidget(continentSettingsLabel);
        list.addDoubleWidget(minLabel, minContinents);
        list.addDoubleWidget(maxLabel, maxContinents);
        list.addDoubleWidget(avgSizeLabel, avgContinentSize);
        list.addDoubleWidget(variationLabel, continentSizeVariation);
        list.addDoubleWidget(spacingLabel, continentSpacing);
        list.addDoubleWidget(uniformityLabel, spacingUniformity);
        list.addSingleWidget(button.build());

        row.addChild(list, cell);
    }

    public void resize(int width) {
        list.updateSize(width, headerAndFooterLayout);
        list.update(width);
    }

    public ContinentSettings getSettings() {
        int minCon = (int)Math.round(minContinents.lerpedValue());
        int maxCon = (int)Math.round(maxContinents.lerpedValue());
        long avgSize = Math.round(avgContinentSize.lerpedValue());
        double var = continentSizeVariation.lerpedValue();
        double spacing = continentSpacing.lerpedValue();
        double uniformity = spacingUniformity.lerpedValue();

        return new ContinentSettings(minCon, maxCon, avgSize, var, spacing, uniformity);
    }

    @Override
    public @NotNull Component getTabTitle() {
        return Component.literal("Continental");
    }
}
