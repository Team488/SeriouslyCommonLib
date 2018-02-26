package xbot.common.logic;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import edu.wpi.first.wpilibj.Timer;
import xbot.common.properties.DoubleProperty;
import xbot.common.properties.XPropertyManager;

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
    
    @Inject
    public HumanVsMachineDecider(@Assisted("name") String name, XPropertyManager propMan) {
        deadbandProp = propMan.createPersistentProperty(name + "/Deadband", 0.1);
        coastTimeProp = propMan.createPersistentProperty(name + "/Coast Time", 0.3);
        reset();
    }
    
    public void reset() {
        lastHumanTime = Timer.getFPGATimestamp();
    }
    
    public HumanVsMachineMode getRecommendedMode(double humanInput) {
                
        if (humanInput > deadbandProp.get()) {
            lastHumanTime = Timer.getFPGATimestamp();
            inAutomaticMode = false;
            return HumanVsMachineMode.HumanControl;
        }
        
        if (Timer.getFPGATimestamp() - lastHumanTime < coastTimeProp.get()) {
            return HumanVsMachineMode.Coast;
        }
        
        if (!inAutomaticMode) {
            inAutomaticMode = true;
            return HumanVsMachineMode.InitializeMachineControl;
        }
        
        return HumanVsMachineMode.MachineControl;
    }
}
