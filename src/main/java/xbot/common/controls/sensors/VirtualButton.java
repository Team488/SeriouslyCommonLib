package xbot.common.controls.sensors;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;

/**
 * A virtual button under program control that can be used for creating chords.
 * This is useful to repurpose buttons in multiple "modes".
 */
public class VirtualButton extends AdvancedButton {

    private boolean pressed;

    @AssistedFactory
    public abstract static class VirtualButtonFactory {
        public abstract VirtualButton create(@Assisted boolean defaultPressed);

        public VirtualButton create() {
            return create(false);
        }
    }
    
    @AssistedInject
    public VirtualButton(@Assisted boolean defaultPressed) {
        this.pressed = defaultPressed;
    }
    
    @Override
    public boolean getAsBoolean() {
        return this.pressed;
    }

    public void set(boolean pressed) {
        this.pressed = pressed;
    }

    public void toggle() {
        this.pressed = !this.pressed;
    }

}
