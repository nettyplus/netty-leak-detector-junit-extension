# netty-leak-detector-junit-extension

```java

import io.github.nettyplus.leakdetector.junit.NettyLeakDetectorExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(NettyLeakDetectorExtension.class)
public class FooTest {
  @Test
  void testSomething() {
    // ...
  }
}

```
