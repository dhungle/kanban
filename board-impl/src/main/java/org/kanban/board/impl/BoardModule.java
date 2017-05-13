/*
 * Copyright (C) 2016-2017 Lightbend Inc. <https://www.lightbend.com>
 */
package org.kanban.board.impl;

import com.google.inject.AbstractModule;
import com.lightbend.lagom.javadsl.server.ServiceGuiceSupport;
import org.kanban.board.api.BoardService;

/**
 * The module that binds the BoardService so that it can be served.
 */
public class BoardModule extends AbstractModule implements ServiceGuiceSupport {
  @Override
  protected void configure() {
    bindServices(serviceBinding(BoardService.class, BoardServiceImpl.class));
  }
}
