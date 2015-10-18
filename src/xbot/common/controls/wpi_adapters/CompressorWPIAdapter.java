package xbot.common.controls.wpi_adapters;

import edu.wpi.first.wpilibj.Compressor;
import xbot.common.controls.XCompressor;

public class CompressorWPIAdapter implements XCompressor {
	Compressor compressor;
	public CompressorWPIAdapter() {
		this.compressor = new Compressor();
	}
}
