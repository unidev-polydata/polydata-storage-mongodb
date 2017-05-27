package com.unidev.polydata.changes;

import com.unidev.changesexecutor.model.ChangeExecutionResult;


public class MongodbChangeExecutionResult extends ChangeExecutionResult<MongodbChange> {

  public MongodbChangeExecutionResult() {
  }

  public MongodbChangeExecutionResult(MongodbChange change, Result result, String message) {
    super(change, result, message);
  }
}
