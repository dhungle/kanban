/*
 * Copyright (C) 2016-2017 Lightbend Inc. <https://www.lightbend.com>
 */
package org.kanban.stream.impl;

import akka.NotUsed;
import akka.stream.javadsl.Source;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import org.kanban.board.api.BoardService;
import org.kanban.stream.api.StreamService;

import javax.inject.Inject;

import static java.util.concurrent.CompletableFuture.completedFuture;

/**
 * Implementation of the HelloString.
 */
public class StreamServiceImpl implements StreamService {

  private final BoardService boardService;

  @Inject
  public StreamServiceImpl(BoardService boardService) {
    this.boardService = boardService;
  }
}

//  @Override
//  public ServiceCall<Source<String, NotUsed>, Source<String, NotUsed>> stream() {
//    return hellos -> completedFuture(
//        hellos.mapAsync(8, name -> boardService.hello(name).invoke()));
//  }
//}
