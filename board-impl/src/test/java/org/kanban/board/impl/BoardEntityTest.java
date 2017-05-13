/*
 * Copyright (C) 2016-2017 Lightbend Inc. <https://www.lightbend.com>
 */
package org.kanban.board.impl;

import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.Optional;

import com.lightbend.lagom.javadsl.persistence.PersistentEntity;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.lightbend.lagom.javadsl.testkit.PersistentEntityTestDriver;
import com.lightbend.lagom.javadsl.testkit.PersistentEntityTestDriver.Outcome;

import akka.Done;
import akka.actor.ActorSystem;
import akka.testkit.JavaTestKit;
import org.kanban.board.api.Board;

public class BoardEntityTest {

    static ActorSystem system;

    @BeforeClass
    public static void setup() {
        system = ActorSystem.create("BoardEntityTest");
    }

    @AfterClass
    public static void teardown() {
        JavaTestKit.shutdownActorSystem(system);
        system = null;
    }

    @Test
    public void testCreateBoard() {
        PersistentEntityTestDriver<BoardCommand, BoardEvent, BoardState> driver = new PersistentEntityTestDriver<>(
                system, new BoardEntity(), "board-1");

        Outcome<BoardEvent, BoardState> outcome = driver.run(
                new BoardCommand.CreateBoard(new Board("board1", "Soft Arch", Board.BoardStatus.CREATED)));
        assertEquals(Done.getInstance(), outcome.getReplies().get(0));
        assertEquals("board1", ((BoardEvent.BoardCreated) outcome.events().get(0)).boardId);
        assertEquals("Soft Arch", ((BoardEvent.BoardCreated) outcome.events().get(0)).title);

    }

    @Test
    public void testRejectDuplicateCreate() {
        PersistentEntityTestDriver<BoardCommand, BoardEvent, BoardState> driver = new PersistentEntityTestDriver<>(
                system, new BoardEntity(), "board-1");
        driver.run(new BoardCommand.CreateBoard(new Board("board1", "Soft Arch", Board.BoardStatus.CREATED)));

        Outcome<BoardEvent, BoardState> outcome = driver.run(
                new BoardCommand.CreateBoard(new Board("board1", "Soft Arch", Board.BoardStatus.CREATED)));
        assertEquals(PersistentEntity.InvalidCommandException.class, outcome.getReplies().get(0).getClass());
        assertEquals(Collections.emptyList(), outcome.events());
    }

    @Test
    public void testGetBoard() {
        PersistentEntityTestDriver<BoardCommand, BoardEvent, BoardState> driver = new PersistentEntityTestDriver<>(
                system, new BoardEntity(), "board-1");
        Board board = new Board("board1", "Soft Arch", Board.BoardStatus.CREATED);
        driver.run(new BoardCommand.CreateBoard(board));

        Outcome<BoardEvent, BoardState> outcome = driver.run(new BoardCommand.GetBoard());
        assertEquals(new BoardCommand.GetBoardReply(Optional.of(board)), outcome.getReplies().get(0));
        assertEquals(Collections.emptyList(), outcome.events());
    }

    @Test
    public void testChangeStatus() {
        PersistentEntityTestDriver<BoardCommand, BoardEvent, BoardState> driver = new PersistentEntityTestDriver<>(
                system, new BoardEntity(), "board-1");
        Board board = new Board("board1", "Soft Arch", Board.BoardStatus.CREATED);
        driver.run(new BoardCommand.CreateBoard(board));

        Outcome<BoardEvent, BoardState> outcome = driver.run(new BoardCommand.ChangeStatus(Board.BoardStatus.ARCHIVED));
        assertEquals(Board.BoardStatus.ARCHIVED, ((BoardEvent.StatusChanged) outcome.events().get(0)).status);
    }

    @Test
    public void testUpdateTitle() {
        PersistentEntityTestDriver<BoardCommand, BoardEvent, BoardState> driver = new PersistentEntityTestDriver<>(
                system, new BoardEntity(), "board-1");
        Board board = new Board("board1", "Soft Arch", Board.BoardStatus.CREATED);
        driver.run(new BoardCommand.CreateBoard(board));

        Outcome<BoardEvent, BoardState> outcome = driver.run(new BoardCommand.UpdateTitle("New title"));
        assertEquals(Done.getInstance(), outcome.getReplies().get(0));
        assertEquals("New title", ((BoardEvent.TitleChanged) outcome.events().get(0)).title);
    }

    @Test
    public void testRejectUpdateTitleOfArchivedBoard() {
        PersistentEntityTestDriver<BoardCommand, BoardEvent, BoardState> driver = new PersistentEntityTestDriver<>(
                system, new BoardEntity(), "board-1");
        Board board = new Board("board1", "Soft Arch", Board.BoardStatus.CREATED);
        driver.run(new BoardCommand.CreateBoard(board));
        driver.run(new BoardCommand.ChangeStatus(Board.BoardStatus.ARCHIVED));

        Outcome<BoardEvent, BoardState> outcome = driver.run(new BoardCommand.UpdateTitle("New Title"));
        assertEquals(PersistentEntity.InvalidCommandException.class, outcome.getReplies().get(0).getClass());
        assertEquals(Collections.emptyList(), outcome.events());
    }

    @Test
    public void testRejectUpdateTitleWithSameContent() {
        PersistentEntityTestDriver<BoardCommand, BoardEvent, BoardState> driver = new PersistentEntityTestDriver<>(
                system, new BoardEntity(), "board-1");
        Board board = new Board("board1", "Soft Arch", Board.BoardStatus.CREATED);
        driver.run(new BoardCommand.CreateBoard(board));

        Outcome<BoardEvent, BoardState> outcome = driver.run(new BoardCommand.UpdateTitle("Soft Arch"));
        assertEquals(PersistentEntity.InvalidCommandException.class, outcome.getReplies().get(0).getClass());
        assertEquals(Collections.emptyList(), outcome.events());
    }
}
