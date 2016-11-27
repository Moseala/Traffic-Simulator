package com.mycompany.trafficsimulator;

import java.awt.Frame;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import static javafx.application.Application.launch;
import javafx.geometry.Pos;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * This is the main launcher for the TrafficSimulator project.
 * 
 * @author Erik Clary
 * @version %I%, %G%
 * @since 1.10b
 * <p> <b>Date Created: </b>November 27, 2016 
 * <p> <b>Version Comments:</b> 
 *      <ul> 
 *          <li> 1.10b | 11/27/2016: Initial commit </li> 
 *      </ul>
 */
public class MainApp extends Application {
    protected static int seed = 54861234;
    private Stage window;
    private BorderPane layout;
    private VBox leftStackBox;
    private HBox bottomHBox;
    private TableView<Output> table;
    private int userCarNum = -1;
    private int userCarTime = -1;
    private Map createdMap;
    
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/Scene.fxml"));
        window = primaryStage;
        window.setTitle("Traffic Simulator 1.0");
        
        //GridPane with 10px padding all around edge
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(8);
        grid.setHgap(10);
        
        //File Menu
        Menu fileMenu = new Menu("_File");
        
        //File Menu items
        MenuItem fileExit = new MenuItem("_Exit");
        fileExit.setOnAction(e -> {
            window.close();
            System.exit(0);
        });
        MenuItem fileReset = new MenuItem("_Reset Configuration");
        fileReset.setOnAction(e  -> {
            userCarNum = -1;
            userCarTime = -1;
            }
        );
        
        fileMenu.getItems().addAll(fileReset, fileExit);
        
        //Help Menu
        Menu helpMenu = new Menu("_Help");
        
        //Help Menu items
        MenuItem about = new MenuItem("_About");
        helpMenu.getItems().add(about);     //EC: if this doesnt return anything, remove it.

        // Main Menu Bar
        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(fileMenu, helpMenu);
        
        //Left-side items, code adapted from http://docs.oracle.com/javafx/2/layout/builtin_layouts.htm
        leftStackBox = new VBox();
        leftStackBox.setFillWidth(true);
        ArrayList<Button> btnArray = new ArrayList<>();
        
        // Car Button - Prompt for Car Number Value
        Button addCarBtn = new Button();
        GridPane.setConstraints(addCarBtn, 1, 2);
        ConfirmBox cfb = new ConfirmBox();
        addCarBtn.setText("Click To Add Cars");      
        // Change Result to parameter for cars to execute. 
        addCarBtn.setOnAction(e -> {
            int[] result = cfb.display("Traffic Simulator 1.0", "Enter a numeric value for number of cars");
            userCarNum = result[0];
            userCarTime = result[1];
        });    
        btnArray.add(addCarBtn);
        
        
        //Start Simulation button
        Button simStartBtn = new Button();
        GridPane.setConstraints(simStartBtn, 1, 2);
        simStartBtn.setText("Start Simulation");
        simStartBtn.setOnAction(e -> {
            try{
                startSimulation();
            }
            catch(InterruptedException ex){
                //EC: add an error message here
            }
        });
        btnArray.add(simStartBtn);
        
        //EC: maybe add an update xlsx button? so the user can update the file. or do this after run.
        
        //End of buttons, add them to box
        for(int x = 0; x<btnArray.size(); x++){
            leftStackBox.getChildren().add(btnArray.get(x));
        }
        
        //Building the Table
        //Destination Column
        TableColumn<Output, String> destColumn = new TableColumn<>("Destination");
        destColumn.setMinWidth(200);
        destColumn.setCellValueFactory(new PropertyValueFactory<>("destination"));
        
        //signalType Column
        TableColumn<Output, String> signalColumn = new TableColumn<>("Signal Type");
        signalColumn.setMinWidth(100);
        signalColumn.setCellValueFactory(new PropertyValueFactory<>("signalType"));
        
        //waitTime Column
        TableColumn<Output, Double> waitColumn = new TableColumn<>("Average Wait Time");
        waitColumn.setMinWidth(100);
        waitColumn.setCellValueFactory(new PropertyValueFactory<>("waitTime"));
        
        //carAlive Column   EC: what does this signify to the user?
        TableColumn<Output, Integer> carAliveColumn = new TableColumn<>("Car Amount");
        carAliveColumn.setMinWidth(100);
        carAliveColumn.setCellValueFactory(new PropertyValueFactory<>("carAlive"));
        
        table = new TableView<>();
        //table.setItems(getOutput()); //EC: only do this after execution.
        table.getColumns().addAll(destColumn, signalColumn, waitColumn, carAliveColumn);
        
        
        
        // Layout of GUI
        layout = new BorderPane();
        layout.setTop(menuBar);
        layout.setLeft(leftStackBox);
        layout.setCenter(table);
        layout.setBottom(bottomHBox);
        Scene scene = new Scene(layout, 1200, 600);
        window.setScene(scene);
        window.show();
        
        
       // Scene scene = new Scene(root);
       // scene.getStylesheets().add("/styles/Styles.css");
        
