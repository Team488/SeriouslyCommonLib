package xbot.common.controls.actuators.wpi_adapters;

import com.google.inject.Inject;

import edu.wpi.first.wpilibj.Compressor;
import xbot.common.controls.actuators.XCompressor;

public class CompressorWPIAdapter extends XCompressor {
    Compressor compressor;

    @Inject
    public CompressorWPIAdapter() {
        this.compressor = new Compressor();
    }
}
