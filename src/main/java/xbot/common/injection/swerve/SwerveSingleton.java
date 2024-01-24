package xbot.common.injection.swerve;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

/**
 * This annotation denotes a singleton scoped to a single swerve module.
 */
@Retention(RetentionPolicy.RUNTIME)
@Scope
public @interface SwerveSingleton {}