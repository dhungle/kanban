package org.kanban.board.impl;

/**
 * Created by duyhung on 5/7/17.
 */

import akka.Done;
import akka.actor.ActorSystem;
import akka.testkit.JavaTestKit;
import com.lightbend.lagom.javadsl.persistence.PersistentEntity;
import com.lightbend.lagom.javadsl.testkit.PersistentEntityTestDriver;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


import org.kanban.board.api.Board;
import org.kanban.board.api.Task;

import java.util.Collections;
import java.util.Optional;

public class TaskEntityTest {

    static ActorSystem system;

    @BeforeClass
    public static void setup() {
        system = ActorSystem.create("TaskEntityTest");
    }

    @AfterClass
    public static void teardown() {
        JavaTestKit.shutdownActorSystem(system);
        system = null;
    }

    @Test
    public void testCreateBoard() {
        PersistentEntityTestDriver<TaskCommand, TaskEvent, TaskState> driver = new PersistentEntityTestDriver<>(
                system, new TaskEntity(), "task-1");

        PersistentEntityTestDriver.Outcome<TaskEvent, TaskState> outcome = driver.run(
                new TaskCommand.CreateTask(new Task("task1", "assignment", "lagom", "blue", 
                        "board1", Task.TaskStatus.BACKLOG)));
        
        assertEquals(Done.getInstance(), outcome.getReplies().get(0));
        assertEquals("task1", ((TaskEvent.TaskCreated) outcome.events().get(0)).taskId);
        assertEquals("assignment", ((TaskEvent.TaskCreated) outcome.events().get(0)).title);
        assertEquals("blue", ((TaskEvent.TaskCreated) outcome.events().get(0)).color);
        assertEquals("lagom", ((TaskEvent.TaskCreated) outcome.events().get(0)).detail);
        assertEquals("board1", ((TaskEvent.TaskCreated) outcome.events().get(0)).boardId);
    }

    @Test
    public void testRejectDuplicateCreate() {
        PersistentEntityTestDriver<TaskCommand, TaskEvent, TaskState> driver = new PersistentEntityTestDriver<>(
                system, new TaskEntity(), "task-1");
        driver.run(new TaskCommand.CreateTask(new Task("task1", "assignment", "lagom", "blue",
                "board1", Task.TaskStatus.BACKLOG)));

        PersistentEntityTestDriver.Outcome<TaskEvent, TaskState> outcome = driver.run(
                new TaskCommand.CreateTask(new Task("task1", "assignment", "lagom", "blue",
                        "board1", Task.TaskStatus.BACKLOG)));
        assertEquals(PersistentEntity.InvalidCommandException.class, outcome.getReplies().get(0).getClass());
        assertEquals(Collections.emptyList(), outcome.events());
    }

    @Test
    public void testGetTask() {
        PersistentEntityTestDriver<TaskCommand, TaskEvent, TaskState> driver = new PersistentEntityTestDriver<>(
                system, new TaskEntity(), "task-1");
        Task task = new Task("task1", "assignment", "lagom", "blue",
                "board1", Task.TaskStatus.BACKLOG);
        driver.run(new TaskCommand.CreateTask(task));

        PersistentEntityTestDriver.Outcome<TaskEvent, TaskState> outcome = driver.run(new TaskCommand.GetTask());
        assertEquals(new TaskCommand.GetTaskReply(Optional.of(task)), outcome.getReplies().get(0));
        assertEquals(Collections.emptyList(), outcome.events());
    }

    @Test
    public void testChangeStatus() {
        PersistentEntityTestDriver<TaskCommand, TaskEvent, TaskState> driver = new PersistentEntityTestDriver<>(
                system, new TaskEntity(), "task-1");
        Task task = new Task("task1", "assignment", "lagom", "blue",
                "board1", Task.TaskStatus.BACKLOG);
        driver.run(new TaskCommand.CreateTask(task));

        PersistentEntityTestDriver.Outcome<TaskEvent, TaskState> outcome = driver.run(new TaskCommand.ChangeStatus(Task.TaskStatus.SCHEDULED));
        assertEquals(Task.TaskStatus.SCHEDULED, ((TaskEvent.StatusChanged) outcome.events().get(0)).status);
    }

    @Test
    public void testRejectChangeStatusFromBacklogToArchived() {
        PersistentEntityTestDriver<TaskCommand, TaskEvent, TaskState> driver = new PersistentEntityTestDriver<>(
                system, new TaskEntity(), "task-1");
        Task task = new Task("task1", "assignment", "lagom", "blue",
                "board1", Task.TaskStatus.BACKLOG);
        driver.run(new TaskCommand.CreateTask(task));

        PersistentEntityTestDriver.Outcome<TaskEvent, TaskState> outcome = driver.run(new TaskCommand.ChangeStatus(Task.TaskStatus.ARCHIVED));
        assertEquals(PersistentEntity.InvalidCommandException.class, outcome.getReplies().get(0).getClass());
        assertEquals(Collections.emptyList(), outcome.events());
    }

    @Test
    public void testUpdate() {
        PersistentEntityTestDriver<TaskCommand, TaskEvent, TaskState> driver = new PersistentEntityTestDriver<>(
                system, new TaskEntity(), "task-1");
        Task task = new Task("task1", "assignment", "lagom", "blue",
                "board1", Task.TaskStatus.BACKLOG);
        driver.run(new TaskCommand.CreateTask(task));

        PersistentEntityTestDriver.Outcome<TaskEvent, TaskState> outcome = driver.run(new TaskCommand.Update("New title", "New detail", "green"));
        assertEquals(Done.getInstance(), outcome.getReplies().get(0));
        assertEquals("New title", ((TaskEvent.InfoChanged) outcome.events().get(0)).title);
        assertEquals("New detail", ((TaskEvent.InfoChanged) outcome.events().get(0)).detail);
        assertEquals("green", ((TaskEvent.InfoChanged) outcome.events().get(0)).color);
    }

    @Test
    public void testRejectUpdateTitleOfArchivedTask() {
        PersistentEntityTestDriver<TaskCommand, TaskEvent, TaskState> driver = new PersistentEntityTestDriver<>(
                system, new TaskEntity(), "task-1");
        Task task = new Task("task1", "assignment", "lagom", "blue",
                "board1", Task.TaskStatus.BACKLOG);
        driver.run(new TaskCommand.CreateTask(task));
        driver.run(new TaskCommand.ChangeStatus(Task.TaskStatus.COMPLETED));
        driver.run(new TaskCommand.ChangeStatus(Task.TaskStatus.ARCHIVED));

        PersistentEntityTestDriver.Outcome<TaskEvent, TaskState> outcome = driver.run(new TaskCommand.Update("New title", "New detail", "green"));
        assertEquals(PersistentEntity.InvalidCommandException.class, outcome.getReplies().get(0).getClass());
        assertEquals(Collections.emptyList(), outcome.events());
    }

    @Test
    public void testRejectUpdateTitleWithSameContent() {
        PersistentEntityTestDriver<TaskCommand, TaskEvent, TaskState> driver = new PersistentEntityTestDriver<>(
                system, new TaskEntity(), "task-1");
        Task task = new Task("task1", "assignment", "lagom", "blue",
                "board1", Task.TaskStatus.BACKLOG);
        driver.run(new TaskCommand.CreateTask(task));

        PersistentEntityTestDriver.Outcome<TaskEvent, TaskState> outcome = driver.run(new TaskCommand.Update("assignment", "lagom","blue"));
        assertEquals(PersistentEntity.InvalidCommandException.class, outcome.getReplies().get(0).getClass());
        assertEquals(Collections.emptyList(), outcome.events());
    }

}