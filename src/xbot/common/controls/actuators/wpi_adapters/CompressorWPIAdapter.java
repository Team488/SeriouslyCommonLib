package xbot.common.controls.actuators.wpi_adapters;

import edu.wpi.first.wpilibj.Compressor;
import xbot.common.controls.actuators.XCompressor;

public class CompressorWPIAdapter implements XCompressor {
	Compressor compressor;
	public CompressorWPIAdapter() {
		this.compressor = new Compressor();
	}
}
