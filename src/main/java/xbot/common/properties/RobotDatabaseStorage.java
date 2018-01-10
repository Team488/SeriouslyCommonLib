package xbot.common.properties;

public class RobotDatabaseStorage extends DatabaseStorageBase {

    private static String location = "/home/lvuser/TeamDatabase";

    public RobotDatabaseStorage() {
        super(location);
    }

}
