# netty-leak-detector-junit-extension

# Java example

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

# Maven usage

```
<dependency>
    <groupId>io.github.nettyplus</groupId>
    <artifactId>netty-leak-detector-junit-extension</artifactId>
    <version>x.y.z</version>
    <scope>test</scope>
</dependency>

```

## How to release a new version?

1. Every change on the main development branch is released as `-SNAPSHOT` version to Sonatype snapshot repo
   at https://oss.sonatype.org/content/repositories/snapshots/io/github/nettyplus/netty-leak-detector-junit-extension/
2. In order to release a non-snapshot version to Maven Central push an annotated tag, for example:

```
git tag -a -m "Release 0.x.y" v0.x.y
git push origin v0.x.y
```

3. At the moment, you **may not create releases from GitHub Web UI**. Doing so will make the CI build fail because the
   CI creates the changelog and posts to GitHub releases. We'll support this in the future.
