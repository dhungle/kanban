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
import org.kanban.board.api.Board;

import java.util.Optional;

/**
 * This interface defines all the commands that the Board entity supports.
 * <p>
 * By convention, the commands should be inner classes of the interface, which
 * makes it simple to get a complete picture of what commands an entity
 * supports.
 */
public interface BoardCommand extends Jsonable {

    @SuppressWarnings("serial")
    @Immutable
    @JsonDeserialize
    public final class CreateBoard implements BoardCommand, PersistentEntity.ReplyType<Done> {
        public final Board board;

        @JsonCreator
        public CreateBoard(Board board) {
            this.board = Preconditions.checkNotNull(board, "board");
        }

        @Override
        public boolean equals(@Nullable Object another) {
            if (this == another)
                return true;
            return another instanceof CreateBoard && equalTo((CreateBoard) another);
        }

        private boolean equalTo(CreateBoard another) {
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
            return MoreObjects.toStringHelper("CreateBoard").add("board", board).toString();
        }
    }


    @SuppressWarnings("serial")
    @Immutable
    @JsonDeserialize
    public final class GetBoard implements BoardCommand, PersistentEntity.ReplyType<GetBoardReply> {

        @Override
        public boolean equals(@Nullable Object another) {
            return this instanceof GetBoard;
        }

        @Override
        public int hashCode() {
            return 2053226012;
        }

        @Override
        public String toString() {
            return "GetBoard{}";
        }
    }

    @SuppressWarnings("serial")
    @Immutable
    @JsonDeserialize
    public final class GetBoardReply implements Jsonable {
        public final Optional<Board> board;

        @JsonCreator
        public GetBoardReply(Optional<Board> board) {
            this.board = Preconditions.checkNotNull(board, "board");
        }

        @Override
        public boolean equals(@Nullable Object another) {
            if (this == another)
                return true;
            return another instanceof GetBoardReply && equalTo((GetBoardReply) another);
        }

        private boolean equalTo(GetBoardReply another) {
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
            return MoreObjects.toStringHelper("GetBoardReply").add("board", board).toString();
        }
    }

    @SuppressWarnings("serial")
    @Immutable
    @JsonDeserialize
    public final class UpdateTitle implements BoardCommand, PersistentEntity.ReplyType<Done> {
        public final String title;

        @JsonCreator
        public UpdateTitle(String title) {
            System.out.println("\nUpdating title in BoardCommand\n");
            this.title = Preconditions.checkNotNull(title, "title");
        }

        @Override
        public boolean equals(@Nullable Object another) {
            if (this == another)
                return true;
            return another instanceof UpdateTitle && equalTo((UpdateTitle) another);
        }

        private boolean equalTo(UpdateTitle another) {
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
            return MoreObjects.toStringHelper("UpdateTitle").add("title", title).toString();
        }
    }

    @SuppressWarnings("serial")
    @Immutable
    @JsonDeserialize
    public final class ChangeStatus implements BoardCommand, PersistentEntity.ReplyType<Done> {
        public final Board.BoardStatus status;

        @JsonCreator
        public ChangeStatus(Board.BoardStatus status) {
            System.out.println("\nChanging status in BoardCommand\n");
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
