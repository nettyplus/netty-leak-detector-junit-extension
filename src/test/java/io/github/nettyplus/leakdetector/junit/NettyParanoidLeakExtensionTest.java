package io.github.nettyplus.leakdetector.junit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(NettyParanoidLeakExtension.class)
public class NettyParanoidLeakExtensionTest {
    @Test
    void verifyLeakDetection() {
        // TODO
    }
}
