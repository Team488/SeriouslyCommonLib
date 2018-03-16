package xbot.common.logic;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import edu.wpi.first.wpilibj.Timer;
import xbot.common.logic.Latch.EdgeType;
import xbot.common.properties.DoubleProperty;
import xbot.common.properties.XPropertyManager;

public class StallDetector {

    public enum StallMode {
        NotStalled,
        StalledRecently
    }
    
    Latch potentialStallLatch;
    Latch certainStallLatch;
    
    double potentialStallTime = -1000;
    double certainStallTime = -1000;
    
    double maxVelocity;
    double activationThreshold;
    double cooldownDuration;
    double criticalLowSpeedPercent;
    double stallCheckTime;
    
    @Inject
    public StallDetector(
            @Assisted("name") String name, 
             
            @Assisted("activationThreshold") double activationThreshold,
            @Assisted("stallCheckTime") double stallCheckTime,
            
            @Assisted("cooldownDuration") double cooldownDuration,
            @Assisted("maxVelocity") double maxVelocity,
            @Assisted("criticalLowSpeedPercent") double criticalLowSpeedPercent,
            
            XPropertyManager propMan) {
        
        this.maxVelocity = maxVelocity;
        this.activationThreshold = activationThreshold;
        this.cooldownDuration = cooldownDuration;
        this.criticalLowSpeedPercent = criticalLowSpeedPercent;
        this.stallCheckTime = stallCheckTime;
        
        potentialStallLatch = new Latch(false, EdgeType.RisingEdge, edge ->  {
            if (edge == EdgeType.RisingEdge) {
                potentialStallTime = Timer.getFPGATimestamp();
            }
        });
        
        certainStallLatch = new Latch(false, EdgeType.RisingEdge, edge ->  {
            if (edge == EdgeType.RisingEdge) {
                certainStallTime = Timer.getFPGATimestamp();
            }
        });
    }
    
    public StallMode checkIsStalled(double commandedPower, double currentVelocity) {
        
        // check for stall condition
        
        // Is system using at least 10% power?
        boolean systemActivated = Math.abs(commandedPower) > activationThreshold;
        // Is system moving at less than 5% of max speed?
        boolean systemNotMoving = Math.abs(currentVelocity / maxVelocity) < criticalLowSpeedPercent;
        
        // Check to see if we are stalling
        boolean inStallCondition = systemActivated && systemNotMoving;
        potentialStallLatch.setValue(inStallCondition);
        
        // Check to see if we have been stalling for a long time
        boolean isDefinitelyStalled = false;
        if (inStallCondition) {
            isDefinitelyStalled = Timer.getFPGATimestamp() - potentialStallTime > 1;
            certainStallLatch.setValue(isDefinitelyStalled);
        }
        
        // Check to see if we have recently stalled, and thus need to cool down
        boolean inCooldown = Timer.getFPGATimestamp() - certainStallTime < 5;
        
        if (inCooldown) {
            return StallMode.StalledRecently;
        }
        
        return StallMode.NotStalled;
    }
}
