package com.pavelilin.cloud.storage.client;

import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.EnumSet;

/**
 * Class containing all the constants related to file sharing.
 */
public final class ClientConsts {

  // size of integer header (file size / operation code)
  public static final int HEADER_BUFFER_SIZE = 4;

  // size of extended header that contains file name
  public static final int EXTENDED_HEADER_BUFFER_SIZE = 100;

  // max size of file content
  public static final int FILE_CONTENT_BUFFER_SIZE = 10240;

  // options for file channel used for writing to file system
  public static final StandardOpenOption[] WRITE_OPTIONS =
      (StandardOpenOption[])Arrays.asList(
          StandardOpenOption.CREATE,
          StandardOpenOption.TRUNCATE_EXISTING,
          StandardOpenOption.WRITE).toArray();

  // options for file channel used for reading from file system
  public static final EnumSet<StandardOpenOption> READ_OPTIONS = EnumSet.of(StandardOpenOption.READ);

  // directory pointing to client storage in file system
  public static final String STORAGE_PATH = "Client DIR";

  public static final int CONNECTION_ATTEMPTS = 30;

  public static final String HOST = "localhost";

  public static final int PORT = 7566;
}
