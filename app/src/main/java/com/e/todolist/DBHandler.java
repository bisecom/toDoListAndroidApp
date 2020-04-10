package com.e.todolist;

import android.content.Context;
import android.util.Log;
import com.e.todolist.models.Condition;
import com.e.todolist.models.Task;
import com.e.todolist.models.TasksPool;
import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.exceptions.RealmException;

public class DBHandler {
    private Context context;
    private ArrayList<Task> tasksList;
    private TasksPool tPool;

    private Realm realm;
    private RealmConfiguration config;
    private static final String DB_NAME = "tasks_list.realm";

    private Condition condActual, condOverdue, condDone, condTrash;

    private static final String POOL_NAME = "lisToDo";
    private static final String ACTUAL_COND = "ACTUAL TASKS"; //1
    private static final String OVERDUE_COND = "OVERDUE"; //2
    private static final String DONE_COND = "DONE"; //3
    private static final String TRASH_COND = "TRASH"; //4

    public DBHandler(Context context) {
        this.context = context;
        tasksList = new ArrayList<>();
        tPool = new TasksPool(POOL_NAME);
        condActual = new Condition(ACTUAL_COND);
        condOverdue = new Condition(OVERDUE_COND);
        condDone = new Condition(DONE_COND);
        condTrash = new Condition(TRASH_COND);

        Realm.init(context);
        config = new RealmConfiguration.Builder().name(DB_NAME).build();
        Realm.setDefaultConfiguration(config);

        try {
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            Task task = realm.where(Task.class).findFirst();
            realm.commitTransaction();
            realm.close();
            if (task == null) {
                initialDataFill();
            }
        } catch (RealmException ex) {
            Log.d("Debug", "Error on creation " + ex);
        }
        readDataFromDb();
    }

    public TasksPool gettPool() {
        return tPool;
    }

    public Context getContext() {
        return context;
    }

    public int addTask(Task task){
        realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        Number maxId = realm.where(Task.class).max("id");
        int nextId = (maxId == null) ? 1 : maxId.intValue() + 1;
        task.setId(nextId);
        realm.insert(task);
        realm.commitTransaction();
        realm.close();
        return nextId;
    }



    public void initialDataFill() {
       /* Condition condActual = new Condition(ACTUAL_COND);
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
        tPool.addCondition(condDone);  tPool.addCondition(condTrash);*/

        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(new Task(0, "Task 1", "Description for task 1", 1, "2020-04-08 09:00:00.0", false, false));
        tasks.add(new Task(0, "Task 2", "Description for task 2", 1, "2020-04-08 09:30:00.0", true, false));
        tasks.add(new Task(0, "Task 3", "Description for task 3", 1, "2020-04-08 10:00:00.0", false, false));
        tasks.add(new Task(0, "Task 4", "Description for task 4", 2, "2020-04-06 09:00:00.0", true, false));
        tasks.add(new Task(0, "Task 5", "Description for task 5", 2, "2020-04-07 09:00:00.0", false, false));
        tasks.add(new Task(0, "Task 6", "Description for task 6", 3, "2020-04-05 09:00:00.0", false, false));
        tasks.add(new Task(0, "Task 7", "Description for task 7", 3, "2020-04-04 07:00:00.0", false, false));
        tasks.add(new Task(0, "Task 8", "Description for task 8", 4, "2020-04-06 09:00:00.0", true, false));

        realm = Realm.getDefaultInstance();
        for (int i = 0; i < tasks.size(); i++) {
            realm.beginTransaction();
            Number maxId = realm.where(Task.class).max("id");
            int nextId = (maxId == null) ? 1 : maxId.intValue() + 1;
            Task temp = tasks.get(i);
            temp.setId(nextId);
            realm.insert(temp);
            realm.commitTransaction();
        }
        realm.close();

    }

    public void readDataFromDb(){
        realm = Realm.getDefaultInstance();
        RealmResults<Task> results = realm.where(Task.class).findAll();
        ArrayList<Task> tasksList = new ArrayList<>();
        tasksList.addAll(realm.copyFromRealm(results));
        realm.close();

        for(Task task : tasksList){
            if(task.getCondition() == 1){
                condActual.addTask(task);
            }
            if(task.getCondition() == 2){
                condOverdue.addTask(task);
            }
            if(task.getCondition() == 3){
                condDone.addTask(task);
            }
            if(task.getCondition() == 4){
                condTrash.addTask(task);
            }
        }
        tPool.addCondition(condActual);  tPool.addCondition(condOverdue);
        tPool.addCondition(condDone);  tPool.addCondition(condTrash);
    }

}