package xbot.common.subsystems;

import java.util.List;

import xbot.common.controls.actuators.XCANTalon;

public abstract class BaseDrivePlatform {

    public abstract List<XCANTalon> getAllMasterTalons();
    
    // Used in Tank Drive, or a Holonomic drive that has "fallen down" to tank drive.
    // Returns an array because more advanced drive modes may have more than one 
    // master CANTalon that needs to cooperate on a side - and it's easier to address
    // that here than change configuration on the fly.
    public abstract List<XCANTalon> getLeftMasterTalons();
    public abstract List<XCANTalon> getRightMasterTalons();
    
    // Used in a Holonomic drive
    public abstract XCANTalon getFrontLeftMasterTalon();
    public abstract XCANTalon getFrontRightMasterTalon();
    public abstract XCANTalon getRearLeftMasterTalon();
    public abstract XCANTalon getRearRightMasterTalon();
}
