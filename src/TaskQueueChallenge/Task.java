package TaskQueueChallenge;

import java.util.ArrayList;
import java.util.List;

public class Task {
    String taskId; // unique identifier for each task
    int priority; // int value that represents the tasks priority (3 == HIGH, 2 == MEDIUM, 1 == LOW)
    List<String> dependencies; // list of taskIds representing the tasks that must be completed before a task can execute

    public Task(String taskId, int priority, List<String> dependencies) {
        this.taskId = taskId;
        this.priority = priority;
        this.dependencies = new ArrayList<>(dependencies);
    }

    @Override
    public String toString() {
        return "Task{" +
                "taskId='" + taskId + '\'' +
                ", priority=" + priority +
                ", dependencies=" + dependencies +
                '}';
    }
}
