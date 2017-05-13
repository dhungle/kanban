/*
 * Copyright (C) 2016-2017 Lightbend Inc. <https://www.lightbend.com>
 */
package org.kanban.board.impl;

import akka.Done;
import akka.NotUsed;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.api.transport.NotFound;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRef;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRegistry;


import javax.inject.Inject;

import org.kanban.board.api.Board;
import org.kanban.board.api.BoardService;
import org.kanban.board.api.Task;
import org.kanban.board.impl.BoardCommand.*;

/**
 * Implementation of the BoardService.
 */
public class BoardServiceImpl implements BoardService {

    private final PersistentEntityRegistry persistentEntities;

    @Inject
    public BoardServiceImpl(PersistentEntityRegistry persistentEntities) {
        this.persistentEntities = persistentEntities;

        persistentEntities.register(BoardEntity.class);
        persistentEntities.register(TaskEntity.class);
    }

    @Override
    public ServiceCall<NotUsed, Board> getBoard(String boardId) {
        System.out.println("\nget board in Board service impl");
        return request -> {
            return boardEntityRef(boardId).ask(new GetBoard()).thenApply(reply -> {
                if (reply.board.isPresent())
                    return reply.board.get();
                else
                    throw new NotFound("board " + boardId + " not found");
            });
        };
    }

    @Override
    public ServiceCall<Board, Done> createBoard() {
        System.out.println("\ncreate user in friend service impl");
        return request -> {
            return boardEntityRef(request.boardId).ask(new CreateBoard(request));
        };
    }

    @Override
    public ServiceCall<Board, Done> updateTitle(String boardId) {
        System.out.println("\nUpdating title");
        return request -> {
            // Look up the hello world entity for the given ID.
            // Tell the entity to use the greeting message specified.
            return boardEntityRef(boardId).ask(new UpdateTitle(request.title));
        };

    }

    @Override
    public ServiceCall<Board, Done> changeStatus(String boardId) {
        System.out.println("\nChanging Status");
        return request -> {
            // Look up the hello world entity for the given ID.
            // Tell the entity to use the greeting message specified.
            return boardEntityRef(boardId).ask(new ChangeStatus(request.status));
        };

    }


    @Override
    public ServiceCall<Task, Done> addTask(String boardId) {
        System.out.println("\ncreate task in Board service impl");
        return request -> {
            return taskEntityRef(request.taskId).ask(new TaskCommand.CreateTask(request));
        };
    }

    @Override
    public ServiceCall<NotUsed, Task> getTask(String boardId, String taskId) {
        System.out.println("\nget board in Board service impl");
        return request -> {
            return taskEntityRef(taskId).ask(new TaskCommand.GetTask()).thenApply(reply -> {
                if (reply.task.isPresent())
                    return reply.task.get();
                else
                    throw new NotFound("task " + taskId + " not found");
            });
        };
    }


    @Override
    public ServiceCall<Task, Done> update(String boardId, String taskId) {
        System.out.println("\nUpdating title");
        return request -> {
            // Look up the hello world entity for the given ID.
            // Tell the entity to use the greeting message specified.
            return taskEntityRef(taskId).ask(new TaskCommand.Update(request.title, request.detail, request.color));
        };

    }

    @Override
    public ServiceCall<Task, Done> changeTaskStatus(String boardId, String taskId) {
        System.out.println("\nChanging Status");
        return request -> {
            // Look up the hello world entity for the given ID.
            // Tell the entity to use the greeting message specified.
            return taskEntityRef(taskId).ask(new TaskCommand.ChangeStatus(request.status));
        };

    }



    private PersistentEntityRef<BoardCommand> boardEntityRef(String boardId) {
        PersistentEntityRef<BoardCommand> ref = persistentEntities.refFor(BoardEntity.class, boardId);
        return ref;
    }

    private PersistentEntityRef<TaskCommand> taskEntityRef(String boardId) {
        PersistentEntityRef<TaskCommand> ref = persistentEntities.refFor(TaskEntity.class, boardId);
        return ref;
    }

}
