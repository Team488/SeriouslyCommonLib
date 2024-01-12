package xbot.common.controls.actuators;

/**
 * Represents an air compressor.
 */
public abstract class XCompressor {
    /**
     * A factory to create XCompressor instances.
     */
    public interface XCompressorFactory {
        /**
         * Creates an XCompressor instance.
         * @return The XCompressor instance.
         */
        XCompressor create();
    }

    /**
     * Disable the compressor.
     */
    public abstract void disable();

    /**
     * Enable the compressor.
     */
    public abstract void enable();

    /**
     * Get if the compressor is currently enabled.
     * @return True if the compressor is enabled.
     */
    public abstract boolean isEnabled();

    /**
     * Gets the current drawn by the compressor.
     * @return The current drawn by the compressor, in amps.
     */
    public abstract double getCurrent();

    /**
     * Gets whether the compressor is at its target pressure.
     * @return True if the compressor is at its target pressure.
     */
    public abstract boolean isAtTargetPressure();
}
