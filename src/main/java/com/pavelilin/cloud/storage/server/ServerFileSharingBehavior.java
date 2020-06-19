package com.pavelilin.cloud.storage.server;

import javax.naming.OperationNotSupportedException;
import java.nio.channels.SocketChannel;

/**
 * Implementations must provide logic for opening server socket channel and logic for listing to the channel
 * and processing the request. The clients that connect to this server must take the responsibility to close the channel.
 */
public interface ServerFileSharingBehavior {

  boolean connect();

  void listenToTheChannel();

  void processRequest(SocketChannel client) throws OperationNotSupportedException;
}
