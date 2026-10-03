package xbot.common.properties;

import org.littletonrobotics.junction.LogTable;
import org.littletonrobotics.junction.inputs.LoggableInputs;

import org.wpilib.tunable.TunableConfig;
import org.wpilib.tunable.TunableDouble;

final class DoubleTunableBinding extends ManagedTunableBinding {
    private final TunablePersistence persistence;
    private final TunableDouble tunable;
    private final Runnable onValueChanged;
    private double value;

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

    DoubleTunableBinding(
            String key,
            double defaultValue,
            TunableLevel level,
            boolean replay,
            TunablePersistence persistence,
            Runnable onValueChanged) {
        super(key, level, replay);
        this.persistence = persistence;
        this.onValueChanged = onValueChanged;
        this.value = load(defaultValue);
        this.tunable = new TunableDouble(new TunableConfig(), true) {
            @Override
            public void set(double newValue) {
                if (Double.compare(value, newValue) == 0 || DoubleTunableBinding.this.replay) {
                    return;
                }
                value = newValue;
                persistence.setDouble(key(), newValue);
                markChanged();
                onValueChanged.run();
            }

            @Override
            public double get() {
                return value;
            }
        };
    }

    private double load(double defaultValue) {
        if (replay) {
            return defaultValue;
        }
        Double stored = persistence.getDouble(key());
        if (stored != null) {
            return stored;
        }
        persistence.setDouble(key(), defaultValue);
        return defaultValue;
    }

    @Override
    TunableDouble tunable() {
        return tunable;
    }

    @Override
    void refreshDataFrame() {
        processInputs(inputs);
    }
}
