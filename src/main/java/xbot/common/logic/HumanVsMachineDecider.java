package xbot.common.logic;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import xbot.common.controls.sensors.XTimer;
import xbot.common.properties.DoubleProperty;
import xbot.common.properties.Property;
import xbot.common.properties.PropertyFactory;

public class HumanVsMachineDecider {

    public enum HumanVsMachineMode {
        HumanControl,
        Coast,
        InitializeMachineControl,
        MachineControl
    }
    
    private double lastHumanTime;
    private final DoubleProperty deadbandProp;
    private final DoubleProperty coastTimeProp;
    private boolean inAutomaticMode;
    
    @AssistedFactory
    public abstract static class HumanVsMachineDeciderFactory {
        public abstract HumanVsMachineDecider create(@Assisted("prefix") String prefix);
    }

    @AssistedInject
    public HumanVsMachineDecider(@Assisted("prefix") String prefix, PropertyFactory propMan) {
        propMan.setPrefix(prefix);
        propMan.appendPrefix("Decider");
        propMan.setDefaultLevel(Property.PropertyLevel.Debug);
        deadbandProp = propMan.createPersistentProperty("Deadband", 0.1);
        coastTimeProp = propMan.createPersistentProperty("Coast Time", 0.3);
        reset();
    }
    
    public void reset() {
        reset(true);
    }

    public void reset(boolean startInAutomaticMode) {
        lastHumanTime = XTimer.getFPGATimestamp()-100;
        inAutomaticMode = startInAutomaticMode;
    }
    
    public HumanVsMachineMode getRecommendedMode(double humanInput) {
                
        if (Math.abs(humanInput) > deadbandProp.get()) {
            lastHumanTime = XTimer.getFPGATimestamp();
            inAutomaticMode = false;
            return HumanVsMachineMode.HumanControl;
        }
        
        if (XTimer.getFPGATimestamp() - lastHumanTime < coastTimeProp.get()) {
            return HumanVsMachineMode.Coast;
        }
        
        if (!inAutomaticMode) {
            inAutomaticMode = true;
            return HumanVsMachineMode.InitializeMachineControl;
        }
        
        return HumanVsMachineMode.MachineControl;
    }

    public double getDeadband() {
        return deadbandProp.get();
    }
}
