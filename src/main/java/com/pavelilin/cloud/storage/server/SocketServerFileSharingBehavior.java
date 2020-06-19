package com.pavelilin.cloud.storage.server;

import org.apache.log4j.Logger;

import javax.naming.OperationNotSupportedException;
import java.io.IOException;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import static com.pavelilin.cloud.storage.server.FileSharingHelper.getOperationType;
import static com.pavelilin.cloud.storage.server.ServerConsts.HOST;
import static com.pavelilin.cloud.storage.server.ServerConsts.PORT;

public class SocketServerFileSharingBehavior implements ServerFileSharingBehavior {

  private static final Logger logger = Logger.getLogger(SocketServerFileSharingBehavior.class);


  public final ServerSocketChannel serverSocketChannel = createServerSocketChannel();

  @Override
  public boolean connect() {
    try {
      serverSocketChannel.socket().bind(new InetSocketAddress(HOST, PORT));
      return checkConnectivity(serverSocketChannel);
    } catch (IOException e) {
      throw new ServerConnectionException("Unable to start server.", e);
    }
  }

  @Override
  public void listenToTheChannel() {
    try {
      checkConnectivity(serverSocketChannel);
      logger.info("Awaiting the incoming connection...");
      SocketChannel client = serverSocketChannel.accept();
      processRequest(client);
      listenToTheChannel();
    } catch(ServerConnectionException e) {
      logger.error("Server error occurred, attempting to reconnect");
      connect();
    } catch (Throwable e) { // todo narrow down the list of exception
      logger.error("Unable to process the incoming request.", e);
      listenToTheChannel();
    }
  }

  @Override
  public void processRequest(SocketChannel client) throws OperationNotSupportedException {
    OperationType operationType = getOperationType(client);
    switch (operationType) {
      case FILE_TRANSFER: {
        FileReceiver.process(client);
        break;
      }
      case FILE_REQUEST: {
        FileSender.shareFile(client);
        break;
      }
      case FILE_LIST_REQUEST: {
        FileSender.shareFileList(client);
        break;
      }
    }
  }

  private ServerSocketChannel createServerSocketChannel() {
    try {
      ServerSocketChannel server = ServerSocketChannel.open();
      server.configureBlocking(true);
      return server;
    } catch (IOException e) {
      throw new RuntimeException("Unable to create server socket channel", e);
    }
  }

  private boolean checkConnectivity(ServerSocketChannel serverSocketChannel) {
    if (!serverSocketChannel.socket().isBound() || serverSocketChannel.socket().isClosed()) {
      throw new ServerConnectionException("Server is not ready for incoming connections.");
    }
    return true;
  }
}
