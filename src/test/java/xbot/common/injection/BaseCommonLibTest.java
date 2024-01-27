package xbot.common.injection;

import xbot.common.injection.components.CommonLibTestComponent;
import xbot.common.injection.components.DaggerCommonLibTestComponent;
import xbot.common.properties.PropertyFactory;
import xbot.common.properties.XPropertyManager;

public class BaseCommonLibTest extends BaseWPITest {

    protected CommonLibTestComponent getInjectorComponent() {
        return (CommonLibTestComponent)super.getInjectorComponent();
    }

    protected XPropertyManager getPropertyManager() {
        return getInjectorComponent().propertyManager();
    }

    @Override
    protected CommonLibTestComponent createDaggerComponent() {
        return DaggerCommonLibTestComponent.create();
    }
    
}
