package xbot.common.properties;

public class RobotDatabaseStorage extends DatabaseStorageBase {

	private static String location = "/TeamDatabase";
	
	public RobotDatabaseStorage() {
		super(location);
	}

}
