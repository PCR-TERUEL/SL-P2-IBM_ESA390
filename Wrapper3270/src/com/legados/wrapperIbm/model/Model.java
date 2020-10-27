package com.legados.wrapperIbm.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Clase que gestiona el acceso a datos.
 */
public class Model {
    Handler3270 s3270Handler;
    public Model() throws IOException, InterruptedException {
        this.s3270Handler = new Handler3270();
    }

    /**
     * Crea una tarea
     * @param task tarea a crear
     * @throws IOException
     * @throws InterruptedException
     */
    public void createTask(Task task) throws IOException, InterruptedException {
        s3270Handler.assignTask(task);
    }

    /**
     * Obtiene todas las tareas del sistema en una matriz dinámica.
     * @return retorna todas las tareas
     * @throws IOException
     * @throws InterruptedException
     */
    public ArrayList<List> getTaskTable() throws IOException, InterruptedException {
        List<Task> tasks = s3270Handler.getTasks();
        Vector<String> row;
        ArrayList<List> list= new ArrayList<>();
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
            list.add(row);
        }


        return  list;
    }

    /**
     * Gestiona la situación en caso que se produzca una desconexión forzosa.
     * @throws IOException
     * @throws InterruptedException
     */
    public void manageForcedDisconnection() throws IOException, InterruptedException {
        disconect();
    }

    /**
     * Termina la conexión con el servidor.
     * @throws IOException
     * @throws InterruptedException
     */
    public void disconect() throws IOException, InterruptedException {
        this.s3270Handler.disconnect();
    }

}
