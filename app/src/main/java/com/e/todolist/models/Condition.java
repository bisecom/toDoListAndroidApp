package com.e.todolist.models;

import java.util.ArrayList;

public class Condition {
    private String title;
    private ArrayList<Task> tasks;

    public Condition(String title) {
        this.title = title;
        tasks = new ArrayList<>();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<Task> getTasks() {
        return tasks;
    }

    public void addTask(Task task) {
        tasks.add(task);
    }
}
