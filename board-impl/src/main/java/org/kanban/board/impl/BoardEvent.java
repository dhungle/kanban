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
import org.kanban.board.api.Board;

import java.time.Instant;
import java.util.Optional;

/**
 * This interface defines all the events that the Board entity supports.
 * <p>
 * By convention, the events should be inner classes of the interface, which
 * makes it simple to get a complete picture of what events an entity has.
 */
public interface BoardEvent extends Jsonable {

    /**
     * An event that represents creating a new board.
     */
    @SuppressWarnings("serial")
    @Immutable
    @JsonDeserialize
    public class BoardCreated implements BoardEvent {
        public final String boardId;
        public final String title;
        public final Instant timestamp;

        public BoardCreated(String boardId, String title) {
            this(boardId, title, Optional.empty());
        }

        @JsonCreator
        private BoardCreated(String boardId, String title, Optional<Instant> timestamp) {
            this.boardId = Preconditions.checkNotNull(boardId, "boardId");
            this.title = Preconditions.checkNotNull(title, "title");
            this.timestamp = timestamp.orElseGet(() -> Instant.now());
        }

        @Override
        public boolean equals(@Nullable Object another) {
            if (this == another)
                return true;
            return another instanceof BoardCreated && equalTo((BoardCreated) another);
        }

        private boolean equalTo(BoardCreated another) {
            return boardId.equals(another.boardId) && title.equals(another.title) && timestamp.equals(another.timestamp);
        }

        @Override
        public int hashCode() {
            int h = 31;
            h = h * 17 + boardId.hashCode();
            h = h * 17 + title.hashCode();
            h = h * 17 + timestamp.hashCode();
            return h;
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper("BoardCreated").add("boardId", boardId).add("title", title)
                    .add("timestamp", timestamp).toString();
        }
    }

    @SuppressWarnings("serial")
    @Immutable
    @JsonDeserialize
    public final class TitleChanged implements BoardEvent {
        public final String title;

        @JsonCreator
        public TitleChanged(String title) {
            System.out.println("\nTitle updating in Board Event\n");
            this.title = Preconditions.checkNotNull(title, "title");
        }

        @Override
        public boolean equals(@Nullable Object another) {
            if (this == another)
                return true;
            return another instanceof TitleChanged && equalTo((TitleChanged) another);
        }

        private boolean equalTo(TitleChanged another) {
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
            return MoreObjects.toStringHelper("TitleChanged").add("title", title).toString();
        }
    }

    @SuppressWarnings("serial")
    @Immutable
    @JsonDeserialize
    public final class StatusChanged implements BoardEvent {
        public final Board.BoardStatus status;

        @JsonCreator
        public StatusChanged(Board.BoardStatus status) {
            System.out.println("\nStatus updating in Board Event\n");
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
