package xbot.common.injection;

import xbot.common.properties.DatabaseStorageBase;

public class PersonalComputerDatabaseStorage extends DatabaseStorageBase {

	public static String testFolder = "./TeamDatabase";
	
	public PersonalComputerDatabaseStorage() {
		super(testFolder);
	}

}
