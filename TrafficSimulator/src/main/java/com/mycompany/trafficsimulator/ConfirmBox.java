
package com.mycompany.trafficsimulator;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 *
 * @author Joey Potter
 */
public class ConfirmBox 
{

    int answer;
    
    public int[] display(String title, String message)
    {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);
        window.setMinWidth(250);
        Label label = new Label();
        label.setText(message);
        VBox vb = new VBox();
        
        //prompt user for car value.
        //prevent string values.
        TextField txtField = new TextField();
        txtField.setPromptText("Enter number of cars: ");
        TextField txtField2 = new TextField();
        txtField2.setPromptText("Enter time to simulate (in seconds): ");
        
        vb.getChildren().addAll(txtField,txtField2);

        Button btn = new Button("Submit");
        btn.setOnAction( e -> {
            if(isInt(txtField, txtField.getText()))
                window.close();
        });

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(30,30,30,30));
        layout.getChildren().addAll(label,vb,btn);
        layout.setAlignment(Pos.CENTER);
        
        // Display window and wait for it to be closed before returning
        Scene scene = new Scene(layout);
        //scene.getStylesheets().add("TrafficGUI.css");
        window.setScene(scene);
        window.showAndWait();
        
        return new int[]{Integer.parseInt(txtField.getText()), Integer.parseInt(txtField2.getText())};
    }

    private boolean isInt(TextField input, String message) //EC: you dont need string message here..
    {
        try{
            int car = Integer.parseInt(input.getText());
            if(car<=0){
                AlertBox.display("Error", "Number too low");    
            }
            return true;
         //   System.out.println("Number of cars is: " + car);
        }catch(NumberFormatException e)
        {
            //System.out.println("Error " + message + " is not a number");
            AlertBox.display("Error", "Enter a Numeric value");
            return false;
        }
    }
    
}

    

