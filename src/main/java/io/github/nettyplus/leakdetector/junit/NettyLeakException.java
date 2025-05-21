package io.github.nettyplus.leakdetector.junit;

public class NettyLeakException extends RuntimeException {
  public NettyLeakException(String message) {
    super(message);
  }
}
