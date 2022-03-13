package xbot.common.subsystems.compressor;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import edu.wpi.first.wpilibj2.command.Command;
import xbot.common.command.BaseSubsystem;
import xbot.common.command.NamedRunCommand;
import xbot.common.controls.actuators.XCompressor;
import xbot.common.injection.wpi_factories.CommonLibFactory;

@Singleton
public class CompressorSubsystem extends BaseSubsystem {
    final XCompressor compressor;
    
    @Inject
    public CompressorSubsystem(CommonLibFactory clf) {
        this.compressor = clf.createCompressor();
    }

    public Command getEnableCommand() {
        return new NamedRunCommand(getName() + "-Enable", ()->compressor.enable(), this);
    }

    public Command getDisableCommand() {
        return new NamedRunCommand(getName() + "-Disable", ()->compressor.disable(), this);
    }
}
