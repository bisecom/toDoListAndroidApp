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

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ExpandableListView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private DBHandler mDbHandler;
    private ExpandableListView exListView;
    static final String ACCESS_MESSAGE = "ACCESS_MESSAGE";
    private static  final int REQUEST_ACCESS_TYPE_ADD = 1;
    private static  final int REQUEST_ACCESS_TYPE_EDIT = 2;
    private ExListAdapter adapter;

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

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
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
        switch(id){
            case R.id.action_create :
                Intent intent = new Intent(this, TaskAddingActivity.class);
                startActivityForResult(intent, REQUEST_ACCESS_TYPE_ADD);
                return true;
            case R.id.action_delete:
                adapter.deletingTasks();
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
                adapter.moveToDone();
                return true;
            case R.id.action_to_trash:
                adapter.moveToTrash();
                return true;
            case R.id.action_clear_trash:
                adapter.clearTrash();
                return true;
            case R.id.action_restore_trash:

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ACCESS_TYPE_ADD) {
            if (resultCode == RESULT_OK) {
                Task task = (Task) data.getSerializableExtra(ACCESS_MESSAGE);
                int id = /*dbWorker.addStudent(newStudent)*/ 0;
                task.setId(id);
                adapter.addingTask(task);
            }
        }
            if (requestCode == REQUEST_ACCESS_TYPE_EDIT) {
                if (resultCode == RESULT_OK) {
                    Task task = (Task) data.getSerializableExtra(ACCESS_MESSAGE);
                    adapter.editTask(task);
                }
            } else {
                super.onActivityResult(requestCode, resultCode, data);
            }


    }
}
