package com.e.todolist;

import android.content.Context;

import com.e.todolist.models.Condition;
import com.e.todolist.models.Task;
import com.e.todolist.models.TasksPool;

import java.util.ArrayList;

public class DBHandler {
    private Context context;
    private ArrayList<Task> tasksList;
    private TasksPool tPool;
    private static final String POOL_NAME = "lisToDo";
    private static final String ACTUAL_COND = "ACTUAL TASKS"; //1
    private static final String OVERDUE_COND = "OVERDUE"; //2
    private static final String DONE_COND = "DONE"; //3
    private static final String TRASH_COND = "TRASH"; //4

    public DBHandler(Context context) {
        this.context = context;
        tasksList = new ArrayList<>();
        tPool = new TasksPool(POOL_NAME);

        initialDataFill();
    }

    public TasksPool gettPool() {
        return tPool;
    }

    public Context getContext() {
        return context;
    }

    public ArrayList<Task> getTasksList() {
        return tasksList;
    }

    public void initialDataFill() {
        Condition condActual = new Condition(ACTUAL_COND);
        Condition condOverdue = new Condition(OVERDUE_COND);
        Condition condDone = new Condition(DONE_COND);
        Condition condTrash = new Condition(TRASH_COND);

        condActual.addTask(new Task(1, "Task 1", "Description for task 1", 1, "2020-04-08 09:00:00.0", false, false));
        condActual.addTask(new Task(2, "Task 2", "Description for task 2", 1, "2020-04-08 09:30:00.0", true, false));
        condActual.addTask(new Task(3, "Task 3", "Description for task 3", 1, "2020-04-08 10:00:00.0", false, false));

        condOverdue.addTask(new Task(4, "Task 4", "Description for task 4", 2, "2020-04-06 09:00:00.0", true, false));
        condOverdue.addTask(new Task(5, "Task 5", "Description for task 5", 2, "2020-04-07 09:00:00.0", false, false));

        condDone.addTask(new Task(6, "Task 6", "Description for task 6", 3, "2020-04-05 09:00:00.0", false, false));
        condDone.addTask(new Task(7, "Task 7", "Description for task 7", 3, "2020-04-04 07:00:00.0", false, false));

        condTrash.addTask(new Task(8,"Task 8", "Description for task 8", 4, "2020-04-06 09:00:00.0", true, false));

        tPool.addCondition(condActual);  tPool.addCondition(condOverdue);
        tPool.addCondition(condDone);  tPool.addCondition(condTrash);
    }
}
