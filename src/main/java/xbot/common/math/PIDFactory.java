package xbot.common.math;

import com.google.inject.assistedinject.Assisted;

public interface PIDFactory {

public PIDManager createPIDManager(
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

    public PIDManager createPIDManager(
            String functionName,
            @Assisted("defaultP") double defaultP, 
            @Assisted("defaultI") double defaultI, 
            @Assisted("defaultD") double defaultD, 
            @Assisted("defaultF") double defaultF,
            @Assisted("defaultMaxOutput") double defaultMaxOutput, 
            @Assisted("defaultMinOutput") double defaultMinOutput,
            @Assisted("errorThreshold") double errorThreshold, 
            @Assisted("derivativeThreshold") double derivativeThreshold,
            @Assisted("timeThreshold") double timeThreshold);
    
    public PIDManager createPIDManager(
            String functionName,
            @Assisted("defaultP") double defaultP, 
            @Assisted("defaultI") double defaultI, 
            @Assisted("defaultD") double defaultD, 
            @Assisted("defaultF") double defaultF,
            @Assisted("defaultMaxOutput") double defaultMaxOutput, 
            @Assisted("defaultMinOutput") double defaultMinOutput);

    
    public PIDManager createPIDManager(
            String functionName, 
            @Assisted("defaultP") double defaultP, 
            @Assisted("defaultI") double defaultI, 
            @Assisted("defaultD") double defaultD, 
            @Assisted("defaultMaxOutput") double defaultMaxOutput, 
            @Assisted("defaultMinOutput") double defaultMinOutput);
    
    
    public PIDManager createPIDManager(
            String functionName, 
            @Assisted("defaultP") double defaultP, 
            @Assisted("defaultI") double defaultI, 
            @Assisted("defaultD") double defaultD);

    
    public PIDManager createPIDManager(String functionName);
    
    public PIDPropertyManager createPIDPropertyManager(
            String functionName,
            @Assisted("defaultP") double defaultP, 
            @Assisted("defaultI") double defaultI, 
            @Assisted("defaultD") double defaultD, 
            @Assisted("defaultF") double defaultF,
            @Assisted("errorThreshold") double errorThreshold, 
            @Assisted("derivativeThreshold") double derivativeThreshold,
            @Assisted("timeThreshold") double timeThreshold);
    
    public PIDPropertyManager createPIDPropertyManager(
            String functionName,
            @Assisted("defaultP") double defaultP, 
            @Assisted("defaultI") double defaultI, 
            @Assisted("defaultD") double defaultD, 
            @Assisted("defaultF") double defaultF);
    
}
