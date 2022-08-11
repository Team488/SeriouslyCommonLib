package xbot.common.injection.factories;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import xbot.common.math.PIDManager;

@AssistedFactory
public abstract class PIDFactory {

        public abstract PIDManager create(
                        String functionName,
                        @Assisted("defaultP") double defaultP,
                        @Assisted("defaultI") double defaultI,
                        @Assisted("defaultD") double defaultD,
                        @Assisted("defaultF") double defaultF,
                        @Assisted("defaultMaxOutput") double defaultMaxOutput,
                        @Assisted("defaultMinOutput") double defaultMinOutput,
                        @Assisted("errorThreshold") double errorThreshold,
                        @Assisted("derivativeThreshold") double derivativeThreshold,
                        @Assisted("timeThreshold") double timeThreshold,
                        @Assisted("iZone") double iZone);

        public PIDManager create(
                        String functionName,
                        double defaultP,
                        double defaultI,
                        double defaultD,
                        double defaultF,
                        double defaultMaxOutput,
                        double defaultMinOutput,
                        double errorThreshold,
                        double derivativeThreshold,
                        double timeThreshold) {
                return create(functionName, defaultP, defaultI, defaultD, defaultF, defaultMaxOutput,
                                defaultMinOutput, errorThreshold, derivativeThreshold, timeThreshold, -1);
        }

        public PIDManager create(
                        String functionName,
                        double defaultP,
                        double defaultI,
                        double defaultD,
                        double defaultF,
                        double defaultMaxOutput,
                        double defaultMinOutput) {
                return create(functionName, defaultP, defaultI, defaultD, defaultF, defaultMaxOutput,
                                defaultMinOutput, -1, -1, -1);
        }

        public PIDManager create(
                        String functionName,
                        double defaultP,
                        double defaultI,
                        double defaultD,
                        double defaultMaxOutput,
                        double defaultMinOutput) {
                return create(functionName, defaultP, defaultI, defaultD, 0, defaultMaxOutput,
                                defaultMinOutput);
        }

        public PIDManager create(
                        String functionName,
                        double defaultP,
                        double defaultI,
                        double defaultD) {
                return create(functionName, defaultP, defaultI, defaultD, 1.0, -1.0);
        }

        public PIDManager create(String functionName) {
                return create(functionName, 0, 0, 0);
        }

}
