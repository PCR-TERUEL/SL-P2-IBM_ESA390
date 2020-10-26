package com.legados.wrapperIbm.model;

public class Task {
    private String description;
    private int id;
    private int ddmm;
    private String name;
    private TaskType type;

    public Task(String description, int id, int ddmm, String name, TaskType type) {
        this.description = description;
        this.id = id;
        this.ddmm = ddmm;
        this.name = name;
        this.type = type;
    }

    public Task() {
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public TaskType getType() {
        return type;
    }

    public void setType(TaskType type) {
        this.type = type;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDdmm() {
        return ddmm;
    }

    public void setDdmm(int ddmm) {
        this.ddmm = ddmm;
    }

    public enum TaskType {
        SPECIFIC, GENERAL
    }

    public String toString() {
        return id + ": " + name + " " + description + " " + ddmm + " " +type;
    }
}
