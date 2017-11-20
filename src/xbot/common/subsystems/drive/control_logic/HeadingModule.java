package xbot.common.subsystems.drive.control_logic;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

import xbot.common.math.ContiguousHeading;
import xbot.common.math.PIDManager;
import xbot.common.properties.XPropertyManager;
import xbot.common.subsystems.pose.BasePoseSubsystem;

public class HeadingModule {

    final BasePoseSubsystem pose;
    private final PIDManager headingDrivePid;
    
    private ContiguousHeading targetHeading;
    
    public final double defaultPValue = 1/80d;
    
    @AssistedInject
    public HeadingModule(
            @Assisted("headingDrivePid") PIDManager headingDrivePid, 
            BasePoseSubsystem pose, 
            XPropertyManager propMan)
    {
        this.pose = pose;
        
        this.headingDrivePid = headingDrivePid;
        targetHeading = new ContiguousHeading();
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
        
        // This will have a side-effect of reversing the power output of the system, so we need to remember
        // at the very end to reverse it one more time to cancel that out.
        targetHeading.setValue(desiredHeading);
        double errorInDegrees = pose.getCurrentHeading().difference(targetHeading);
        
        // Let's normalize the error into a -1 to 1 range. Convenient for further math.
        double normalizedError = errorInDegrees / 180;
        
        // Now we feed it into a PID system, where the goal is to have 0 error.
        double rotationalPower = headingDrivePid.calculate(0, normalizedError);
        
        // reverse the power (see above)
        rotationalPower *= -1;
        
        return rotationalPower;        
    }
}