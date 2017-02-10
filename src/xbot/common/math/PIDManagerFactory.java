package xbot.common.math;

import com.google.inject.assistedinject.Assisted;

public interface PIDManagerFactory {

    public PIDManager create(
            String functionName,
            @Assisted("defaultP") double defaultP, 
            @Assisted("defaultI") double defaultI, 
            @Assisted("defaultD") double defaultD, 
            @Assisted("defaultF") double defaultF,
            @Assisted("defaultMaxOutput") double defaultMaxOutput, 
            @Assisted("defaultMinOutput") double defaultMinOutput,
            @Assisted("errorThreshold") double errorThreshold, 
            @Assisted("derivativeThreshold") double derivativeThreshold);
    
    public PIDManager create(
            String functionName,
            @Assisted("defaultP") double defaultP, 
            @Assisted("defaultI") double defaultI, 
            @Assisted("defaultD") double defaultD, 
            @Assisted("defaultF") double defaultF,
            @Assisted("defaultMaxOutput") double defaultMaxOutput, 
            @Assisted("defaultMinOutput") double defaultMinOutput);

    
    public PIDManager create(
            String functionName, 
            @Assisted("defaultP") double defaultP, 
            @Assisted("defaultI") double defaultI, 
            @Assisted("defaultD") double defaultD, 
            @Assisted("defaultMaxOutput") double defaultMaxOutput, 
            @Assisted("defaultMinOutput") double defaultMinOutput);
    
    
    public PIDManager create(
            String functionName, 
            @Assisted("defaultP") double defaultP, 
            @Assisted("defaultI") double defaultI, 
            @Assisted("defaultD") double defaultD);

    
    public PIDManager create(String functionName);
}
