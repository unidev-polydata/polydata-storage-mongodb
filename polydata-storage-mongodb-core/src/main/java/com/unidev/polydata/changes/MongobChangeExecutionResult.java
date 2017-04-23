package com.unidev.polydata.changes;

import com.unidev.changesexecutor.model.ChangeExecutionResult;


public class MongobChangeExecutionResult extends ChangeExecutionResult<MongodbChange> {

    public MongobChangeExecutionResult() {
    }

    public MongobChangeExecutionResult(MongodbChange change, Result result, String message) {
        super(change, result, message);
    }
}
