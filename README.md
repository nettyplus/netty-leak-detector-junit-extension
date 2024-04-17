# netty-leak-detector-junit-extension [![Maven Central](https://img.shields.io/maven-central/v/io.github.nettyplus/netty-leak-detector-junit-extension.svg)](https://search.maven.org/artifact/io.github.nettyplus/netty-leak-detector-junit-extension)

# About this library

This JUnit 5 [extension](https://junit.org/junit5/docs/current/user-guide/#extensions) detects resource leaks by registering a Netty [LeakListener](https://netty.io/4.1/api/io/netty/util/ResourceLeakDetector.LeakListener.html)

# Example: Java unit test


```java

import io.github.nettyplus.leakdetector.junit.NettyLeakDetectorExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(NettyLeakDetectorExtension.class)
class FooTest {
  @Test
  void testSomething() {
    // ...
  }
}

```

# Example: Maven pom.xml

```
<dependency>
    <groupId>io.github.nettyplus</groupId>
    <artifactId>netty-leak-detector-junit-extension</artifactId>
    <version>0.0.5</version>
    <scope>test</scope>
</dependency>

```

# Projects that use this library

- [async-http-client](https://github.com/AsyncHttpClient/async-http-client)

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
