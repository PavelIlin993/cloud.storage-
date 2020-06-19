package com.pavelilin.cloud.storage.client;

import org.apache.log4j.Logger;
import sun.awt.SunToolkit.OperationTimedOut;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.pavelilin.cloud.storage.client.ClientConsts.CONNECTION_ATTEMPTS;
import static com.pavelilin.cloud.storage.client.ClientConsts.HOST;
import static com.pavelilin.cloud.storage.client.ClientConsts.PORT;
import static com.pavelilin.cloud.storage.client.HeaderFactory.getFileDownloadingHeader;
import static com.pavelilin.cloud.storage.client.HeaderFactory.getFileListRequestHeader;
import static com.pavelilin.cloud.storage.client.HeaderFactory.getFileUploadingHeader;

public final class SocketClientFileSharingBehavior implements ClientFileSharingBehavior {

  private static final Logger logger = Logger.getLogger(SocketClientFileSharingBehavior.class);

  @Override
  public AsynchronousSocketChannel connect() {
    try {
      AsynchronousSocketChannel socketChannel = createSocketChannel();
      Future<Void> connect = socketChannel.connect(new InetSocketAddress(HOST, PORT));
      // connection may take sometime
      checkConnectivity(connect);
      return socketChannel;
    } catch (Throwable e) {
      logger.error("Unable to connect to server.", e);
      throw new RuntimeException("Connection to server has failed.");
    }
  }

  @Override
  public void uploadFile(Path filePath) {
    logger.info(String.format("Uploading file %s to server", filePath.toString()));
    AsynchronousSocketChannel socketChannel = connect();
    ByteBuffer header = getFileUploadingHeader(filePath.getFileName().toString());
    socketChannel.write(header);
    FileSender.transferFile(socketChannel, filePath);
    disconnect(socketChannel);
  }

  @Override
  public void downloadFile(String serverFileName) {
    logger.info(String.format("Downloading file %s from server", serverFileName));
    AsynchronousSocketChannel socketChannel = connect();
    ByteBuffer header = getFileDownloadingHeader(serverFileName);
    socketChannel.write(header);
    FileReceiver.downloadFile(socketChannel, serverFileName);
    disconnect(socketChannel);
  }

  @Override
  public List<String> getServerFileList() {
    AsynchronousSocketChannel socketChannel = connect();
    ByteBuffer header = getFileListRequestHeader();
    socketChannel.write(header);
    List<String> serverFileList = FileReceiver.getServerFileList(socketChannel);
    disconnect(socketChannel);
    return serverFileList;
  }

  @Override
  public void disconnect(AsynchronousSocketChannel channel) {
    try {
      channel.close();
    } catch (IOException e) {
      logger.warn("Unable to disconnect from server.", e);
    }
  }

  private AsynchronousSocketChannel createSocketChannel() {
    try {
      return AsynchronousSocketChannel.open();
    } catch (IOException e) {
      throw new RuntimeException("Unable to create socket channel", e);
    }
  }

  private boolean checkConnectivity(Future<Void> connectionStatus) throws InterruptedException, ExecutionException, TimeoutException {
    int attempts = 0;
    while (attempts < CONNECTION_ATTEMPTS) {
      if (connectionStatus.isDone()) {
        return true;
      }
      logger.warn("Connection has not yet established.");
      connectionStatus.get(30, TimeUnit.SECONDS);
      attempts++;
    }
    throw new OperationTimedOut();
  }
}
