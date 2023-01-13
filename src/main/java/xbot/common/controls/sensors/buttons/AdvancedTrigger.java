package xbot.common.controls.sensors.buttons;

import java.util.function.BooleanSupplier;

import edu.wpi.first.wpilibj2.command.button.Trigger;

public abstract class AdvancedTrigger extends Trigger {
    protected InvertingBooleanSupplier supplier;

    public AdvancedTrigger(BooleanSupplier supplier) {
        this(new InvertingBooleanSupplier(supplier));
    }

    private AdvancedTrigger(InvertingBooleanSupplier supplier) {
        super(supplier);
        this.supplier = supplier;
    }

    public void setInverted(boolean inverted) {
        this.supplier.setInverted(inverted);
    }

    public boolean getInverted() {
        return this.supplier.getInverted();
    }
}
