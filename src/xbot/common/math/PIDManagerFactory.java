package xbot.common.math;

public interface PIDManagerFactory {

    public PIDManager create(String functionName, double defaultP, double defaultI, double defaultD, double defaultF,
            double defaultMaxOutput, double defaultMinOutput, double errorThreshold, double derivativeThreshold);

    public PIDManager create(String functionName, double defaultP, double defaultI, double defaultD, double defaultF,
            double defaultMaxOutput, double defaultMinOutput);

    public PIDManager create(String functionName, double defaultP, double defaultI, double defaultD,
            double defaultMaxOutput, double defaultMinOutput);

    public PIDManager create(String functionName, double defaultP, double defaultI, double defaultD);

    public PIDManager create(String functionName);
}
