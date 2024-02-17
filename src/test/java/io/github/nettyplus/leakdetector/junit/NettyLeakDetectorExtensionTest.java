package io.github.nettyplus.leakdetector.junit;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.util.ResourceLeakDetector;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(NettyLeakDetectorExtension.class)
public class NettyLeakDetectorExtensionTest {
    @Test
    void verifyLeakDetection() {
        for (int i = 0; i < 100; i++) {
            ByteBuf buf = ByteBufAllocator.DEFAULT.buffer(500);
            buf.ensureWritable(10);
            assertEquals(1, buf.refCnt());
            buf.release();
            assertEquals(0, buf.refCnt());
        }
    }

    @AfterAll
    static void verifyAfterAll() {
        assertTrue(ResourceLeakDetector.isEnabled());
        assertEquals(ResourceLeakDetector.Level.PARANOID, ResourceLeakDetector.getLevel());
        assertEquals("paranoid", System.getProperty( "io.netty.leakDetection.level"));
        NettyLeakListener listener = NettyLeakDetectorExtension.getLeakListener();
        assertNotNull(listener);
        assertTrue(listener.getLeakCount() >= 0);
    }
}
