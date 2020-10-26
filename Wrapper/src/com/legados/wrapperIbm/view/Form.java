/*Formulario.java
 *
 * Versión 0 Eduardo y Martin (05/2019)
 *
 */
package com.legados.wrapperIbm.view;

import com.legados.wrapperIbm.model.Task;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

/**
 * La clase se encarga de rellenar las areas de la vista segun su los datos 
 * dados y se encarga de enviar los datos introducidos en la vista.
 */
public class Form extends JPanel{
    private Task task;


    private JLabel lbDate;
    private JLabel lbDescription;
    private JTextField tfDescription;
    private JTextField tfDate;
    private JLabel lbName;
    private JLabel lbTaskType;
    private JTextField tfName;
    private JComboBox taskTypeCombo;


    public Form(ActionListener listener) {
        setLayout(new GridLayout(8,0));

        JPanel pnTaskType = new JPanel(new FlowLayout(FlowLayout.LEFT));
        lbTaskType = new JLabel("Tipo de nota");
        lbTaskType.setPreferredSize(new Dimension(180,20));
        pnTaskType.add(lbTaskType);

        taskTypeCombo = new JComboBox();
        taskTypeCombo.setPreferredSize(new Dimension(180,20));
        pnTaskType.add(taskTypeCombo);
        taskTypeCombo.addItem("Nota específica");
        taskTypeCombo.addItem("Nota general");


        taskTypeCombo.addActionListener (new ActionListener () {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(taskTypeCombo.getSelectedItem().toString().equalsIgnoreCase("Nota general")){
                    tfName.setEnabled(false);
                }else{
                    tfName.setEnabled(true);
                }
            }
        });

        add(pnTaskType);

        JPanel pnDescription=new JPanel(new FlowLayout(FlowLayout.LEFT));
        lbDescription = new JLabel("Descripción");
        lbDescription.setPreferredSize(new Dimension(180,20));
        pnDescription.add(lbDescription);

        tfDescription = new JTextField();
        tfDescription.setPreferredSize(new Dimension(180,20));
        pnDescription.add(tfDescription);
        add(pnDescription);

        JPanel pnTamano=new JPanel(new FlowLayout(FlowLayout.LEFT));
        lbDate = new JLabel("Fecha DDMM");
        lbDate.setPreferredSize(new Dimension(180,20));
        pnTamano.add(lbDate);

        tfDate = new JTextField();
        tfDate.setPreferredSize(new Dimension(180,20));
        pnTamano.add(tfDate);
        add(pnTamano);



        JPanel pnName = new JPanel(new FlowLayout(FlowLayout.LEFT));
        lbName = new JLabel("Nombre");
        lbName.setPreferredSize(new Dimension(180,20));
        pnName.add(lbName);

        tfName = new JTextField();
        tfName.setPreferredSize(new Dimension(180,20));

        pnName.add(tfName);
        add(pnName);

        setPreferredSize(new Dimension(400,100));

    }


    /*
     * Devuelve una tarea tomando los datos de los elementos de la vista.
     */
    public Task getTask() {
        Task task = new Task();
        task.setDescription(tfDescription.getText());
        if(taskTypeCombo.getSelectedItem().toString().equalsIgnoreCase("Nota general")){
            task.setType(Task.TaskType.GENERAL);
        }else{
            task.setType(Task.TaskType.SPECIFIC);
        }
        if(tfDate.getText().equals("")){
            task.setDdmm(0000);
        }else{
            task.setDdmm(Integer.parseInt(tfDate.getText()));
        }
        if(tfName.isEnabled()) {
            task.setName(tfName.getText());
        }

        return task;
    }

    /*
     * Habilita y deshabilita los elementos de la vista.
     */
    public void enableEvent(boolean habilitacion) {
        for(Component componente : getComponents()){
            if(componente instanceof JPanel){
                JPanel paneles =(JPanel) componente;
                for(Component subcomponente: paneles.getComponents()){

                    subcomponente.setEnabled(habilitacion);
                }
            }else{
                componente.setEnabled(habilitacion);
            }
        }
    }

    /*
     * Borra la información que había en los elementos del formulario.
     */
    public void cleanFields() {
        tfDescription.setText("");
        tfDate.setText("");
        tfName.setText("");
    }

    /*
     * Comprueba que se cumplan los requisitos minimos a la hora de
     * rellenar el formulario.
     */
    public boolean verifyFields() {
        Color color = new Color(233,99,93);
        boolean correct = true;

        if(tfDescription.getText().equals("")){
            tfDescription.setBackground(color);
            correct = false;
        }else{
            tfDescription.setBackground(new Color(255,255,255));
        }
        if(tfDate.getText().length() != 4){
            tfDate.setBackground(color);
            correct = false;
        }else{
            tfDate.setBackground(new Color(255,255,255));
        }

        return correct;
    }
}