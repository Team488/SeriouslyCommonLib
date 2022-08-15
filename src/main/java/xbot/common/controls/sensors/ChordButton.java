package xbot.common.controls.sensors;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;

public class ChordButton extends AdvancedButton {

    AdvancedButton a;
    AdvancedButton b;

    @AssistedFactory
    public abstract static class ChordButtonFactory {
        public abstract ChordButton create(
            @Assisted("a") AdvancedButton a,
            @Assisted("b") AdvancedButton b);
    }

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