package xbot.common.subsystems.drive.control_logic;


import org.apache.log4j.Logger;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;

import xbot.common.controls.sensors.XTimer;
import xbot.common.properties.DoubleProperty;
import xbot.common.properties.PropertyFactory;
import xbot.common.subsystems.pose.BasePoseSubsystem;

/**
 * Encaspulates a useful bit of driving logic: - The robot should attempt to
 * hold its current heading, even if it's temporarily rotated by outside forces.
 * - If the human driver wants the robot to turn (above some small threshold),
 * then the robot should turn according to human desires. - After a few moments,
 * the robot should memorize its current heading, and attempt to hold it again.
 * 
 * The automatic response can take one of two forms: - Hold a specific heading,
 * as mentioned above, using a PID controller - Resist changes in rotational
 * velocity using a D-only controller.
 * 
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

    @AssistedFactory
    public abstract static class HeadingAssistModuleFactory {
        /**
         * Creates a heading assist module. Can either hold an orientation, or resist
         * rotational motion.
         * 
         * @param headingModule Tune this one to rotate to a target orientation (PD, or
         *                      PID controller)
         * @param decayModule   Tune this one to resist rotation (D controller)
         * @return
         */
        public abstract HeadingAssistModule create(
            @Assisted("headingModule") HeadingModule headingModule,
            @Assisted("decayModule") HeadingModule decayModule,
            @Assisted("prefix") String prefix);

        public HeadingAssistModule create(HeadingModule headingModule, String prefix) {
            return create(headingModule, null, prefix);
        }
    }

    @AssistedInject
    public HeadingAssistModule(
            @Assisted("headingModule") HeadingModule headingModule,
            @Assisted("decayModule") HeadingModule decayModule,
            @Assisted("prefix") String prefix,
            PropertyFactory propMan,
            BasePoseSubsystem pose) {
        this.headingModule = headingModule;
        this.decayModule = decayModule;
        this.pose = pose;
        propMan.setPrefix(prefix);
        humanThreshold = propMan.createPersistentProperty("HeadingAssistModule/Human Threshold", 0.05);
        coastTime = propMan.createPersistentProperty("Heading Assist Module/Coast Time", 0.5);
        lastHumanInput = 0;
        this.headingMode = HeadingAssistMode.HoldOrientation;
    }

    public void setMode(HeadingAssistMode mode) {
        this.headingMode = mode;
    }

    public void reset() {
        headingModule.reset();
        if (decayModule != null) {
            decayModule.reset();
        }
        inAutomaticMode = false;
    }

    /**
     * Core method of the HeadingAssistModule. You input the human rotational power
     * input,
     * and this will determine whether or not to have the human drive the robot or
     * whether the robot
     * should continue on its last heading.
     * 
     * In either case, it will return the suggested power output to the drive
     * subsystem in order to achieve
     * those goals.
     * 
     * @param humanRotationalPower - values between -1 and 1, in terms of "maximum
     *                             rotational power"
     * @return values between -1 and 1, in terms of "how hard you should try in
     *         order to satisfy rotational goals."
     */
    public double calculateHeadingPower(double humanRotationalPower) {

        if (pose.getHeadingResetRecently()) {
            reset();
            return humanRotationalPower;
        }

        // if human rotational power above some threshold, return that.
        // Also, update a timestamp that says this happened recently
        if (Math.abs(humanRotationalPower) > humanThreshold.get()) {
            inAutomaticMode = false;
            lastHumanInput = XTimer.getFPGATimestamp();
            return humanRotationalPower;
        }

        // If not under threshold, but too close to timestamp,
        // "coast"
        double timeSinceHumanInput = XTimer.getFPGATimestamp() - lastHumanInput;

        if (timeSinceHumanInput < coastTime.get()) {
            return 0;
        } else if (timeSinceHumanInput >= coastTime.get() && !inAutomaticMode) {
            desiredHeading = pose.getCurrentHeading().getDegrees();
            inAutomaticMode = true;
            headingModule.reset();
            if (decayModule != null) {
                decayModule.reset();
            }
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
                    if (decayModule != null) {
                        return decayModule.calculateHeadingPower(0);
                    } else {
                        return 0;
                    }
                default:
                    return 0;
            }
        }
    }
}
