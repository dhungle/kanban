
package org.kanban.board.api;

/**
 * Created by duyhung on 5/6/17.
 */

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.MoreObjects;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

@Immutable
@JsonDeserialize
public final class Task {

    public enum TaskStatus {
        BACKLOG, SCHEDULED, STARTED, COMPLETED, ARCHIVED, DELETED
    }

    public final String taskId;
    public final String title;
    public final String detail;
    public final String color;
    public final String boardId;
    public final TaskStatus status;


    @JsonCreator
    public Task(String taskId, String title, String detail, String color, String boardId, TaskStatus status) {
        this.taskId = taskId;
        this.title = title;
        this.detail = detail;
        this.color = color;
        this.boardId = boardId;
        this.status = status;
    }

    @Override
    public boolean equals(@Nullable Object another) {
        if (this == another)
            return true;
        return another instanceof Task && equalTo((Task) another);
    }

    private boolean equalTo(Task another) {
        return taskId.equals(another.taskId) && status.equals(another.status) && title.equals(another.title);
    }

    @Override
    public int hashCode() {
        int h = 31;
        h = h * 17 + taskId.hashCode();
        return h;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper("TaskId").add("taskId", taskId).add("status", status).toString();
    }

}
