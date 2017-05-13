/*
 * Copyright (C) 2016-2017 Lightbend Inc. <https://www.lightbend.com>
 */
package org.kanban.board.impl;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.lightbend.lagom.serialization.Jsonable;
import org.kanban.board.api.Task;

import java.time.Instant;
import java.util.Optional;

/**
 * This interface defines all the events that the Task entity supports.
 * <p>
 * By convention, the events should be inner classes of the interface, which
 * makes it simple to get a complete picture of what events an entity has.
 */
public interface TaskEvent extends Jsonable {

    /**
     * An event that represents a change in greeting title.
     */
    @SuppressWarnings("serial")
    @Immutable
    @JsonDeserialize
    public class TaskCreated implements TaskEvent {
        public final String taskId;
        public final String title;
        public final String detail;
        public final String color;
        public final String boardId;
        public final Instant timestamp;

        public TaskCreated(String taskId, String title, String detail, String color, String boardId) {
            this(taskId, title, detail, color, boardId, Optional.empty());
        }

        @JsonCreator
        private TaskCreated(String taskId, String title, String detail, String color, String boardId, Optional<Instant> timestamp) {
            this.taskId = Preconditions.checkNotNull(taskId, "taskId");
            this.title = Preconditions.checkNotNull(title, "title");
            this.detail = Preconditions.checkNotNull(detail, "detail");
            this.color = Preconditions.checkNotNull(color, "color");
            this.boardId = Preconditions.checkNotNull(boardId, "boardId");
            this.timestamp = timestamp.orElseGet(() -> Instant.now());
        }

        @Override
        public boolean equals(@Nullable Object another) {
            if (this == another)
                return true;
            return another instanceof TaskCreated && equalTo((TaskCreated) another);
        }

        private boolean equalTo(TaskCreated another) {
            return taskId.equals(another.taskId) && title.equals(another.title) && timestamp.equals(another.timestamp);
        }

        @Override
        public int hashCode() {
            int h = 31;
            h = h * 17 + taskId.hashCode();
            h = h * 17 + title.hashCode();
            h = h * 17 + timestamp.hashCode();
            return h;
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper("TaskCreated").add("taskId", taskId).add("title", title)
                    .add("timestamp", timestamp).toString();
        }
    }

    @SuppressWarnings("serial")
    @Immutable
    @JsonDeserialize
    public final class InfoChanged implements TaskEvent {
        public final String title;
        public final String detail;
        public final String color;

        @JsonCreator
        public InfoChanged(String title, String detail, String color) {
            System.out.println("\nTitle updating in Task Event");
            this.title = Preconditions.checkNotNull(title, "title");
            this.detail = Preconditions.checkNotNull(detail, "detail");
            this.color = Preconditions.checkNotNull(color, "color");
        }

        @Override
        public boolean equals(@Nullable Object another) {
            if (this == another)
                return true;
            return another instanceof InfoChanged && equalTo((InfoChanged) another);
        }

        private boolean equalTo(InfoChanged another) {
            return title.equals(another.title);
        }

        @Override
        public int hashCode() {
            int h = 31;
            h = h * 17 + title.hashCode();
            return h;
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper("InfoChanged").add("title", title).toString();
        }
    }

    @SuppressWarnings("serial")
    @Immutable
    @JsonDeserialize
    public final class StatusChanged implements TaskEvent {
        public final Task.TaskStatus status;

        @JsonCreator
        public StatusChanged(Task.TaskStatus status) {
            System.out.println("\nStatus updating in Task Event");
            this.status = Preconditions.checkNotNull(status, "status");
        }

        @Override
        public boolean equals(@Nullable Object another) {
            if (this == another)
                return true;
            return another instanceof StatusChanged && equalTo((StatusChanged) another);
        }

        private boolean equalTo(StatusChanged another) {
            return status.equals(another.status);
        }

        @Override
        public int hashCode() {
            int h = 31;
            h = h * 17 + status.hashCode();
            return h;
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper("StatusChanged").add("status", status).toString();
        }
    }
}
