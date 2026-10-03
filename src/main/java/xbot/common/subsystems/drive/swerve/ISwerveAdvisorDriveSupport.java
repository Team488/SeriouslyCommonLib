package xbot.common.subsystems.drive.swerve;

import org.wpilib.math.geometry.Rotation2d;
import org.wpilib.math.geometry.Translation2d;

public interface ISwerveAdvisorDriveSupport {

    public void setDesiredHeading(double heading);
    public double getDesiredHeading();

    public boolean getStaticHeadingActive();
    public boolean getLookAtPointActive();

    public boolean getLookAtPointInverted();

    public Rotation2d getStaticHeadingTarget();
    public Translation2d getLookAtPointTarget();

}
