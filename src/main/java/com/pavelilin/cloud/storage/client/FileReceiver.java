package com.pavelilin.cloud.storage.client;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import static com.pavelilin.cloud.storage.client.ClientConsts.FILE_CONTENT_BUFFER_SIZE;
import static com.pavelilin.cloud.storage.client.ClientConsts.STORAGE_PATH;
import static com.pavelilin.cloud.storage.client.ClientConsts.WRITE_OPTIONS;

public final class FileReceiver {

  private static final Logger logger = Logger.getLogger(FileReceiver.class);

  public static List<String> getServerFileList(AsynchronousSocketChannel socketChannel) {
    try {
      ByteBuffer buffer = ByteBuffer.allocate(FILE_CONTENT_BUFFER_SIZE);
      Future<Integer> readResult = socketChannel.read(buffer);
      readResult.get();
      buffer.flip();
      return Arrays.stream(new String(buffer.array()).trim().split(",")).collect(Collectors.toList());
    } catch (InterruptedException | ExecutionException e) {
      logger.error("Unable to read file list.", e);
      return null;
    }
  }

  public static void downloadFile(AsynchronousSocketChannel socketChannel, String fileName) {
    Path filePath = Paths.get(STORAGE_PATH + "/" + fileName);
    try (FileChannel fileChannel = FileChannel.open(filePath, WRITE_OPTIONS)) {
      ByteBuffer buffer = ByteBuffer.allocate(FILE_CONTENT_BUFFER_SIZE);
      int lastOpBytes = buffer.capacity();
      // if during last read operation buffer did not fill completely, then this is last op
      while (lastOpBytes == buffer.capacity()) {
        Future<Integer> read = socketChannel.read(buffer);
        // todo investigate why it hangs at the end for huge files (despite files are successfully downloaded)
        lastOpBytes = read.get(3, TimeUnit.SECONDS);
        buffer.flip();
        fileChannel.write(buffer);
        buffer.clear();
      }
    } catch (IOException | InterruptedException | ExecutionException e) {
      logger.error("Unable to download file.", e);
    } catch (TimeoutException e) {
      logger.warn("File downloading has stopped unexpectedly.");
    }
  }
}
