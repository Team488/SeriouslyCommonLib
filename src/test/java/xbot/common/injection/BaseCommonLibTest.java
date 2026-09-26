package xbot.common.injection;

import xbot.common.injection.components.CommonLibTestComponent;
import xbot.common.injection.components.DaggerCommonLibTestComponent;
import xbot.common.properties.TunableManager;

public class BaseCommonLibTest extends BaseWPITest {

    protected CommonLibTestComponent getInjectorComponent() {
        return (CommonLibTestComponent)super.getInjectorComponent();
    }

    protected TunableManager getTunableManager() {
        return getInjectorComponent().tunableManager();
    }

    @Override
    protected CommonLibTestComponent createDaggerComponent() {
        return DaggerCommonLibTestComponent.create();
    }
    
}
