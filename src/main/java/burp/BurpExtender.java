package burp;

import com.alumm0x.generator.GeneratorFactory;
import com.alumm0x.ui.UIShow;
import com.alumm0x.util.CommonStore;

import java.awt.*;

public class BurpExtender implements IBurpExtender, IExtensionStateListener, ITab {
    @Override
    public void registerExtenderCallbacks(IBurpExtenderCallbacks callbacks) {
        // 存储核心类
        CommonStore.callbacks = callbacks;
        // 存储数据处理类
        CommonStore.helpers = callbacks.getHelpers();

        callbacks.setExtensionName("FuzzGOGOGO");

        callbacks.registerExtensionStateListener(this);
        callbacks.registerIntruderPayloadGeneratorFactory(new GeneratorFactory());

        callbacks.addSuiteTab(BurpExtender.this);
    }

    @Override
    public void extensionUnloaded() {

    }

    @Override
    public String getTabCaption() {
        return "FuzzGoGoGo";
    }

    @Override
    public Component getUiComponent() {
        return UIShow.getUI();
    }
}
