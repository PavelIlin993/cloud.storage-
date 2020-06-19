package com.pavelilin.cloud.storage.server;

import org.apache.log4j.Logger;

import java.nio.channels.SocketChannel;

import static com.pavelilin.cloud.storage.server.FileSharingHelper.readFileName;

public final class FileReceiver {

  private static final Logger logger = Logger.getLogger(FileReceiver.class);

  /**
   * Reads name of incoming file and passes the channel further for writing the file into storage.
   *
   * @param client socket channel transferring the content of file
   */
  public static void process(SocketChannel client) {
    String fileName = readFileName(client);
    logger.info(String.format("Downloading the file %s from client.", fileName));
    FileWriter.writeFile(client, fileName); // passing the channel further
  }
}
