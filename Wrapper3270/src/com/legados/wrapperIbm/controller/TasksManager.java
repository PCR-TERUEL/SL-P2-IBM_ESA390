package com.legados.wrapperIbm.controller;

import com.legados.wrapperIbm.model.Model;
import com.legados.wrapperIbm.model.Task;
import com.legados.wrapperIbm.view.TaskManagementWindow;

import java.io.*;

public class TasksManager implements WindowObserver {
    TaskManagementWindow window;
    Model model;
    public TasksManager(){
        try {
            window = new TaskManagementWindow(this, null);
            model = new Model();
            window.setModel(model);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[]args) {
        new TasksManager();
    }

    @Override
    public void eventHappened(Event event, Object obj) {
        try {
            switch (event) {
                case NEW:
                    if (model.createTask((Task) obj)) {
                        window.cleanFields();
                        window.refreshTable();
                    }
                    break;
                case CLOSE:
                        model.disconect();
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public void manageForcedDisconnection(){
        window.showError();
        try {
            model.manageForcedDisconnection();
            model = new Model();
        } catch (IOException e) {
            window.showFatalError();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        window.setModel(model);
    }

}
