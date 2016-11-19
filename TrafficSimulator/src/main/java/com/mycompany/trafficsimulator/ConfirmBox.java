
package com.mycompany.trafficsimulator;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
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
    
    public int display(String title, String message)
    {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);
        window.setMinWidth(250);
        Label label = new Label();
        label.setText(message);
        
        //prompt user for car value.
        //prevent string values.
        TextField txtField = new TextField();
        txtField.setPromptText("Enter number of cars");

        Button btn = new Button("Submit");
        btn.setOnAction( e -> {
            isInt(txtField, txtField.getText()); 
            window.close();
                });

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(30,30,30,30));
        layout.getChildren().addAll(label,txtField,btn);
        layout.setAlignment(Pos.CENTER);
        
        // Display window and wait for it to be closed before returning
        Scene scene = new Scene(layout);
        //scene.getStylesheets().add("TrafficGUI.css");
        window.setScene(scene);
        window.showAndWait();
        
        return answer = Integer.parseInt(txtField.getText());
    }

    private boolean isInt(TextField input, String message)
    {
        try{
            int car = Integer.parseInt(input.getText());
         //   System.out.println("Number of cars is: " + car);
        }catch(NumberFormatException e)
        {
            //System.out.println("Error " + message + " is not a number");
            AlertBox.display("Error", "Enter a Numeric value");
        }
        return false;
    }
    
}

    

