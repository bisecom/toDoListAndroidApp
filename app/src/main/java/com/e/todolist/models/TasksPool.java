package com.e.todolist.models;

import java.util.ArrayList;

public class TasksPool {
    private String title;
    private ArrayList<Condition>conditions;

    public TasksPool(String title) {
        this.title = title;
        conditions = new ArrayList<>();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<Condition> getConditions() {
        return conditions;
    }

    public void addCondition(Condition condition) {
        conditions.add(condition);
    }
}
