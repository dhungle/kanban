/*
 * Copyright (C) 2016 Lightbend Inc. <http://www.lightbend.com>
 */
package org.kanban.board.impl;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.lightbend.lagom.serialization.Jsonable;
import org.kanban.board.api.Board;
import org.pcollections.PSequence;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.Optional;

@SuppressWarnings("serial")
@Immutable
@JsonDeserialize
public final class BoardState implements Jsonable {

    public final Optional<Board> board;

    @JsonCreator
    public BoardState(Optional<Board> board) {
        this.board = Preconditions.checkNotNull(board, "board");
    }

    public BoardState addTask(String taskId) {
        if (!board.isPresent())
            throw new IllegalStateException("friend can't be added before board is created");
        PSequence<String> newTasks = board.get().tasks.plus(taskId);
        return new BoardState(Optional.of(new Board(board.get().boardId, board.get().title, board.get().status, Optional.of(newTasks))));
    }

    @Override
    public boolean equals(@Nullable Object another) {
        if (this == another)
            return true;
        return another instanceof BoardState && equalTo((BoardState) another);
    }

    private boolean equalTo(BoardState another) {
        return board.equals(another.board);
    }

    @Override
    public int hashCode() {
        int h = 31;
        h = h * 17 + board.hashCode();
        return h;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper("BoardState").add("board", board).toString();
    }
}
