package xbot.common.math;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

import xbot.common.properties.DoubleProperty;
import xbot.common.properties.XPropertyManager;

public class FieldPosePropertyManager {
    
    private final DoubleProperty xProp;
    private final DoubleProperty yProp;
    private final DoubleProperty headingProp;

    @AssistedInject
    public FieldPosePropertyManager(
        @Assisted("poseName") String poseName, 
        @Assisted("x") double x,
        @Assisted("y") double y,
        @Assisted("heading") double heading,
        XPropertyManager propMan) {
        xProp = propMan.createPersistentProperty(poseName + "/" + "X", x);
        yProp = propMan.createPersistentProperty(poseName + "/" + "Y", y);
        headingProp = propMan.createPersistentProperty(poseName + "/" + "Heading", heading);
    }

    @AssistedInject
    public FieldPosePropertyManager(
        @Assisted("poseName") String poseName, 
        @Assisted("fieldPose") FieldPose fieldPose,
        XPropertyManager propMan) {
        xProp = propMan.createPersistentProperty(poseName + "/" + "X", fieldPose.getPoint().x);
        yProp = propMan.createPersistentProperty(poseName + "/" + "Y", fieldPose.getPoint().y);
        headingProp = propMan.createPersistentProperty(poseName + "/" + "Heading", fieldPose.getHeading().getValue());
    }

    public FieldPose getPose() {
        return new FieldPose(new XYPair(xProp.get(), yProp.get()), new ContiguousHeading(headingProp.get()));
    }
}