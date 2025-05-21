package io.github.nettyplus.leakdetector.junit;

import io.netty.util.ResourceLeakDetector.LeakListener;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NettyLeakListener implements LeakListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(NettyLeakListener.class);
    private Map<String, List<String>> leaks = Collections.synchronizedMap(new LinkedHashMap<>());

    @Override
    public void onLeak(String resourceType, String records) {
        LOGGER.debug("onLeak: resourceType={} records={}", resourceType, records);
        List<String> recordsList = leaks.computeIfAbsent(resourceType, (x) -> new CopyOnWriteArrayList<>());
        recordsList.add(records);
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

            for (String resourceType: leaks.keySet()) {
                message.append("{ ");
                message.append("resourceType=");
                message.append(resourceType);
                message.append(", ");
                message.append("records=");
                message.append(leaks.get(resourceType));
                message.append(" } ");
            }

            LOGGER.error(message.toString());
            throw new NettyLeakException(message.toString());
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