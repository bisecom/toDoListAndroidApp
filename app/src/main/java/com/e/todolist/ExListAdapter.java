package com.e.todolist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.e.todolist.models.Condition;
import com.e.todolist.models.Task;
import com.e.todolist.models.TasksPool;

import java.util.ArrayList;

public class ExListAdapter extends BaseExpandableListAdapter {
    public Context context;
    private LayoutInflater vi;
    public TasksPool tPool;

    public ExListAdapter(Context context, TasksPool tPool)
    {
        this.context = context;
        this.tPool = tPool;
        vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getGroupCount() {
        return tPool.getConditions().size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return tPool.getConditions().get(groupPosition).getTasks().size();
    }

    @Override
    public Condition getGroup(int groupPosition) {
        return tPool.getConditions().get(groupPosition);
    }

    @Override
    public Task getChild(int groupPosition, int childPosition) {
        Task task = tPool.getConditions().get(groupPosition).getTasks().get(childPosition);
        return task;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        View resultView = vi.inflate(R.layout.condition_item, null);
        TextView text = resultView.findViewById(R.id.groupName);
        Condition condiotion = tPool.getConditions().get(groupPosition);

        text.setText(condiotion.getTitle());
        return resultView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        View resultView = vi.inflate(R.layout.task_list_item, null);
        ImageView imageView = resultView.findViewById(R.id.conditionIcon);
        TextView subjTv = resultView.findViewById(R.id.subject);
        TextView  descrTv = resultView.findViewById(R.id.description);
        LinearLayout taskLl = resultView.findViewById(R.id.taskLine);
        CheckBox isCheckedCB = resultView.findViewById(R.id.isChecked);

        Task mTask = tPool.getConditions().get(groupPosition).getTasks().get(childPosition);
        subjTv.setText(mTask.getSubject() + " | " + mTask.getPlacementTime().substring(2));
        descrTv.setText(mTask.getDescription());
        taskLl.setTag(tPool.getConditions().get(groupPosition).getTitle());
        isCheckedCB.setOnCheckedChangeListener(myCheckChangeList);
        isCheckedCB.setTag(childPosition);
        isCheckedCB.setChecked(mTask.getChecked());
        imageView.setImageResource(mTask.getImportant() ? R.drawable.important_attn : R.drawable.normal_attn);

        return resultView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    CompoundButton.OnCheckedChangeListener myCheckChangeList = new CompoundButton.OnCheckedChangeListener() {
        public void onCheckedChanged(CompoundButton buttonView,
                                     boolean isChecked) {
            int taskIndex = (Integer) buttonView.getTag();
            View v = (View) buttonView.getParent();
            //TextView tv = v.findViewById(R.id.groupName);
            LinearLayout lv = v.findViewById(R.id.taskLine);

            String condTemp = lv.getTag().toString();
            for (Condition cond : tPool.getConditions()) {
                if (cond.getTitle().equals(condTemp)) {
                    cond.getTasks().get(taskIndex).setChecked(isChecked);
                }
            }
        }
    };

    @Override
    public void notifyDataSetChanged()
    {
        super.notifyDataSetChanged();
    }

    public ArrayList<Task> deletingTasks() {
        ArrayList<Task> list = new ArrayList<>();
        for (Condition cond : tPool.getConditions()) {
            for (int i = cond.getTasks().size() - 1; i >= 0; i--) {
                if (cond.getTasks().get(i).getChecked().equals(true)) {
                    list.add(cond.getTasks().get(i));
                    cond.getTasks().remove(i);
                }
            }
        }
        this.notifyDataSetChanged();
        return list;
    }

    public void addingTask(Task task) {
        task.setId(tPool.getConditions().get(0).getTasks().size()
        + tPool.getConditions().get(1).getTasks().size()
        + tPool.getConditions().get(2).getTasks().size()
        + tPool.getConditions().get(3).getTasks().size() + 1);

        tPool.getConditions().get(0).getTasks().add(task);
        this.notifyDataSetChanged();
    }

    public Task getTaskEditing() {
        int counter = 0;
        Task task = null;
        for (Condition c : tPool.getConditions()) {
            for (Task t : c.getTasks()) {
                if (t.getChecked()) {
                    counter++;
                    task = t;
                }
            }
        }
        return counter > 1 ? null : task;
    }

    public void editTask(Task task){
        int condIndex = 0; int taskIndex = 0;
        search:
        {
            for (Condition c : tPool.getConditions()) {
                taskIndex = 0;
                for (Task t : c.getTasks()) {
                    if (/*t.getCondition() == task.getCondition() &&*/ t.getId() == task.getId()) {
                        break search;
                    }
                    taskIndex++;
                }
                condIndex++;
            }
        }
        tPool.getConditions().get(condIndex).getTasks().remove(taskIndex);
        tPool.getConditions().get(condIndex).getTasks().add(taskIndex, task);

        this.notifyDataSetChanged();
    }

    public ArrayList<Task> moveToCondition(int conditionsInWork, int condOutOfWork, int condToApply, int condIndex){
        ArrayList<Task> list = new ArrayList<>();
        for (Condition c : tPool.getConditions()) {
            for (Task t : c.getTasks()) {
                if(condIndex != 0) {
                    if (t.getChecked() && t.getCondition() < conditionsInWork) {
                        list.add(t);
                    }
                    if (t.getChecked() && t.getCondition() > condOutOfWork) {
                        return null;
                    }
                }else{
                    if (t.getChecked() && t.getCondition() > conditionsInWork) {
                        list.add(t);
                    }
                    if(t.getChecked() && t.getCondition() < condOutOfWork){
                        return null;
                    }
                }
            }
        }

        for (Task t : list) {
            for (Condition c : tPool.getConditions()) {
                if (c.getTasks().size() > 0) {
                    for (int i = c.getTasks().size() - 1; i >= 0; i--) {
                        if (c.getTasks().get(i).getId() == t.getId()) {
                            c.getTasks().remove(i);
                        }

                    }
                }
            }
        }
        for (Task t : list) {
            t.setChecked(false);
            t.setCondition(condToApply);
            tPool.getConditions().get(condIndex).getTasks().add(t);
        }
        this.notifyDataSetChanged();

        return list;
    }

    public void moveToOverdue(Task task){
        for(Task temp : tPool.getConditions().get(0).getTasks()){
            if(temp.getId() == task.getId()){
                tPool.getConditions().get(0).getTasks().remove(temp);
                break;
            }
        }
        tPool.getConditions().get(1).getTasks().add(task);
        this.notifyDataSetChanged();
    }

    public void clearTrash(){
        tPool.getConditions().get(3).getTasks().clear();
        this.notifyDataSetChanged();
    }
}
