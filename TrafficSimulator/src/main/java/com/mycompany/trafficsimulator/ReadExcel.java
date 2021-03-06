package com.mycompany.trafficsimulator;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * ReadExcel class is responsible for reading in the excel file and populating 
 * roads, Traffic Signals, and Signal Groups.
 * 
 * 
 * @author Chris Tisdale, Erik Clary
 * @version %I%, %G%
 * @since 1.06a
 * <p> <b>Date Created: </b>November 8, 2016 
 * <p> <b>Version Comments:</b> 
 *      <ul> 
 *          <li> 1.06a | 11/08/2016:    Implemented FileInputStreams of both sheet 0 and sheet 1 of the data.xls workbook.
 *                                      Initialized and populated two 2d String arrays roadData and nodeData from the stream.
 *                                      Looped through the number of roads and populated array of roads and array of trafficSignals
 *                                          from the corresponding roadData rows.
 *                                      Looped through the number of nodes and populated appropriate array lists then entered
 *                                          these lists into array of SignalGroup.</li>
 *          <li> 1.07a | 11/09/2016:    Changed main to runnable thread. Class changed from main to actual class. Brought variables to 
 *                                      match standard java coding characteristics</li>
 *          <li> 1.07a | 11/14/2016:    Changed array of roads, traffic signals, and signal groups to array lists.
 *                                      Cleaned up logic for setting signal order.
 *                                      Added deepClone, getRoadLength, getRoadSpeed methods.
 *                                      match standard java coding characteristics</li>
 *          <li> 1.10b | 11/27/2016:    Fixed carQueue read/write methods.
 *      </ul>
 */
public class ReadExcel implements Runnable{
    
    private final XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private FileInputStream file = null;
    private final ArrayList<TrafficSignal> trafficSignals = new ArrayList<>();
    private final ArrayList<SignalGroup> signalGroup = new ArrayList<>();
    private final ArrayList<Road> roads = new ArrayList<>();
    private String progress;
    
    protected boolean threadHasRun;
    
    private int numRoads;
    private int numNodes;
    
    public ReadExcel() throws FileNotFoundException, IOException {
         
        file = new FileInputStream(new File("data.xlsx"));
        //Create Workbook instance holding reference to .xlsx file
        workbook = new XSSFWorkbook(file);
               
        //Set thread flag
        threadHasRun = false;
        
        progress = "";
    }

