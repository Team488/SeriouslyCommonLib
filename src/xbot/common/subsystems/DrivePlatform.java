package xbot.common.subsystems;

import java.util.ArrayList;

import xbot.common.controls.actuators.XCANTalon;

public abstract class DrivePlatform {

    public abstract ArrayList<XCANTalon> getAllMasterTalons();
    
    // Used in Tank Drive, or Holonomic that has "fallen down" to tank drive
    public abstract XCANTalon getLeftMasterTalon();
    public abstract XCANTalon getRightMasterTalon();
    
    // Used in holonomic drive
    public abstract XCANTalon getFrontLeftMasterTalon();
    public abstract XCANTalon getFrontRightMasterTalon();
    public abstract XCANTalon getRearLeftMasterTalon();
    public abstract XCANTalon getRearRightMasterTalon();
}
