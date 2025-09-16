package realcolin.continental.client;

import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.tabs.GridLayoutTab;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import realcolin.continental.world.continent.ContinentSettings;

public class ContinentalTab extends GridLayoutTab {
    private final Screen parent;
    private final Slider minContinents;
    private final Slider maxContinents;
    private final Slider avgContinentSize;
    private final Slider continentSizeVariation;

    public ContinentalTab(Screen parent) {
        super(Component.literal("Continental"));
        this.parent = parent;

        GridLayout.RowHelper row = this.layout.columnSpacing(10).rowSpacing(8).createRowHelper(2);
        LayoutSettings cell = row.newCellSettings();

        var minLabel = new StringWidget(Component.literal("Min Continents"), parent.getFont());
        minLabel.setWidth(200);
        minLabel.setHeight(20);
        minLabel.alignLeft();
        var maxLabel = new StringWidget(Component.literal("Max Continents"), parent.getFont());
        maxLabel.setWidth(200);
        maxLabel.setHeight(20);
        maxLabel.alignLeft();

        // TODO clean this up a bit lol
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

        var avgSizeLabel = new StringWidget(Component.literal("Mean Continent Size"), parent.getFont());
        avgSizeLabel.setWidth(200);
        avgSizeLabel.setHeight(20);
        avgSizeLabel.alignLeft();

        avgContinentSize = new Slider(200, 20, 5000, 2000, 20000, (s, v) -> v, true);

        var variationLabel = new StringWidget(Component.literal("Continent Size Variance"), parent.getFont());
        variationLabel.setWidth(200);
        variationLabel.setHeight(20);
        variationLabel.alignLeft();

        continentSizeVariation = new Slider(200, 20, 0.25, 0, 0.8, (s, v) -> v, false);

        row.addChild(minLabel, cell);
        row.addChild(minContinents, cell);
        row.addChild(maxLabel, cell);
        row.addChild(maxContinents, cell);
        row.addChild(avgSizeLabel, cell);
        row.addChild(avgContinentSize, cell);
        row.addChild(variationLabel, cell);
        row.addChild(continentSizeVariation, cell);
    }

    public ContinentSettings getSettings() {
        long minCon = Math.round(minContinents.lerpedValue());
        long maxCon = Math.round(maxContinents.lerpedValue());
        long avgSize = Math.round(avgContinentSize.lerpedValue());
        double var = Math.round(continentSizeVariation.lerpedValue());

        return new ContinentSettings(minCon, maxCon, avgSize, var);
    }

    @Override
    public Component getTabTitle() {
        return Component.literal("Continental");
    }
}
