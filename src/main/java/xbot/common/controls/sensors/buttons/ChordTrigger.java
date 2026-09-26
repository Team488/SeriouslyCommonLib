package xbot.common.controls.sensors.buttons;

import org.wpilib.command2.button.Trigger;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;

public class ChordTrigger extends AdvancedTrigger {

    AdvancedTrigger a;
    AdvancedTrigger b;

    @AssistedFactory
    public abstract static class ChordTriggerFactory {
        public abstract ChordTrigger create(
            @Assisted("a") Trigger a,
            @Assisted("b") Trigger b);
    }

    @AssistedInject
    public ChordTrigger(@Assisted("a") Trigger a, @Assisted("b") Trigger b) {
        super(() -> a.getAsBoolean() && b.getAsBoolean());
    }
    
}
