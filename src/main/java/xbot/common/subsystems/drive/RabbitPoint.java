package xbot.common.subsystems.drive;

import java.util.ArrayList;
import java.util.List;

import xbot.common.math.ContiguousHeading;
import xbot.common.math.FieldPose;
import xbot.common.math.XYPair;

public class RabbitPoint {

    public enum PointType {
        PositionAndHeading,
        HeadingOnly
    }
    
    public enum PointTerminatingType {
        Continue,
        Stop
    }
    
    public enum PointDriveStyle {
        Macro,
        Micro
    }
    
    public PointType pointType;
    public PointTerminatingType terminatingType;
    public PointDriveStyle driveStyle;
    public FieldPose pose;
    
    public RabbitPoint(double x, double y, double heading) {
        this(new FieldPose(new XYPair(x, y), new ContiguousHeading(heading)));
    }
    
    public RabbitPoint(FieldPose pose) {
        this.pose = pose;
        pointType = PointType.PositionAndHeading;
        terminatingType = PointTerminatingType.Continue;
        driveStyle = PointDriveStyle.Macro;
    }
    
    public RabbitPoint(FieldPose pose, PointType pointType, PointTerminatingType terminatingType, PointDriveStyle driveStyle) {
        this.pose = pose;
        this.pointType = pointType;
        this.terminatingType = terminatingType;
        this.driveStyle = driveStyle;
    }
    
    public static List<RabbitPoint> upgradeFieldPoseList(List<FieldPose> oldPoints) {
        List<RabbitPoint> rabbitPoints = new ArrayList<>();
        oldPoints.stream().forEach(point -> rabbitPoints.add(new RabbitPoint(point)));
        return rabbitPoints;
    }
    
    public static List<FieldPose> downgradeRabbitPointList(List<RabbitPoint> rabbitPoints) {
        List<FieldPose> fieldPoints = new ArrayList<>();
        rabbitPoints.stream().forEach(rabbitPoint -> fieldPoints.add(rabbitPoint.pose));
        return fieldPoints;
    }
}
