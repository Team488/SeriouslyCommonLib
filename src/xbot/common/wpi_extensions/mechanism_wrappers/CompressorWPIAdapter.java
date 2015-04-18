package xbot.common.wpi_extensions.mechanism_wrappers;

import edu.wpi.first.wpilibj.Compressor;

public class CompressorWPIAdapter implements XCompressor {
	Compressor compressor;
	public CompressorWPIAdapter() {
		this.compressor = new Compressor();
	}
}
