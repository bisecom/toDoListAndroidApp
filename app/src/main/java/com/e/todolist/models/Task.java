package com.e.todolist.models;

import java.io.Serializable;

import io.realm.RealmObject;

public class Task extends RealmObject implements Serializable {

    private int id;
    private String subject;
    private String description;
    private int condition;
    private String placementTime;
    private Boolean isImportant;
    private Boolean isChecked;

    public Task(int id, String subject, String description, int condition,
                String placementTime, Boolean isImportant, Boolean isChecked) {
        this.id = id;
        this.subject = subject;
        this.description = description;
        this.condition = condition;
        this.placementTime = placementTime;
        this.isImportant = isImportant;
        this.isChecked = isChecked;
    }
    public Task(){}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCondition() {
        return condition;
    }

    public void setCondition(int condition) {
        this.condition = condition;
    }

    public String getPlacementTime() {
        return placementTime;
    }

    public void setPlacementTime(String placementTime) {
        this.placementTime = placementTime;
    }

    public Boolean getImportant() {
        return isImportant;
    }

    public void setImportant(Boolean important) {
        isImportant = important;
    }

    public Boolean getChecked() {
        return isChecked;
    }

    public void setChecked(Boolean checked) {
        isChecked = checked;
    }
}
