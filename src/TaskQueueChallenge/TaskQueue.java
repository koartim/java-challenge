package TaskQueueChallenge;

import java.util.*;

public class TaskQueue {
    private Map<String, Task> tasks; // stores each task by its taskId
    private Map<String, Set<String>> dependencyGraph; // maps each taskId to its dependencies
    private PriorityQueue<Task> taskQueue; // priorityQueue to store tasks by priority
    private Map<String, List<Task>> nodeAssignments; // stores the tasks assigned to each node
    private Set<String> availableNodes; // set of availableNodes for task assignment

    public TaskQueue() {
        this.tasks = new HashMap<>();
        this.dependencyGraph = new HashMap<>();
        // priorityQueue set to use a MAX HEAP structure so that higher priority tasks execute first
        this.taskQueue = new PriorityQueue<>((task1, task2) -> task2.priority - task1.priority);
        // using linkedHashMap and linkedHashSet to preserve insertion order for node assigment
        this.nodeAssignments = new LinkedHashMap<>();
        this.availableNodes = new LinkedHashSet<>();
    }
    // adds a new task to the graph and queue
    public void addTask(String taskId, int priority, List<String> dependencies) {
        // if we already have a task with the same taskId, reject this insertion
        if (tasks.containsKey(taskId)) {
            throw new IllegalArgumentException("Task already exists! All task ids must be unique!");
        }
        // check to see that the new task has valid dependencies, if it has a taskId in its dependency list that doesn't exist in our graph don't add the task
        for (String dependency : dependencies) {
            if (!tasks.containsKey(dependency)) {
                throw new IllegalArgumentException("Task has a dependency: " + dependency + " that does not exist as a task in our system");
            }
        }
        // create the task
        Task task = new Task(taskId, priority, dependencies);

        // add the task to the tasks map
        tasks.put(taskId, task);

        // add the task's dependencies the dependencyGraph, each taskId maps to a set of its dependencies {"A", [], "B": ["A"], "C": ["B"] }
        dependencyGraph.put(taskId, new HashSet<>(dependencies));

        // add the task to the priorityQueue, using offer instead of add to gracefully handle capacity failures
        taskQueue.add(task);
    }

    // removes a task and cleans up any references in the queue and graph
    public void removeTask(String taskId) {
        // remove task from the tasks map
        tasks.remove(taskId);

        // remove the tasks' dependencies from the dependency graph
        dependencyGraph.remove(taskId);

        // Remove the task from other tasks' dependency lists.
        // If the task is gone,no other task should have it in their list of dependencies
        for (String key : dependencyGraph.keySet()) {
            dependencyGraph.get(key).remove(taskId);
        }

        // remove the task from the queue
        taskQueue.removeIf(task -> task.taskId.equals(taskId));

        // remove the task from the dependency lists of other tasks in the queue
        for (Task task : taskQueue) {
            task.dependencies.remove(taskId);
        }
    }

    // removes an existing task and re-adds it with new priority and dependencies
    public void updateTask(String taskId, int priority, List<String> dependencies) {
        // remove the existing task
        removeTask(taskId);
        // re add the task with updated values
        addTask(taskId, priority, dependencies);
    }
    // returns a list of taskIds in their correct execution order, with respect to their dependencies and priority
    // tasks must wait for their dependencies to complete before execution
    public List<String> getExecutionOrder() {
        // list of taskIds we will return at the end
        List<String> executionOrder = new ArrayList<>();

        // map to keep track of the unmet dependencies a task has, tasks with an empty list can be executed
        Map<String, Integer> dependencyCount = new HashMap<>();

        // populate dependencyCount with the number of dependencies for each task
        for (Task task : tasks.values()) {
            dependencyCount.put(task.taskId, task.dependencies.size());
        }

        // max heap (higher priority first) priority queue to store tasks that are ready to be executed (tasks with no unmet dependencies)
        Queue<Task> queue = new PriorityQueue<>((task1, task2) -> task2.priority - task1.priority);
        // iterate through the dependencyCount map we created, and add only those tasks without dependencies to our queue.
        for (Task task : tasks.values()) {
        // if the dependency count for the task == 0, it has no unmet dependencies and ready to be executed and can be added to the queue
            if (dependencyCount.get(task.taskId) == 0) {
                queue.add(task);
            }
        }
        // the highest priority task will always be at the head
        while (!queue.isEmpty()) {
            // poll() will extract the task at the top of our heap and remove it
            Task highestPriorityTask = queue.poll();
            // add this task's id to the execution list
            executionOrder.add(highestPriorityTask.taskId);
            // update the dependency count for other tasks that depend on the completed task
            for (String taskId : dependencyGraph.keySet()) {
                // if the newly removed highestPriorityTask is in their dependency list
                if (dependencyGraph.get(taskId).contains(highestPriorityTask.taskId)) {
                    // decrement the unmet dependencyCount
                    int newCount = dependencyCount.get(taskId) - 1;
                    dependencyCount.put(taskId, newCount);
                    // if the newCount is 0, all dependencies are met
                    if (newCount == 0) {
                        //add the task to the queue for processing
                        queue.add(tasks.get(taskId));
                    }
                }
            }
        }
        return executionOrder;
    }
    // distributes tasks across available nodes in a round-robin fashion, ensuring that each node
    // is assigned a balanced workload. Tasks are assigned based on execution order, considering
    // priority and dependencies
    public void assignTasksToNodes() {
        // init nodeAssignments with an empty list for each node
        for (String nodeId : availableNodes) {
            nodeAssignments.put(nodeId, new ArrayList<>());
        }
        // get the execution order for the tasks
        List<String> executionOrder = getExecutionOrder();
        // iterator so we can move through the list via .next();
        Iterator<String> nodeIterator = availableNodes.iterator();
        for (String taskId : executionOrder) {
            // if the current node has no .next()...
            if (!nodeIterator.hasNext()) {
                // we are at the last node and need to reset to the first node. This way if we have more tasks remaining
                // they will get assigned evenly across all nodes
                nodeIterator = availableNodes.iterator();
            }
            // otherwise, retrieve the nodes assignmentList and add the current taskId to it
            String nodeId = nodeIterator.next();
            nodeAssignments.get(nodeId).add(tasks.get(taskId));
        }
    }

    // Handles the failure of a node by reassigning its tasks to the task queue for redistribution
    private void handleNodeFailure(String currentNodeId) {
        // remove the failed node's task assignments from the nodeAssignments map
        List<Task> failedTasks = nodeAssignments.remove(currentNodeId);
        // if there are any tasks assigned to the failed node...
        if (failedTasks != null) {
            // Re-add each task from the failed node back into the task queue
            for (Task task : failedTasks) {
                taskQueue.add(task);
            }
            // reassign tasks across the remaining available nodes
            assignTasksToNodes();
        }
    }

    // Helper method: Add a node to the system
    public void addNode(String nodeId) {
        availableNodes.add(nodeId);
    }
    // Method to remove a specified node from the pool of available nodes
    public void removeNode(String nodeId) {
        availableNodes.remove(nodeId);
        // ensure that any tasks assigned to this removed node are reassigned amongst the remaining nodes
        handleNodeFailure(nodeId);
    }
}
