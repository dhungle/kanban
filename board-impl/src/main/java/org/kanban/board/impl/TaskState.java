/*
 * Copyright (C) 2016 Lightbend Inc. <http://www.lightbend.com>
 */
package org.kanban.board.impl;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.lightbend.lagom.serialization.Jsonable;
import org.kanban.board.api.Task;


import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.Optional;

@SuppressWarnings("serial")
@Immutable
@JsonDeserialize
public final class TaskState implements Jsonable {

    public final Optional<Task> task;

    @JsonCreator
    public TaskState(Optional<Task> task) {
        this.task = Preconditions.checkNotNull(task, "task");
    }

    @Override
    public boolean equals(@Nullable Object another) {
        if (this == another)
            return true;
        return another instanceof TaskState && equalTo((TaskState) another);
    }

    private boolean equalTo(TaskState another) {
        return task.equals(another.task);
    }

    @Override
    public int hashCode() {
        int h = 31;
        h = h * 17 + task.hashCode();
        return h;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper("TaskState").add("task", task).toString();
    }
}
