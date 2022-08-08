package xbot.common.properties;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class MockPermamentStorage extends TableProxy implements PermanentStorage {

    @Inject
    public MockPermamentStorage() {}

}
