package com.legados.wrapperIbm.controller;

import com.legados.wrapperIbm.model.Model;
import com.legados.wrapperIbm.model.Task;
import com.legados.wrapperIbm.view.TaskManagementWindow;

import java.io.*;

/**
 * Clase que gestiona el control de la aplicación de gestión de notas.
 */
public class TasksManager implements WindowObserver {
    TaskManagementWindow window;
    Model model;
    public TasksManager(){
        try {
            window = new TaskManagementWindow(this, null);
            model = new Model();
            window.setModel(model);
            window.manageConnected();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[]args) {
        new TasksManager();
    }

    /**
     * Reacciona a un evento sucedido en la vista
     * @param event evento sucedido en la vista
     * @param obj contenido de interés para la gestión del evento.
     */
    @Override
    public void eventHappened(Event event, Object obj) {
        try {
            switch (event) {
                case NEW:
                    if(model != null) {
                        model.createTask((Task) obj);
                        window.cleanFields();
                        window.refreshTable();
                    }
                    break;
                case CLOSE:
                        model.disconect();
                    break;
            }
        } catch (Exception e) {
            manageForcedDisconnection();
        }
    }

    /**
     * Reacciona cuando la conexión con el servidor se ha perdido o bien se ha producido algún problema con el servidor.
     */
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
