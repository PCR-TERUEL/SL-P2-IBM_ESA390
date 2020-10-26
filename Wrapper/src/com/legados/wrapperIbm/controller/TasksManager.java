package com.legados.wrapperIbm.controller;

import com.legados.wrapperIbm.model.Model;
import com.legados.wrapperIbm.model.Task;
import com.legados.wrapperIbm.view.TaskManagementWindow;

import java.io.*;

public class TasksManager implements WindowObserver {
    TaskManagementWindow window;
    Model model;
    public TasksManager(){
        model = new Model();
        window = new TaskManagementWindow(this, model);

    }
    public static void main(String[]args) {
        new TasksManager();
    }

    @Override
    public void eventHappened(Event event, Object obj) {
        if (event == Event.NEW){
            if(model.createTask((Task) obj)){
                window.cleanFields();
                window.refreshTable();
            }
        }
    }

}
