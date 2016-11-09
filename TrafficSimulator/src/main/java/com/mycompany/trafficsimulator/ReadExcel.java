package com.mycompany.trafficsimulator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * ReadExcel class is responsible for reading in the excel file and populating 
 * Roads, Traffic Signals, and Signal Groups.
 * 
 * 
 * @author Chris Tisdale
 * @version %I%, %G%
 * @since 1.06a
 * <p> <b>Date Created: </b>November 8, 2016 
 * <p> <b>Version Comments:</b> 
 *      <ul> 
 *          <li> 1.06a | 11/08/2016:    Implemented FileInputStreams of both sheet 0 and sheet 1 of the data.xls workbook.
 *                                      Initialized and populated two 2d String arrays roadData and nodeData from the stream.
 *                                      Looped through the number of roads and populated array of Roads and array of TrafficSignals
 *                                          from the corresponding roadData rows.
 *                                      Looped through the number of nodes and populated appropriate array lists then entered
 *                                          these lists into array of SignalGroup.</li>
 *      </ul>
 */
public class ReadExcel {

    public static void main(String[] args) throws IOException {

        //Try catch setting up input stream
        FileInputStream file = null;
        try {
            file = new FileInputStream(new File("data.xlsx"));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ReadExcel.class.getName()).log(Level.SEVERE, null, ex);
        }

        //Create Workbook instance holding reference to .xlsx file
        XSSFWorkbook workbook = new XSSFWorkbook(file);

        //Get first sheet from the workbook
        XSSFSheet sheet = workbook.getSheetAt(0);

        //Set value of row and column and create appropriate size array roadData
        int rowNum = sheet.getLastRowNum() + 1;
        int colNum = sheet.getRow(0).getLastCellNum();
        String[][] roadData = new String[rowNum][colNum];

        DataFormatter formatter;   //used to set cell value to string
        formatter = new DataFormatter();
        //Loop through rows and cells (road info) inserting cell to data array
        for (int i = 0; i < rowNum; i++) {
            XSSFRow row = sheet.getRow(i);
            for (int j = 0; j < colNum; j++) {
                XSSFCell cell = row.getCell(j);
                String value = formatter.formatCellValue(cell);
                roadData[i][j] = value;
            }
            
        }
        
        //Get second sheet from the workbook
        sheet = workbook.getSheetAt(1);
        rowNum = sheet.getLastRowNum() + 1;
        colNum = sheet.getRow(0).getLastCellNum();
        String[][] nodeData = new String[rowNum][colNum];
        
        formatter = new DataFormatter();
        //Loop through rows and cells (node info) inserting cell to data1 array
        for (int i = 0; i < rowNum; i++) {
            XSSFRow row = sheet.getRow(i);
            for (int j = 0; j < colNum; j++) {
                XSSFCell cell = row.getCell(j);
                String value = formatter.formatCellValue(cell);
                nodeData[i][j] = value;
            }
            
        }
        
        //Initialize array of Road and array of TrafficSignal
        //Loop through and populate array with roads
        //coords array is given to constructor in (x,y) order
        final int numRoads = 236;
        Road[] Roads = new Road[numRoads];
        TrafficSignal[] TrafficSignals = new TrafficSignal[numRoads];
        int[] coords = new int[2];
        for (int i = 0; i <= numRoads-1; i++) {
            coords[0] = Integer.parseInt(roadData[i][7]);
            coords[1] = Integer.parseInt(roadData[i][8]);
            //Constructors for Road and TrafficSignal
            Roads[i] = new Road(roadData[i][2], roadData[i][6], Double.parseDouble(roadData[i][3]), Integer.parseInt(roadData[i][4]));
            TrafficSignals[i] = new TrafficSignal(Integer.parseInt(roadData[i][5]), Roads[i], roadData[i][0]+roadData[i][1], coords);
        }
       
