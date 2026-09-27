package xbot.common.properties;

import org.littletonrobotics.junction.LogTable;
import org.littletonrobotics.junction.inputs.LoggableInputs;

import org.wpilib.tunable.Tunable;
import org.wpilib.tunable.TunableConfig;

final class StringTunableBinding extends ManagedTunableBinding {
    private final TunablePersistence persistence;
    private final Tunable<String> tunable;
    private final Runnable onValueChanged;
    private String value;

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

    StringTunableBinding(
            String key,
            String defaultValue,
            TunableLevel level,
            boolean replay,
            TunablePersistence persistence,
            Runnable onValueChanged) {
        super(key, level, replay);
        this.persistence = persistence;
        this.onValueChanged = onValueChanged;
        this.value = load(defaultValue);
        this.tunable = new Tunable<String>(new TunableConfig(), true) {
            @Override
            public void set(String newValue) {
                if (value.equals(newValue) || StringTunableBinding.this.replay) {
                    return;
                }
                value = newValue;
                persistence.setString(key(), newValue);
                markChanged();
                onValueChanged.run();
            }

            @Override
            public String get() {
                return value;
            }

            @Override
            public Class<String> getTypeClass() {
                return String.class;
            }
        };
    }

    private String load(String defaultValue) {
        if (replay) {
            return defaultValue;
        }
        String stored = persistence.getString(key());
        if (stored != null) {
            return stored;
        }
        persistence.setString(key(), defaultValue);
        return defaultValue;
    }

    @Override
    Tunable<String> tunable() {
        return tunable;
    }

    @Override
    void refreshDataFrame() {
        processInputs(inputs);
    }
}
