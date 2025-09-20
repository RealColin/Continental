package realcolin.continental.client;

import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class Slider extends AbstractSliderButton {
    private final double min;
    private final double max;
    private Callback callback;
    private final boolean integer;

    public Slider(int width, int height, double value, double min, double max, Callback callback, boolean integer) {
        super(-1, -1, width, height, CommonComponents.EMPTY, 0.0);
        this.min = min;
        this.max = max;
        this.callback = callback;
        this.value = unlerp(value);
        this.integer = integer;
        this.updateMessage();
    }

    @Override
    protected void updateMessage() {
        this.setMessage(getMsg());
    }

    private Component getMsg() {
        var txt = "";
        if (integer)
            txt = String.valueOf(Math.round(lerpedValue()));
        else
            txt = String.format("%.3f", lerpedValue());

        return Component.literal(txt);
    }

    @Override
    protected void applyValue() {
        if (integer)
            this.value = unlerp(Math.round(Mth.lerp(this.value, min, max)));

        if (callback != null) {
            this.value = callback.apply(this, this.value);
        }
    }

    // setting-relative min-max value
    public double lerpedValue() {
        return Mth.lerp(this.value, this.min, this.max);
    }

    // slider-relative 0.0-1.0 value
    public double value() {
        return this.value;
    }

    public double unlerp(double value) {
        return (value - min) / (max - min);
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public interface Callback {
        double apply(Slider slider, double value);
    }
}
