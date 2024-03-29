package io.github.nettyplus.leakdetector.junit;

import io.netty.buffer.ByteBufUtil;
import io.netty.util.ResourceLeakDetector;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class NettyLeakDetectorExtension
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

    private final boolean beforeEach;
    private final boolean beforeAll;
    private final boolean afterEach;
    private final boolean afterAll;

    static NettyLeakListener getLeakListener() {
        return leakListener;
    }

    public NettyLeakDetectorExtension() {
        this(true, true, true, true);
    }

    public NettyLeakDetectorExtension(boolean beforeEach, boolean beforeAll, boolean afterEach, boolean afterAll) {
        this.beforeEach = beforeEach;
        this.beforeAll = beforeAll;
        this.afterEach = afterEach;
        this.afterAll = afterAll;
    }

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        if (this.beforeAll) {
            checkResourceLeakDetectorLevel();
            leakListener.assertZeroLeaks();
        }
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) throws Exception {
        if (this.afterAll) {
            checkResourceLeakDetectorLevel();
            leakListener.assertZeroLeaks();
        }
    }

    private void checkResourceLeakDetectorLevel() {
        if (!ResourceLeakDetector.isEnabled()) {
            throw new IllegalStateException("ResourceLeakDetector is disabled");
        }
    }

    @Override
    public void afterEach(ExtensionContext extensionContext) throws Exception {
        if (this.afterEach) {
            leakListener.assertZeroLeaks("after [" + extensionContext.getDisplayName() + "]");
        }
    }

    @Override
    public void beforeEach(ExtensionContext extensionContext) throws Exception {
        if (this.beforeEach) {
            leakListener.assertZeroLeaks();
        }
    }

    public void reset() {
        leakListener.reset();
    }

    @Override
    public String toString() {
        if (null == leakListener) {
            return super.toString();
        } else {
            return this.getClass().getSimpleName() + ": leakCount=" + leakListener.getLeakCount();
        }
    }
}
