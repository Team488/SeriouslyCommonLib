package xbot.common.injection;

import xbot.common.properties.DatabaseStorageBase;

public class OffRobotDatabaseStorage extends DatabaseStorageBase {

	public static String testFolder = "./TeamDatabase";
	
	public OffRobotDatabaseStorage() {
		super(testFolder);
	}

}
