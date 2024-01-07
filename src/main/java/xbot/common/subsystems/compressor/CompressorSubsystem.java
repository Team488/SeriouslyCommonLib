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

/**
 * Subsystem for managing compressor state.
 */
@Singleton
public class CompressorSubsystem extends BaseSubsystem {
    protected final XCompressor compressor;
    protected final BooleanProperty isEnabledProperty;

    /**
     * Create a new CompressorSubsystem.
     * @param compressorFactory The compressor factory.
     * @param pf The property factory.
     */
    @Inject
    public CompressorSubsystem(XCompressorFactory compressorFactory, PropertyFactory pf) {
        pf.setPrefix("CompressorSubsystem");
        this.compressor = compressorFactory.create();
        this.isEnabledProperty = pf.createEphemeralProperty("Compressor Enabled", compressor.isEnabled());
        this.register();
    }

    /**
     * Gets whether the compressor is enabled.
     * @return True if enabled, false if not.
     */
    public boolean isEnabled() {
        return this.compressor.isEnabled();
    }

    /**
     * Enable the compressor.
     */
    public void enable() {
        this.compressor.enable();
    }

    /**
     * Disable the compressor.
     */
    public void disable() {
        this.compressor.disable();
    }

    /**
     * Gets the current consumed by the compressor.
     * @return The current in amps.
     */
    public double getCompressorCurrent() {
        return this.compressor.getCurrent();
    }

    /**
     * Gets a command to enable the compressor.
     * @return The command.
     */
    public final Command getEnableCommand() {
        return new NamedRunCommand(getName() + "-Enable", this::enable, this);
    }

    /**
     * Gets a command to disable the compressor.
     * @return The command.
     */
    public final Command getDisableCommand() {
        return new NamedRunCommand(getName() + "-Disable", this::disable, this);
    }

    @Override
    public void periodic() {
        super.periodic();
        isEnabledProperty.set(compressor.isEnabled());
    }
}
