package xbot.common.command;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import xbot.common.advantage.AKitLogger;
import xbot.common.advantage.DataFrameRefreshable;
import xbot.common.properties.IPropertySupport;

public abstract class BaseSubsystem extends SubsystemBase implements IPropertySupport, DataFrameRefreshable {
    
    protected final Logger log;
    protected final AKitLogger aKitLog;
    protected final List<DataFrameRefreshable> dataFrameRefreshables = new ArrayList<>();

    public BaseSubsystem() {
        log = LogManager.getLogger(this.getName());
        aKitLog = new AKitLogger(this);
    }

    public String getPrefix() {
        return this.getName() + "/";
    }

    protected void registerDataFrameRefreshable(DataFrameRefreshable refreshable) {
        dataFrameRefreshables.add(refreshable);
    }
    
    @Override
    public void refreshDataFrame() {
        for (DataFrameRefreshable refreshable : dataFrameRefreshables) {
            refreshable.refreshDataFrame();
        }
    }
}
