package xbot.common.controls.sensors;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

public class ChordButton extends AdvancedButton {

    AdvancedButton a;
    AdvancedButton b;

    @AssistedInject
    public ChordButton(@Assisted("a") AdvancedButton a, @Assisted("b") AdvancedButton b) {
        this.a = a;
        this.b = b;
    }

    @Override
    public boolean get() {
        return a.get() && b.get();
    }

}