package xbot.common.subsystems.drive.control_logic;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

import edu.wpi.first.wpilibj.Timer;
import xbot.common.properties.DoubleProperty;
import xbot.common.properties.XPropertyManager;
import xbot.common.subsystems.pose.BasePoseSubsystem;

public class HeadingAssistModule {

    final BasePoseSubsystem pose;
    final HeadingModule headingModule;
    final DoubleProperty humanThreshold;
    final DoubleProperty coastTime;
    double desiredHeading;
    double lastHumanInput;
    boolean inAutomaticMode;
    
    @AssistedInject
    public HeadingAssistModule(
            @Assisted("headingModule") HeadingModule headingModule,
            XPropertyManager propMan,
            BasePoseSubsystem pose) {
        this.headingModule = headingModule;
        this.pose = pose;
        humanThreshold = propMan.createPersistentProperty("HeadingAssistModule - Human Threshold", 0.05);
        coastTime = propMan.createPersistentProperty("Heading Assist Module - Coast Time", 0.5);
        lastHumanInput = 0;
    }
    
    public double calculateHeadingPower(double humanRotationalPower) {
        
        // if human rotational power above some threshold, return that.
        // Also, update a timestamp that says this happened recently
        if (humanRotationalPower > humanThreshold.get()) {
            inAutomaticMode = false;
            lastHumanInput = Timer.getFPGATimestamp();
            return humanRotationalPower;
        }
        
        // If not under threshold, but too close to timestamp,
        // "coast"
        double timeSinceHumanInput = Timer.getFPGATimestamp() - lastHumanInput;
        
        if (timeSinceHumanInput < coastTime.get()) {
            return 0;
        }
        
        if (timeSinceHumanInput > coastTime.get() && !inAutomaticMode) {
            desiredHeading = pose.getCurrentHeading().getValue();
            inAutomaticMode = true;
            headingModule.reset();
            return 0;
        }
        
        if (inAutomaticMode) {
            return headingModule.calculateHeadingPower(desiredHeading);
        }        
        
        return 0;
    }
}
