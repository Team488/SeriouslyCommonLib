package xbot.common.controls.actuators.wpi_adapters;

import com.google.inject.Inject;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import xbot.common.controls.actuators.XCompressor;

public class CompressorWPIAdapter extends XCompressor {
    Compressor compressor;

    @Inject
    public CompressorWPIAdapter() {
        this.compressor = new Compressor(PneumaticsModuleType.REVPH);
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
        return compressor.enabled();
    }
}
