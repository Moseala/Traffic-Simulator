package com.mycompany.trafficsimulator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class MainApp extends Application {
    protected static int seed = 54861234;
    
    
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/Scene.fxml"));
        
        Scene scene = new Scene(root);
        scene.getStylesheets().add("/styles/Styles.css");
        
        stage.setTitle("JavaFX and Maven");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) throws InterruptedException {
        launch(args);
        
        //Read excel doc and build its lists& queues.
        ReadExcel creator;
        try {
            creator= new ReadExcel();
        } catch (IOException ex) {
            Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        Thread creationThread = new Thread(creator);
        creationThread.start();
        while(creationThread.isAlive()){
            System.out.println("XLSX Progress: " + creator.getProgress());
            Thread.sleep(10000); //test to make sure this doesnt delay the created thread.
        }
        
        //build map and cars from the read excel document
        Map createdMap = creator.getMap();
        DirectionCreation directions = new DirectionCreation(seed);
        Queue carQueue = new LinkedList();
        Random rand = new Random(seed);
        for(int x = 0; x<createdMap.getTotalCarsNeeded(); x++){
            Car newCar = new Car(Car.REGULAR_CAR,directions.getDirections(createdMap, createdMap.getRandomPoint(rand), createdMap.getRandomPoint(rand)));
            carQueue.add(newCar);
        }
        //add cars to the map
        createdMap.addInitialCars(carQueue);
        
        //now the map has everything needed to run. execute map's runtime in a new thread
        Thread mapThread = new Thread(createdMap);
        mapThread.start();
        while(mapThread.isAlive()){
            System.out.println("Map Progress: " + createdMap.getProgress());
            Thread.sleep(10000);
        }
        
        //Mapthread is now finished, dump despawned cars back to xlsx for metrics.
        ArrayList<Car> despawned = createdMap.getDespawnedCars();
        for(Car e: despawned){
            creator.writeACar(e);
        }
        
    }

}
