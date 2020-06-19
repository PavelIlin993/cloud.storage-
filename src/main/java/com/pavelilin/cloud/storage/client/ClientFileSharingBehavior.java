package com.pavelilin.cloud.storage.client;

import java.nio.channels.AsynchronousSocketChannel;
import java.nio.file.Path;
import java.util.List;

/**
 * Implementations provide logic for connecting/disconnecting from server and basic file sharing operations.
 */
public interface ClientFileSharingBehavior {

  AsynchronousSocketChannel connect();

  void uploadFile(Path filePath);

  void downloadFile(String serverFileName);

  List<String> getServerFileList();

  void disconnect(AsynchronousSocketChannel channel);
}
