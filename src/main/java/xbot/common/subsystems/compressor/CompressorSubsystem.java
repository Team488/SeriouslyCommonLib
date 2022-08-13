package xbot.common.subsystems.compressor;

import javax.inject.Inject;
import javax.inject.Singleton;

import edu.wpi.first.wpilibj2.command.Command;
import xbot.common.command.BaseSubsystem;
import xbot.common.command.NamedRunCommand;
import xbot.common.controls.actuators.XCompressor;
import xbot.common.controls.actuators.XCompressor.XCompressorFactory;
import xbot.common.properties.BooleanProperty;
import xbot.common.properties.PropertyFactory;

@Singleton
public class CompressorSubsystem extends BaseSubsystem {
    final XCompressor compressor;
    final BooleanProperty isEnabledProperty;
    
    @Inject
    public CompressorSubsystem(XCompressorFactory compressorFactory, PropertyFactory pf) {
        pf.setPrefix("CompressorSubsystem");
        this.compressor = compressorFactory.create();
        this.isEnabledProperty = pf.createEphemeralProperty("Compressor Enabled", compressor.isEnabled());
        this.register();
    }

    public void enable() {
        this.compressor.enable();
    }

    public void disable() {
        this.compressor.disable();
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
