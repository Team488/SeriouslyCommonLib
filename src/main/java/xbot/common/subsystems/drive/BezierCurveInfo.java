package xbot.common.subsystems.drive;

import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.units.measure.LinearVelocity;
import edu.wpi.first.units.measure.Time;

import java.util.List;

public record BezierCurveInfo(LinearVelocity speed, Time operationTime, List<Translation2d> controlPoints) {}
