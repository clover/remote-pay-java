package com.clover.remote;

public enum TxStartResponseResult {
  SUCCESS(true, 0),
  DUPLICATE(false, 1),
  ORDER_MODIFIED(false, 2),
  ORDER_LOAD(false, 3),
  FAIL(false, 4);

  public final boolean success;
  public final int messageId;

  TxStartResponseResult(boolean success, int messageId) {
    this.success = success;
    this.messageId = messageId;
  }
}
