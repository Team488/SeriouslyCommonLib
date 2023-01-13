package xbot.common.controls.sensors.buttons;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;

/**
 * A virtual button under program control that can be used for creating chords.
 * This is useful to repurpose buttons in multiple "modes".
 */
public class VirtualTrigger extends AdvancedTrigger {

    private SettableBooleanSuppler supplier;

    @AssistedFactory
    public abstract static class VirtualTriggerFactory {
        public abstract VirtualTrigger create(@Assisted boolean defaultPressed);

        public VirtualTrigger create() {
            return create(false);
        }
    }
    
    @AssistedInject
    public VirtualTrigger(@Assisted boolean defaultPressed) {
        this(new SettableBooleanSuppler(defaultPressed));
    }

    public VirtualTrigger(SettableBooleanSuppler supplier) {
        super(supplier);
        this.supplier = supplier;
    }
 
    public void set(boolean value) {
        this.supplier.set(value);
    }

    public void toggle() {
        this.supplier.toggle();
    }

}
