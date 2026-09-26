package xbot.common.math;

import org.wpilib.math.geometry.Rotation2d;
import org.wpilib.tunable.TunableDouble;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;

import xbot.common.properties.TunableFactory;

public class FieldPosePropertyManager {

    private final TunableDouble xTunable;
    private final TunableDouble yTunable;
    private final TunableDouble headingTunable;

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
            TunableFactory tunableFactory) {
        tunableFactory.setPrefix(poseName);
        xTunable = tunableFactory.createDouble("X", x);
        yTunable = tunableFactory.createDouble("Y", y);
        headingTunable = tunableFactory.createDouble("Heading", heading);
    }

    public FieldPose getPose() {
        return new FieldPose(
                new XYPair(xTunable.get(), yTunable.get()),
                Rotation2d.fromDegrees(headingTunable.get()));
    }
}
