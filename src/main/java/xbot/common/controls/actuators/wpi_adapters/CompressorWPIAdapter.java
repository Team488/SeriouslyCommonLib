package xbot.common.controls.actuators.wpi_adapters;

import org.wpilib.hardware.bus.CANPort;
import org.wpilib.hardware.pneumatic.Compressor;
import org.wpilib.hardware.pneumatic.PneumaticsModuleType;

import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;

import xbot.common.controls.actuators.XCompressor;

public class CompressorWPIAdapter extends XCompressor {
    Compressor compressor;

    @AssistedFactory
    public abstract static class CompressorWPIAdapterFactory implements XCompressorFactory {
        public abstract CompressorWPIAdapter create();
    }

    @AssistedInject
    public CompressorWPIAdapter() {
        this.compressor = new Compressor(CANPort.CAN_D0, PneumaticsModuleType.REV_PH);
    }

    @Override
    public void disable() {
        compressor.disable();
    }

    @Override
    public void enable() {
        compressor.enableDigital();
    }

    @Override
    public boolean isEnabled() {
        return compressor.isEnabled();
    }

    @Override
    public double getCurrent() {
        return compressor.getCurrent();
    }

    @Override
    public boolean isAtTargetPressure() {
        return !compressor.getPressureSwitchValue();
    }
}
