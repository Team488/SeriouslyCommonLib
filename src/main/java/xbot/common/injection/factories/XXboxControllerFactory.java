package xbot.common.injection.factories;

import xbot.common.controls.sensors.XXboxController;

public interface XXboxControllerFactory {
    XXboxController create(int port);
}
