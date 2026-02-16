# netty-leak-detector-junit-extension 

[![Maven Central](https://img.shields.io/maven-central/v/io.github.nettyplus/netty-leak-detector-junit-extension.svg)](https://search.maven.org/artifact/io.github.nettyplus/netty-leak-detector-junit-extension)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![CI](https://github.com/nettyplus/netty-leak-detector-junit-extension/actions/workflows/ci.yml/badge.svg)](https://github.com/nettyplus/netty-leak-detector-junit-extension/actions/workflows/ci.yml)

A JUnit 5 extension that automatically detects Netty resource leaks in your unit tests.

## Table of Contents
- [About](#about)
- [Why Use This Library?](#why-use-this-library)
- [How It Works](#how-it-works)
- [Getting Started](#getting-started)
  - [Maven](#maven)
  - [Gradle](#gradle)
- [Usage Examples](#usage-examples)
  - [Basic Usage](#basic-usage)
  - [Advanced Configuration](#advanced-configuration)
- [Configuration Options](#configuration-options)
- [Leak Detection Levels](#leak-detection-levels)
- [Troubleshooting](#troubleshooting)
- [Projects Using This Library](#projects-using-this-library)
- [Presentations](#presentations)
- [Similar Work](#similar-work)
- [License](#license)

## About

This JUnit 5 [extension](https://junit.org/junit5/docs/current/user-guide/#extensions) automatically detects resource leaks by registering a Netty [LeakListener](https://netty.io/4.1/api/io/netty/util/ResourceLeakDetector.LeakListener.html) that monitors all Netty resource allocations and deallocations during test execution.

## Why Use This Library?

Memory leaks in network applications can be subtle and hard to debug. When working with Netty's reference-counted objects like `ByteBuf`, forgetting to release them can lead to:

- **Memory leaks** that accumulate over time
- **Out of memory errors** in production
- **Performance degradation** due to excessive garbage collection

This extension helps you catch these issues early by failing your tests when resources are not properly released, making it easy to identify and fix leaks during development.

## How It Works

The extension works by:

1. **Setting Netty's leak detection level to `PARANOID`** (configurable via system property)
2. **Registering a custom `LeakListener`** that tracks all resource leaks
3. **Checking for leaks** before and after each test method (and test class)
4. **Failing tests** immediately if any leaks are detected, with detailed diagnostic information

The leak detection happens automatically - just add the extension to your test class and it will monitor all Netty resources used during test execution.

## Getting Started

### Maven

Add the following dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>io.github.nettyplus</groupId>
    <artifactId>netty-leak-detector-junit-extension</artifactId>
    <version>0.2.0</version>
    <scope>test</scope>
</dependency>
```

### Gradle

Add the following to your `build.gradle`:

```groovy
testImplementation 'io.github.nettyplus:netty-leak-detector-junit-extension:0.2.0'
```

Or in `build.gradle.kts`:

```kotlin
testImplementation("io.github.nettyplus:netty-leak-detector-junit-extension:0.2.0")
```

## Usage Examples

### Basic Usage

Simply annotate your test class with `@ExtendWith(NettyLeakDetectorExtension.class)`:

```java
import io.github.nettyplus.leakdetector.junit.NettyLeakDetectorExtension;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(NettyLeakDetectorExtension.class)
class MyNettyTest {
  
  @Test
  void testWithProperRelease() {
    ByteBuf buf = ByteBufAllocator.DEFAULT.buffer(10);
    try {
      buf.writeInt(42);
      assertEquals(42, buf.readInt());
    } finally {
      buf.release(); // Properly released - test passes
    }
  }
  
  @Test
  void testWithLeak() {
    ByteBuf buf = ByteBufAllocator.DEFAULT.buffer(10);
    buf.writeInt(42);
    // Missing buf.release() - test will fail with leak detection
  }
}
```

### Advanced Configuration

You can customize when leak checks are performed by using the parameterized constructor:

```java
import io.github.nettyplus.leakdetector.junit.NettyLeakDetectorExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

class CustomizedTest {
  
  // Check for leaks only after each test, not before
  @RegisterExtension
  static NettyLeakDetectorExtension extension = 
      new NettyLeakDetectorExtension(
          false,  // beforeEach
          false,  // beforeAll
          true,   // afterEach
          true    // afterAll
      );
  
  @Test
  void testSomething() {
    // Your test code here
  }
}
```

## Configuration Options

The `NettyLeakDetectorExtension` constructor accepts four boolean parameters:

| Parameter | Description | Default |
|-----------|-------------|---------|
| `beforeEach` | Check for leaks before each test method | `true` |
| `beforeAll` | Check for leaks before all tests in a class | `true` |
| `afterEach` | Check for leaks after each test method | `true` |
| `afterAll` | Check for leaks after all tests in a class | `true` |

The default behavior (all checks enabled) ensures the most comprehensive leak detection.

## Leak Detection Levels

By default, this extension sets Netty's leak detection level to `PARANOID`, which instruments **100% of buffer allocations**. This provides the highest level of leak detection accuracy but has a performance overhead suitable for testing.

You can override the leak detection level by setting the system property before the extension initializes:

```java
System.setProperty("io.netty.leakDetection.level", "advanced");
```

Available levels:
- `DISABLED` - No leak detection (not recommended with this extension)
- `SIMPLE` - 1% sampling, reports if a leak is found
- `ADVANCED` - 1% sampling, reports with location info
- `PARANOID` - 100% sampling (default for this extension)

## Troubleshooting

### Tests are slower with this extension

The extension forces garbage collection to ensure all leaks are detected. This is necessary for accurate leak detection but can slow down tests. The overhead is typically acceptable for unit tests but may be noticeable in large test suites.

To minimize impact:
- Only use the extension on tests that actually use Netty resources
- Consider using the custom configuration to reduce check frequency
- Run tests in parallel where possible

### False positives

If you see false positive leak detections:
1. Ensure all `ByteBuf` and other reference-counted objects are properly released
2. Check that resources are released in `finally` blocks or try-with-resources
3. Verify that async operations complete before test ends
4. Consider adding explicit cleanup in `@AfterEach` methods

### Getting detailed leak information

When a leak is detected, the exception message includes:
- The resource type that leaked (e.g., `ByteBuf`)
- Detailed stack traces showing where the resource was allocated
- The number of leaked resources

Use this information to identify where in your code the resource was created but not released.

## Projects Using This Library

- [async-http-client](https://github.com/AsyncHttpClient/async-http-client) - Asynchronous HTTP client library
- [kroxylicious](https://github.com/kroxylicious/kroxylicious) - Kubernetes-native Apache Kafka proxy

## Presentations

This library was discussed in the following presentations:
- 2024: [Chicago Java User Group](https://speakerdeck.com/sullis/netty-chicago-java-user-group-2024-04-17)
- 2025: [Portland Java User Group](https://speakerdeck.com/sullis/netty-portland-java-user-group-2025-02-18)
- 2025: [ConFoo Montreal](https://speakerdeck.com/sullis/netty-confoo-montreal-2025-02-27)

## Similar Work

Other projects with similar functionality:
- Netty [LeakPresenceExtension](https://github.com/netty/netty/pull/15622) - Netty's own JUnit extension (in development)
- Apache Flink [NettyLeakDetectionExtension](https://github.com/apache/flink/tree/master/flink-runtime/src/test/java/org/apache/flink/runtime/io/network/netty) - Internal leak detection for Flink's tests

## License

This project is licensed under the [Apache License 2.0](LICENSE).
