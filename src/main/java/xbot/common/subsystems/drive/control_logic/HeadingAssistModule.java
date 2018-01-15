package xbot.common.subsystems.drive.control_logic;

import org.apache.log4j.Logger;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

import edu.wpi.first.wpilibj.Timer;
import xbot.common.properties.DoubleProperty;
import xbot.common.properties.XPropertyManager;
import xbot.common.subsystems.pose.BasePoseSubsystem;

/**
 * Encaspulates a useful bit of driving logic:
 * - The robot should attempt to hold its current heading, even if it's temporarily rotated by outside
 *   forces.
 * - If the human driver wants the robot to turn (above some small threshold), then the robot should turn
 *   according to human desires.
 * - After a few moments, the robot should memorize its current heading, and attempt to hold it again.
 * @author jogilber
 *
 */
public class HeadingAssistModule {

    final BasePoseSubsystem pose;
    final HeadingModule headingModule;
    final HeadingModule decayModule;

    final DoubleProperty humanThreshold;
    final DoubleProperty coastTime;
    double desiredHeading;
    double lastHumanInput;
    boolean inAutomaticMode;
    private HeadingAssistMode headingMode;

    public enum HeadingAssistMode {
        HoldOrientation,
        DecayVelocity
    }

    private Logger log = Logger.getLogger("HeadingAssistModule");

    @AssistedInject
    public HeadingAssistModule(
            @Assisted("headingModule") HeadingModule headingModule,
            @Assisted("decayModule") HeadingModule decayModule,
            XPropertyManager propMan,
            BasePoseSubsystem pose) {
        this.headingModule = headingModule;
        this.decayModule = decayModule;
        this.pose = pose;
        humanThreshold = propMan.createPersistentProperty("HeadingAssistModule - Human Threshold", 0.05);
        coastTime = propMan.createPersistentProperty("Heading Assist Module - Coast Time", 0.5);
        lastHumanInput = 0;
        this.headingMode = HeadingAssistMode.HoldOrientation;
    }

    public void setMode(HeadingAssistMode mode) {
        this.headingMode = mode;
    }

    /**
     * Core method of the HeadingAssistModule. You input the human rotational power input,
     * and this will determine whether or not to have the human drive the robot or whether the robot
     * should continue on its last heading. 
     * 
     * In either case, it will return the suggested power output to the drive subsystem in order to achieve
     * those goals.
     * 
     * @param humanRotationalPower - values between -1 and 1, in terms of "maximum rotational power"
     * @return values between -1 and 1, in terms of "how hard you should try in order to satisfy rotational goals."
     */
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
        else if (timeSinceHumanInput >= coastTime.get() && !inAutomaticMode) {
            desiredHeading = pose.getCurrentHeading().getValue();
            inAutomaticMode = true;
            headingModule.reset();
            decayModule.reset();
            return 0;
        }
        // by this point, the only option left is that the machine is in automatic mode.
        else {
            if (headingMode == null) {
                log.warn("Heading module did not have any mode set. Forcing to HoldOrientation.");
                this.headingMode = HeadingAssistMode.HoldOrientation;
            }

            switch (headingMode) {
            case HoldOrientation:
                return headingModule.calculateHeadingPower(desiredHeading);
            case DecayVelocity:
                return decayModule.calculateHeadingPower(0);
            default:
                return 0;
            }
        }
    }
}
