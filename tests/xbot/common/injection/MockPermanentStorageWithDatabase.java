package xbot.common.injection;

import xbot.common.properties.PermanentStorageBase;

public class MockPermanentStorageWithDatabase extends PermanentStorageBase {

	public static String testFolder = "./488Database";
	
	public MockPermanentStorageWithDatabase() {
		super(testFolder);
	}

}
