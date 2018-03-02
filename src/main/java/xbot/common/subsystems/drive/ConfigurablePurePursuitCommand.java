package xbot.common.subsystems.drive;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import com.google.inject.Inject;

import xbot.common.injection.wpi_factories.CommonLibFactory;
import xbot.common.math.FieldPose;
import xbot.common.properties.XPropertyManager;
import xbot.common.subsystems.pose.BasePoseSubsystem;

public class ConfigurablePurePursuitCommand extends PurePursuitCommand {
    private List<FieldPose> originalPoints;
    private PursuitMode mode;
    private Supplier<List<FieldPose>> externalPointSource;

    @Inject
    public ConfigurablePurePursuitCommand(CommonLibFactory clf, BasePoseSubsystem pose, BaseDriveSubsystem drive,
            XPropertyManager propMan) {
        super(clf, pose, drive, propMan);
        mode = PursuitMode.Absolute;
        originalPoints = new ArrayList<>();
        externalPointSource = null;
    }

    public void addPoint(FieldPose point) {
        originalPoints.add(point);
    }

    public void setMode(PursuitMode mode) {
        this.mode = mode;
    }
    
    public void setPointSupplier(Supplier<List<FieldPose>> externalPointSource) {
        this.externalPointSource = externalPointSource;
    }
    
    protected List<FieldPose> getOriginalPoints() {
        if (externalPointSource != null) {
            originalPoints = externalPointSource.get();
        }
        return originalPoints;
    }
    
    protected PursuitMode getPursuitMode() {
        return this.mode;
    }

}
