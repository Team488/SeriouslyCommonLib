package xbot.common.advantage;

import org.littletonrobotics.junction.Logger;

import edu.wpi.first.units.Measure;
import edu.wpi.first.units.Unit;
import edu.wpi.first.util.WPISerializable;
import edu.wpi.first.util.protobuf.Protobuf;
import edu.wpi.first.util.struct.Struct;
import edu.wpi.first.util.struct.StructSerializable;
import edu.wpi.first.wpilibj.smartdashboard.Mechanism2d;
import us.hebi.quickbuf.ProtoMessage;
import xbot.common.properties.IPropertySupport;

public class AKitLogger {

    private String prefix = "";

    public AKitLogger(String prefix) {
        this.prefix = prefix;
    }
    
    public AKitLogger(IPropertySupport parent) {
        this(parent.getPrefix());
    }

    public void record(String key, byte[] value) {
        Logger.recordOutput(this.prefix + key, value);
    }

    public void record(String key, boolean value) {
        Logger.recordOutput(this.prefix + key, value);
    }

    public void record(String key, int value) {
        Logger.recordOutput(this.prefix + key, value);
    }

    public void record(String key, long value) {
        Logger.recordOutput(this.prefix + key, value);
    }

    public void record(String key, float value) {
        Logger.recordOutput(this.prefix + key, value);
    }

    public void record(String key, double value) {
        Logger.recordOutput(this.prefix + key, value);
    }

    public void record(String key, String value) {
        Logger.recordOutput(this.prefix + key, value);
    }

    public <E extends Enum<E>> void record(String key, E value) {
        Logger.recordOutput(this.prefix + key, value);
    }

    public <U extends Unit<U>> void record(String key, Measure<U> value) {
        Logger.recordOutput(this.prefix + key, value);
    }

    public void record(String key, boolean[] value) {
        Logger.recordOutput(this.prefix + key, value);
    }

    public void record(String key, int[] value) {
        Logger.recordOutput(this.prefix + key, value);
    }

    public void record(String key, long[] value) {
        Logger.recordOutput(this.prefix + key, value);
    }

    public void record(String key, float[] value) {
        Logger.recordOutput(this.prefix + key, value);
    }

    public void record(String key, double[] value) {
        Logger.recordOutput(this.prefix + key, value);
    }

    public void record(String key, String[] value) {
        Logger.recordOutput(this.prefix + key, value);
    }

    public <T> void record(String key, Struct<T> struct, T value) {
        Logger.recordOutput(this.prefix + key, struct, value);
    }

    @SuppressWarnings("unchecked")
    public <T> void record(String key, Struct<T> struct, T... value) {
        Logger.recordOutput(this.prefix + key, struct, value);
    }

    //CHECKSTYLE:OFF
    public <T, MessageType extends ProtoMessage<?>> void record(String key, Protobuf<T, MessageType> proto, T value) {
        Logger.recordOutput(this.prefix + key, proto, value);
    }
    //CHECKSTYLE:ON

    public <T extends WPISerializable> void record(String key, T value) {
        Logger.recordOutput(this.prefix + key, value);
    }

    @SuppressWarnings("unchecked")
    public <T extends StructSerializable> void record(String key, T... value) {
        Logger.recordOutput(this.prefix + key, value);
    }

    public void record(String key, Mechanism2d value) {
        Logger.recordOutput(this.prefix + key, value);
    }

}
