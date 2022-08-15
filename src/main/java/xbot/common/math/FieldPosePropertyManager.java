package xbot.common.math;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;

import edu.wpi.first.math.geometry.Rotation2d;
import xbot.common.properties.DoubleProperty;
import xbot.common.properties.PropertyFactory;

public class FieldPosePropertyManager {

    private final DoubleProperty xProp;
    private final DoubleProperty yProp;
    private final DoubleProperty headingProp;

    @AssistedFactory
    public abstract static class FieldPosePropertyManagerFactory {
        public abstract FieldPosePropertyManager create(
                @Assisted("poseName") String poseName,
                @Assisted("x") double x,
                @Assisted("y") double y,
                @Assisted("heading") double heading);

        public FieldPosePropertyManager create(
                String poseName,
                FieldPose fieldPose) {
            return create(poseName, fieldPose.getPoint().x, fieldPose.getPoint().y, fieldPose.getHeading().getDegrees());
        }
    }

    @AssistedInject
    public FieldPosePropertyManager(
            @Assisted("poseName") String poseName,
            @Assisted("x") double x,
            @Assisted("y") double y,
            @Assisted("heading") double heading,
            PropertyFactory propMan) {
        propMan.setPrefix(poseName);
        xProp = propMan.createPersistentProperty("X", x);
        yProp = propMan.createPersistentProperty("Y", y);
        headingProp = propMan.createPersistentProperty("Heading", heading);
    }

    public FieldPose getPose() {
        return new FieldPose(new XYPair(xProp.get(), yProp.get()), Rotation2d.fromDegrees(headingProp.get()));
    }
}