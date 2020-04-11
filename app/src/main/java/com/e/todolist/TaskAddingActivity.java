package com.e.todolist;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TimePicker;
import androidx.appcompat.app.AppCompatActivity;
import com.e.todolist.models.Task;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TaskAddingActivity extends AppCompatActivity {

    private EditText subjET, descrET;
    private Boolean isImportant;
    private TextView currentDateTime;
    private Calendar dateAndTime;
    private int initialId = -1;
    private int taskCondition = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_adding);
        dateAndTime = Calendar.getInstance();
        subjET = findViewById(R.id.subject);
        descrET = findViewById(R.id.description);
        currentDateTime = findViewById(R.id.currentDateTime);
        setInitialDateTime();

        Bundle arguments = getIntent().getExtras();
        if(arguments!= null){
            Task task = (Task) arguments.getSerializable(Task.class.getSimpleName());
            subjET.setText(task.getSubject());
            descrET.setText(task.getDescription());
            currentDateTime.setText(task.getPlacementTime());
            initialId = task.getId();
            taskCondition = task.getCondition();
        }
    }

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        switch (view.getId()) {
            case R.id.importantRB:
                if (checked) {
                    isImportant = true;
                }
                break;
            case R.id.normalRB:
                if (checked) {
                    isImportant = false;
                }
                break;
        }
    }

    public void onButtonClick(View view){
        switch (view.getId()) {
            case R.id.saveClick:
                String subjStr = subjET.getText().toString();
                String descrStr = descrET.getText().toString();
                String taskTime = currentDateTime.getText().toString();
                Task task = new Task(initialId == -1 ? -1 : initialId, subjStr, descrStr, taskCondition == -1 ? 1 : taskCondition, taskTime, isImportant, false);
                Intent intentFilled = new Intent();
                intentFilled.putExtra(MainActivity.ACCESS_MESSAGE, task);
                setResult(RESULT_OK, intentFilled);
                finish();
                break;
            case R.id.cancelClick:
                Intent initialIntent = new Intent(this, MainActivity.class);
                setResult(RESULT_CANCELED, initialIntent);
                finish();
                break;
        }
    }

    // отображаем диалоговое окно для выбора даты
    public void setDate(View v) {
        new DatePickerDialog(this, d,
                dateAndTime.get(Calendar.YEAR),
                dateAndTime.get(Calendar.MONTH),
                dateAndTime.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    // отображаем диалоговое окно для выбора времени
    public void setTime(View v) {
        new TimePickerDialog(this, t,
                dateAndTime.get(Calendar.HOUR_OF_DAY),
                dateAndTime.get(Calendar.MINUTE), true)
                .show();
    }
    // установка начальных даты и времени
    private void setInitialDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        dateFormat.setTimeZone(dateAndTime.getTimeZone());
        //System.out.println(dateFormat.format(dateAndTime.getTime()));
        //String dateTime = dateFormat.format(dateAndTime.getTime());
        currentDateTime.setText(dateFormat.format(dateAndTime.getTime()));
        /*currentDateTime.setText(DateUtils.formatDateTime(this,
                dateAndTime.getTimeInMillis(),
                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR
                        | DateUtils.FORMAT_SHOW_TIME));*/
    }

    // установка обработчика выбора времени
    TimePickerDialog.OnTimeSetListener t=new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            dateAndTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            dateAndTime.set(Calendar.MINUTE, minute);
            setInitialDateTime();
        }
    };

    // установка обработчика выбора даты
    DatePickerDialog.OnDateSetListener d=new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dateAndTime.set(Calendar.YEAR, year);
            dateAndTime.set(Calendar.MONTH, monthOfYear);
            dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            setInitialDateTime();
        }
    };


}
