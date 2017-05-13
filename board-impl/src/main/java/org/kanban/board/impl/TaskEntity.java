/*
 * Copyright (C) 2016-2017 Lightbend Inc. <https://www.lightbend.com>
 */
package org.kanban.board.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.lightbend.lagom.javadsl.persistence.PersistentEntity;

import akka.Done;
import org.kanban.board.api.Task;

/**
 * This is an event sourced entity. It has a state, {@link TaskState}, which
 * stores what the task should be.
 */
public class TaskEntity extends PersistentEntity<TaskCommand, TaskEvent, TaskState> {

    @Override
    public Behavior initialBehavior(Optional<TaskState> snapshotState) {
        System.out.print("\n\n\nCreating task initial behavior\n\n\n");

        BehaviorBuilder b = newBehaviorBuilder(snapshotState.orElse(
                new TaskState(Optional.empty())));

        b.setCommandHandler(TaskCommand.CreateTask.class, (cmd, ctx) -> {

            if (state().task.isPresent()) {
                ctx.invalidCommand("Task " + entityId() + " is already created");
                return ctx.done();
            } else {
                System.out.print("\n\n\n No existing task. Creating new Task Entity\n\n\n");
                Task task = cmd.task;
                List<TaskEvent> events = new ArrayList<>();
                events.add(new TaskEvent.TaskCreated(task.taskId, task.title, task.detail, task.color, task.boardId));
                return ctx.thenPersistAll(events, () -> ctx.reply(Done.getInstance()));
            }
        });

        b.setEventHandler(TaskEvent.TaskCreated.class,
                evt -> new TaskState(Optional.of(new Task(evt.taskId, evt.title, evt.detail, evt.color, evt.boardId, Task.TaskStatus.BACKLOG))));


        b.setReadOnlyCommandHandler(TaskCommand.GetTask.class, (cmd, ctx) -> {
            ctx.reply(new TaskCommand.GetTaskReply(state().task));
        });

        b.setCommandHandler(TaskCommand.Update.class, (cmd, ctx) -> {
            if (state().task.get().title.equals(cmd.title)) {
                ctx.invalidCommand("New title is the same as the old one, no need to change!");
                return ctx.done();
            } else if (state().task.get().status.equals(Task.TaskStatus.ARCHIVED)) {
                ctx.invalidCommand("Task is archived, cannot change its info!");
                return ctx.done();
            } else {
                return ctx.thenPersist(new TaskEvent.InfoChanged(cmd.title, cmd.detail, cmd.color),
                        // Then once the event is successfully persisted, we respond with done.
                        evt -> ctx.reply(Done.getInstance()));
            }
        });

        b.setEventHandler(TaskEvent.InfoChanged.class,
                evt -> new TaskState(Optional.of(new Task(state().task.get().taskId, evt.title, evt.detail, evt.color,
                        state().task.get().boardId, state().task.get().status))));

        b.setCommandHandler(TaskCommand.ChangeStatus.class, (cmd, ctx) -> {
            if (state().task.get().status.equals(cmd.status)) {
                ctx.invalidCommand("New status is the same as the old one, no need to change!");
                return ctx.done();
            } else if (cmd.status.equals(Task.TaskStatus.ARCHIVED) && !state().task.get().status.equals(Task.TaskStatus.COMPLETED)) {
                ctx.invalidCommand("Task needs to be completed before being archived");
                return ctx.done();
            } else {
                return ctx.thenPersist(new TaskEvent.StatusChanged(cmd.status),
                        // Then once the event is successfully persisted, we respond with done.
                        evt -> ctx.reply(Done.getInstance()));
            }

        });

        b.setEventHandler(TaskEvent.StatusChanged.class, evt -> new TaskState(Optional.of(new Task(state().task.get().taskId,
                state().task.get().title, state().task.get().detail, state().task.get().color, state().task.get().boardId, evt.status))));

        return b.build();
    }

}
