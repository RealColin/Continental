package realcolin.continental.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ContinentOptionsList extends ContainerObjectSelectionList<ContinentOptionsList.Entry> {
    private static final int ROW_HEIGHT = 24;
    private int rowWidth;

    public ContinentOptionsList(int width, int height) {
        super(Minecraft.getInstance(), width, height, 0, ROW_HEIGHT);
        rowWidth = width - 20;
    }

    @Override
    public int getRowWidth() {
        return rowWidth;
    }

    public void update(int width) {
        rowWidth = Math.min(width, 410);
    }

    public void addSingleWidget(AbstractWidget widget) {
        this.addEntry(new SingleWidgetEntry(widget));
    }

    public void addDoubleWidget(AbstractWidget left, AbstractWidget right) {
        this.addEntry(new DoubleWidgetEntry(left, right));
    }

    public static abstract class Entry extends ContainerObjectSelectionList.Entry<Entry> {}

    public static class SingleWidgetEntry extends Entry {
        private final AbstractWidget widget;

        public SingleWidgetEntry(AbstractWidget widget) {
            this.widget = widget;
        }

        @Override
        public @NotNull List<? extends NarratableEntry> narratables() {
            return List.of(widget);
        }

        @Override
        public void render(@NotNull GuiGraphics guiGraphics, int index, int y, int x, int rowWidth, int rowHeight, int mouseX, int mouseY, boolean b, float v) {
            int gap = 0;
            int half = (rowWidth - gap) / 2;
            int h = 20;

            widget.setX(x + 4);
            widget.setY(y + (rowHeight - h) / 2);
            widget.setWidth(half - 8);

            widget.render(guiGraphics, mouseX, mouseY, v);
        }

        @Override
        public @NotNull List<? extends GuiEventListener> children() {
            return List.of(widget);
        }
    }

    public static class DoubleWidgetEntry extends Entry {
        private final AbstractWidget left;
        private final AbstractWidget right;

        public DoubleWidgetEntry(AbstractWidget left, AbstractWidget right) {
            this.left = left;
            this.right = right;
        }

        @Override
        public @NotNull List<? extends NarratableEntry> narratables() {
            return List.of(left, right);
        }

        @Override
        public void render(@NotNull GuiGraphics guiGraphics, int index, int y, int x, int rowWidth, int rowHeight, int mouseX, int mouseY, boolean b, float v) {
            int gap = 6;
            int half = (rowWidth - gap) / 2;
            int h = 20;

            left.setX(x + 4);
            left.setY(y + (rowHeight - h) / 2);
            left.setWidth(half - 8);

            right.setX(x + 4 + half + gap);
            right.setY(y + (rowHeight - h) / 2);
            right.setWidth(half - 8);

            left.render(guiGraphics, mouseX, mouseY, v);
            right.render(guiGraphics, mouseX, mouseY, v);
        }

        @Override
        public @NotNull List<? extends GuiEventListener> children() {
            return List.of(left, right);
        }
    }
}
