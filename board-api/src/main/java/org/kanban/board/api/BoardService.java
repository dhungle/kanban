/*
 * Copyright (C) 2016-2017 Lightbend Inc. <https://www.lightbend.com>
 */
package org.kanban.board.api;

import static com.lightbend.lagom.javadsl.api.Service.named;
import static com.lightbend.lagom.javadsl.api.Service.namedCall;
import static com.lightbend.lagom.javadsl.api.Service.pathCall;

import akka.Done;
import akka.NotUsed;
import com.lightbend.lagom.javadsl.api.Descriptor;
import com.lightbend.lagom.javadsl.api.Service;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import org.pcollections.PSequence;

/**
 * The Board service interface.
 * <p>
 * This describes everything that Lagom needs to know about how to serve and
 * consume the Board.
 */
public interface BoardService extends Service {

    /**
     * Service call for getting a board.
     * <p>
     * The ID of this service call is the board name, and the response message is the Board object.
     */
    ServiceCall<NotUsed, Board> getBoard(String boardId);

    /**
     * Service call for creating a board.
     * <p>
     * The request message is the Board to create.
     */
    ServiceCall<Board, Done> createBoard();

    /**
     * Service call for updating the title of a board.
     */
    ServiceCall<Board, Done> updateTitle(String boardId);

    /**
     * Service call for changing the status of a board.
     */
    ServiceCall<Board, Done> changeStatus(String boardId);

    /**
     * Service call for adding a task to a board.
     */
    ServiceCall<Task, Done> addTask(String boardId);

    /**
     * Service call for getting the task info of a board.
     */
    ServiceCall<NotUsed, Task> getTask(String boardId, String taskId);

    /**
     * Service call for updating info (detail, title, color) of a task.
     */
    ServiceCall<Task, Done> update(String boardId, String taskId);

    /**
     * Service call for changing status of a task.
     */
    ServiceCall<Task, Done> changeTaskStatus(String boardId, String taskId);


    @Override
    default Descriptor descriptor() {
        // @formatter:off
        return named("boardservice").withCalls(
                pathCall("/api/boards/:boardId", this::getBoard),
                namedCall("/api/boards", this::createBoard),
                pathCall("/api/boards/:boardId/updateTitle", this::updateTitle),
                pathCall("/api/boards/:boardId/changeStatus", this::changeStatus),
                pathCall("/api/boards/:boardId/tasks", this::addTask),
                pathCall("/api/boards/:boardId/tasks/:taskId", this::getTask),
                pathCall("/api/boards/:boardId/tasks/:taskId/update", this::update),
                pathCall("/api/boards/:boardId/tasks/:taskId/changeStatus", this::changeTaskStatus)
        ).withAutoAcl(true);
        // @formatter:on
    }
}
