package xbot.common.properties;

import org.littletonrobotics.junction.LogTable;
import org.littletonrobotics.junction.inputs.LoggableInputs;

import org.wpilib.tunable.TunableBoolean;
import org.wpilib.tunable.TunableConfig;

final class BooleanTunableBinding extends ManagedTunableBinding {
    private final TunablePersistence persistence;
    private final TunableBoolean tunable;
    private final Runnable onValueChanged;
    private boolean value;

    private final LoggableInputs inputs = new LoggableInputs() {
        @Override
        public void toLog(LogTable table) {
            table.put(logName, value);
        }

        @Override
        public void fromLog(LogTable table) {
            value = table.get(logName, value);
        }
    };

    BooleanTunableBinding(
            String key,
            boolean defaultValue,
            TunableLevel level,
            boolean replay,
            TunablePersistence persistence,
            Runnable onValueChanged) {
        super(key, level, replay);
        this.persistence = persistence;
        this.onValueChanged = onValueChanged;
        this.value = load(defaultValue);
        this.tunable = new TunableBoolean(new TunableConfig(), true) {
            @Override
            public void set(boolean newValue) {
                if (value == newValue || BooleanTunableBinding.this.replay) {
                    return;
                }
                value = newValue;
                persistence.setBoolean(key(), newValue);
                markChanged();
                onValueChanged.run();
            }

            @Override
            public boolean get() {
                return value;
            }
        };
    }

    private boolean load(boolean defaultValue) {
        if (replay) {
            return defaultValue;
        }
        Boolean stored = persistence.getBoolean(key());
        if (stored != null) {
            return stored;
        }
        persistence.setBoolean(key(), defaultValue);
        return defaultValue;
    }

    @Override
    TunableBoolean tunable() {
        return tunable;
    }

    @Override
    void refreshDataFrame() {
        processInputs(inputs);
    }
}
