# kanban
A simple implementation of Kanban board (backend only), using Lagom framework for microservices architecture.

Some examples of intended usage (on Unix):

```curl http://localhost:9000/api/boards -H "Content-Type: application/json" -X POST -d '{"boardId":"b1", “title":"assignment 1"}'``` :  creates a new board with id b1 and title assignment 1, board’s status is automatically initialized to CREATED

```curl http://localhost:9000/api/boards/b1``` : gets the information of board b1 if it was created, “Not found” otherwise

```curl http://localhost:9000/api/boards/b1/updateTitle -H "Content-Type: application/json" -X POST -d '{“title":"assignment 2”}'``` : updates the title of the board b1 to “assignment 2”. Note that if new title and old title are the same, or the current status is ARCHIVED, the action would return invalidCommand error

```curl http://localhost:9000/api/boards/b1/task -H "Content-Type: application/json" -X POST -d '{"boardId":"b1", “taskId”: “kanban”, “description”: “lagom”, “color”: “blue”, “title”:”assignment 1”}``` : creates a new task with id kanban for the board b1, besides the provided attributes, status of a task is BACKLOG

And there are some more, which were declared in BoardService.java file of Board-api module.

This implementation does not include front-end part, an example of Kanban front-end can be found here https://github.com/rhumbertgz/kanban-board.
