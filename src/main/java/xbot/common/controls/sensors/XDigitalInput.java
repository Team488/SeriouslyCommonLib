package xbot.common.controls.sensors;

import org.littletonrobotics.junction.Logger;
import xbot.common.controls.XBaseIO;
import xbot.common.controls.io_inputs.XDigitalInputs;
import xbot.common.controls.io_inputs.XDigitalInputsAutoLogged;
import xbot.common.injection.DevicePolice;
import xbot.common.injection.DevicePolice.DeviceType;
import xbot.common.injection.electrical_contract.DeviceInfo;

public abstract class XDigitalInput implements XBaseIO {

    boolean inverted;
    final XDigitalInputsAutoLogged inputs;
    final DeviceInfo info;
    
    public interface XDigitalInputFactory {
        XDigitalInput create(DeviceInfo info);
    }

    public XDigitalInput(DevicePolice police, DeviceInfo info) {
        police.registerDevice(DeviceType.DigitalIO, info.channel, this);
        inputs = new XDigitalInputsAutoLogged();
        this.info = info;
    }
    
    public boolean get() {
        return inputs.signal ^ inverted;
    }
    
    public void setInverted(boolean inverted) {
        this.inverted = inverted;
    }
    
    public boolean getInverted() {
        return inverted;
    }

    public abstract void updateInputs(XDigitalInputs inputs);

    public void refreshDataFrame() {
        updateInputs(inputs);
        Logger.getInstance().processInputs(info.name+"/DigitalInput", inputs);
    }
}
