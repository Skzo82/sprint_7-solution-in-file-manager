package taskmanager.test;

import taskmanager.managers.FileBackedTaskManager;
import taskmanager.tasks.Task;
import taskmanager.tasks.Epic;
import taskmanager.tasks.Subtask;
import taskmanager.tasks.TaskStatus;

import org.junit.jupiter.api.Test;
import java.io.File;
import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest {
    private final File file = new File("test.csv"); // временный файл для тестирования
    private FileBackedTaskManager taskManager = new FileBackedTaskManager(file);

    @Test
    public void testSaveAndLoad() {
        Task task = new Task("Test Task", "This is a test task.");
        taskManager.addNewTask(task);

        Epic epic = new Epic("Test Epic", "Epic description");
        taskManager.addNewEpic(epic);

        Subtask subtask = new Subtask("Test Subtask", "Subtask description", epic.getId());
        taskManager.addNewSubtask(subtask);

        taskManager = FileBackedTaskManager.loadFromFile(file);

        assertNotNull(taskManager.getTask(task.getId()), "Task should be loaded.");
        assertNotNull(taskManager.getEpic(epic.getId()), "Epic should be loaded.");
        assertNotNull(taskManager.getSubtask(subtask.getId()), "Subtask should be loaded.");
    }
}
