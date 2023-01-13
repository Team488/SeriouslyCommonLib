package xbot.common.controls.sensors.buttons;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;

public class ChordTrigger extends AdvancedTrigger {

    AdvancedTrigger a;
    AdvancedTrigger b;

    @AssistedFactory
    public abstract static class ChordTriggerFactory {
        public abstract ChordTrigger create(
            @Assisted("a") AdvancedTrigger a,
            @Assisted("b") AdvancedTrigger b);
    }

    @AssistedInject
    public ChordTrigger(@Assisted("a") AdvancedTrigger a, @Assisted("b") AdvancedTrigger b) {
        super(() -> a.getAsBoolean() && b.getAsBoolean());
    }
    
}