package xbot.common.properties;

import org.littletonrobotics.junction.Logger;
import org.littletonrobotics.junction.inputs.LoggableInputs;

import org.wpilib.tunable.TunableBase;
import org.wpilib.tunable.Tunables;

abstract class ManagedTunableBinding {
    private final String key;
    private final String logPrefix;
    protected final String logName;
    private final TunableLevel level;
    protected final boolean replay;
    private boolean published;

    ManagedTunableBinding(String key, TunableLevel level, boolean replay) {
        this.key = key;
        this.level = level;
        this.replay = replay;

        String normalizedKey = key.startsWith("/") ? key.substring(1) : key;
        int lastSlash = normalizedKey.lastIndexOf('/');
        String logParent =
                lastSlash < 0 ? "" : normalizedKey.substring(0, lastSlash) + "/";
        this.logPrefix = TunableManager.AKIT_LOG_NAMESPACE + logParent;
        this.logName = normalizedKey.substring(lastSlash + 1);
    }

    final String key() {
        return key;
    }

    final TunableLevel level() {
        return level;
    }

    abstract TunableBase tunable();

    abstract void refreshDataFrame();

    final boolean publish() {
        if (replay || published) {
            return true;
        }
        published = Tunables.publish(key, tunable());
        return published;
    }

    final void remove() {
        if (published) {
            Tunables.remove(key);
            published = false;
        }
    }

    final void processInputs(LoggableInputs inputs) {
        Logger.processInputs(logPrefix, inputs);
    }
}