       // stage.setTitle("JavaFX and Maven");
       // stage.setScene(scene);
       // stage.show();
    }

    
    //Get all of the Output
//    public ObservableList<Output> getOutput()
//        {
//            ObservableList<Output> output = FXCollections.observableArrayList();
//            output.add(new Output("Killeen Airport", 3, 3, 4));
//            return output;
//        }
    
    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @author Erik Clary
     * @param args the command line arguments
     */
    public static void main(String[] args) throws InterruptedException {
        launch(args);
    }

    private void startSimulation() throws InterruptedException{
        ProgressWindow pWindow = new ProgressWindow(new javax.swing.JFrame(),false);
        //Read excel doc and build its lists& queues.
        pWindow.updateAction("Reading in file.");
        ReadExcel creator;
        try {
            creator= new ReadExcel();
        } catch (IOException ex) {
            Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        pWindow.addToTextField("Reading in Excel file...");
        creator.run();
        pWindow.addToTextField("Done!");
        /*
        Thread creationThread = new Thread(creator);
        creationThread.start();
        while(creationThread.isAlive()){
            System.out.println("XLSX Progress: " + creator.getProgress());
            Thread.sleep(10000); //test to make sure this doesnt delay the created thread.
        }*/
        pWindow.addToTextField("Starting Map Generation...");
        //build map and cars from the read excel document
        createdMap = creator.getMap();
        if(userCarNum !=-1 && userCarTime !=-1)
            createdMap.userSettings(userCarNum, userCarTime);
        DirectionCreation directions = new DirectionCreation(seed);
        Queue<Car> carQueue = new LinkedList();
        Random rand = new Random(seed);
        pWindow.addToTextField("Done!");
        
        //create read from xlsx to check to see how many cars are already within.
        pWindow.updateAction("Car Generation");
        pWindow.addToTextField("Reading previously created cars...");
        try {
            carQueue = creator.readCarQueue();
            if(carQueue == null)
                carQueue = new LinkedList();
        } catch (IOException ex) {
            Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE, null, ex);
            pWindow.addToTextField("ERROR: Creator cant find the car file!");
        }
        pWindow.addToTextField("Done!");
        pWindow.addToTextField("Found " + carQueue.size() + " car(s) from previous execution!");
        
        
        pWindow.addToTextField("Generating Cars...");
        int totalN = createdMap.getTotalCarsNeeded()-carQueue.size();
        for(int x = 0; x<totalN; x++){
            Car newCar = new Car(Car.REGULAR_CAR,directions.getDirections(createdMap, createdMap.getRandomPoint(rand), createdMap.getRandomPoint(rand)));
            carQueue.add(newCar);
            pWindow.updateProgressBar((double)x/totalN);
        }
        pWindow.addToTextField("Done!");
        
        creator.writeCarQueue(carQueue);
        
        //need to write this to xlsx to enhance runtime.
        //maybe thread the above?
        //add cars to the map
        createdMap.addInitialCars(carQueue);
        
        //createdMap.run(); //Debug run
        
        //now the map has everything needed to run. execute map's runtime in a new thread
        pWindow.addToTextField("Pooling resources for multithreading...");
        Thread mapThread = new Thread(createdMap);
        pWindow.addToTextField("Done!");
        mapThread.start();
        pWindow.addToTextField("Simulation has begun!");
        pWindow.updateAction("Running Simulation");
        while(mapThread.isAlive()){
//            System.out.println("Map Progress:       " + createdMap.getProgress());
//            System.out.println("Actors in System:   " + createdMap.actorsInSystem());
//            System.out.println("Cars finished:      " + createdMap.getDespawnedCars().size());
            pWindow.updateProgressBar(createdMap.getProgress());
            Thread.sleep(1000);
        }
        pWindow.addToTextField("Done!");
        
        //Mapthread is now finished, dump despawned cars back to xlsx for metrics.
        //System.out.println("Finished!");
        //ArrayList<Car> despawned = createdMap.getDespawnedCars();
        /*for(Car e: despawned){
            try {
                creator.writeACar(e);
            } catch (IOException ex) {
                Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE, null, ex);
            }
        }*/
        
        //outputdump
        pWindow.addToTextField("Gathering output...");
        pWindow.updateAction("Collecting output");
        ObservableList<Output> output = FXCollections.observableArrayList();
        ArrayList<TrafficSignal> finishedSignals = createdMap.getTrafficSignals();
        for(int x = 0; x<finishedSignals.size(); x++){
            output.add(new Output(finishedSignals.get(x).getSourceRoad().getName(), finishedSignals.get(x).getTotalCarsThrough(), finishedSignals.get(x).getSignalType(), finishedSignals.get(x).getAverageWaitTime()));
            pWindow.updateProgressBar((double)x/finishedSignals.size());
        }
        pWindow.addToTextField("Done!");
        table.setItems(output);
        pWindow.updateAction("Simulation Finished!");
        pWindow.updateProgressBar(1);
        
        pWindow.setVisible(false);
    }
    
}
