package com.pavelilin.cloud.storage.client;

public enum OperationType {

  FILE_TRANSFER(1),
  FILE_LIST_REQUEST(2),
  FILE_REQUEST(3);

  private final int operationCode;

  public int getOperationCode() {
    return operationCode;
  }

  OperationType(int code) {
    this.operationCode = code;
  }
}
