package xbot.common.logic;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import xbot.common.controls.sensors.XTimer;
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
    private XTimer timer;
    
    @Inject
    public HumanVsMachineDecider(@Assisted("name") String name, XPropertyManager propMan, XTimer timer) {
        this.timer = timer;
        deadbandProp = propMan.createPersistentProperty(name + "Decider/Deadband", 0.1);
        coastTimeProp = propMan.createPersistentProperty(name + "Decider/Coast Time", 0.3);
        reset();
    }
    
    public void reset() {
        lastHumanTime = timer.getFPGATimestamp()-100;
        inAutomaticMode = true;
    }
    
    public HumanVsMachineMode getRecommendedMode(double humanInput) {
                
        if (Math.abs(humanInput) > deadbandProp.get()) {
            lastHumanTime = timer.getFPGATimestamp();
            inAutomaticMode = false;
            return HumanVsMachineMode.HumanControl;
        }
        
        if (timer.getFPGATimestamp() - lastHumanTime < coastTimeProp.get()) {
            return HumanVsMachineMode.Coast;
        }
        
        if (!inAutomaticMode) {
            inAutomaticMode = true;
            return HumanVsMachineMode.InitializeMachineControl;
        }
        
        return HumanVsMachineMode.MachineControl;
    }
}
