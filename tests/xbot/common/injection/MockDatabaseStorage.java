package xbot.common.injection;

import xbot.common.properties.DatabaseStorageBase;

public class MockDatabaseStorage extends DatabaseStorageBase {

	public static String testFolder = "./488Database";
	
	public MockDatabaseStorage() {
		super(testFolder);
	}

}
