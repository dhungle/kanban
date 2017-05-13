/*
 * Copyright (C) 2016-2017 Lightbend Inc. <https://www.lightbend.com>
 */
package org.kanban.board.impl;

import static com.lightbend.lagom.javadsl.testkit.ServiceTest.defaultSetup;
import static com.lightbend.lagom.javadsl.testkit.ServiceTest.withServer;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import org.kanban.board.api.Board;
import org.kanban.board.api.BoardService;

public class BoardServiceTest {

    @Test
    public void shouldStoreBoardContent() throws Exception {
        withServer(defaultSetup().withCassandra(true), server -> {
            BoardService service = server.client(BoardService.class);

            Board board1 = new Board("board1", "Soft Arch", Board.BoardStatus.CREATED);
            service.createBoard().invoke(board1).toCompletableFuture().get(10, SECONDS);

            Board board2 = new Board("board2", "title2", Board.BoardStatus.CREATED);
            service.createBoard().invoke(board2).toCompletableFuture().get(3, SECONDS);

            Board fetchedBoard1 = service.getBoard("board1").invoke().toCompletableFuture().get(3, SECONDS);
            assertEquals(board1.boardId, fetchedBoard1.boardId);

            service.changeStatus("board1").invoke(new Board("board1", "Soft Arch", Board.BoardStatus.ARCHIVED)).toCompletableFuture().get(3, SECONDS);
            assertEquals(board1.status, fetchedBoard1.status);
        });
    }

}
