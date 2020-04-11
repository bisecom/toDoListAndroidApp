package com.e.todolist;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.e.todolist.models.Condition;
import com.e.todolist.models.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ExpandableListView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private DBHandler mDbHandler;
    private ExpandableListView exListView;
    static final String ACCESS_MESSAGE = "ACCESS_MESSAGE";
    private static  final int REQUEST_ACCESS_TYPE_ADD = 1;
    private static  final int REQUEST_ACCESS_TYPE_EDIT = 2;
    private ExListAdapter adapter;
    private Runnable overdueChecker;
    private Thread threadWorker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDbHandler = new DBHandler(this);
        exListView = findViewById(R.id.expListView);
        adapter = new ExListAdapter(this, mDbHandler.gettPool());
        exListView.setAdapter(adapter);

        runBackgroundThread();
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        int conditionsInWork, condOutOfWork, condToApply, condIndex;
        ArrayList<Task> list;
        switch(id){
            case R.id.action_create :
                Intent intent = new Intent(this, TaskAddingActivity.class);
                startActivityForResult(intent, REQUEST_ACCESS_TYPE_ADD);
                return true;
            case R.id.action_delete:
                list = adapter.deletingTasks();
                if(list != null && list.size() != 0){
                    mDbHandler.removeTasks(list);
                    list.clear();
                }
                return true;
            case R.id.action_edit:
                Task task = adapter.getTaskEditing();
                if(task == null){
                    Toast.makeText(this, "Check one task only!",Toast.LENGTH_LONG).show();
                    break;
                }
                Intent intentEdit = new Intent(this, TaskAddingActivity.class);
                intentEdit.putExtra(Task.class.getSimpleName(), task);
                startActivityForResult(intentEdit, REQUEST_ACCESS_TYPE_EDIT);
            return true;
            case R.id.action_done:
                conditionsInWork = 3; condOutOfWork = 2; condToApply = 3; condIndex = 2;
                list = adapter.moveToCondition(conditionsInWork, condOutOfWork, condToApply, condIndex);
                if(list != null && list.size() != 0){
                    mDbHandler.moveTasksToCondition(list, condToApply);
                    list.clear();
                }
                return true;
            case R.id.action_to_trash:
                conditionsInWork = 4; condOutOfWork = 3; condToApply = 4; condIndex = 3;
                list = adapter.moveToCondition(conditionsInWork, condOutOfWork, condToApply, condIndex);
                if(list != null && list.size() != 0){
                    mDbHandler.moveTasksToCondition(list, condToApply);
                    list.clear();
                }
                return true;
            case R.id.action_clear_trash:
                adapter.clearTrash();
                mDbHandler.clearTrash();
                return true;
            case R.id.action_restore_trash:
                conditionsInWork = 3; condOutOfWork = 4; condToApply = 1; condIndex = 0;
                list = adapter.moveToCondition(conditionsInWork, condOutOfWork, condToApply, condIndex);
                if(list != null && list.size() != 0){
                    mDbHandler.moveTasksToCondition(list, condToApply);
                    list.clear();
                    runBackgroundThread();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ACCESS_TYPE_ADD) {
            if (resultCode == RESULT_OK) {
                Task task = (Task) data.getSerializableExtra(ACCESS_MESSAGE);
                int id = mDbHandler.addTask(task);
                task.setId(id);
                adapter.addingTask(task);

                runBackgroundThread();
            }
        }
        if (requestCode == REQUEST_ACCESS_TYPE_EDIT) {
            if (resultCode == RESULT_OK) {
                Task task = (Task) data.getSerializableExtra(ACCESS_MESSAGE);
                adapter.editTask(task);
                mDbHandler.editTask(task);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case  R.id.fab: {
                Intent intent = new Intent(this, TaskAddingActivity.class);
                startActivityForResult(intent, REQUEST_ACCESS_TYPE_ADD);
                break;
            }
        }
    }

    public class CustomRunnable implements Runnable {
        private ArrayList<Task>activeTasks;
        private volatile boolean isShutdown;
        CustomRunnable(ArrayList<Task>tasksList) {
            this.activeTasks = tasksList;
            isShutdown = false;
        }

        @Override
        public void run() {
            Message msg = null;
            try {
                while (msg == null /*|| !isShutdown*/) {
                    Thread.sleep(10000);
                    Calendar curTimeAndDate = new GregorianCalendar();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    dateFormat.setTimeZone(curTimeAndDate.getTimeZone());
                    long millisCurrTimeAndDate = curTimeAndDate.getTimeInMillis();

                    for (Task task : activeTasks) {
                        long taskTimeMill = mDbHandler.dateConvertToMillis(task.getPlacementTime());
                        if (millisCurrTimeAndDate > taskTimeMill && taskTimeMill != 0) {
                            msg = Message.obtain();
                            msg.obj = task;
                            myHandle.sendMessage(msg);
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                new Exception("Something bad happened in background thread.");
            }
        }

       /* public void interrupt() {
            isShutdown = true;
        }*/
    }

    Handler myHandle = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            int overdueCond = 2;
            Task taskToMove = (Task)msg.obj;
            if(taskToMove != null) {
                taskToMove.setCondition(overdueCond);
                adapter.moveToOverdue(taskToMove);
                mDbHandler.editTask(taskToMove);
            }
            return false;
        }
    });

    public void runBackgroundThread() {
        if (overdueChecker != null && threadWorker != null) {
            threadWorker.interrupt();
            overdueChecker = null;
        }
        overdueChecker = new CustomRunnable(adapter.tPool.getConditions().get(0).getTasks());
        threadWorker = new Thread(overdueChecker);
        threadWorker.start();
    }

}
