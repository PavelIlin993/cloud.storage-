package com.pavelilin.cloud.storage.client;

import java.nio.ByteBuffer;

import static com.pavelilin.cloud.storage.client.ClientConsts.EXTENDED_HEADER_BUFFER_SIZE;
import static com.pavelilin.cloud.storage.client.ClientConsts.HEADER_BUFFER_SIZE;

public final class HeaderFactory {

  public static ByteBuffer getFileUploadingHeader(String fileName) {
    ByteBuffer header = ByteBuffer.allocate(EXTENDED_HEADER_BUFFER_SIZE);
    header.putInt(OperationType.FILE_TRANSFER.getOperationCode());
    header.putInt(fileName.length());
    header.put(fileName.getBytes());
    header.flip();
    return header;
  }

  public static ByteBuffer getFileListRequestHeader() {
    ByteBuffer header = ByteBuffer.allocate(HEADER_BUFFER_SIZE);
    header.putInt(OperationType.FILE_LIST_REQUEST.getOperationCode());
    header.flip();
    return header;
  }

  public static ByteBuffer getFileDownloadingHeader(String serverFileName) {
    ByteBuffer header = ByteBuffer.allocate(EXTENDED_HEADER_BUFFER_SIZE);
    header.putInt(OperationType.FILE_REQUEST.getOperationCode()); // operation code
    header.putInt(serverFileName.length());
    header.put(serverFileName.getBytes());
    header.flip();
    return header;
  }
}
