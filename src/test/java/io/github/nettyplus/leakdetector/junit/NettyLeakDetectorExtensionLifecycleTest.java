package io.github.nettyplus.leakdetector.junit;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExecutableInvoker;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstances;
import org.junit.jupiter.api.parallel.ExecutionMode;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests that the JUnit lifecycle callbacks (beforeAll, afterAll, beforeEach, afterEach)
 * are invoked correctly by calling them directly with a stub ExtensionContext.
 */
class NettyLeakDetectorExtensionLifecycleTest {

    private static ExtensionContext stubContext(String displayName) {
        return new ExtensionContext() {
            @Override public String getDisplayName() { return displayName; }
            @Override public Optional<ExtensionContext> getParent() { return Optional.empty(); }
            @Override public ExtensionContext getRoot() { return this; }
            @Override public String getUniqueId() { return displayName; }
            @Override public Set<String> getTags() { return Collections.emptySet(); }
            @Override public Optional<AnnotatedElement> getElement() { return Optional.empty(); }
            @Override public Optional<Class<?>> getTestClass() { return Optional.empty(); }
            @Override public Optional<org.junit.jupiter.api.TestInstance.Lifecycle> getTestInstanceLifecycle() { return Optional.empty(); }
            @Override public Optional<Object> getTestInstance() { return Optional.empty(); }
            @Override public Optional<TestInstances> getTestInstances() { return Optional.empty(); }
            @Override public Optional<Method> getTestMethod() { return Optional.empty(); }
            @Override public Optional<Throwable> getExecutionException() { return Optional.empty(); }
            @Override public Optional<String> getConfigurationParameter(String key) { return Optional.empty(); }
            @Override public <T> Optional<T> getConfigurationParameter(String key, Function<String, T> transformer) { return Optional.empty(); }
            @Override public void publishReportEntry(Map<String, String> map) {}
            @Override public Store getStore(Namespace namespace) { throw new UnsupportedOperationException(); }
            @Override public ExecutionMode getExecutionMode() { return ExecutionMode.SAME_THREAD; }
            @Override public ExecutableInvoker getExecutableInvoker() { throw new UnsupportedOperationException(); }
        };
    }

    @Test
    void beforeEachDoesNotThrowWhenNoLeaks() throws Exception {
        NettyLeakDetectorExtension ext = new NettyLeakDetectorExtension();
        ext.reset();
        ext.beforeEach(stubContext("myTest"));
    }

    @Test
    void afterEachDoesNotThrowWhenNoLeaks() throws Exception {
        NettyLeakDetectorExtension ext = new NettyLeakDetectorExtension();
        ext.reset();
        ext.afterEach(stubContext("myTest"));
    }

    @Test
    void afterEachThrowsWithDisplayNameWhenLeaksExist() throws Exception {
        NettyLeakDetectorExtension ext = new NettyLeakDetectorExtension();
        ext.reset();
        NettyLeakDetectorExtension.getLeakListener().onLeak("ByteBuf", "fake-record");
        try {
            NettyLeakException e = assertThrows(NettyLeakException.class,
                    () -> ext.afterEach(stubContext("myTest")));
            assertTrue(e.getMessage().contains("after [myTest]"),
                    "Message should include display name: " + e.getMessage());
        } finally {
            ext.reset();
        }
    }

    @Test
    void beforeAllDoesNotThrowWhenNoLeaks() throws Exception {
        NettyLeakDetectorExtension ext = new NettyLeakDetectorExtension();
        ext.reset();
        ext.beforeAll(stubContext("myTest"));
    }

    @Test
    void afterAllDoesNotThrowWhenNoLeaks() throws Exception {
        NettyLeakDetectorExtension ext = new NettyLeakDetectorExtension();
        ext.reset();
        ext.afterAll(stubContext("myTest"));
    }

    @Test
    void disabledCallbacksDoNotThrowEvenWithLeaks() throws Exception {
        NettyLeakDetectorExtension ext = new NettyLeakDetectorExtension(false, false, false, false);
        NettyLeakDetectorExtension.getLeakListener().onLeak("ByteBuf", "fake-record");
        try {
            // none should throw since all callbacks are disabled
            ext.beforeEach(stubContext("t"));
            ext.beforeAll(stubContext("t"));
            ext.afterEach(stubContext("t"));
            ext.afterAll(stubContext("t"));
        } finally {
            ext.reset();
        }
    }
}
