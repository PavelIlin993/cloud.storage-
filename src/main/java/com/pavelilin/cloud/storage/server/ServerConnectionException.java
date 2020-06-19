package com.pavelilin.cloud.storage.server;

public class ServerConnectionException extends RuntimeException {

  public ServerConnectionException(String message) {
    super(message);
  }

  public ServerConnectionException(String message, Throwable cause) {
    super(message, cause);
  }
}
