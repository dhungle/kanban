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
import com.lightbend.lagom.javadsl.persistence.PersistentEntity;
import com.lightbend.lagom.serialization.Jsonable;

import akka.Done;
import org.kanban.board.api.Task;

import java.util.Optional;

/**
 * This interface defines all the commands that the Task entity supports.
 * <p>
 * By convention, the commands should be inner classes of the interface, which
 * makes it simple to get a complete picture of what commands an entity
 * supports.
 */
public interface TaskCommand extends Jsonable {

    @SuppressWarnings("serial")
    @Immutable
    @JsonDeserialize
    public final class CreateTask implements TaskCommand, PersistentEntity.ReplyType<Done> {
        public final Task task;

        @JsonCreator
        public CreateTask(Task task) {
            this.task = Preconditions.checkNotNull(task, "task");
        }

        @Override
        public boolean equals(@Nullable Object another) {
            if (this == another)
                return true;
            return another instanceof CreateTask && equalTo((CreateTask) another);
        }

        private boolean equalTo(CreateTask another) {
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
            return MoreObjects.toStringHelper("CreateTask").add("task", task).toString();
        }
    }


    @SuppressWarnings("serial")
    @Immutable
    @JsonDeserialize
    public final class GetTask implements TaskCommand, PersistentEntity.ReplyType<GetTaskReply> {

        @Override
        public boolean equals(@Nullable Object another) {
            return this instanceof GetTask;
        }

        @Override
        public int hashCode() {
            return 2053226012;
        }

        @Override
        public String toString() {
            return "GetTask{}";
        }
    }

    @SuppressWarnings("serial")
    @Immutable
    @JsonDeserialize
    public final class GetTaskReply implements Jsonable {
        public final Optional<Task> task;

        @JsonCreator
        public GetTaskReply(Optional<Task> task) {
            this.task = Preconditions.checkNotNull(task, "task");
        }

        @Override
        public boolean equals(@Nullable Object another) {
            if (this == another)
                return true;
            return another instanceof GetTaskReply && equalTo((GetTaskReply) another);
        }

        private boolean equalTo(GetTaskReply another) {
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
            return MoreObjects.toStringHelper("GetTaskReply").add("task", task).toString();
        }
    }

    @SuppressWarnings("serial")
    @Immutable
    @JsonDeserialize
    public final class Update implements TaskCommand, PersistentEntity.ReplyType<Done> {
        public final String title;
        public final String detail;
        public final String color;

        @JsonCreator
        public Update(String title, String detail, String color) {
            System.out.println("\nUpdating title in TaskCommand\n\n");
            this.title = Preconditions.checkNotNull(title, "title");
            this.detail = Preconditions.checkNotNull(detail, "detail");
            this.color = Preconditions.checkNotNull(color, "color");
        }

        @Override
        public boolean equals(@Nullable Object another) {
            if (this == another)
                return true;
            return another instanceof Update && equalTo((Update) another);
        }

        private boolean equalTo(Update another) {
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
            return MoreObjects.toStringHelper("Update").add("title", title).toString();
        }
    }

    @SuppressWarnings("serial")
    @Immutable
    @JsonDeserialize
    public final class ChangeStatus implements TaskCommand, PersistentEntity.ReplyType<Done> {
        public final Task.TaskStatus status;

        @JsonCreator
        public ChangeStatus(Task.TaskStatus status) {
            System.out.println("\nChanging status in TaskCommand\n\n");
            this.status = Preconditions.checkNotNull(status, "status");
        }

        @Override
        public boolean equals(@Nullable Object another) {
            if (this == another)
                return true;
            return another instanceof ChangeStatus && equalTo((ChangeStatus) another);
        }

        private boolean equalTo(ChangeStatus another) {
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
            return MoreObjects.toStringHelper("ChangeStatus").add("status", status).toString();
        }
    }
}
