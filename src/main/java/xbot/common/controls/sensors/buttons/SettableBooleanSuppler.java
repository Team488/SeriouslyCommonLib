package xbot.common.controls.sensors.buttons;

import java.util.function.BooleanSupplier;

public class SettableBooleanSuppler implements BooleanSupplier {

    private boolean value;

    public SettableBooleanSuppler(boolean defaultValue) {
        this.value = defaultValue;
    }

    @Override
    public boolean getAsBoolean() {
        return value;
    }

    public void set(boolean value) {
        this.value = value;
    }

    public void toggle() {
        this.value = !this.value;
    }
    
}
