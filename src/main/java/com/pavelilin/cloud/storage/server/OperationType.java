package com.pavelilin.cloud.storage.server;

public enum OperationType {

  FILE_TRANSFER(1),
  FILE_LIST_REQUEST(2),
  FILE_REQUEST(3),
  TRANSFER_SUCCESS(0),
  TRANSFER_ERROR(-1);

  private final int operationCode;

  OperationType(int code) {
    this.operationCode = code;
  }

  public int getOperationCode() {
    return operationCode;
  }

  public static OperationType fromCode(int code) {
    for (OperationType value : OperationType.values()) {
      if (value.getOperationCode() == code) {
        return value;
      }
    }
    throw new RuntimeException("Could not match any operation type");
  }
}
