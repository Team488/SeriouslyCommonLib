package xbot.common.subsystems.drive.control_logic;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;

import edu.wpi.first.math.geometry.Rotation2d;
import xbot.common.math.PIDManager;
import xbot.common.properties.PropertyFactory;
import xbot.common.subsystems.pose.BasePoseSubsystem;

/**
 * Encapsulates the logic needed to rotate the robot to a specific angle.
 * When using a PD or PID controller, it will attempt to reach that angle.
 * When using a D-only controller, it will just resist any rotational motion.
 * @author John
 *
 */
public class HeadingModule {

    final BasePoseSubsystem pose;
    private final PIDManager headingDrivePid;
    
    private Rotation2d targetHeading;
    
    public final double defaultPValue = 1/80d;

    @AssistedFactory
    public abstract static class HeadingModuleFactory {
        public abstract HeadingModule create(@Assisted("headingDrivePid") PIDManager headingDrivePid);
    }

    @AssistedInject
    public HeadingModule(
            @Assisted("headingDrivePid") PIDManager headingDrivePid, 
            BasePoseSubsystem pose, 
            PropertyFactory propMan)
    {
        this.pose = pose;
        
        this.headingDrivePid = headingDrivePid;
        targetHeading = new Rotation2d();
    }
    
    public boolean isOnTarget() {
        return headingDrivePid.isOnTarget();
    }
    
    public void reset() {
        headingDrivePid.reset();
    }
    
    public double calculateHeadingPower(double desiredHeading) {
        // We need to calculate our own error function. Why?
        // PID works great, but it assumes there is a linear relationship between your current state and
        // your target state. Since rotation is circular, that's not the case: if you are at 170 degrees,
        // and you want to go to -170 degrees, you could travel -340 degrees... or just +20. 
        
        // So, we perform our own error calculation here that takes that into account (thanks to the ContiguousDouble
        // class, which is aware of such circular effects), and then feed that into a PID where
        // Goal is 0 and Current is our error.
        
        targetHeading = Rotation2d.fromDegrees(desiredHeading);
        double errorInDegrees = pose.getCurrentHeading().minus(targetHeading).getDegrees();
                
        // Now we feed it into a PID system, where the goal is to have 0 error.
        double rotationalPower = headingDrivePid.calculate(0, errorInDegrees);
        
        return rotationalPower;        
    }

    public double calculateHeadingPower(Rotation2d desiredHeading) { 
        return calculateHeadingPower(desiredHeading.getDegrees());
    }
}