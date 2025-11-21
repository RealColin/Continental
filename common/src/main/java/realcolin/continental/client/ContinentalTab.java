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
import realcolin.continental.world.continent.Transitions;

public class ContinentalTab extends GridLayoutTab {
    private final Slider minContinents;
    private final Slider maxContinents;
    private final Slider avgContinentSize;
    private final Slider continentSizeVariation;
    private final Slider continentSpacing;
    private final Slider spacingUniformity;
    private final Slider coastDistance;
    private final Slider nearInlandDistance;
    private final Slider midInlandDistance;
    private final Slider farInlandDistance;
    private final Slider oceanDistance;
    private final Slider deepOceanDistance;
    private final ContinentOptionsList list;
    private final HeaderAndFooterLayout headerAndFooterLayout;

    public ContinentalTab(Screen parent, HeaderAndFooterLayout headerAndFooterLayout) {
        super(Component.literal("Continental"));
        this.headerAndFooterLayout = headerAndFooterLayout;

        GridLayout.RowHelper row = this.layout.columnSpacing(10).rowSpacing(8).createRowHelper(2);
        LayoutSettings cell = row.newCellSettings();

        /* labels */
        var sizeAndPositionSettingsLabel = new StringWidget(200, 20, Component.literal("Continent Size and Position Settings"), parent.getFont());
        sizeAndPositionSettingsLabel.alignLeft();
        var minLabel = new StringWidget(200, 20, Component.literal("Min Continents"), parent.getFont());
        minLabel.alignLeft();
        var maxLabel = new StringWidget(200, 20, Component.literal("Max Continents"), parent.getFont());
        maxLabel.alignLeft();
        var avgSizeLabel = new StringWidget(200, 20, Component.literal("Mean Continent Size"), parent.getFont());
        avgSizeLabel.alignLeft();
        var variationLabel = new StringWidget(200, 20, Component.literal("Continent Size Variance"), parent.getFont());
        variationLabel.alignLeft();
        var spacingLabel = new StringWidget(200, 20, Component.literal("Continent Spacing"), parent.getFont());
        spacingLabel.alignLeft();
        var uniformityLabel = new StringWidget(200, 20, Component.literal("Continent Spacing Uniformity"), parent.getFont());
        uniformityLabel.alignLeft();

        var shapeSettingsLabel = new StringWidget(200, 20, Component.literal("Continent Shape Settings"), parent.getFont());
        shapeSettingsLabel.alignLeft();
        var coastLabel = new StringWidget(200, 20, Component.literal("Coast"), parent.getFont());
        coastLabel.alignLeft();
        var nearInlandLabel = new StringWidget(200, 20, Component.literal("Near Inland"), parent.getFont());
        nearInlandLabel.alignLeft();
        var midInlandLabel = new StringWidget(200, 20, Component.literal("Mid Inland"), parent.getFont());
        midInlandLabel.alignLeft();
        var farInlandLabel = new StringWidget(200, 20, Component.literal("Far Inland"), parent.getFont());
        farInlandLabel.alignLeft();
        var oceanLabel = new StringWidget(200, 20, Component.literal("Ocean"), parent.getFont());
        oceanLabel.alignLeft();
        var deepOceanLabel = new StringWidget(200, 20, Component.literal("Deep Ocean"), parent.getFont());
        deepOceanLabel.alignLeft();


        /* sliders */
        maxContinents = new Slider(200, 20,  7.0, 1, 20, (s, v) -> v, true);
        minContinents = new Slider(200, 20,  5.0, 1, 20, (s, v) -> {
            if (maxContinents.value() < v)
                v = maxContinents.value();
            return v;
        }, true);
        maxContinents.setCallback((s, v ) -> {
            if (minContinents.value() > v)
                v = minContinents.value();
            return v;
        });
        avgContinentSize = new Slider(200, 20, 5000, 2000, 20000, (s, v) -> v, true);
        continentSizeVariation = new Slider(200, 20, 0.25, 0, 0.8, (s, v) -> v, false);
        continentSpacing = new Slider(200, 20, 0.25, 0, 1.0, (s, v) -> v, false);
        spacingUniformity = new Slider(200, 20, 0.25, 0, 1.0, (s, v) -> v, false);

        coastDistance = new Slider(200, 20, 60, 0, 1600, (s, v) -> v, true);
        nearInlandDistance = new Slider(200, 20, 200, 0, 1600, (s, v) -> v, true);
        midInlandDistance = new Slider(200, 20, 900, 0, 1600, (s, v) -> v, true);
        farInlandDistance = new Slider(200, 20, 1600, 0, 1600, (s, v) -> v, true);

        oceanDistance = new Slider(200, 20, 120, 0, 800, (s, v) -> v, true);
        deepOceanDistance = new Slider(200, 20, 500, 0, 800, (s, v) -> v, true);

        coastDistance.setCallback((s, v) -> {
            if (nearInlandDistance.value() < v)
                v = nearInlandDistance.value();
            return v;
        });
        nearInlandDistance.setCallback((s, v) -> {
            if (coastDistance.value() > v)
                v = coastDistance.value();
            if (midInlandDistance.value() < v)
                v = midInlandDistance.value();
            return v;
        });
        midInlandDistance.setCallback((s, v) -> {
            if (nearInlandDistance.value() > v)
                v = nearInlandDistance.value();
            if (farInlandDistance.value() < v)
                v = farInlandDistance.value();
            return v;
        });
        farInlandDistance.setCallback((s, v) -> {
            if (midInlandDistance.value() > v)
                v = midInlandDistance.value();
            return v;
        });

        oceanDistance.setCallback((s, v) -> {
            if (deepOceanDistance.value() < v)
                v = deepOceanDistance.value();
            return v;
        });
        deepOceanDistance.setCallback((s, v) -> {
            if (oceanDistance.value() > v)
                v = oceanDistance.value();
            return v;
        });

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
        list.addSingleWidget(sizeAndPositionSettingsLabel);
        list.addDoubleWidget(minLabel, minContinents);
        list.addDoubleWidget(maxLabel, maxContinents);
        list.addDoubleWidget(avgSizeLabel, avgContinentSize);
        list.addDoubleWidget(variationLabel, continentSizeVariation);
        list.addDoubleWidget(spacingLabel, continentSpacing);
        list.addDoubleWidget(uniformityLabel, spacingUniformity);
        list.addSingleWidget(shapeSettingsLabel);
        list.addDoubleWidget(coastLabel, coastDistance);
        list.addDoubleWidget(nearInlandLabel, nearInlandDistance);
        list.addDoubleWidget(midInlandLabel, midInlandDistance);
        list.addDoubleWidget(farInlandLabel, farInlandDistance);
        list.addDoubleWidget(oceanLabel, oceanDistance);
        list.addDoubleWidget(deepOceanLabel, deepOceanDistance);
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
        int coastDist = (int)Math.round(coastDistance.lerpedValue());
        int nearInlandDist = (int)Math.round(nearInlandDistance.lerpedValue());
        int midInlandDist = (int)Math.round(midInlandDistance.lerpedValue());
        int farInlandDist = (int)Math.round(farInlandDistance.lerpedValue());
        int oceanDist = (int)Math.round(oceanDistance.lerpedValue());
        int deepOceanDist = (int)Math.round(deepOceanDistance.lerpedValue());
        var transitions = new Transitions(coastDist, nearInlandDist, midInlandDist, farInlandDist, oceanDist, deepOceanDist);

        return new ContinentSettings(minCon, maxCon, avgSize, var, spacing, uniformity, transitions);
    }

    @Override
    public @NotNull Component getTabTitle() {
        return Component.literal("Continental");
    }
}
