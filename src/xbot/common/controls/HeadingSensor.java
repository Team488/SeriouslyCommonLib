package xbot.common.controls;

/*
 * NOTE: NOT USED FOR REAL ROBOT - USE <code>Nav6Gyro</code> INSTEAD
 */
public class HeadingSensor {
	double orientation;
	
	public double getYaw() {
		return orientation;
	}
	
	public void setYaw(double yaw) {
		this.orientation = yaw;
	}
	
	public void incrementYaw(double delta) {
		this.orientation += delta;
	}
}
