package xbot.common.properties;

public class RobotPermanentStorage extends PermanentStorageBase {

	private static String location = "/488Database";
	
	public RobotPermanentStorage() {
		super(location);
	}

}
