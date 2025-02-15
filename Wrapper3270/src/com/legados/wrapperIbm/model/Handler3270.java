package com.legados.wrapperIbm.model;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Handler3270 {
    private final Process EMULATOR;
    private static final String IP_HOST = "155.210.152.51";
    private static final int PORT = 101;
    private static final String EMULATOR_PORT = "5000";
    private static final String USER = "grupo_01";
    private static final String PASSWORD = "secreto6";
    private static final String S3270_PATH = "C:\\Program Files\\wc3270\\x3270if.exe";

    public Handler3270() throws IOException, InterruptedException {
        EMULATOR = Runtime.getRuntime().exec("C:\\Program Files\\wc3270\\s3270.exe -scriptport " + EMULATOR_PORT);
        disconnect(); //Se usa por si la aplicación se ha parado anteriormente si desconectar la sesión.
        startProgram();

    }

    public void disconnect() throws IOException, InterruptedException {
        executeCommand("Disconnect");
    }

    private void executeCommand(String command) throws IOException, InterruptedException {
        Runtime.getRuntime().exec(new String[]{S3270_PATH, "-t", EMULATOR_PORT, command}).waitFor();
        Thread.sleep(750);
    }

    private String getScreen() throws IOException, InterruptedException {
        String screen = "";
        Process process = Runtime.getRuntime().exec(new String[]{S3270_PATH, "-t", EMULATOR_PORT, "ascii"});
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        process.waitFor();
        String readerLine = reader.readLine();
        while (readerLine != null) {
            screen += "\n" + readerLine;
            readerLine = reader.readLine();
        }
        return  screen;
    }


    private void startProgram() throws IOException, InterruptedException {
        System.out.println("Starting mainframe connection...");
        List<String> startCommands = List.of("connect(" + IP_HOST + ":" + PORT + ")", "Enter",
                "String(\"" + USER + "\")", "Enter",  "String(\"" + PASSWORD + "\")", "Enter", "Enter",
                "String(\"tareas.c\")", "Enter", "wait(InputField)");
        for(String command: startCommands){
            executeCommand(command);
        }

        System.out.println("Ready. Awaiting user inputs...");
    }


    public void assignTask(Task task) throws IOException, InterruptedException {
        //Darle al 1, enter,seleccionar tipo tarea (1 General, 2 especifica), enter,
        executeCommand("String(\"1\")");
        checkMainframeStatus();
        executeCommand("Enter");
        task.setDescription(parserIn(task.getDescription()));

        switch (task.getType()){
            case GENERAL:
                assignGeneralTask(task.getDescription(), task.getDdmm());
                break;
            case SPECIFIC:
                assignSpecificTask(task.getName(), task.getDescription(), task.getDdmm());
                break;
        }

        returnToMainMenu();
        System.out.println("Assigned task OK: " + task);
    }

    private void assignGeneralTask(String description, int date) throws IOException, InterruptedException {
        //dar fecha DDMM, enter, dar descripcion, enter, volver al menu

        List<String> assignTaskCommands = List.of("String(\"1\")", "Enter",
                "String(\"" + date + "\")", "Enter",  "String(\"" + description + "\")", "Enter");

        executeCommands(assignTaskCommands);
    }

    private boolean emulatorIsNotReading() throws IOException, InterruptedException {
        String[] linesScreen = getScreen().split("\n");
        return !linesScreen[linesScreen.length - 1].contains("Reading");
    }

    private void assignSpecificTask(String name, String description, int date)
            throws IOException, InterruptedException {
        name = parserIn(name);
        //dar fecha DDMM, enter, dar nombre, enter,dar descripcion, enter, volver al menu
        List<String> assignTaskCommands = List.of("String(\"2\")", "Enter",
                "String(\"" + date + "\")", "Enter", "String(\"" + name + "\")", "Enter",
                "String(\"" + description + "\")", "Enter");

        executeCommands(assignTaskCommands);
    }

    private void returnToMainMenu() throws IOException, InterruptedException {
        while(isNotMainMenu()){
            executeCommand("String(\"" + 3 + "\")");
            executeCommand("Enter");
        }
    }

    private Boolean isNotMainMenu() throws IOException, InterruptedException {
        String screen =  getScreen();
        String[] linesScreen;
        int position = 0;
        if(emulatorIsNotReading()) {
            linesScreen = screen.split("------T-----------------------------------------------------------------T");
            linesScreen = linesScreen[linesScreen.length - 2].split("\\?");
            position = linesScreen.length - 1;
        }else {
            linesScreen = screen.split("\\?");
            position = linesScreen.length - 2;
        }
        if(linesScreen[position].contains("MENU PRINCIPAL")) {
            return false;
        }

        return true;
    }

    //Cambiar los espacios por | para evitar error al asignar tarea
    private String parserIn(String text){
        return  text.replace(" ", "|");
    }

    //Cambiar los | por  espacios para evitar error al asignar tarea
    private String parserOut(String text){
        return text.replace("|", " ");
    }

    public List<Task> getTasks() throws IOException, InterruptedException {
        //2 ver tareas, enter
        executeCommand("String(\"2\")");
        checkMainframeStatus();
        executeCommand("Enter");
        List<Task> tasks = new ArrayList<>();
        tasks.addAll(getGeneralTasks());
        tasks.addAll(getSpecificTasks());

        //salida al 3, enter --> menu
        returnToMainMenu();
        return tasks;
    }

    private List<Task> getSpecificTasks() throws IOException, InterruptedException {
        //2 para elegir especifica, enter,
        checkMainframeStatus();
        executeCommand("String(\"2\")");
        executeCommand("Enter");
        checkMainframeStatus();
        return scrapeTasks();
    }

    private List<Task> getGeneralTasks() throws IOException, InterruptedException {
        //1 para elegir general, enter,
        executeCommand("String(\"1\")");
        checkMainframeStatus();
        executeCommand("Enter");
        List<Task> tasks = scrapeTasks();

        return tasks;
    }

    private List<Task> scrapeTasks() throws IOException, InterruptedException {
        List<Task> tasks = new ArrayList<>();
        if (emulatorIsNotReading() && screenTasks()) {
            String[] linesScreen = getScreen().split("VIEW TASK");
            String[] lastData = linesScreen[linesScreen.length - 1].split("\n");
            for (String data : lastData) {
                if (data.startsWith("TASK ")) {
                    tasks.add(parseTask(data));
                }
            }
            checkMainframeStatus();
            lastData = getScreen().split("\\?");
            lastData = lastData[lastData.length - 1].split("\n");
            for (String data : lastData) {
                if (data.startsWith("TASK ")) {
                    tasks.add(parseTask(data));
                }
            }
        } else {
            String[] linesScreen = getScreen().split("TOTAL TASK");
            String[] lastData = linesScreen[linesScreen.length - 2].split("\\?");

            lastData = lastData[lastData.length - 1].split("\n");
            for (String data : lastData) {
                if (data.startsWith("TASK ")) {
                    tasks.add(parseTask(data));
                }
            }
        }
        return tasks;
    }

    private Boolean screenTasks() throws IOException, InterruptedException {
        String[] linesScreen = getScreen().split("------T-----------------------------------------------------------------T");
        String[] lastData = linesScreen[linesScreen.length - 1].split("\n");
        return lastData[lastData.length - 1].equals("TOTAL TASK") || lastData[lastData.length - 1].startsWith("TASK ");
    }

    private void checkMainframeStatus() throws IOException, InterruptedException {
        while(emulatorIsNotReading()){
            System.out.println("Not reading...");
            executeCommand("Enter");
        }
    }

    private Task parseTask(String data) {
        String[] stringTask = data.split(" ");
        Task task = new Task();
        task.setId(Integer.parseInt(stringTask[1].replace(":", "")));
        if(stringTask[2].equals("GENERAL")){
            task.setType(Task.TaskType.GENERAL);
        } else {
            task.setType(Task.TaskType.SPECIFIC);
        }
        task.setDdmm(Integer.parseInt(stringTask[3]));
        task.setName(parserOut(stringTask[4]));
        task.setDescription(parserOut(stringTask[5]));

        return  task;
    }

    private void executeCommands(List<String> commands) throws IOException, InterruptedException {
        for(String command : commands) {
            checkMainframeStatus();
            executeCommand(command);
        }
    }

   /** public static void main (String [] args){
        try {
            Handler3270 h = new Handler3270();
            h.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

}
