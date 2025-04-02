package io.github.nettyplus.leakdetector.junit;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.util.ResourceLeakDetector;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class NettyLeakDetectorExtensionTest {
    static private final NettyLeakListener leakListener = NettyLeakDetectorExtension.getLeakListener();

    @BeforeAll
    static void beforeAllTests() {
        leakListener.assertZeroLeaks();
    }

    @Test
    void allocateByteBuf() {
        for (int i = 0; i < 50000; i++) {
            ByteBuf buf = ByteBufAllocator.DEFAULT.buffer(128);
            buf.ensureWritable(10);
            assertEquals(1, buf.refCnt());
        }
    }

    @Test
    void forceGc() {
        long startTimeMs = System.currentTimeMillis();
        NettyLeakListener.forceGc();
        long elapsedTimeMs = (System.currentTimeMillis() - startTimeMs);
        assertThat(elapsedTimeMs).isLessThan(100);
    }

    @AfterAll
    static void verifyAfterAll() {
        assertTrue(ResourceLeakDetector.isEnabled());
        assertEquals(ResourceLeakDetector.Level.PARANOID, ResourceLeakDetector.getLevel());
        assertEquals("paranoid", System.getProperty( "io.netty.leakDetection.level"));
        int leakCount = leakListener.getLeakCount();
        assertTrue(leakCount > 0);
        IllegalStateException e = assertThrows(IllegalStateException.class, () -> leakListener.assertZeroLeaks());
        assertTrue(e.getMessage().startsWith("Netty leaks"));
        assertTrue(e.getMessage().contains("[ByteBuf"));
    }
}
