package io.github.nettyplus.leakdetector.junit;

import io.netty.buffer.ByteBufUtil;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class NettyParanoidLeakExtension
    implements BeforeAllCallback, BeforeEachCallback, AfterEachCallback, AfterAllCallback {

    private static final NettyLeakListener leakListener;
    private static final String LEAK_DETECTION_LEVEL_PROP_KEY = "io.netty.leakDetection.level";

    static {
        if (System.getProperty(LEAK_DETECTION_LEVEL_PROP_KEY) == null) {
            System.setProperty(LEAK_DETECTION_LEVEL_PROP_KEY, "paranoid");
        }
        leakListener = new NettyLeakListener();
        ByteBufUtil.setLeakListener(leakListener);
    }

    static NettyLeakListener getLeakListener() {
        return leakListener;
    }

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        leakListener.assertZeroLeaks();
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) throws Exception {
        leakListener.assertZeroLeaks();
    }

    @Override
    public void afterEach(ExtensionContext extensionContext) throws Exception {
        leakListener.assertZeroLeaks();
    }

    @Override
    public void beforeEach(ExtensionContext extensionContext) throws Exception {
        leakListener.assertZeroLeaks();
    }
}
