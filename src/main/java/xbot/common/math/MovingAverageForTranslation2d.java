package xbot.common.math;

import edu.wpi.first.math.geometry.Translation2d;

public class MovingAverageForTranslation2d extends MovingAverage<Translation2d> {
    public MovingAverageForTranslation2d(int size) {
        super(new SumFunction<Translation2d>() {
            @Override
            public Translation2d add(Translation2d a, Translation2d b) {
                return a.plus(b);
            }

            @Override
            public Translation2d subtract(Translation2d a, Translation2d b) {
                return a.minus(b);
            }

            @Override
            public Translation2d divide(Translation2d a, int b) {
                return new Translation2d(a.getX() / b, a.getY() / b);
            }
        }, new Translation2d(), size);
    }

}
