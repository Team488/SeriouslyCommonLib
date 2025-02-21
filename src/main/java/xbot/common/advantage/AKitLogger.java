package xbot.common.advantage;

import org.littletonrobotics.junction.Logger;

import edu.wpi.first.units.Measure;
import edu.wpi.first.units.Unit;
import edu.wpi.first.util.WPISerializable;
import edu.wpi.first.util.protobuf.Protobuf;
import edu.wpi.first.util.struct.Struct;
import edu.wpi.first.util.struct.StructSerializable;
import org.littletonrobotics.junction.mechanism.LoggedMechanism2d;
import us.hebi.quickbuf.ProtoMessage;
import xbot.common.properties.IPropertySupport;

public class AKitLogger {
    public enum LogLevel {
        DEBUG, INFO
    }
    private static LogLevel globalLogLevel = LogLevel.INFO;

    private String prefix = "";
    private LogLevel logLevel = globalLogLevel;

    /**
     * This controls the log level for all AKitLoggers.
     * This will generally be set to INFO during competitions so that debug logs are not sent
     * to the network table.
     * @param level new level to set
     */
    public static void setGlobalLogLevel(LogLevel level) {
        globalLogLevel = level;
    }

    public AKitLogger(String prefix) {
        this.prefix = prefix;
    }

    public AKitLogger(IPropertySupport parent) {
        this(parent.getPrefix());
    }

    /**
     * Set the log level for this particular logger instance.
     * Log calls made after this will have that level when checking
     * if they should record or not.
     * @param level new level to set
     */
    public void setLogLevel(LogLevel level) {
        this.logLevel = level;
    }

    /**
     * Changes the log prefix. May be needed for subsystems that have the same name, such as
     * multiple instances of the Swerve modules.
     * @param prefix logging prefix, should end with a "/"
     */
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    protected boolean shouldSkipLogging() {
        return this.logLevel == LogLevel.DEBUG && globalLogLevel == LogLevel.INFO;
    }

    public void record(String key, byte[] value) {
        if(this.shouldSkipLogging()) {
            return;
        }
        Logger.recordOutput(this.prefix + key, value);
    }

    public void record(String key, boolean value) {
        if(this.shouldSkipLogging()) {
            return;
        }
        Logger.recordOutput(this.prefix + key, value);
    }

    public void record(String key, int value) {
        if(this.shouldSkipLogging()) {
            return;
        }
        Logger.recordOutput(this.prefix + key, value);
    }

    public void record(String key, long value) {
        if(this.shouldSkipLogging()) {
            return;
        }
        Logger.recordOutput(this.prefix + key, value);
    }

    public void record(String key, float value) {
        if(this.shouldSkipLogging()) {
            return;
        }
        Logger.recordOutput(this.prefix + key, value);
    }

    public void record(String key, double value) {
        if(this.shouldSkipLogging()) {
            return;
        }
        Logger.recordOutput(this.prefix + key, value);
    }

    public void record(String key, String value) {
        if(this.shouldSkipLogging()) {
            return;
        }
        Logger.recordOutput(this.prefix + key, value);
    }

    public <E extends Enum<E>> void record(String key, E value) {
        if(this.shouldSkipLogging()) {
            return;
        }
        Logger.recordOutput(this.prefix + key, value);
    }

    public <U extends Unit> void record(String key, Measure<U> value) {
        if(this.shouldSkipLogging()) {
            return;
        }
        Logger.recordOutput(this.prefix + key, value);
    }

    public void record(String key, boolean[] value) {
        if(this.shouldSkipLogging()) {
            return;
        }
        Logger.recordOutput(this.prefix + key, value);
    }

    public void record(String key, int[] value) {
        if(this.shouldSkipLogging()) {
            return;
        }
        Logger.recordOutput(this.prefix + key, value);
    }

    public void record(String key, long[] value) {
        if(this.shouldSkipLogging()) {
            return;
        }
        Logger.recordOutput(this.prefix + key, value);
    }

    public void record(String key, float[] value) {
        if(this.shouldSkipLogging()) {
            return;
        }
        Logger.recordOutput(this.prefix + key, value);
    }

    public void record(String key, double[] value) {
        if(this.shouldSkipLogging()) {
            return;
        }
        Logger.recordOutput(this.prefix + key, value);
    }

    public void record(String key, String[] value) {
        if(this.shouldSkipLogging()) {
            return;
        }
        Logger.recordOutput(this.prefix + key, value);
    }

    public <T> void record(String key, Struct<T> struct, T value) {
        if(this.shouldSkipLogging()) {
            return;
        }
        Logger.recordOutput(this.prefix + key, struct, value);
    }

    @SuppressWarnings("unchecked")
    public <T> void record(String key, Struct<T> struct, T... value) {
        if(this.shouldSkipLogging()) {
            return;
        }
        Logger.recordOutput(this.prefix + key, struct, value);
    }

    //CHECKSTYLE:OFF
    public <T, MessageType extends ProtoMessage<?>> void record(String key, Protobuf<T, MessageType> proto, T value) {
        if(this.shouldSkipLogging()) {
            return;
        }
        Logger.recordOutput(this.prefix + key, proto, value);
    }
    //CHECKSTYLE:ON

    public <T extends WPISerializable> void record(String key, T value) {
        if(this.shouldSkipLogging()) {
            return;
        }
        Logger.recordOutput(this.prefix + key, value);
    }

    @SuppressWarnings("unchecked")
    public <T extends StructSerializable> void record(String key, T... value) {
        if(this.shouldSkipLogging()) {
            return;
        }
        Logger.recordOutput(this.prefix + key, value);
    }

    public void record(String key, LoggedMechanism2d value) {
        if(this.shouldSkipLogging()) {
            return;
        }
        Logger.recordOutput(this.prefix + key, value);
    }

}
