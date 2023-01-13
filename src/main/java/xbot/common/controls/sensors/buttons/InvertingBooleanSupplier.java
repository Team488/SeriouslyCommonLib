package xbot.common.controls.sensors.buttons;

import java.util.function.BooleanSupplier;

public class InvertingBooleanSupplier implements BooleanSupplier {

    private boolean isInverted = false;
    private BooleanSupplier supplier;

    public InvertingBooleanSupplier(BooleanSupplier supplier) {
        this.supplier = supplier;
    }

    public void setInverted(boolean isInverted) {
        this.isInverted = isInverted;
    }

    public boolean getInverted() {
        return this.isInverted;
    }

    @Override
    public boolean getAsBoolean() {
        return this.supplier.getAsBoolean() ^ this.isInverted;
    }
    


}