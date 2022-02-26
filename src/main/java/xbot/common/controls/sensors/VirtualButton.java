package xbot.common.controls.sensors;

/**
 * A virtual button under program control that can be used for creating chords.
 * This is useful to repurpose buttons in multiple "modes".
 */
public class VirtualButton extends AdvancedButton {

    private boolean pressed;

    public VirtualButton() {
        this(false);
    }

    public VirtualButton(boolean defaultPressed) {
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
