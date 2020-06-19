package com.pavelilin.cloud.storage.client;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class FileInfo {

  public static final String UP_TOKEN = "[..]";

  private final String fileName;
  private final long length;

  public String getFileName() {
    return fileName;
  }

  public long getLength() {
    return length;
  }

  public boolean isRegularFile() {
    return !isDirectory() && !isUpElement() && !isServerFile();
  }

  public boolean isDirectory() {
    return length == -1L;
  }

  public boolean isUpElement() {
    return length == -2L;
  }

  public boolean isServerFile() {
    return length == -3L;
  }

  public FileInfo(String fileName, long length) {
    this.fileName = fileName;
    this.length = length;
  }

  public FileInfo(Path path) {
    this.fileName = path.getFileName().toString();
    if (Files.isDirectory(path)) {
      this.length = -1L;
    } else {
      try {
        this.length = Files.size(path);
      } catch (IOException e) {
        throw new RuntimeException("Something wrong with this file: " + path.toAbsolutePath().toString());
      }
    }
  }

}
