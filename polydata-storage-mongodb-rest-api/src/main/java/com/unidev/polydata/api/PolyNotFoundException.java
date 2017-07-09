package com.unidev.polydata.api;


public class PolyNotFoundException extends RuntimeException {

  public PolyNotFoundException() {
  }

  public PolyNotFoundException(String message) {
    super(message);
  }

  public PolyNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }

  public PolyNotFoundException(Throwable cause) {
    super(cause);
  }
}
