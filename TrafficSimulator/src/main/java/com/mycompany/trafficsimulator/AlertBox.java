/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.trafficsimulator;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 *
 * @author Joey Potter
 */
public class AlertBox 
{
    public static void display(String title, String message)
    {
        Stage window = new Stage();
        
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);
        window.setMinWidth(250);
        
        Label label = new Label();
        label.setText(message);
        Button closeBtn = new Button("Close the window");
        closeBtn.setOnAction(e->window.close());
        
        VBox layout = new VBox(10);
        layout.getChildren().addAll(label,closeBtn);
        layout.setPadding(new Insets(10,10,10,10));
        layout.setAlignment(Pos.CENTER);
        
        Scene scene = new Scene(layout);
        //scene.getStylesheets().add("TrafficGUI.css");
        window.setScene(scene);
        window.showAndWait();
        
    }
}