    @Override
    public void run(){              //this could be put in the constructor, but it is better suited as a thread so we can check progress & maintain responsiveness in the user gui
        
        //Get first sheet from the workbook
        sheet = workbook.getSheetAt(0);

        //Set value of row and column and create appropriate size array roadData
        int rowNum = sheet.getLastRowNum() + 1;
        numRoads = rowNum;
        int colNum = sheet.getRow(0).getLastCellNum();
        String[][] roadData = new String[rowNum][colNum];
        
        progress = ".2";
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
        
        progress = ".4";
        //Get second sheet from the workbook
        sheet = workbook.getSheetAt(1);
        rowNum = sheet.getLastRowNum() + 1;
        numNodes = rowNum;
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
        
        progress = ".5";
        //Loop through and populate array with roads
        //coords array is given to constructor in (x,y) order
        int[] coords = new int[2];
        for (int i = 0; i <= numRoads-1; i++) {
            coords[0] = Integer.parseInt(roadData[i][7]);
            coords[1] = Integer.parseInt(roadData[i][8]);
            //Constructors for Road and TrafficSignal
            roads.add(new Road(roadData[i][2], roadData[i][6], Double.parseDouble(roadData[i][3]), Integer.parseInt(roadData[i][4])));
            trafficSignals.add(new TrafficSignal(Integer.parseInt(roadData[i][5]), roads.get(i), roadData[i][0]+roadData[i][1], coords));
        }
       
        
        //Loops through the number of nodes to get base node for each group
        for (int i = 0; i <= numNodes-1; i++) {
            String tempNode = nodeData[i][0];       //get target node from nodeData array
            ArrayList<TrafficSignal> exitTemp = new ArrayList<>();
            ArrayList<TrafficSignal> signalTemp = new ArrayList<>();
            ArrayList<TrafficSignal> signalCopy = new ArrayList<>();
            
            //loops through number of roads to find traffic signals
            //Those that start with same ID as the node are added as exits
            //Thise that end with same ID as the node are added as entering traffic signals
            for (int j = 0; j <= numRoads-1; j++) {
                if (trafficSignals.get(j).getIdentifier().startsWith(tempNode))
                    exitTemp.add(trafficSignals.get(j));
                if (trafficSignals.get(j).getIdentifier().endsWith(tempNode))
                    signalTemp.add(trafficSignals.get(j));       
            }
            
            //deep copy signalTemp (the signals at that node)
            //signalCopy used to populate signalOrder to pass operation order to constructor
            for (int k = 0; k < signalTemp.size(); k++){
                signalCopy.add((TrafficSignal)deepClone(signalTemp.get(k)));
            }
            
            //Create arrays for tracking order of signals since there are no more than two types of signals per node
            //And array size trackers for updating their elements
            TrafficSignal[] tempSignalA = new TrafficSignal[4];
            TrafficSignal[] tempSignalB = new TrafficSignal[3];
            int tempSignalASize = 0;
            int tempSignalBSize = 0;

            progress = ".7";
            //While the signals list is not empty this loop runs
            //It places the first signal in tempSignalA
            //Then any subsequent signals that have the same signalType as the next index of tempSignalA
            //          Added a getSignalType() method in order to compare signals to those already in array
            //Else it puts the trafficSignal in the tempSignalB array
            while (!(signalCopy.isEmpty())) {
                if (tempSignalASize == 0) {
                    tempSignalA[0] = signalCopy.get(0);
                    signalCopy.remove(0);
                    tempSignalASize++;
                } else if (signalCopy.get(0).getSignalType() == tempSignalA[0].getSignalType()) {
                    tempSignalA[tempSignalASize] = signalCopy.get(0);
                    signalCopy.remove(0);
                    tempSignalASize++;
                } else {
                    tempSignalB[tempSignalBSize] = signalCopy.get(0);
                    signalCopy.remove(0);
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

            //switch to copy tempSignalA and B arrays to an array of appropriate length using tempSignalA and B Size variables
            switch (tempSignalASize) {
                case 1:
                    switch (tempSignalBSize) {
                        case 0:
                            a1[0] = tempSignalA[0];
                            signalOrder.add(a1);
                            break;
                        case 1:
                            a1[0] = tempSignalA[0];
                            b1[0] = tempSignalB[0];
                            signalOrder.add(a1);
                            signalOrder.add(b1);
                            break;
                        case 2:
                            a1[0] = tempSignalA[0];
                            b2[0] = tempSignalB[0];
                            b2[1] = tempSignalB[1];
                            signalOrder.add(a1);
                            signalOrder.add(b2);
                            break;
                        default:
                            a1[0] = tempSignalA[0];
                            signalOrder.add(a1);
                            signalOrder.add(tempSignalB);
                            break;
                    }
                    break;
                case 2:
                    switch (tempSignalBSize) {
                        case 0:
                            a2[0] = tempSignalA[0];
                            a2[1] = tempSignalA[1];
                            signalOrder.add(a2);
                            break;
                        case 1:
                            a2[0] = tempSignalA[0];
                            a2[1] = tempSignalA[1];
                            b1[0] = tempSignalB[0];
                            signalOrder.add(a2);
                            signalOrder.add(b1);
                            break;
                        case 2:
                            a2[0] = tempSignalA[0];
                            a2[1] = tempSignalA[1];
                            b2[0] = tempSignalB[0];
                            b2[1] = tempSignalB[1];
                            signalOrder.add(a2);
                            signalOrder.add(b2);
                            break;
                        default:
                            a2[0] = tempSignalA[0];
                            a2[1] = tempSignalA[1];
                            signalOrder.add(a2);
                            signalOrder.add(tempSignalB);
                            break;
                    }
                    break;
                case 3:
                    switch (tempSignalBSize) {
                        case 0:
                            a3[0] = tempSignalA[0];
                            a3[1] = tempSignalA[1];
                            a3[2] = tempSignalA[2];
                            signalOrder.add(a3);
                            break;
                        case 1:
                            a3[0] = tempSignalA[0];
                            a3[1] = tempSignalA[1];
                            a3[2] = tempSignalA[2];
                            b1[0] = tempSignalB[0];
                            signalOrder.add(a3);
                            signalOrder.add(b1);
                            break;
                        case 2:
                            a3[0] = tempSignalA[0];
                            a3[1] = tempSignalA[1];
                            a3[2] = tempSignalA[2];
                            b2[0] = tempSignalB[0];
                            b2[1] = tempSignalB[1];
                            signalOrder.add(a3);
                            signalOrder.add(b2);
                            break;
                        default:
                            a3[0] = tempSignalA[0];
                            a3[1] = tempSignalA[1];
                            a3[2] = tempSignalA[2];
                            signalOrder.add(a3);
                            signalOrder.add(tempSignalB);
                            break;
                    }
                    break;
                default:
                    switch (tempSignalBSize) {
                        case 0:
                            signalOrder.add(tempSignalA);
                            break;
                        case 1:
                            b1[0] = tempSignalB[0];
                            signalOrder.add(tempSignalA);
                            signalOrder.add(b1);
                            break;
                        case 2:
                            b2[0] = tempSignalB[0];
                            b2[1] = tempSignalB[1];
                            signalOrder.add(tempSignalA);
                            signalOrder.add(b2);
                            break;
                        default:
                            signalOrder.add(tempSignalA);
                            signalOrder.add(tempSignalB);
                            break;
                    }
                    break;
            }

            //Constructor for SignalGroup
            signalGroup.add(new SignalGroup(exitTemp, signalTemp, signalOrder));
        }
        
        try {
            file.close();
        } catch (IOException ex) {
            Logger.getLogger(ReadExcel.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        progress = "1";
        threadHasRun = true;
    }
    

     /**
     * returns the nextRoad's distance/length
     * 
     * @param nextRoad
     * @return the distance/length of the nextRoad.
     * @author Chris Tisdale
     * @since 1.07a
     */
    public static double getRoadLength(TrafficSignal nextRoad) {
        return nextRoad.getSourceRoad().getDistance();
    }

    /**
     * returns the nextRoad's speed limit
     * 
     * @param nextRoad
     * @return the speed limit of the nextRoad.
     * @author Chris Tisdale
     * @since 1.07a
     */
    public static double getRoadSpeed(TrafficSignal nextRoad) {
        return nextRoad.getSourceRoad().getSpeedLimit();
    }
    
    /**
     * This will return a map created from the XLSX document. 
     * @return A map created from the XLSX document. This can only be called after this thread has been run, otherwise it will fail.
     * @since 1.07a
     */
    public Map getMap(){
        if(threadHasRun){
            return new Map(signalGroup, trafficSignals);
        }
        return null;
    }

    public String getProgress() {
        return progress;
    }
    
    /**
     * This method writes the serialized car queue to the carQueue.txt file.
     *
     * @param outCarQueue that is to be written to file.
     * @author Chris Tisdale
     * @since 1.08a
     */
    public void writeCarQueue(Queue<Car> outCarQueue) {
        try {
            PrintWriter pw = new PrintWriter("carQueue.txt");
            pw.close();
            FileOutputStream fos = new FileOutputStream("carQueue.txt"); 
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(outCarQueue);
            oos.close();
        } catch (IOException ex) {
            Logger.getLogger(ReadExcel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * This method reads the serialized car queue from carQueue.txt file.
     *
     * @return car queue.
     * @throws IOException
     * @author Chris Tisdale
     * @since 1.08a
     */
    public Queue<Car> readCarQueue() throws IOException {
        Queue<Car> carQueue = new LinkedList();
        FileInputStream fis = new FileInputStream("carQueue.txt");
        try (ObjectInputStream ois = new ObjectInputStream(fis)) {
                carQueue = (Queue<Car>) ois.readObject();
                ois.close();
            } catch (ClassNotFoundException | EOFException ex) {
                
            } finally{
                fis.close();
                PrintWriter pw = new PrintWriter("carQueue.txt");
                pw.close();
                return carQueue;
            }
    }

    /**
     * This method writes the serialized traffic signal queue to the signalQueue.txt file.
     *
     * @param outTrafficSignalQueue that is to be written to file.
     * @author Chris Tisdale
     * @since 1.08a
     */
    public void writeTrafficSignalQueue(Queue<TrafficSignal> outTrafficSignalQueue) {
        try (FileOutputStream fos = new FileOutputStream("signalQueue.txt"); ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(outTrafficSignalQueue);
            oos.close();
        } catch (IOException ex) {
            Logger.getLogger(ReadExcel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * This method reads the serialized traffic signal queue from signalQueue.txt file.
     *
     * @return trafficSignalQueue
     * @throws IOException
     * @author Chris Tisdale
     * @since 1.08a
     */
    public Queue<TrafficSignal> readTrafficSignalQueue() throws IOException {
        Queue<TrafficSignal> trafficSignalQueue = null;
        FileInputStream fis = new FileInputStream("signalQueue.txt");
        try (ObjectInputStream ois = new ObjectInputStream(fis)) {
            try {
                trafficSignalQueue = (Queue<TrafficSignal>) ois.readObject();
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(ReadExcel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return trafficSignalQueue;
    }

    /**
     * This method writes a car to the next row in the third sheet of data.xlsx
     * file. In the first column it writes the destination. In the second column
     * it writes the time alive. In the third column it writes the time waiting
     * at stop signs. In the fourth column it writes the time waiting at traffic
     * lights.
     *
     * @param outCar that is to be written to file.
     * @throws FileNotFoundException
     * @throws IOException
     * @author Chris Tisdale
     * @since 1.08a
     */
    public void writeCarOutput(Car outCar) throws FileNotFoundException, IOException {
        sheet = workbook.getSheetAt(2);
        int rowNum = sheet.getLastRowNum();


        //writes car output to next row
        XSSFRow row = sheet.createRow(rowNum + 1);
        XSSFCell cell = row.getCell(0);
        if (cell == null) {
            cell = row.createCell(0);
        }
        cell.setCellType(Cell.CELL_TYPE_STRING);
        cell.setCellValue(outCar.getEndPoint()); //these methods will need to be implemented


        cell = row.createCell(1);
        cell.setCellType(Cell.CELL_TYPE_NUMERIC);
        cell.setCellValue(outCar.getAliveTime());


        cell = row.createCell(2);
        cell.setCellType(Cell.CELL_TYPE_NUMERIC);
        cell.setCellValue(outCar.getTimeAtStopSigns());


        cell = row.createCell(3);
        cell.setCellType(Cell.CELL_TYPE_NUMERIC);
        cell.setCellValue(outCar.getTimeAtSignalLight());


        try (FileOutputStream fileOut = new FileOutputStream("data.xlsx")) {
            workbook.write(fileOut);
            fileOut.close();
        }
    }

    /**
     * This method iterates over all the rows except for the header on the sheet
     * that holds the written car output in data.xlsx and deletes them. This must be run at
     * the beginning or end of the program in order to ensure that data from the
     * previous runs are not included in the car output sheet.
     *
     * @throws IOException
     * @author Chris Tisdale
     * @since 1.08a
     */
    public void resetCarOutput() throws IOException {
        sheet = workbook.getSheetAt(2);
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            XSSFRow row = sheet.getRow(i);
            sheet.removeRow(row);
        }

        try (FileOutputStream fileOut = new FileOutputStream("data.xlsx")) {
            workbook.write(fileOut);
            fileOut.close();
        }
    }
  
    /**
     * This method makes a "deep clone" of any Java object it is given.
     * Algorithm for this method from alvinalexander.com
     * @param object
     * @return 
     */
    public static Object deepClone(Object object) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(object);
            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bais);
            return ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
