package com.legados.wrapperIbm.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class Model {
    Handler3270 s3270Handler;

    public Model() {
        this.s3270Handler = new Handler3270();
    }

    public boolean createTask(Task task) {
        s3270Handler.assignTask(task);
        return true;
    }

    public ArrayList<List> getTaskTable() {
        List<Task> tasks = s3270Handler.getTasks();
        Vector<String> row;
        for(Task task : tasks){
            row = new Vector<>();
            row.add(String.valueOf(task.getId()));
            row.add(task.getDescription());
            if(task.getType() == Task.TaskType.GENERAL) {
                row.add("General");
                row.add("");
            }else if (task.getType() == Task.TaskType.SPECIFIC) {
                row.add("Especifico");
                row.add(task.getName());
                row.add(task.getDescription());
            }
            row.add(task.getDescription());
        }
        ArrayList<List> list= new ArrayList<>();

        return  list;
    }
}
