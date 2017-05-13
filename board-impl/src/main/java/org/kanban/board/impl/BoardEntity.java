/*
 * Copyright (C) 2016-2017 Lightbend Inc. <https://www.lightbend.com>
 */
package org.kanban.board.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import akka.NotUsed;
import com.lightbend.lagom.javadsl.persistence.PersistentEntity;

import akka.Done;
import org.kanban.board.api.Board;

/**
 * This is an event sourcshoulded entity. It has a state, {@link BoardState}, which
 * stores what the board  be.
 * <p>
 * Event sourced entities are interacted with by sending them commands.
 * <p>
 * Commands get translated to events, and it's the events that get persisted by
 * the entity. Each event will have an event handler registered for it, and an
 * event handler simply applies an event to the current state. This will be done
 * when the event is first created, and it will also be done when the entity is
 * loaded from the database - each event will be replayed to recreate the state
 * of the entity.
 */
public class BoardEntity extends PersistentEntity<BoardCommand, BoardEvent, BoardState> {

    @Override
    public Behavior initialBehavior(Optional<BoardState> snapshotState) {
        System.out.print("\nCreating board initial behavior\n");

        BehaviorBuilder b = newBehaviorBuilder(snapshotState.orElse(
                new BoardState(Optional.empty())));

        b.setCommandHandler(BoardCommand.CreateBoard.class, (cmd, ctx) -> {
            if (state().board.isPresent()) {
                ctx.invalidCommand("Board " + entityId() + " is already created");
                return ctx.done();
            } else {
                System.out.print("\nNo existing board. Creating new Board Entity\n");
                Board board = cmd.board;
                List<BoardEvent> events = new ArrayList<>();
                events.add(new BoardEvent.BoardCreated(board.boardId, board.title));
//                for (String friendId : user.friends) {
//                    events.add(new FriendAdded(user.userId, friendId));
//                }
                return ctx.thenPersistAll(events, () -> ctx.reply(Done.getInstance()));
            }
        });

        b.setEventHandler(BoardEvent.BoardCreated.class,
                evt -> new BoardState(Optional.of(new Board(evt.boardId, evt.title, Board.BoardStatus.CREATED))));


        b.setReadOnlyCommandHandler(BoardCommand.GetBoard.class, (cmd, ctx) -> {
            ctx.reply(new BoardCommand.GetBoardReply(state().board));
        });

        b.setCommandHandler(BoardCommand.UpdateTitle.class, (cmd, ctx) -> {
            if (state().board.get().title.equals(cmd.title)) {
                ctx.invalidCommand("New title is the same as the old one, no need to change!");
                return ctx.done();
            } else if (state().board.get().status.equals(Board.BoardStatus.ARCHIVED)) {
                ctx.invalidCommand("Board is archived, cannot change its title!");
                return ctx.done();
            } else {
                return ctx.thenPersist(new BoardEvent.TitleChanged(cmd.title),
                        // Then once the event is successfully persisted, we respond with done.
                        evt -> ctx.reply(Done.getInstance()));
            }
        });

        b.setEventHandler(BoardEvent.TitleChanged.class,
                evt -> new BoardState(Optional.of(new Board(state().board.get().boardId, evt.title, state().board.get().status))));

        b.setCommandHandler(BoardCommand.ChangeStatus.class, (cmd, ctx) -> {
            if (state().board.get().status.equals(cmd.status)) {
                ctx.invalidCommand("New status is the same as the old one, no need to change!");
                return ctx.done();
            } else {
                return ctx.thenPersist(new BoardEvent.StatusChanged(cmd.status),
                        // Then once the event is successfully persisted, we respond with done.
                        evt -> ctx.reply(Done.getInstance()));
            }

        });

        b.setEventHandler(BoardEvent.StatusChanged.class, evt -> new BoardState(Optional.of(new Board(state().board.get().boardId, state().board.get().title, evt.status))));

        return b.build();
    }

}
