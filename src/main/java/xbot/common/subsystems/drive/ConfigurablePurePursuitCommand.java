package xbot.common.subsystems.drive;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import javax.inject.Inject;

import xbot.common.math.FieldPose;
import xbot.common.properties.PropertyFactory;
import xbot.common.subsystems.drive.control_logic.HeadingModule.HeadingModuleFactory;
import xbot.common.subsystems.pose.BasePoseSubsystem;

public class ConfigurablePurePursuitCommand extends PurePursuitCommand {
    private List<RabbitPoint> originalPoints;
    private PointLoadingMode mode;
    private Supplier<List<RabbitPoint>> externalPointSource;

    @Inject
    public ConfigurablePurePursuitCommand(HeadingModuleFactory headingModuleFactory, BasePoseSubsystem pose, BaseDriveSubsystem drive,
            PropertyFactory propMan) {
        super(headingModuleFactory, pose, drive, propMan);
        mode = PointLoadingMode.Absolute;
        originalPoints = new ArrayList<>();
        externalPointSource = null;
    }

    public void addPoint(RabbitPoint point) {
        originalPoints.add(point);
    }
    
    public void addPoint(FieldPose point) {
        originalPoints.add(new RabbitPoint(point));
    }

    public void setMode(PointLoadingMode mode) {
        this.mode = mode;
    }

    public void setPoints(List<RabbitPoint> points) {
        this.originalPoints = points;
    }
    
    public void setPointSupplier(Supplier<List<RabbitPoint>> externalPointSource) {
        this.externalPointSource = externalPointSource;
    }
    
    protected List<RabbitPoint> getOriginalPoints() {
        if (externalPointSource != null) {
            originalPoints = externalPointSource.get();
        }
        return originalPoints;
    }
    
    protected PointLoadingMode getPursuitMode() {
        return this.mode;
    }

}
