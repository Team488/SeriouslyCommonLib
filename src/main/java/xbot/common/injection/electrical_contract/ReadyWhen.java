package xbot.common.injection.electrical_contract;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks an electrical contract getter as part of a robot mechanism. The device only exists on a
 * robot whose contract reports that mechanism as ready ({@link XElectricalReportContract#isReady(String)}).
 *
 * <p>The value is the name of a constant in the robot's own hardware enum (for example
 * {@code @ReadyWhen("LeftShooter")} for {@code Hardware.LeftShooter}). The library can't name a
 * robot enum type, so the electrical report checks every name against
 * {@link XElectricalReportContract#getHardwareNames()} and fails on a typo.
 *
 * <p>Put it on the getter where it's first declared (usually the robot's abstract
 * ElectricalContract); overrides in subclasses inherit it.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ReadyWhen {
    /** Name of the hardware/mechanism this device belongs to. */
    String value();
}
