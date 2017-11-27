package xbot.common.subsystems;

import java.util.Map;

import xbot.common.controls.actuators.XCANTalon;

public abstract class BaseDrivePlatform {

    public class MotionRegistration {

        public double x;
        public double y;
        public double w;
        
        public MotionRegistration(double x, double y, double w) {
            this.x = x;
            this.y = y;
            this.w = w;
        }
        
        
        public double calculateTotalImpact(double x, double y, double w) {
            return this.x * x + this.y*y + this.w * w;
        }
    }
    
    public abstract Map<XCANTalon, MotionRegistration> getAllMasterTalons();
}
