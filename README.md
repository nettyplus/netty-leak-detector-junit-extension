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
    <version>0.2.0</version>
    <scope>test</scope>
</dependency>

```

# Projects that use this library

- [async-http-client](https://github.com/AsyncHttpClient/async-http-client)
- [kroxylicious](https://github.com/kroxylicious/kroxylicious)

# Presentations

This library was discussed in the following presentations:
- 2024: [Chicago Java User Group](https://speakerdeck.com/sullis/netty-chicago-java-user-group-2024-04-17)
- 2025: [ConFoo Montreal](https://speakerdeck.com/sullis/netty-confoo-montreal-2025-02-27)
