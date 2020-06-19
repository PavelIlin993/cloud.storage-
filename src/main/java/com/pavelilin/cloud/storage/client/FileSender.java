package com.pavelilin.cloud.storage.client;

import org.apache.log4j.Logger;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.util.concurrent.Future;

import static com.pavelilin.cloud.storage.client.ClientConsts.FILE_CONTENT_BUFFER_SIZE;
import static com.pavelilin.cloud.storage.client.ClientConsts.READ_OPTIONS;

/**
 * Performs file transferring over socket channel
 */
public final class FileSender {

  private static final Logger logger = Logger.getLogger(FileSender.class);

  public static void transferFile(AsynchronousSocketChannel socketChannel, Path filePath) {
    try (FileChannel fileChannel = FileChannel.open(filePath, READ_OPTIONS)) {
      ByteBuffer buffer = ByteBuffer.allocate(FILE_CONTENT_BUFFER_SIZE);
      while (fileChannel.read(buffer) > 0) {
        buffer.flip();
        Future<Integer> write = socketChannel.write(buffer);
        // await file uploading
        write.get();
        buffer.clear();
      }
    } catch (Throwable e) {
      logger.error("Unable to send file.", e);
    }
  }
}
