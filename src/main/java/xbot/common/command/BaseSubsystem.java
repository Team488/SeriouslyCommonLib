package xbot.common.command;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import xbot.common.properties.IPropertySupport;

public abstract class BaseSubsystem extends SubsystemBase implements IPropertySupport {
    
    protected Logger log;

    public BaseSubsystem() {
        log = LogManager.getLogger(this.getName());
    }

    public String getPrefix() {
        return this.getName() + "/";
    }
}
