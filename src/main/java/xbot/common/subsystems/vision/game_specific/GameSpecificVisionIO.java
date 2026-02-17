// Copyright 2021-2025 FRC 6328
// http://github.com/Mechanical-Advantage
//
// This program is free software; you can redistribute it and/or
// modify it under the terms of the GNU General Public License
// version 3 as published by the Free Software Foundation or
// available in the root directory of this project.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU General Public License for more details.

package xbot.common.subsystems.vision.game_specific;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Transform3d;
import org.littletonrobotics.junction.AutoLog;

/**
 * IO interface for game-specific vision inputs.
 * Based on the AdvantageKit sample implementation by team 6328.
 */
public interface GameSpecificVisionIO {
    @AutoLog
    class GameSpecificVisionIOInputs {
        public boolean connected = false;
        public TargetObservation latestTargetObservation = new TargetObservation(0, 0, new Rotation2d(),
                new Rotation2d(), new Transform3d(), 1, true);
        public TargetObservation[] targetObservations = new TargetObservation[0];
    }

    /** Represents the angle to a simple target. */
    record TargetObservation(double timestamp, int fiducialId, Rotation2d tx, Rotation2d ty, Transform3d cameraToTarget,
            double ambiguity, boolean stale) {
    }

    default void updateInputs(GameSpecificVisionIOInputs inputs) {
    }
}