        //Initialize array of SignalGroup
        //Loops through the number of nodes to get base node for each group
        final int numNodes = 83;
        SignalGroup[] SignalGroup = new SignalGroup[numNodes];
        for (int i = 0; i <= numNodes-1; i++) {
            String tempNode = nodeData[i][0];       //get target node from nodeData array
            ArrayList<TrafficSignal> exitTemp = new ArrayList<>();
            ArrayList<TrafficSignal> signalTemp = new ArrayList<>();
            
            //loops through number of roads to find traffic signals
            //Those that start with same ID as the node are added as exits
            //Thise that end with same ID as the node are added as entering traffic signals
            for (int j = 0; j <= numRoads-1; j++) {
                if (TrafficSignals[j].getIdentifier().startsWith(tempNode))
                    exitTemp.add(TrafficSignals[j]);
                if (TrafficSignals[j].getIdentifier().endsWith(tempNode))
                    signalTemp.add(TrafficSignals[j]);       
            }
            
            //Initialize an ArrayList called trafficSignal and copy signalTemp (the signals at that node)
            //used to populate signalOrder to pass operation order to constructor
            ArrayList<TrafficSignal> trafficSignal = new ArrayList<>();
            trafficSignal = signalTemp;
            
            //Create arrays for tracking order of signals since there are no more than two types of signals per node
            //And array size trackers for updating their elements
            TrafficSignal[] tempSignalA = new TrafficSignal[4];
            TrafficSignal[] tempSignalB = new TrafficSignal[3];
            int tempSignalASize = 0;
            int tempSignalBSize = 0;
            
            //While the signals list is not empty this loop runs
            //It places the first signal in tempSignalA
            //Then any subsequent signals that have the same signalType as the next index of tempSignalA
            //          Added a getSignalType() method in order to compare signals to those already in array
            //Else it puts the trafficSignal in the tempSignalB array
            while (!(trafficSignal.isEmpty())){
                if (tempSignalASize ==0){
                    tempSignalA[0] = trafficSignal.get(0);
                    trafficSignal.remove(0);
                    tempSignalASize++;
                }
                else if (trafficSignal.get(0).getSignalType() == tempSignalA[0].getSignalType()){
                    tempSignalA[tempSignalASize] = trafficSignal.get(0);
                    trafficSignal.remove(0);
                    tempSignalASize++;
                }
                else {
                    tempSignalB[tempSignalBSize] = trafficSignal.get(0);
                    trafficSignal.remove(0);
                    tempSignalBSize++;
                }
            }

            //Initialize ArrayList of TrafficSignal[] signalOrder for passing order of operation to constructor
            //Several place holder arrays since these have to have static size and there are varying
            //numbers of signal behaviors at each signal group. For example: 1 signal and despawn, 4 passthrough signals,
            //2 passthrough signals with a stop sign, two different traffic light times, etc.
            ArrayList<TrafficSignal[]> signalOrder = new ArrayList<>();
            TrafficSignal[] a1 = new TrafficSignal[1];
            TrafficSignal[] a2 = new TrafficSignal[2];
            TrafficSignal[] a3 = new TrafficSignal[3];
            TrafficSignal[] b1 = new TrafficSignal[1];
            TrafficSignal[] b2 = new TrafficSignal[2];

            //the next 100 lines copy tempSignalA and B arrays to an array of appropriate length through tempSignalSize variables
            if (tempSignalASize == 1 && tempSignalBSize == 0){
                a1[0] = tempSignalA[0];
                signalOrder.add(a1);
            }
            
            if (tempSignalASize == 2 && tempSignalBSize == 0){
                a2[0] = tempSignalA[0];
                a2[1] = tempSignalA[1];
                signalOrder.add(a2);
            }
            
            if (tempSignalASize == 3 && tempSignalBSize == 0){
                a3[0] = tempSignalA[0];
                a3[1] = tempSignalA[1];
                a3[2] = tempSignalA[2];
                signalOrder.add(a3);
            }
            
            if (tempSignalASize == 4 && tempSignalBSize == 0){
                signalOrder.add(tempSignalA);
            }
            
            if (tempSignalASize == 1 && tempSignalBSize == 1){
                a1[0] = tempSignalA[0];
                b1[0] = tempSignalB[0];
                signalOrder.add(a1);
                signalOrder.add(b1);
            }
 
            if (tempSignalASize == 2 && tempSignalBSize == 1){
                a2[0] = tempSignalA[0];
                a2[1] = tempSignalA[1];
                b1[0] = tempSignalB[0];
                signalOrder.add(a2);
                signalOrder.add(b1);
            }         
            
            if (tempSignalASize == 3 && tempSignalBSize == 1){
                a3[0] = tempSignalA[0];
                a3[1] = tempSignalA[1];
                a3[2] = tempSignalA[2];
                b1[0] = tempSignalB[0];
                signalOrder.add(a3);
                signalOrder.add(b1);
            }
            
            if (tempSignalASize == 4 && tempSignalBSize == 1){
                b1[0] = tempSignalB[0];
                signalOrder.add(tempSignalA);
                signalOrder.add(b1);
            }
            
            if (tempSignalASize == 1 && tempSignalBSize == 2){
                a1[0] = tempSignalA[0];
                b2[0] = tempSignalB[0];
                b2[1] = tempSignalB[1];
                signalOrder.add(a1);
                signalOrder.add(b2);
            }
 
            if (tempSignalASize == 2 && tempSignalBSize == 2){
                a2[0] = tempSignalA[0];
                a2[1] = tempSignalA[1];
                b2[0] = tempSignalB[0];
                b2[1] = tempSignalB[1];
                signalOrder.add(a2);
                signalOrder.add(b2);
            }         
            
            if (tempSignalASize == 3 && tempSignalBSize == 2){
                a3[0] = tempSignalA[0];
                a3[1] = tempSignalA[1];
                a3[2] = tempSignalA[2];
                b2[0] = tempSignalB[0];
                b2[1] = tempSignalB[1];
                signalOrder.add(a3);
                signalOrder.add(b2);
            }
            
            if (tempSignalASize == 4 && tempSignalBSize == 2){
                b2[0] = tempSignalB[0];
                b2[1] = tempSignalB[1];
                signalOrder.add(tempSignalA);
                signalOrder.add(b2);
            }
            
            if (tempSignalASize == 1 && tempSignalBSize == 3){
                signalOrder.add(a1);
                signalOrder.add(tempSignalB);
            }
 
            if (tempSignalASize == 2 && tempSignalBSize == 3){
                a2[0] = tempSignalA[0];
                a2[1] = tempSignalA[1];
                signalOrder.add(a2);
                signalOrder.add(tempSignalB);
            }         
            
            if (tempSignalASize == 3 && tempSignalBSize == 3){
                a3[0] = tempSignalA[0];
                a3[1] = tempSignalA[1];
                a3[2] = tempSignalA[2];
                signalOrder.add(a3);
                signalOrder.add(tempSignalB);
            }
            
            if (tempSignalASize == 4 && tempSignalBSize == 3){
                signalOrder.add(tempSignalA);
                signalOrder.add(tempSignalB);
            }
            //Constructor for SignalGroup
            SignalGroup[i] = new SignalGroup(exitTemp, signalTemp, signalOrder);
        }
        
    file.close();
    }
}
