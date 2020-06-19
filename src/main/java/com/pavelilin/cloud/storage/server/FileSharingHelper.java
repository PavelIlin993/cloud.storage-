package com.pavelilin.cloud.storage.server;

import org.apache.log4j.Logger;

import javax.naming.OperationNotSupportedException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import static com.pavelilin.cloud.storage.server.ServerConsts.HEADER_BUFFER_SIZE;

public final class FileSharingHelper {

  private static final Logger logger = Logger.getLogger(FileSender.class);

  public static String readFileName(SocketChannel client) {
    try {
      ByteBuffer fileNameBuffer = ByteBuffer.allocate(HEADER_BUFFER_SIZE);
      client.read(fileNameBuffer);
      fileNameBuffer.flip();
      int fileNameSize = fileNameBuffer.getInt(); // get the size of file name

      fileNameBuffer = ByteBuffer.allocate(fileNameSize); // prepare storage for file name

      while (fileNameBuffer.hasRemaining()) { // writing the file name into buffer
        client.read(fileNameBuffer);
      }
      fileNameBuffer.flip();

      return new String(fileNameBuffer.array());
    } catch (Throwable e) {
      logger.error("Unable to read requested file name");
      throw new IllegalArgumentException();
    }
  }

  public static OperationType getOperationType(SocketChannel client) throws OperationNotSupportedException {

    ByteBuffer codeBuffer = ByteBuffer.allocate(HEADER_BUFFER_SIZE);
    try {
      while (codeBuffer.hasRemaining()) {
        client.read(codeBuffer);
      }
      codeBuffer.flip();
      return OperationType.fromCode(codeBuffer.getInt());
    } catch (Throwable e) {
      throw new OperationNotSupportedException();
    }
  }
}
