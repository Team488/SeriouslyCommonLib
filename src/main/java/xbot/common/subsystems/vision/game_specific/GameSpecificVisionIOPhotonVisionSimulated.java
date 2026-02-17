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

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import edu.wpi.first.math.geometry.Transform3d;

/**
 * IO implementation for a simulated PhotonVision environment.
 * Based on the AdvantageKit sample implementation by team 6328.
 */
public class GameSpecificVisionIOPhotonVisionSimulated extends GameSpecificVisionIOPhotonVision {

    @AssistedFactory
    public abstract static class FactoryImpl implements GameSpecificVisionIOFactory {
        public abstract GameSpecificVisionIOPhotonVisionSimulated create(String name, Transform3d robotToCamera);
    }

    /**
     * Creates a new GameSpecificVisionIOPhotonVisionSimulated.
     *
     * @param name          The configured name of the camera.
     * @param robotToCamera The 3D position of the camera relative to the robot.
     */
    @AssistedInject
    public GameSpecificVisionIOPhotonVisionSimulated(@Assisted String name, @Assisted Transform3d robotToCamera) {
        super(name, robotToCamera);
    }

    @Override
    public void updateInputs(GameSpecificVisionIOInputs inputs) {
        // In simulation, just call parent update
        super.updateInputs(inputs);
    }

}
