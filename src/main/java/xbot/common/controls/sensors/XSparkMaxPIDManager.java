package xbot.common.controls.sensors;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.revrobotics.CANPIDController;

import xbot.common.properties.DoubleProperty;
import xbot.common.properties.PropertyFactory;

public class XSparkMaxPIDManager {

    CANPIDController cpc;

    final DoubleProperty kPprop;
    final DoubleProperty kIprop;
    final DoubleProperty kDprop;
    final DoubleProperty kIzProp;
    final DoubleProperty kFFprop;
    final DoubleProperty kMaxOutputProp;
    final DoubleProperty kMinOutoutProp;

    @Inject
    public XSparkMaxPIDManager(
        @Assisted("controller") CANPIDController controller,
        @Assisted("sparkPrefix") String sparkPrefix, 
        PropertyFactory pf) {

        this.cpc = controller;
        pf.setPrefix(sparkPrefix);
        pf.appendPrefix("PIDManager");        

        kPprop = pf.createPersistentProperty("kP", 0);
        kIprop = pf.createPersistentProperty("kI", 0);
        kDprop = pf.createPersistentProperty("kD", 0);
        kIzProp = pf.createPersistentProperty("kIzone", 0);
        kFFprop = pf.createPersistentProperty("kFeedForward", 0);
        kMaxOutputProp = pf.createPersistentProperty("kMaxOutput", 1);
        kMinOutoutProp = pf.createPersistentProperty("kMinOutput", -1);

        setAllProperties();
    }
    
    public CANPIDController getPIDController() {
        return cpc;
    }

    private void setAllProperties() {
        cpc.setP(kPprop.get());
        cpc.setI(kIprop.get());
        cpc.setD(kDprop.get());
        cpc.setIZone(kIzProp.get());
        cpc.setFF(kFFprop.get());
        cpc.setOutputRange(kMinOutoutProp.get(), kMaxOutputProp.get());
    }

    public void periodic() {
        kPprop.hasChangedSinceLastCheck((value) -> cpc.setP(value));
        kIprop.hasChangedSinceLastCheck((value) -> cpc.setI(value));
        kDprop.hasChangedSinceLastCheck((value) -> cpc.setD(value));
        kIzProp.hasChangedSinceLastCheck((value) -> cpc.setIZone(value));
        kFFprop.hasChangedSinceLastCheck((value) -> cpc.setFF(value));
        kMaxOutputProp.hasChangedSinceLastCheck((value) -> cpc.setOutputRange(kMinOutoutProp.get(), value));
        kMinOutoutProp.hasChangedSinceLastCheck((value) -> cpc.setOutputRange(value, kMaxOutputProp.get()));
    }
}