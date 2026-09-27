package xbot.common.properties;

import java.util.function.Consumer;
import java.util.function.Supplier;

import org.wpilib.tunable.Tunable;
import org.wpilib.tunable.TunableConfig;
import org.wpilib.tunable.TunableDouble;
import org.wpilib.units.Measure;
import org.wpilib.units.Unit;

/**
 * A direct WPILib tunable that presents a unit-aware value while publishing its
 * magnitude in the unit named in its key.
 *
 * @param <M> measure type
 * @param <U> unit type
 */
public final class TunableMeasure<M extends Measure<U>, U extends Unit>
        extends Tunable<M>
        implements Tunable.CustomTunable {
    private final U displayUnit;
    private final Supplier<M> getter;
    private final Consumer<M> setter;
    private final TunableDouble magnitudeTunable;

    TunableMeasure(
            U displayUnit,
            Supplier<M> getter,
            Consumer<M> setter,
            TunableConfig config) {
        super(config);
        this.displayUnit = displayUnit;
        this.getter = getter;
        this.setter = setter;
        this.magnitudeTunable = new TunableDouble(config, true) {
            @Override
            public void set(double value) {
                TunableMeasure.this.setter.accept(castMeasure(displayUnit.of(value)));
                markChanged();
            }

            @Override
            public double get() {
                return TunableMeasure.this.getter.get().in(displayUnit);
            }
        };
    }

    @SuppressWarnings("unchecked")
    private M castMeasure(Measure<?> value) {
        return (M) value;
    }

    @Override
    public void set(M value) {
        if (!displayUnit.getBaseUnit().equivalent(value.unit().getBaseUnit())) {
            throw new IllegalArgumentException(
                    "Measure unit " + value.unit() + " is not compatible with " + displayUnit);
        }
        magnitudeTunable.set(value.in(displayUnit));
    }

    @Override
    public M get() {
        return getter.get();
    }

    @Override
    public Class<M> getTypeClass() {
        @SuppressWarnings("unchecked")
        Class<M> measureClass = (Class<M>) (Class<?>) Measure.class;
        return measureClass;
    }

    @Override
    public TunableDouble getInnerTunable() {
        return magnitudeTunable;
    }

    @Override
    public boolean hasChanged() {
        return magnitudeTunable.hasChanged();
    }

    @Override
    public boolean supportsChangeNotification() {
        return magnitudeTunable.supportsChangeNotification();
    }

    @Override
    public void resetChanged() {
        magnitudeTunable.resetChanged();
    }
}
