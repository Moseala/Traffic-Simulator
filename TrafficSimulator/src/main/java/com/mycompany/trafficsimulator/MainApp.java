package com.mycompany.trafficsimulator;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import static javafx.application.Application.launch;
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


public class MainApp extends Application {
    protected static int seed = 54861234;
    Stage window;
    BorderPane layout;
    TableView<Output> table;
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/Scene.fxml"));
        window = primaryStage;
        window.setTitle("Traffic Simulator 2.0");
        
        //GridPane with 10px padding all around edge
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(8);
        grid.setHgap(10);
        
        //File Menu
        Menu fileMenu = new Menu("_File");
        
        //File Menu items
        MenuItem fileExit = new MenuItem("_Exit");
        fileMenu.getItems().add(fileExit);
        fileExit.setOnAction(e -> window.close());
        
        //Help Menu
        Menu helpMenu = new Menu("_Help");
        
        //Help Menu items
        MenuItem about = new MenuItem("_About");
        helpMenu.getItems().add(about);

        // Main Menu Bar
        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(fileMenu, helpMenu);
        
        // Car Button - Prompt for Car Number Value
        Button btn = new Button();
        GridPane.setConstraints(btn, 1, 2);
        ConfirmBox cfb = new ConfirmBox();
        btn.setText("Click To Add Cars");      
        // Change Result to parameter for cars to execute. 
        btn.setOnAction(e -> {
        int result = cfb.display("Traffic Simulator 2.0","Enter a numaric value for number of cars");
        System.out.print(result);
        });    
        
        //Building the Table
        //Destination Column
        TableColumn<Output, String> destColumn = new TableColumn<>("Destination");
        destColumn.setMinWidth(200);
        destColumn.setCellValueFactory(new PropertyValueFactory<>("destination"));
        
        //signalType Column
        TableColumn<Output, Integer> signalColumn = new TableColumn<>("Signal Type");
        signalColumn.setMinWidth(100);
        signalColumn.setCellValueFactory(new PropertyValueFactory<>("signalType"));
        
        //waitTime Column
        TableColumn<Output, Integer> waitColumn = new TableColumn<>("Wait Time");
        waitColumn.setMinWidth(100);
        waitColumn.setCellValueFactory(new PropertyValueFactory<>("waitTime"));
        
        //carAlive Column
        TableColumn<Output, Integer> carAliveColumn = new TableColumn<>("Car Alive");
        carAliveColumn.setMinWidth(100);
        carAliveColumn.setCellValueFactory(new PropertyValueFactory<>("carAlive"));
        
        table = new TableView<>();
        table.setItems(getOutput());
        table.getColumns().addAll(destColumn, signalColumn, waitColumn, carAliveColumn);
        
        
        
        // Layout of GUI
        layout = new BorderPane();
        layout.setTop(menuBar);
        layout.setLeft(btn);
        layout.setCenter(table);
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
    public ObservableList<Output> getOutput()
        {
            ObservableList<Output> output = FXCollections.observableArrayList();
            output.add(new Output("Killeen Airport", 3, 3, 4));
            return output;
        }
    
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
        //launch(args);
        
        //Read excel doc and build its lists& queues.
        ReadExcel creator;
        try {
            creator= new ReadExcel();
        } catch (IOException ex) {
            Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        creator.run();
        /*
        Thread creationThread = new Thread(creator);
        creationThread.start();
        while(creationThread.isAlive()){
            System.out.println("XLSX Progress: " + creator.getProgress());
            Thread.sleep(10000); //test to make sure this doesnt delay the created thread.
        }*/
        System.out.println("Starting Map Generation.");
        //build map and cars from the read excel document
        Map createdMap = creator.getMap();
        DirectionCreation directions = new DirectionCreation(seed);
        Queue<Car> carQueue = new LinkedList();
        Random rand = new Random(seed);
        
        //create read from xlsx to check to see how many cars are already within.
        /*
        if(creator.getNumCars() != createdMap.getTotalCarsNeeded()){
            for(int x = 0; x<(createdMap.getTotalCarsNeeded()-creator.getNumCars()); x++){
                try{
                    Car newCar = new Car(Car.REGULAR_CAR,directions.getDirections(createdMap, createdMap.getRandomPoint(rand), createdMap.getRandomPoint(rand)));
                    creator.writeACar(newCar);
                    System.out.println("Created car: " +x + " of " +createdMap.getTotalCarsNeeded());
                }
                catch(IOException ex){
                    Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        
        for(int x = 0; x<createdMap.getTotalCarsNeeded(); x++){
            try{
                carQueue.add(creator.readACar());
            }
            catch(IOException ex){
                Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE, null, ex);
            }
        }*/
        
        for(int x = 0; x<createdMap.getTotalCarsNeeded(); x++){
            Car newCar = new Car(Car.REGULAR_CAR,directions.getDirections(createdMap, createdMap.getRandomPoint(rand), createdMap.getRandomPoint(rand)));
            carQueue.add(newCar);
            System.out.println("Created car: " +x + " of " +createdMap.getTotalCarsNeeded());
        }
        
        
        //need to write this to xlsx to enhance runtime.
        //maybe thread the above?
        //add cars to the map
        createdMap.addInitialCars(carQueue);
        
        //createdMap.run(); //Debug run
        
        //now the map has everything needed to run. execute map's runtime in a new thread
        Thread mapThread = new Thread(createdMap);
        mapThread.start();
        while(mapThread.isAlive()){
            System.out.println("Map Progress:       " + createdMap.getProgress());
            System.out.println("Actors in System:   " + createdMap.actorsInSystem());
            System.out.println("Cars finished:      " + createdMap.getDespawnedCars().size());
            Thread.sleep(1000);
        }
        
        //Mapthread is now finished, dump despawned cars back to xlsx for metrics.
        System.out.println("Finished!");
        ArrayList<Car> despawned = createdMap.getDespawnedCars();
        System.out.println("Despawned: " + despawned.size());
        /*for(Car e: despawned){
            try {
                creator.writeACar(e);
            } catch (IOException ex) {
                Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE, null, ex);
            }
        }*/
     
        
        System.exit(0);
    }

}
