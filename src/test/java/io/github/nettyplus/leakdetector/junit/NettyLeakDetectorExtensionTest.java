package io.github.nettyplus.leakdetector.junit;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.util.ResourceLeakDetector;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class NettyLeakDetectorExtensionTest {
    private final NettyLeakDetectorExtension extension = new NettyLeakDetectorExtension();
    static private final NettyLeakListener leakListener = NettyLeakDetectorExtension.getLeakListener();

    @Test
    void allocateByteBuf() {
        leakListener.assertZeroLeaks();
        for (int i = 0; i < 200000; i++) {
            ByteBuf buf = ByteBufAllocator.DEFAULT.buffer(128);
            buf.ensureWritable(10);
            assertEquals(1, buf.refCnt());
        }
    }

    @AfterAll
    static void verifyAfterAll() {
        assertTrue(ResourceLeakDetector.isEnabled());
        assertEquals(ResourceLeakDetector.Level.PARANOID, ResourceLeakDetector.getLevel());
        assertEquals("paranoid", System.getProperty( "io.netty.leakDetection.level"));
        assertTrue(leakListener.getLeakCount() > 0);
    }
}
