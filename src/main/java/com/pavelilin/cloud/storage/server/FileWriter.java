package com.pavelilin.cloud.storage.server;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.pavelilin.cloud.storage.server.ServerConsts.FILE_CONTENT_BUFFER_SIZE;
import static com.pavelilin.cloud.storage.server.ServerConsts.STORAGE_PATH;
import static com.pavelilin.cloud.storage.server.ServerConsts.WRITE_OPTIONS;

/**
 * Perform write operation to the storage (file system) that is transferred via socket channel
 */
public final class FileWriter {

  private static final Logger logger = Logger.getLogger(FileReceiver.class);

  public static void writeFile(SocketChannel client, String fileName) {
    Path filePath = Paths.get(STORAGE_PATH + "/" + fileName);
    try (FileChannel fileChannel = FileChannel.open(filePath, WRITE_OPTIONS)) {
      ByteBuffer buffer = ByteBuffer.allocate(FILE_CONTENT_BUFFER_SIZE);
      while (client.read(buffer) > 0) {
        buffer.flip();
        fileChannel.write(buffer);
        buffer.clear();
      }
    } catch (IOException e) {
      logger.error("Unable to save file in server storage.", e);
    }
  }
}
