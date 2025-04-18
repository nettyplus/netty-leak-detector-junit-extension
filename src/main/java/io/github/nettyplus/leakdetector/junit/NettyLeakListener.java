package io.github.nettyplus.leakdetector.junit;

import io.netty.util.ResourceLeakDetector.LeakListener;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class NettyLeakListener implements LeakListener {
    private final List<String> leaks = new CopyOnWriteArrayList<>();

    @Override
    public void onLeak(String resourceType, String records) {
        leaks.add(resourceType);
    }

    public int getLeakCount() {
        return leaks.size();
    }

    public void assertZeroLeaks() {
        assertZeroLeaks(null);
    }

    public void assertZeroLeaks(String detail) {
        forceGc();
        if (!leaks.isEmpty()) {
            StringBuilder message = new StringBuilder("Netty leaks: ");
            if (detail != null) {
                message.append(detail);
                message.append(" ");
            }
            message.append(leaks);
            throw new IllegalStateException(message.toString());
        }
    }

    // visible for testing
    static void forceGc() {
        WeakReference<Object> dummy = new WeakReference<>(new Object());
        while (dummy.get() != null) {
            System.gc();
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                // ignore
            }
        }
    }

    public void reset() {
        this.leaks.clear();
    }

    @Override
    public String toString() {
        return "leakCount=" + this.getLeakCount();
    }
}