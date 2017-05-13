package org.kanban.board.api;

/**
 * Created by duyhung on 5/6/17.
 */

import java.util.Optional;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import org.pcollections.PSequence;
import org.pcollections.TreePVector;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.MoreObjects;

@Immutable
@JsonDeserialize
public final class Board {

    public enum BoardStatus {CREATED, ARCHIVED}

    ;
    public final String boardId;
    public final String title;
    public final BoardStatus status;
    public final PSequence<String> tasks;

    public Board(String boardId, String title, BoardStatus status) {
        this(boardId, title, status, Optional.empty());
    }

    @JsonCreator
    public Board(String boardId, String title, BoardStatus status, Optional<PSequence<String>> tasks) {
        this.boardId = boardId;
        this.title = title;
        this.status = status;
        this.tasks = tasks.orElse(TreePVector.empty());
    }

    @Override
    public boolean equals(@Nullable Object another) {
        if (this == another)
            return true;
        return another instanceof Board && equalTo((Board) another);
    }

    private boolean equalTo(Board another) {
        return boardId.equals(another.boardId) && title.equals(another.title) && tasks.equals(another.tasks);
    }

    @Override
    public int hashCode() {
        int h = 31;
        h = h * 17 + boardId.hashCode();
        h = h * 17 + title.hashCode();
        h = h * 17 + status.hashCode();
        h = h * 17 + tasks.hashCode();
        return h;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper("Board").add("boardId", boardId).add("title", title).add("status", status).add("tasks", tasks)
                .toString();
    }
}

