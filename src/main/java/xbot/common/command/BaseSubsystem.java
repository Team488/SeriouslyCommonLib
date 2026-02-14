package xbot.common.command;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import xbot.common.advantage.AKitLogger;
import xbot.common.advantage.DataFrameRefreshable;
import xbot.common.properties.IPropertySupport;

public abstract class BaseSubsystem extends SubsystemBase implements IPropertySupport, DataFrameRefreshable {

    protected final Logger log;
    protected final AKitLogger aKitLog;
//    protected final List<DataFrameRefreshable> dataFrameRefreshables = new ArrayList<>();

    public BaseSubsystem() {
        super();
        log = LogManager.getLogger(this.getName());
        aKitLog = new AKitLogger(this);

        DataFrameRegistry.registerSubsystem(this);
    }

    public String getPrefix() {
        return this.getName() + "/";
    }

//    protected void registerDataFrameRefreshable(DataFrameRefreshable refreshable) {
//        dataFrameRefreshables.add(refreshable);
//    }

    /**
     * This method is called on each {@link edu.wpi.first.wpilibj2.command.CommandScheduler} loop.
     * @apiNote Subsystem periodic() methods are not executed in a predictable order.
     * If you need to ensure that some state is available before any other subsystems are run,
     * you should implement {@link #refreshDataFrame()} to update the state before the scheduler runs periodic() on all the subsystems.
     */
    @Override
    public void periodic() {
        super.periodic();
    }

    @Override
    public void refreshDataFrame() {
    }
}
