package xbot.common.injection.electrical_contract;

/**
 * Represents power sources for devices on the robot.
 * Includes PDH ports (0-19) and VRM1 outputs.
 */
public enum PowerSource {
    // PDH Ports (Power Distribution Hub)
    PDH00, PDH01, PDH02, PDH03, PDH04, PDH05, PDH06, PDH07, PDH08, PDH09,
    PDH10, PDH11, PDH12, PDH13, PDH14, PDH15, PDH16, PDH17, PDH18, PDH19,
    PDH20, PDH21, PDH22, PDH23,
    
    // Pneumatic Hub Solenoid Ports (0-15)
    PneumaticHub00, PneumaticHub01, PneumaticHub02, PneumaticHub03, 
    PneumaticHub04, PneumaticHub05, PneumaticHub06, PneumaticHub07,
    PneumaticHub08, PneumaticHub09, PneumaticHub10, PneumaticHub11,
    PneumaticHub12, PneumaticHub13, PneumaticHub14, PneumaticHub15,
    
    // VRM1 Outputs (Voltage Regulator Module)
    VRM1_12V_2A("VRM1-12v_2a"),
    VRM1_12V_2B("VRM1-12v_2b"),
    VRM1_12V_500MA("VRM1-12v_500ma"),
    VRM1_12V_500MB("VRM1-12v_500mb"),
    VRM1_5V_2A("VRM1-5v_2a"),
    VRM1_5V_2B("VRM1-5v_2b"),
    VRM1_5V_500MA("VRM1-5v_500ma"),
    VRM1_5V_500MB("VRM1-5v_500mb"),

    // VRM2 Outputs (Voltage Regulator Module)
    VRM2_12V_2A("VRM2-12v_2a"),
    VRM2_12V_2B("VRM2-12v_2b"),
    VRM2_12V_500MA("VRM2-12v_500ma"),
    VRM2_12V_500MB("VRM2-12v_500mb"),
    VRM2_5V_2A("VRM2-5v_2a"),
    VRM2_5V_2B("VRM2-5v_2b"),
    VRM2_5V_500MA("VRM2-5v_500ma"),
    VRM2_5V_500MB("VRM2-5v_500mb"),

    // Other power sources
    NONE("NONE"),
    BATTERY("BATTERY"),
    MOTOR("MOTOR"),
    INJECTOR("INJECTOR"),
    RIO("RIO");

    
    
    private final String displayName;
    
    PowerSource() {
        this.displayName = this.name();
    }
    
    PowerSource(String displayName) {
        this.displayName = displayName;
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}
