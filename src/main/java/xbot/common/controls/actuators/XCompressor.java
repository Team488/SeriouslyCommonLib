package xbot.common.controls.actuators;

public abstract class XCompressor {
    
    public interface XCompressorFactory {
        XCompressor create();
    }

    public abstract void disable();
    public abstract void enable();
    public abstract boolean isEnabled();
}
