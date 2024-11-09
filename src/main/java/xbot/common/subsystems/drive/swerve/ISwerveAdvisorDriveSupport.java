package xbot.common.subsystems.drive.swerve;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;

public interface ISwerveAdvisorDriveSupport {

    public void setDesiredHeading(double heading);
    public double getDesiredHeading();

    public boolean getStaticHeadingActive();
    public boolean getLookAtPointActive();

    public Rotation2d getStaticHeadingTarget();
    public Translation2d getLookAtPointTarget();

}
