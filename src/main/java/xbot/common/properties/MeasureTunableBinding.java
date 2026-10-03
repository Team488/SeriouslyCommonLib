package xbot.common.properties;

import org.littletonrobotics.junction.LogTable;
import org.littletonrobotics.junction.inputs.LoggableInputs;

import org.wpilib.tunable.TunableConfig;
import org.wpilib.units.Measure;
import org.wpilib.units.Unit;

final class MeasureTunableBinding<M extends Measure<U>, U extends Unit>
        extends ManagedTunableBinding {
    private final TunablePersistence persistence;
    private final U displayUnit;
    private final TunableMeasure<M, U> tunable;
    private final Runnable onValueChanged;
    private M value;

    private final LoggableInputs inputs = new LoggableInputs() {
        @Override
        public void toLog(LogTable table) {
            table.putMeasure(logName, value);
        }

        @Override
        public void fromLog(LogTable table) {
            value = table.getMeasure(logName, value);
        }
    };

    MeasureTunableBinding(
            String key,
            M defaultValue,
            TunableLevel level,
            boolean replay,
            TunablePersistence persistence,
            Runnable onValueChanged) {
        super(key, level, replay);
        this.persistence = persistence;
        this.displayUnit = defaultValue.unit();
        this.onValueChanged = onValueChanged;
        this.value = load(defaultValue);
        TunableConfig config = new TunableConfig()
                .withProperty("unit", quoteJson(defaultValue.unit().symbol()));
        this.tunable = new TunableMeasure<>(
                displayUnit,
                () -> value,
                this::setValue,
                config);
    }

    private M load(M defaultValue) {
        if (replay) {
            return defaultValue;
        }
        Double stored = persistence.getDouble(key());
        if (stored != null) {
            return castMeasure(defaultValue.unit().of(stored));
        }
        persistence.setDouble(key(), defaultValue.in(defaultValue.unit()));
        return defaultValue;
    }

    private void setValue(M newValue) {
        if (replay || value.isEquivalent(newValue)) {
            return;
        }
        value = newValue;
        persistence.setDouble(key(), newValue.in(displayUnit));
        onValueChanged.run();
    }

    @SuppressWarnings("unchecked")
    private M castMeasure(Measure<?> measure) {
        return (M) measure;
    }

    private static String quoteJson(String value) {
        return "\"" + value.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
    }

    @Override
    TunableMeasure<M, U> tunable() {
        return tunable;
    }

    @Override
    void refreshDataFrame() {
        processInputs(inputs);
    }
}
