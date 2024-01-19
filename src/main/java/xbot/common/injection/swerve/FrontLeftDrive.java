package xbot.common.injection.swerve;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Qualifier;

/**
 * This annotation denotes the front left swerve module.
 */
@Retention(RetentionPolicy.RUNTIME)
@Qualifier
public @interface FrontLeftDrive {}