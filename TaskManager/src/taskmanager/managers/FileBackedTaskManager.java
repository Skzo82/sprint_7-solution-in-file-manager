package taskmanager.managers;

import taskmanager.tasks.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    @Override
    public int addNewTask(Task task) {
        int id = super.addNewTask(task);
        save();
        return id;
    }

    @Override
    public int addNewEpic(Epic epic) {
        int id = super.addNewEpic(epic);
        save();
        return id;
    }

    @Override
    public int addNewSubtask(Subtask subtask) {
        int id = super.addNewSubtask(subtask);
        save();
        return id;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void removeTask(int id) {
        super.removeTask(id);
        save();
    }

    @Override
    public void removeEpic(int id) {
        super.removeEpic(id);
        save();
    }

    @Override
    public void removeSubtask(int id) {
        super.removeSubtask(id);
        save();
    }

    private void save() {
        StringBuilder sb = new StringBuilder();
        sb.append("id,type,name,status,description,epic
                ");

        for (Task task : getTasks()) {
            sb.append(toString(task)).append("\n");
        }

        for (Epic epic : getEpics()) {
            sb.append(toString(epic)).append("\n");
        }

        for (Subtask subtask : getSubtasks()) {
            sb.append(toString(subtask)).append("\n");
        }

        try {
            Files.writeString(file.toPath(), sb.toString());
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения менеджера в файл", e);
        }
    }

    private String toString(Task task) {
        if (task instanceof Subtask) {
            Subtask subtask = (Subtask) task;
            return String.format("%d,SUBTASK,%s,%s,%s,%d", task.getId(), task.getName(), task.getStatus(), task.getDescription(), subtask.getEpicId());
        } else if (task instanceof Epic) {
            return String.format("%d,EPIC,%s,%s,%s,", task.getId(), task.getName(), task.getStatus(), task.getDescription());
        } else {
            return String.format("%d,TASK,%s,%s,%s,", task.getId(), task.getName(), task.getStatus(), task.getDescription());
        }
    }

    private static Task fromString(String value) {
        String[] fields = value.split(",");
        int id = Integer.parseInt(fields[0]);
        String type = fields[1];
        String name = fields[2];
        String status = fields[3];
        String description = fields[4];
        int epicId = type.equals("SUBTASK") ? Integer.parseInt(fields[5]) : -1;

        Task task;
        switch (type) {
            case "TASK":
                task = new Task(id, name, description);
                break;
            case "EPIC":
                task = new Epic(id, name, description);
                break;
            case "SUBTASK":
                task = new Subtask(name, description, epicId);
                task.setId(id);
                break;
            default:
                throw new IllegalArgumentException("Unknown task type: " + type);
        }

        task.setStatus(TaskStatus.valueOf(status));
        return task;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        try {
            List<String> lines = Files.readAllLines(Paths.get(file.getPath()));
            for (String line : lines.subList(1, lines.size())) {
                Task task = fromString(line);
                if (task instanceof Epic) {
                    manager.addNewEpic((Epic) task);
                } else if (task instanceof Subtask) {
                    manager.addNewSubtask((Subtask) task);
                } else {
                    manager.addNewTask(task);
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка загрузки менеджера из файла", e);
        }
        return manager;
    }
}
