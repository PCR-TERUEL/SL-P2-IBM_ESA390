package com.legados.wrapperIbm.view;

import com.legados.wrapperIbm.controller.WindowObserver;

import com.legados.wrapperIbm.model.Model;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;



public class TaskManagementWindow extends JFrame implements ActionListener{
    private Form form;

    private JTable table;
    private DefaultTableModel dtm;

    private WindowObserver wObserver;
    private JButton btNew;
    private JButton btSave;

    private JButton btCancel;
    private JPanel buttonBox;

    private WindowState state;
    private Model model;

    public TaskManagementWindow(WindowObserver wObserver, Model model) {
        this.wObserver = wObserver;
        createWindow();
        state = WindowState.START;
        this.model = model;
        enableEvent(state);
    }


    /*
     * Muestra los datos de los documentos extraidos de la base de datos en la tabla.
     */
    public void refreshTable() {
        dtm.setNumRows(0);
        ArrayList<List> fila= null;

        fila = model.getTaskTable();
        if(fila != null){
            for(List row:fila)
                dtm.addRow((Vector) row);
        }else{
            mostrarError("Se ha producido un error al cargar la tabla");
        }

    }

    /*
     *
     */
    private void createWindow() {
        setTitle("Gestor de tareas");
        setLayout(new BorderLayout());

        form=new Form(this);
        createTaskTable();
        createButtonBox();

        JPanel pnSuperior = new JPanel();

        JLabel lbTitulo = new JLabel("Tareas");
        lbTitulo.setFont(new Font("Arial",0,50));

        pnSuperior.add(lbTitulo);
        add(pnSuperior,BorderLayout.NORTH);
        add(form,BorderLayout.WEST);

        setSize(900,600);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {

                System.exit(0);
            }
        });
    }

    /*
     *
     */
    private void createTaskTable() {
        dtm = new DefaultTableModel();
        dtm.addColumn("Id");
        dtm.addColumn("Tipo");
        dtm.addColumn("Descripcion");
        dtm.addColumn("Fecha");
        dtm.addColumn("Nombre");
        dtm.addColumn("");
        dtm.addColumn("");
        dtm.addColumn("");
        dtm.addColumn("");


        table = new JTable(dtm);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollTaskTable = new JScrollPane(table);

        for(int i = 5; i<9;i++){
            table.getColumnModel().getColumn(i).setMaxWidth(0);
            table.getColumnModel().getColumn(i).setMinWidth(0);
            table.getTableHeader().getColumnModel().getColumn(i).setMaxWidth(0);
            table.getTableHeader().getColumnModel().getColumn(i).setMinWidth(0);
        }

        add(scrollTaskTable,BorderLayout.CENTER);
    }

    /*
     * Habilita o deshabilita los elementos de la vista dependiendo del estado
     * pasado por parametro.
     */
    private void enableEvent(WindowState estado) {
        this.state = estado;
        switch(estado) {
            case NEW:
                table.clearSelection();
                form.enableEvent(true);
                btNew.setEnabled(false);
                btSave.setEnabled(true);
                btCancel.setEnabled(true);
                table.setEnabled(false);
                break;
            case START:
                form.enableEvent(false);
                table.setEnabled(true);
                btNew.setEnabled(true);
                btSave.setEnabled(false);
                btCancel.setEnabled(false);
                break;
        }
    }


    /*
     * Da el aviso a control para hacer los procesos necesario segun el
     * evento producido.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        switch(e.getActionCommand()){
            case "guardar":
                if(form.verifyFields()){
                    wObserver.eventHappened(WindowObserver.Event.NEW,form.getTask());
                    enableEvent(WindowState.START);
                }
                break;
            case "nuevo":
                enableEvent(WindowState.NEW);
                break;
            case "cancelar":
                cleanFields();
                table.clearSelection();
                enableEvent(WindowState.START);
                break;
        }
    }

    /*
     * Muestra un panel de error con el mensaje pasado por parámetro.
     */
    private void mostrarError(String errorMessage) {
        JOptionPane.showMessageDialog(this, errorMessage,
                "Error",JOptionPane.ERROR_MESSAGE);
    }

    /*
     * Carga los datos del modelo en la vista.
     */
/*    public void cargarDatos() {
        habilitarEvento(WindowState.START);
        refreshTable();
    }*/




    private void createButtonBox() {
        buttonBox = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        btNew = new JButton("Nuevo");
        btNew.addActionListener(this);
        btNew.setActionCommand("nuevo");

        btSave = new JButton("Guardar");
        btSave.addActionListener(this);
        btSave.setActionCommand("guardar");


        btCancel = new JButton("Cancelar");
        btCancel.addActionListener(this);
        btCancel.setActionCommand("cancelar");

        buttonBox.add(btNew);
        buttonBox.add(btSave);
        buttonBox.add(btCancel);
        add(buttonBox,BorderLayout.SOUTH);
    }


    public void cleanFields(){
        form.cleanFields();
    }
    public enum WindowState {
        START, NEW,
    }
}


