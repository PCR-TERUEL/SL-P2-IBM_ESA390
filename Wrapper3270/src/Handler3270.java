import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Handler3270 {
    private final Process EMULATOR;
    private static final String IP_HOST = "155.210.152.51";
    private static final int PORT = 101;
    private static final int EMULATOR_PORT = 5000;
    private static final String USER = "grupo_01";
    private static final String PASSWORD = "secreto6";



    public Handler3270() throws IOException, InterruptedException {
        EMULATOR = Runtime.getRuntime().exec("C:\\Program Files\\wc3270\\s3270.exe -scriptport " + EMULATOR_PORT);
        try{
            /*executeCommand("connect(" + IP_HOST + ":" + PORT + ")");
            executeCommand("Enter");
            Thread.sleep(1500);
            System.out.println(getScreen());*/
            startProgram();
            //System.out.println(getScreen());
            assignTask(new Task("descrip uno", 0, 1902,null, Task.TaskType.GENERAL));
            assignTask(new Task("describ dos", 0, 1902,"null1a", Task.TaskType.SPECIFIC));
            assignTask(new Task("desc tres", 0, 1903,"null", Task.TaskType.GENERAL));
            assignTask(new Task("desiptone", 0, 2202,"name one", Task.TaskType.GENERAL));
            assignTask(new Task("deiption", 0, 2002,"name", Task.TaskType.SPECIFIC));
            assignTask(new Task("desc tres", 0, 1903,"null", Task.TaskType.GENERAL));
            assignTask(new Task("desiptone", 0, 2202,"name one", Task.TaskType.GENERAL));
            assignTask(new Task("deiption", 0, 2002,"name", Task.TaskType.SPECIFIC));
            assignTask(new Task("desc tres", 0, 1903,"null", Task.TaskType.GENERAL));
            assignTask(new Task("desiptone", 0, 2202,"name one", Task.TaskType.GENERAL));
            assignTask(new Task("deiption", 0, 2002,"name", Task.TaskType.SPECIFIC));

            List<Task> tasks = getTasks();
            for(Task task: tasks){
                System.out.println("///////////////////////////////////////////////////////////////");
                System.out.println(task.getId() + "\\" + task.getType() + "\\" + task.getDdmm() + "\\" + task.getName() + "\\" + task.getDescription());
                System.out.println("///////////////////////////////////////////////////////////////");
            }
            disconnect();
        } catch (Exception e) {
            System.out.println(getScreen());
            disconnect();
            e.printStackTrace();
        }
    }

    public void disconnect() throws IOException, InterruptedException {
        executeCommand("disconect");
    }

    private void executeCommand(String command) throws IOException, InterruptedException {
        Process process = Runtime.getRuntime().exec( new String[]{"C:\\Program Files\\wc3270\\x3270if.exe", "-t",
                    String.valueOf(EMULATOR_PORT), command});
        process.waitFor();

        //Si se rompe al realizar comandos, aumentar el tiempo de espera
        Thread.sleep(1000);

    }

    /*private String getErrors() throws IOException {
        Process process = Runtime.getRuntime().exec( new String[]{"C:\\Program Files\\wc3270\\x3270if.exe", "-t",
                String.valueOf(EMULATOR_PORT), "ascii"});
        BufferedReader errorReader =  new BufferedReader(new InputStreamReader(process.getErrorStream()));
        String errorLine = errorReader.readLine();
        while (errorLine != null) {
            System.out.println(errorLine);
            errorLine = errorReader.readLine();
        }
    }*/

    private String getScreen() throws IOException, InterruptedException {
        String screen = "";
        Process process = Runtime.getRuntime().exec( new String[]{"C:\\Program Files\\wc3270\\x3270if.exe", "-t",
                String.valueOf(EMULATOR_PORT), "ascii"});
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
        System.out.println("ENTROOOOOOOOOOOOOOOOOOOOOOOOOO");
        List<String> startCommands = List.of("connect(" + IP_HOST + ":" + PORT + ")", "Enter",
                "String(\"" + USER + "\")", "Enter",  "String(\"" + PASSWORD + "\")", "Enter", "Enter",
                "String(\"tareas.c\")", "Enter", "wait(InputField)");
        for(String command: startCommands){
            executeCommand(command);
        }
        System.out.println(getScreen());
        System.out.println("EMPEZAMOS-----------------------------------------------------------------------");
    }


    public void assignTask(Task task) throws IOException, InterruptedException {
        //Darle al 1, enter,seleccionar tipo tarea (1 General, 2 especifica), enter,
        executeCommand("String(\"1\")");
        putInMoodRead();
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
        System.out.println(getScreen());
        System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        goMainMenu();
    }

    private void assignGeneralTask(String description, int date) throws IOException, InterruptedException {
        //dar fecha DDMM, enter, dar descripcion, enter, volver al menu
        List<String> assignTaskCommands = List.of("String(\"1\")", "Enter",
                "String(\"" + date + "\")", "Enter",  "String(\"" + description + "\")", "Enter");
        for(String command: assignTaskCommands){
            if(command.startsWith("String")){
                putInMoodRead();
            }
            executeCommand(command);
        }
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

        for(String command: assignTaskCommands){
            System.out.println(command);
            if(command.startsWith("String")){
                putInMoodRead();
            }
            executeCommand(command);
        }
    }

    private void goMainMenu() throws IOException, InterruptedException {
        while(isNotMainMenu()){
            executeCommand("String(\"" + 3 + "\")");
            executeCommand("Enter");
        }
    }

    private Boolean isNotMainMenu() throws IOException, InterruptedException {
        String screen =  getScreen();
        String[] linesScreen = screen.split("\\?");
        if(linesScreen[linesScreen.length - 2].contains("MENU PRINCIPAL")){
            //System.out.println(getScreen());
            return false;
        }else{
            return true;
        }
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
        System.out.println(getScreen());
        executeCommand("String(\"2\")");
        putInMoodRead();
        executeCommand("Enter");
        System.out.println(getScreen());
        List<Task> tasks = new ArrayList<>();
        tasks.addAll(getGeneralTasks());
        tasks.addAll(getSpecificTasks());

        //salida al 3, enter --> menu
        goMainMenu();
        System.out.println(getScreen());
        return tasks;
    }

    private List<Task> getSpecificTasks() throws IOException, InterruptedException {
        //2 para elegir especifica, enter,
        System.out.println("ENTRO GET SPECIFIC TASK");
        System.out.println(getScreen());
        executeCommand("String(\"2\")");
        putInMoodRead();
        System.out.println(getScreen());
        executeCommand("Enter");
        System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        System.out.println(getScreen());
        System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        List<Task> tasks = scrapeTasks();
        System.out.println("SALGO DE GET SPECIFIC TASK");
        return tasks;
    }

    private List<Task> getGeneralTasks() throws IOException, InterruptedException {
        //1 para elegir general, enter,
        putInMoodRead();
        executeCommand("String(\"1\")");

        executeCommand("Enter");
       // executeCommand("Enter");
        List<Task> tasks = scrapeTasks();
        return tasks;
    }

    private List<Task> scrapeTasks() throws IOException, InterruptedException {
       // System.out.println(getScreen());
        List<Task> tasks = new ArrayList<>();
        String[] linesScreen = getScreen().split("TOTAL TASK");
        String[] lastData = linesScreen[linesScreen.length - 2].split("\\?");

        System.out.println("******************************************************************************");
        lastData = lastData[lastData.length - 1].split("\n");
        for (String data: lastData){
            System.out.println(data);
            if(data.startsWith("TASK ")){
                tasks.add(parseTask(data));
            }
        }
        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");

        return tasks;
    }

    private void putInMoodRead() throws IOException, InterruptedException {
        while(emulatorIsNotReading()){
            System.out.println("Not in mood Read");
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

    public static void main (String [] args){
        try {
            Handler3270 s3270Handler = new Handler3270();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

