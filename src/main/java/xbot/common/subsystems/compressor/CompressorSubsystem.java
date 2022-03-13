package xbot.common.subsystems.compressor;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import xbot.common.command.BaseSubsystem;
import xbot.common.command.NamedRunCommand;
import xbot.common.controls.actuators.XCompressor;
import xbot.common.injection.wpi_factories.CommonLibFactory;
import xbot.common.properties.BooleanProperty;
import xbot.common.properties.PropertyFactory;

@Singleton
public class CompressorSubsystem extends BaseSubsystem {
    final XCompressor compressor;
    final BooleanProperty isEnabledProperty;
    
    @Inject
    public CompressorSubsystem(CommonLibFactory clf, PropertyFactory pf) {
        this.compressor = clf.createCompressor();
        this.isEnabledProperty = pf.createEphemeralProperty("Compressor Enabled", compressor.isEnabled());

        CommandScheduler.getInstance().registerSubsystem(this);
    }

    public Command getEnableCommand() {
        return new NamedRunCommand(getName() + "-Enable", ()->compressor.enable(), this);
    }

    public Command getDisableCommand() {
        return new NamedRunCommand(getName() + "-Disable", ()->compressor.disable(), this);
    }

    @Override
    public void periodic() {
        super.periodic();
        isEnabledProperty.set(compressor.isEnabled());
    }
}
