import TaskQueueChallenge.TaskQueue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        TaskQueue taskQueue = new TaskQueue();

        taskQueue.addTask("A", 3, Collections.emptyList());
        taskQueue.addTask("B", 2, Arrays.asList("A"));
        taskQueue.addTask("C", 1, Arrays.asList("A"));
        taskQueue.addTask("D", 3, Arrays.asList("B", "C"));
        taskQueue.addTask("E", 2, Arrays.asList("C", "D"));

        taskQueue.updateTask("D", 2, Arrays.asList("B"));

        taskQueue.addNode("node1");
        taskQueue.addNode("node2");
        taskQueue.addNode("node3");

        taskQueue.removeNode("node2");

        taskQueue.assignTasksToNodes();

        List<String> executionOrder = taskQueue.getExecutionOrder();
        System.out.println("Execution Order: " + executionOrder);

        SinglyLinkedList list = new SinglyLinkedList();

        list.push(100);
        list.push(200);
        list.push(300);
        list.push(3);

        list.remove(3);
        list.insert(2, 1);

        list.printList();

        list.reverse();

        list.printList();
    }
}