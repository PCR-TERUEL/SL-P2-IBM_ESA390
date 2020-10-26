package com.legados.wrapperIbm.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class Model {
    Handler3270 s3270Handler;

    public Model() throws IOException, InterruptedException {
        this.s3270Handler = new Handler3270();
    }

    public boolean createTask(Task task) throws IOException, InterruptedException {
        s3270Handler.assignTask(task);
        return true;
    }

    public ArrayList<List> getTaskTable() throws IOException, InterruptedException {
        List<Task> tasks = s3270Handler.getTasks();
        Vector<String> row;
        ArrayList<List> list= new ArrayList<>();
        System.out.println("ENTRO 1");
        for(Task task : tasks){
            System.out.println("Hay tareas " + task.getName());
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
            list.add(row);
        }


        return  list;
    }

    public void manageForcedDisconnection() throws IOException, InterruptedException {
        disconect();
    }

    public void disconect() throws IOException, InterruptedException {
        this.s3270Handler.disconnect();
    }
}
