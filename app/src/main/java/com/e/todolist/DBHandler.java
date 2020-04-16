package com.e.todolist;

import android.content.Context;
import android.util.Log;
import com.e.todolist.models.Condition;
import com.e.todolist.models.Task;
import com.e.todolist.models.TasksPool;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

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

    public void editTask(Task task) {
        realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        Task temp = realm.where(Task.class).equalTo("id", task.getId()).findFirst();
        if(temp != null) {
            temp.setSubject(task.getSubject());
            temp.setDescription(task.getDescription());
            temp.setPlacementTime(task.getPlacementTime());
            temp.setCondition(task.getCondition());
            temp.setImportant(task.getImportant());

            realm.insertOrUpdate(temp);
        }
        realm.commitTransaction();
        realm.close();
    }

    public void moveTasksToCondition(ArrayList<Task> tasksList, int condToApply){
        realm = Realm.getDefaultInstance();
        for(Task task : tasksList) {
            realm.beginTransaction();
            Task temp = realm.where(Task.class).equalTo("id", task.getId()).findFirst();
            if (temp != null) {
                temp.setCondition(condToApply);
                realm.insertOrUpdate(temp);
            }
            realm.commitTransaction();
        }
        realm.close();
    }

    public void clearTrash() {
        int trashCond = 4;
        realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        RealmResults<Task> result = realm.where(Task.class).equalTo("condition", trashCond).findAll();
        if (result != null) {
            result.deleteAllFromRealm();
        }
        realm.commitTransaction();
        realm.close();
    }

    public void removeTasks(ArrayList<Task> tasksList){
        realm = Realm.getDefaultInstance();
        for(Task task : tasksList) {
            realm.beginTransaction();
            RealmResults<Task> result = realm.where(Task.class).equalTo("id",task.getId()).findAll();
            result.deleteAllFromRealm();
            realm.commitTransaction();
        }
        realm.close();
    }

    public void initialDataFill() {
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(new Task(0, "Task 1", "Description for task 1", 1, "2020-04-13 20:00", false, false));
        tasks.add(new Task(0, "Task 2", "Description for task 2", 1, "2020-04-12 11:55", true, false));
        tasks.add(new Task(0, "Task 3", "Description for task 3", 1, "2020-04-12 12:00", false, false));
        tasks.add(new Task(0, "Task 4", "Description for task 4", 2, "2020-04-06 09:00", true, false));
        tasks.add(new Task(0, "Task 5", "Description for task 5", 2, "2020-04-07 09:00", false, false));
        tasks.add(new Task(0, "Task 6", "Description for task 6", 3, "2020-04-05 09:00", false, false));
        tasks.add(new Task(0, "Task 7", "Description for task 7", 3, "2020-04-04 07:00", false, false));
        tasks.add(new Task(0, "Task 8", "Description for task 8", 4, "2020-04-06 09:00", true, false));

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

        Calendar curTimeAndDate = new GregorianCalendar();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        dateFormat.setTimeZone(curTimeAndDate.getTimeZone());
        long millisCurrTimeAndDate = curTimeAndDate.getTimeInMillis();

        for (Task task : tasksList) {
            if (task.getCondition() == 2) {
                condOverdue.addTask(task);
            }
            if (task.getCondition() == 3) {
                condDone.addTask(task);
            }
            if (task.getCondition() == 4) {
                condTrash.addTask(task);
            }
            if (task.getCondition() == 1) {
                long millisTaskTime = dateConvertToMillis(task.getPlacementTime());
                if (millisCurrTimeAndDate > millisTaskTime) {
                    task.setCondition(2);
                    condOverdue.addTask(task);
                    editTask(task);
                } else {
                    condActual.addTask(task);
                }
            }
        }
        tPool.addCondition(condActual);
        tPool.addCondition(condOverdue);
        tPool.addCondition(condDone);
        tPool.addCondition(condTrash);
    }

    public long dateConvertToMillis(String dateAndTime){
        Calendar taskDate = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            if(dateFormat.parse(dateAndTime) != null) {
                taskDate.setTime(dateFormat.parse(dateAndTime));
                return taskDate.getTimeInMillis();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

}