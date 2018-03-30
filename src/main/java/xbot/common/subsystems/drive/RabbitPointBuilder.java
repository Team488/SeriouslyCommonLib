package xbot.common.subsystems.drive;

import java.util.ArrayList;
import java.util.List;

import xbot.common.math.FieldPose;
import xbot.common.subsystems.drive.RabbitPoint.PointDriveStyle;
import xbot.common.subsystems.drive.RabbitPoint.PointTerminatingType;
import xbot.common.subsystems.drive.RabbitPoint.PointType;

public class RabbitPointBuilder {

    List<RabbitPoint> output;
    
    private PointType pointType = PointType.PositionAndHeading;
    private PointTerminatingType terminatingType = PointTerminatingType.Continue;
    private PointDriveStyle driveStyle = PointDriveStyle.Macro;
    
    public RabbitPointBuilder() {
        output = new ArrayList<>();
    }
    
    public RabbitPointBuilder addPose(FieldPose pose) {
        output.add(new RabbitPoint(pose, pointType, terminatingType, driveStyle));
        return this;
    }
    
    public RabbitPointBuilder changePointType(PointType newPointType) {
        pointType = newPointType;
        return this;
    }
    
    public RabbitPointBuilder changeTerminatingType(PointTerminatingType newTerminatingType) {
        terminatingType = newTerminatingType;
        return this;
    }
    
    public RabbitPointBuilder changeDriveStyle(PointDriveStyle newStyle) {
        driveStyle = newStyle;
        return this;
    }
    
    public List<RabbitPoint> build() {
        return output;
    }
}
