package com.unidev.polydata.api;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
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
