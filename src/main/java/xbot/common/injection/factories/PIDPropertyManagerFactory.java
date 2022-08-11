package xbot.common.injection.factories;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;

import xbot.common.math.PIDPropertyManager;

@AssistedFactory
public abstract class PIDPropertyManagerFactory {

        public abstract PIDPropertyManager create(
                        String functionName,
                        @Assisted("defaultP") double defaultP,
                        @Assisted("defaultI") double defaultI,
                        @Assisted("defaultD") double defaultD,
                        @Assisted("defaultF") double defaultF,
                        @Assisted("errorThreshold") double errorThreshold,
                        @Assisted("derivativeThreshold") double derivativeThreshold,
                        @Assisted("timeThreshold") double timeThreshold,
                        @Assisted("iZone") double defaultIZone);

        public PIDPropertyManager create(
                        String functionName,
                        double defaultP,
                        double defaultI,
                        double defaultD,
                        double defaultF,
                        double errorThreshold,
                        double derivativeThreshold,
                        double timeThreshold) {
                return create(functionName, defaultP, defaultI, defaultD, defaultF, errorThreshold, derivativeThreshold,
                                timeThreshold, -1);
        }

        public PIDPropertyManager create(
                        String functionName,
                        double defaultP,
                        double defaultI,
                        double defaultD,
                        double defaultF) {
                return create(functionName, defaultP, defaultI, defaultD, defaultF, -1, -1, -1);
        }

}
