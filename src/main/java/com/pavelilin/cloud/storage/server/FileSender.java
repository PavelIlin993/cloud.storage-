package com.pavelilin.cloud.storage.server;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import static com.pavelilin.cloud.storage.server.FileSharingHelper.readFileName;
import static com.pavelilin.cloud.storage.server.ServerConsts.FILE_CONTENT_BUFFER_SIZE;
import static com.pavelilin.cloud.storage.server.ServerConsts.READ_OPTIONS;
import static com.pavelilin.cloud.storage.server.ServerConsts.STORAGE_PATH;

public class FileSender {

  private static final Logger logger = Logger.getLogger(FileSender.class);

  public static void shareFileList(SocketChannel client) {
    try {
      logger.info("Sharing file list with client.");
      ByteBuffer buffer = ByteBuffer.allocate(FILE_CONTENT_BUFFER_SIZE);
      String stringFileList = String.join(",", Objects.requireNonNull(new File(STORAGE_PATH).list()));
      buffer.put(stringFileList.getBytes());
      buffer.flip();
      while (buffer.hasRemaining()) {
        client.write(buffer);
      }
    } catch (Throwable e) {
      logger.error("Unable to share file list.", e);
    }
  }

  public static void shareFile(SocketChannel client) {
    String fileName = readFileName(client);
    Path filePath = Paths.get(STORAGE_PATH + "/" + fileName);
    try (FileChannel fileChannel = FileChannel.open(filePath, READ_OPTIONS)) {
      logger.info("Sharing file with client.");
      ByteBuffer buffer = ByteBuffer.allocate(FILE_CONTENT_BUFFER_SIZE);
      while (fileChannel.read(buffer) > 0) {
        buffer.flip();
        client.write(buffer);
        buffer.clear();
      }
    } catch (IOException e) {
      logger.error("Unable to send file.", e);
    }
  }

}
