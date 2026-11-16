package me.yaakul.kaiding.client;

import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;

import java.util.function.DoubleConsumer;
import java.util.function.DoubleSupplier;

public class CpsSlider extends AbstractSliderButton {

    private final String label;
    private final DoubleSupplier getter;
    private final DoubleConsumer setter;
    private final Runnable onChange;

    public CpsSlider(int x, int y, int width, int height, String label,
                     DoubleSupplier getter, DoubleConsumer setter, Runnable onChange) {
        super(x, y, width, height, Component.empty(), toSlider(getter.getAsDouble()));
        this.label = label;
        this.getter = getter;
        this.setter = setter;
        this.onChange = onChange;
        updateMessage();
    }

    private static double toSlider(double cps){
        return (cps-ClickerState.LIMIT_MIN) / (ClickerState.LIMIT_MAX - ClickerState.LIMIT_MIN);
    }

    private double fromSlider(){
        double raw = ClickerState.LIMIT_MIN
                + this.value * (ClickerState.LIMIT_MAX - ClickerState.LIMIT_MIN);
        return Math.round(raw * 2.0) / 2.0;
    }

    public void sync(){
        this.value = toSlider(getter.getAsDouble());
        updateMessage();
    }

    @Override
    protected void updateMessage() {
        setMessage(Component.literal(label + ": " + String.format("%.1f", getter.getAsDouble())));
    }

    @Override
    protected void applyValue() {
        setter.accept(fromSlider());
        onChange.run();
    }
}
