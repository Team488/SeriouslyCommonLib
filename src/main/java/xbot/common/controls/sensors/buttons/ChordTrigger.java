package xbot.common.controls.sensors.buttons;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import edu.wpi.first.wpilibj2.command.button.Trigger;

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