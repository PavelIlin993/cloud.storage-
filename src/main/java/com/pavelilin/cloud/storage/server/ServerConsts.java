package com.pavelilin.cloud.storage.server;

import java.nio.file.StandardOpenOption;
import java.util.EnumSet;

public class ServerConsts {
  // size of integer header (file size / operation code)
  public static final int HEADER_BUFFER_SIZE = 4;

  // max size of file content
  public static final int FILE_CONTENT_BUFFER_SIZE = 10240;

  // options for file channel used for writing to file system
  public static final EnumSet<StandardOpenOption> WRITE_OPTIONS =
      EnumSet.of(
          StandardOpenOption.CREATE,
          StandardOpenOption.TRUNCATE_EXISTING,
          StandardOpenOption.WRITE);

  // options for file channel used for reading from file system
  public static final EnumSet<StandardOpenOption> READ_OPTIONS = EnumSet.of(StandardOpenOption.READ);

  public static final String STORAGE_PATH = "Server DIR";

  public static final String HOST = "localhost";

  public static final int PORT = 7566;
}
