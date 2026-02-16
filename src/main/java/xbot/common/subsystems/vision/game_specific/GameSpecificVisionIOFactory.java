package xbot.common.subsystems.vision.game_specific;

import edu.wpi.first.math.geometry.Transform3d;

public interface GameSpecificVisionIOFactory {
    GameSpecificVisionIO create(String name, Transform3d robotToCamera);
}
