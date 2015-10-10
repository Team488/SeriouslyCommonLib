package xbot.common.properties;

public class RobotDatabaseStorage extends DatabaseStorageBase {

	private static String location = "/488Database";
	
	public RobotDatabaseStorage() {
		super(location);
	}

}
