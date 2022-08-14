package xbot.common.injection;

import xbot.common.injection.components.CommonLibTestComponent;
import xbot.common.injection.components.DaggerCommonLibTestComponent;

public class BaseCommonLibTest extends BaseWPITest {

    protected CommonLibTestComponent getInjectorComponent() {
        return (CommonLibTestComponent)super.getInjectorComponent();
    }

    @Override
    protected CommonLibTestComponent createDaggerComponent() {
        return DaggerCommonLibTestComponent.create();
    }
    
}
