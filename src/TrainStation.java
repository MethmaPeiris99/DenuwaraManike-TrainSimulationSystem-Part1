import com.mongodb.Block;
import com.mongodb.client.*;
import org.bson.Document;
import java.util.logging.Level;
import java.util.*;
import java.io.*;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import java.time.LocalDate;

public class TrainStation extends Application {
    Scanner input = new Scanner(System.in);
    static final int MAXIMUM_SEATS = 42;

    private PassengerQueue trainQueue = new PassengerQueue(); //creating an object from the PassengerQueue class
    private Passenger[] waitingRoomList = new Passenger[MAXIMUM_SEATS]; //used to store arrived passenger's Passenger class objects

    public static void main(String[] args) {
        launch();
    }
    @Override
    public void start(Stage primaryStage) throws Exception {

        System.out.println("---D E N U W A R A   M A N I K E  I N T E R C I T Y  E X P R E S S---");
        System.out.println("                           FIRST CLASS                        ");
        System.out.println("              COLOMBO TO BADULLA - BADULLA TO COLOMBO              ");
        System.out.println("---------------------------------------------------------------------");

        Stage stageOne = new Stage(); //stage for the GUI used to pick route
        stageOne.setTitle("D E N U W A R A   M A N I K E - Train seat booking program");
        AnchorPane layOne = new AnchorPane(); //layout for the GUI used to pick route
        layOne.setStyle("-fx-background-color:#ffa463;");

        Label pickTrip = new Label("- SELECT YOUR DESTINATION -");
        pickTrip.setStyle("-fx-font-weight: bold ; -fx-font-size:24; -fx-font-family:Anton");
        pickTrip.setLayoutX(120);
        pickTrip.setLayoutY(50);

        //-------------label used to inform select a route-------------//
        Label tripSelectAlert = new Label("* Select your destination");
        tripSelectAlert.setLayoutX(180);
        tripSelectAlert.setLayoutY(120);
        tripSelectAlert.setStyle("-fx-text-fill:red; -fx-font-size:18;");
        tripSelectAlert.setVisible(false);

        ChoiceBox<String> choiceBox=new ChoiceBox<>();
        //--------adding items to the choice box--------//
        choiceBox .getItems().add("Badulla");
        choiceBox .getItems().add("Colombo");
        choiceBox .getItems().add("--CHOOSE YOUR DESTINATION--");
        choiceBox.setValue("--CHOOSE YOUR DESTINATION--"); //setting the displayed value of choice box
        choiceBox.setLayoutX(160);
        choiceBox.setLayoutY(90);

        Button nextButton=new Button("Next");
        nextButton.setLayoutX(510);
        nextButton.setLayoutY(240);
        nextButton.setStyle("-fx-pref-height:40; -fx-pref-width:70;");

        String[] selectedDestination = new String[1]; //used to store the value selected from the choice box
        String[] selectedDate = new String[1]; //used to store the local date value

        nextButton.setOnAction(event -> {
            if (choiceBox.getValue().equals("--CHOOSE YOUR DESTINATION--")){
                tripSelectAlert.setVisible(true); //displaying a message if user didn't select a route
            }else {
                selectedDestination[0] = choiceBox.getValue();
                if(selectedDestination[0].equals("Badulla")) {
                    System.out.println("Colombo to "+selectedDestination[0]);
                }
                else{
                    System.out.println("Badulla to "+selectedDestination[0]);
                }
                stageOne.close();
            }
        });

        layOne.getChildren().addAll(pickTrip,choiceBox,tripSelectAlert,nextButton); //adding all the GUI elements to the layout
        Scene sceneOne = new Scene(layOne,600,300); //creating a object from the scene class
        stageOne.setScene(sceneOne);
        stageOne.showAndWait();

        //-----------getting the current date value-----------//
        LocalDate currentDate = LocalDate.now();
        selectedDate[0] = String.valueOf(currentDate);
        System.out.println(selectedDate[0]);

        HashMap<String, ArrayList<String>> passengersForToday = new HashMap<>(); //used to store passenger names on a particular date
        ArrayList<String> allPassengers = new ArrayList<String>(); //used to store hashmap values based on current date
        String[] temporaryArray = new String[MAXIMUM_SEATS]; //used to store null values

        if(selectedDestination[0].equals("Badulla")){
            java.util.logging.Logger.getLogger("org.mongodb.driver").setLevel(Level.SEVERE);
            MongoClient monClient = MongoClients.create("mongodb://LocalHost:27017"); //create client
            MongoDatabase monDatabase = monClient.getDatabase("TrainSeatBooking_Denuwara"); //create database
            MongoCollection<Document> monCollection1 = monDatabase.getCollection("DenuwaraManike_PassengerDetails_FromColombo"); //create table

            monCollection1.find().forEach(new Block<Document>() { //finding data stored in the collection
                @Override
                public void apply(Document document) {
                    for(String dateAndRoute:document.keySet()){ //creating set of keys in the map which contains in document
                        if(dateAndRoute.equals("_id"))
                            continue;
                        Object arrayObject = document.get(dateAndRoute); //getting the value relevant to the key in map
                        ArrayList passengerName = (ArrayList) arrayObject; //setting the value in map as an array list index
                        passengersForToday.put(dateAndRoute,passengerName);
                    }
                }
            });
            String keyOfMap = selectedDate[0] + selectedDestination[0]; //concatenating date and route value into a single variable
            System.out.println("Loaded successfully COLOMBO TO BADULLA trip data!");

            if(passengersForToday.containsKey(keyOfMap)){ //checking if the current date and selected date is including the map as a key
                allPassengers = passengersForToday.get(keyOfMap); //adding map values into the array list
                System.out.println(allPassengers);
            }
            else{
                for(int i=0; i<MAXIMUM_SEATS; i++){
                    temporaryArray[i] = null;
                    allPassengers.add(temporaryArray[i]); //adding all the null values in array into array list
                }
                System.out.println(allPassengers+"\nNO ONE RESERVED A BOOKING TODAY!");
            }

        }else{
            java.util.logging.Logger.getLogger("org.mongodb.driver").setLevel(Level.SEVERE);
            MongoClient monClient = MongoClients.create("mongodb://LocalHost:27017");
            MongoDatabase monDatabase = monClient.getDatabase("TrainSeatBooking_Denuwara");
            MongoCollection<Document> monCollection2 = monDatabase.getCollection("DenuwaraManike_PassengerDetails_FromBadulla");

            monCollection2.find().forEach(new Block<Document>() {
                @Override
                public void apply(Document document) {
                    for(String dateAndRoute:document.keySet()){
                        if(dateAndRoute.equals("_id"))
                            continue;
                        Object arrayObject = document.get(dateAndRoute);
                        ArrayList passengerName = (ArrayList) arrayObject;
                        passengersForToday.put(dateAndRoute,passengerName);
                    }
                }
            });
            String keyOfMap = selectedDate[0] + selectedDestination[0];
            System.out.println("Loaded successfully BADULLA TO COLOMBO trip data!");

            if(passengersForToday.containsKey(keyOfMap)){
                allPassengers = passengersForToday.get(keyOfMap);
                System.out.println(allPassengers);
            }
            else{
                for(int i=0; i<MAXIMUM_SEATS; i++){
                    temporaryArray[i] = null;
                    allPassengers.add(temporaryArray[i]);
                }
                System.out.println(allPassengers+"\nNO ONE RESERVED A BOOKING TODAY!");
            }
        }

        ArrayList<Passenger> temporaryArrayListOfObjects = new ArrayList<>(); /*used to store Passenger class objects
                                                                               temporary before adding to the waiting room*/


        ArrayList<Passenger> objectArrayList = new ArrayList<>(); //used to store arrived passenger's Passenger class objects

        for(int i=0; i<MAXIMUM_SEATS; i++) {
            if (allPassengers.get(i) != null) {
                Passenger passengerDetail = new Passenger(); //creating Passenger class objects for all the booked passengers
                passengerDetail.setFirstName(allPassengers.get(i)); //setting the name of the booked passenger to the object
                passengerDetail.setSeatNumber(i+1); //setting the seat number of the booked passenger to the object
                temporaryArrayListOfObjects.add(passengerDetail);
            }
        }

        System.out.println("------------------ ONLINE BOOKINGS ------------------");
        for (int i=0; i< temporaryArrayListOfObjects.size(); i++){
            System.out.print("NAME: "+temporaryArrayListOfObjects.get(i).getFirstName()+" -> "); //displaying booked passenger's name
            System.out.println("SEAT NUMBER: "+temporaryArrayListOfObjects.get(i).getSeatNumber()); //displaying booked passenger's seat number
        }
        System.out.println("-----------------------------------------------------");

        int countOfAdding = 0; //used to get the count how many times passenger objects were added to the waiting room array and object array list
        System.out.print("IS THERE ANY PASSENGERS TO ADD TO THE WAITING ROOM? y/n: ");
        String answerForAdding = input.next().toLowerCase();
        while(!answerForAdding.equals("n") ) { //executes until entering "n"
            try {
                if (answerForAdding.matches("[a-zA-Z]*")) { //checking whether the entered value is an alphabetical value or not
                    if (answerForAdding.equals("y")) {
                        System.out.print("Enter SEAT NUMBER: ");
                        int seatNumberOfArrived = input.nextInt();
                        if (seatNumberOfArrived > 0 && seatNumberOfArrived < 42) { //checking whether the entered value is an actual seat number or not
                            for (int i = 0; i < temporaryArrayListOfObjects.size(); i++) {
                                if (seatNumberOfArrived == temporaryArrayListOfObjects.get(i).getSeatNumber()) { //checking whether the entered seat number is in the list
                                    waitingRoomList[i] = temporaryArrayListOfObjects.get(i); /*adding passenger object relevant to that seat number
                                                                                               to the waiting room list and an array list*/
                                    objectArrayList.add(temporaryArrayListOfObjects.get(i));
                                }
                            }
                            countOfAdding++;
                            System.out.print("IS THERE ANY PASSENGERS TO ADD TO THE WAITING ROOM? y/n: ");
                            answerForAdding = input.next().toLowerCase();
                        } else {
                            System.out.println("OUT OF RANGE!");
                        }
                    }
                }
                else{
                    System.out.println("INVALID INPUT, PLEASE ENTER AGAIN!");
                    System.out.print("IS THERE ANY PASSENGERS TO ADD TO THE WAITING ROOM? y/n: ");
                    answerForAdding = input.next().toLowerCase();
                }
            }
            catch(Exception e){
                System.out.println("INVALID INPUT, PLEASE ENTER AGAIN!");
                System.out.print("IS THERE ANY PASSENGERS TO ADD TO THE WAITING ROOM? y/n: ");
                input.nextLine();
                answerForAdding = input.next().toLowerCase();
            }
        }
        if(countOfAdding ==0){ //executes if the user entered "n" without adding objects to the waiting room
            System.out.println("ALL BOOKINGS WERE CANCELLED");
        }
        System.out.println("==================== WAITING ROOM ====================");
        for (int i = 0; i < waitingRoomList.length; i++) {
            if(waitingRoomList[i] != null) {
                System.out.print("NAME: " + waitingRoomList[i].getFirstName() + " -> ");
                System.out.println("SEAT NUMBER: " + waitingRoomList[i].getSeatNumber());
            }
        }
        System.out.println("======================================================");

        String choice = "";
        while (!choice.equalsIgnoreCase("Q")) { //executing until "q" entered while ignoring upper and lower cases
            System.out.println("----------------------------------------------------------------");
            System.out.println("|      Enter \"A\" - ADD passengers to the Train queue           |");
            System.out.println("|      Enter \"V\" - VIEW the Train queue                        |");
            System.out.println("|      Enter \"D\" - DELETE passenger from the Train queue       |");
            System.out.println("|      Enter \"S\" - STORE Train queue data                      |");
            System.out.println("|      Enter \"L\" - LOAD stored data                            |");
            System.out.println("|      Enter \"R\" - RUN the simulation and produce report       |");
            System.out.println("|      Enter \"Q\" - QUIT                                        |");
            System.out.println("----------------------------------------------------------------");
            System.out.print("                        ENTER YOUR CHOICE: ");
            choice = input.next().toUpperCase(); //getting input format of uppercase

            try {
                switch (choice) {
                    case "A":
                        addToTrainQueue(objectArrayList);
                        break;
                    case "V":
                        viewTrainQueue();
                        break;
                    case "D":
                        deleteFromTrainQueue();
                        break;
                    case "S":
                        storeDataToFile(objectArrayList);
                        break;
                    case "L":
                        loadDataToStructure(objectArrayList);
                        break;
                    case "R":
                        reportAndSimulation();
                        break;
                    default:
                        if (!choice.equalsIgnoreCase("Q")) { //setting condition to avoid print the following when "q" entered to quit
                            System.out.println("Can't enter any input other than A,V,D,S,L,R and Q !");
                        }
                }
            } catch (Exception e) {
                System.out.println("SOMETHING WENT WRONG!");
            }
        }
    }
    private void addToTrainQueue(ArrayList<Passenger> objectArrayList) {
        System.out.println("==================== WAITING ROOM ====================");
        for (int i = 0; i < objectArrayList.size(); i++) {
            if(objectArrayList.get(i) != null) {
                System.out.print("NAME: " + objectArrayList.get(i).getFirstName() + " -> ");
                System.out.println("SEAT NUMBER: " + objectArrayList.get(i).getSeatNumber());
            }
        }
        System.out.println("======================================================");

        int minimumDiceNumber = 1;
        int maximumDiceNumber = 6;
        int randomCount = (int) (Math.random()*(maximumDiceNumber - minimumDiceNumber+1)+minimumDiceNumber);

        if(randomCount <= objectArrayList.size()){
            for(int i=0; i < randomCount; i++){ //used to add passenger objects equal to the value of generated random number
                trainQueue.add(objectArrayList.get(0)); //adding the first object to the train queue
                objectArrayList.remove(0); //removing the added object
            }

            displayTrainQueueAfterAdd();
        }
        else{
            System.out.println("THERE'S NO ENOUGH PASSENGERS TO ADD TO THE TRAIN QUEUE!");
        }
    }

    private void displayTrainQueueAfterAdd(){ //used to display added passengers to the train queue
        Label labelAddView = new Label("- TRAIN QUEUE -");
        labelAddView.setStyle("-fx-font-weight: bold; -fx-font-size:40; -fx-font-family:Calibri; -fx-text-fill: white");
        labelAddView.setLayoutX(820);
        labelAddView.setLayoutY(50);

        HBox queueRow1 = new HBox(20);
        queueRow1.setLayoutX(35);
        queueRow1.setLayoutY(200);
        for (int i = 0; i <= 20; i += 2) {
            Button buttonSet1 = new Button();
            buttonSet1.setPrefSize(150, 80);
            queueRow1.getChildren().addAll(buttonSet1);
            if (trainQueue.getQueueArray()[i] != null) {
                buttonSet1.setText(trainQueue.getQueueArray()[i].getFirstName()+"\nSEAT NO - "+trainQueue.getQueueArray()[i].getSeatNumber());
                buttonSet1.setStyle("-fx-font-weight: bold; -fx-font-size:18; -fx-text-fill: white; -fx-background-color:#00b3b3");
            }
            else {
                buttonSet1.setText("EMPTY");
                buttonSet1.setStyle("-fx-font-weight: bold; -fx-font-size:18; -fx-text-fill: red; -fx-background-color:#ffa463");
            }
        }
        HBox queueRow2 = new HBox(20);
        queueRow2.setLayoutX(35);
        queueRow2.setLayoutY(290);
        for (int i = 1; i <= 21; i += 2) {
            Button buttonSet2 = new Button();
            buttonSet2.setPrefSize(150, 80);
            queueRow2.getChildren().addAll(buttonSet2);
            if (trainQueue.getQueueArray()[i] != null) {
                buttonSet2.setText(trainQueue.getQueueArray()[i].getFirstName()+"\nSEAT NO - "+trainQueue.getQueueArray()[i].getSeatNumber());
                buttonSet2.setStyle("-fx-font-weight: bold; -fx-font-size:18; -fx-text-fill: white; -fx-background-color:#00b3b3");
            }
            else {
                buttonSet2.setText("EMPTY");
                buttonSet2.setStyle("-fx-font-weight: bold; -fx-font-size:18; -fx-text-fill: red; -fx-background-color:#ffa463");
            }
        }
        HBox queueRow3 = new HBox(20);
        queueRow3.setLayoutX(35);
        queueRow3.setLayoutY(400);
        for (int i = 23; i <= 41; i += 2) {
            Button buttonSet3 = new Button();
            buttonSet3.setPrefSize(150, 80);
            queueRow3.getChildren().addAll(buttonSet3);
            if (trainQueue.getQueueArray()[i] != null) {
                buttonSet3.setText(trainQueue.getQueueArray()[i].getFirstName()+"\nSEAT NO - "+trainQueue.getQueueArray()[i].getSeatNumber());
                buttonSet3.setStyle("-fx-font-weight: bold; -fx-font-size:18; -fx-text-fill: white; -fx-background-color:#00b3b3");
            }
            else {
                buttonSet3.setText("EMPTY");
                buttonSet3.setStyle("-fx-font-weight: bold; -fx-font-size:18; -fx-text-fill: red; -fx-background-color:#ffa463");
            }
        }
        HBox queueRow4 = new HBox(20);
        queueRow4.setLayoutX(35);
        queueRow4.setLayoutY(490);
        for (int i = 22; i <= 40; i += 2) {
            Button buttonSet4 = new Button();
            buttonSet4.setPrefSize(150, 80);
            queueRow4.getChildren().addAll(buttonSet4);
            if (trainQueue.getQueueArray()[i] != null) {
                buttonSet4.setText(trainQueue.getQueueArray()[i].getFirstName()+"\nSEAT NO - "+trainQueue.getQueueArray()[i].getSeatNumber());
                buttonSet4.setStyle("-fx-font-weight: bold; -fx-font-size:18; -fx-text-fill: white; -fx-background-color:#00b3b3");
            }
            else {
                buttonSet4.setText("EMPTY");
                buttonSet4.setStyle("-fx-font-weight: bold; -fx-font-size:18; -fx-text-fill: red; -fx-background-color:#ffa463");
            }
        }
        Stage viewAddStage = new Stage();
        viewAddStage.setTitle("D E N U W A R A   M A N I K E - ADD TO QUEUE");
        AnchorPane layoutAddView = new AnchorPane();
        layoutAddView.getChildren().addAll(queueRow1,queueRow2,queueRow3,queueRow4,labelAddView);
        layoutAddView.setStyle("-fx-background-color:#999999;");
        Scene addCusScene = new Scene(layoutAddView, 1900, 800);
        viewAddStage.setScene(addCusScene);
        viewAddStage.showAndWait();
    }

    private void viewTrainQueue() {
        Label labelView = new Label("- VIEW TRAIN QUEUE -");
        labelView.setStyle("-fx-font-weight: bold; -fx-font-size:32; -fx-font-family:Calibri; -fx-text-fill: white");
        labelView.setLayoutX(820);
        labelView.setLayoutY(440);

        HBox queueRow1 = new HBox(20);
        queueRow1.setLayoutX(35);
        queueRow1.setLayoutY(490);
        for (int i = 0; i <= 20; i += 2) {
            Button buttonSet1 = new Button();
            buttonSet1.setPrefSize(150, 80);
            queueRow1.getChildren().addAll(buttonSet1);
            if (trainQueue.getQueueArray()[i] != null) {
                buttonSet1.setText(trainQueue.getQueueArray()[i].getFirstName()+"\nSEAT NO - "+trainQueue.getQueueArray()[i].getSeatNumber());
                buttonSet1.setStyle("-fx-font-weight: bold; -fx-font-size:18; -fx-text-fill: white; -fx-background-color:#00b3b3");
            }
            else {
                buttonSet1.setText("EMPTY");
                buttonSet1.setStyle("-fx-font-weight: bold; -fx-font-size:18; -fx-text-fill: red; -fx-background-color:#ffa463");
            }
        }
        HBox queueRow2 = new HBox(20);
        queueRow2.setLayoutX(35);
        queueRow2.setLayoutY(580);
        for (int i = 1; i <= 21; i += 2) {
            Button buttonSet2 = new Button();
            buttonSet2.setPrefSize(150, 80);
            queueRow2.getChildren().addAll(buttonSet2);
            if (trainQueue.getQueueArray()[i] != null) {
                buttonSet2.setText(trainQueue.getQueueArray()[i].getFirstName()+"\nSEAT NO - "+trainQueue.getQueueArray()[i].getSeatNumber());
                buttonSet2.setStyle("-fx-font-weight: bold; -fx-font-size:18; -fx-text-fill: white; -fx-background-color:#00b3b3");
            }
            else {
                buttonSet2.setText("EMPTY");
                buttonSet2.setStyle("-fx-font-weight: bold; -fx-font-size:18; -fx-text-fill: red; -fx-background-color:#ffa463");
            }
        }
        HBox queueRow3 = new HBox(20);
        queueRow3.setLayoutX(35);
        queueRow3.setLayoutY(720);
        for (int i = 23; i <= 41; i += 2) {
            Button buttonSet3 = new Button();
            buttonSet3.setPrefSize(150, 80);
            queueRow3.getChildren().addAll(buttonSet3);
            if (trainQueue.getQueueArray()[i] != null) {
                buttonSet3.setText(trainQueue.getQueueArray()[i].getFirstName()+"\nSEAT NO - "+trainQueue.getQueueArray()[i].getSeatNumber());
                buttonSet3.setStyle("-fx-font-weight: bold; -fx-font-size:18; -fx-text-fill: white; -fx-background-color:#00b3b3");
            }
            else {
                buttonSet3.setText("EMPTY");
                buttonSet3.setStyle("-fx-font-weight: bold; -fx-font-size:18; -fx-text-fill: red; -fx-background-color:#ffa463");
            }
        }
        HBox queueRow4 = new HBox(20);
        queueRow4.setLayoutX(35);
        queueRow4.setLayoutY(810);
        for (int i = 22; i <= 40; i += 2) {
            Button buttonSet4 = new Button();
            buttonSet4.setPrefSize(150, 80);
            queueRow4.getChildren().addAll(buttonSet4);
            if (trainQueue.getQueueArray()[i] != null) {
                buttonSet4.setText(trainQueue.getQueueArray()[i].getFirstName()+"\nSEAT NO - "+trainQueue.getQueueArray()[i].getSeatNumber());
                buttonSet4.setStyle("-fx-font-weight: bold; -fx-font-size:18; -fx-text-fill: white; -fx-background-color:#00b3b3");
            }
            else {
                buttonSet4.setText("EMPTY");
                buttonSet4.setStyle("-fx-font-weight: bold; -fx-font-size:18; -fx-text-fill: red; -fx-background-color:#ffa463");
            }
        }
        Image image = new Image("file:trainQueue.jpg");
        ImageView imgView = new ImageView();
        imgView.setImage(image);
        imgView.setFitWidth(1920);
        imgView.setFitHeight(420);

        Stage viewCusStage = new Stage();
        viewCusStage.setTitle("D E N U W A R A   M A N I K E - VIEW QUEUE");
        AnchorPane layoutView = new AnchorPane();
        layoutView.getChildren().addAll(queueRow1,queueRow2,queueRow3,queueRow4,imgView,labelView);
        layoutView.setStyle("-fx-background-color:#999999;");
        Scene addCusScene = new Scene(layoutView, 1900, 1000);
        viewCusStage.setScene(addCusScene);
        viewCusStage.setMaximized(true);
        viewCusStage.showAndWait();
    }

    private void deleteFromTrainQueue() {
        try {
            System.out.print("ENTER SEAT NUMBER TO DELETE FROM TRAIN QUEUE: ");
            int seatNumberToDelete = input.nextInt();
            if (0 < seatNumberToDelete && seatNumberToDelete < 42) { //checking whether the entered value is in the range of actual seat numbers
                trainQueue.deletePassengerFromQueue(seatNumberToDelete);
                trainQueue.swapNullAfterDelete();
            } else {
                System.out.println("OUT OF RANGE!");
            }
        }
        catch (InputMismatchException e){
            System.out.println("INVALID INPUT!");
        }
    }

    private void storeDataToFile(ArrayList<Passenger> objectArrayList) throws IOException {
        File arrivedPassengerDetailsFile = new File("Arrived_Passenger_Details.txt"); //used to represent a particular file in hard disk
        FileOutputStream arrivedFileOut = new FileOutputStream(arrivedPassengerDetailsFile); //used to write java objects to the file
        ObjectOutputStream arrivedObjectOut = new ObjectOutputStream(arrivedFileOut);

        for (int i = 0; i < objectArrayList.size(); i++) {
            arrivedObjectOut.writeObject(objectArrayList.get(i)); //writing passenger objects to the file
        }
    }

    private void loadDataToStructure(ArrayList<Passenger> objectArrayList) throws IOException {
        File arrivedPassengerDetailsFile = new File("Arrived_Passenger_Details.txt");
        FileInputStream arrivedFileInput = new FileInputStream(arrivedPassengerDetailsFile); //used to read java objects from the file
        ObjectInputStream arrivedObjectInput = new ObjectInputStream(arrivedFileInput);

        while(true){
            try{
                Passenger readObjFromFile = (Passenger)arrivedObjectInput.readObject(); //reading passenger objects from file
                objectArrayList.add(readObjFromFile); //adding passenger objects to the data structure

                for(int i=0; i < objectArrayList.size(); i++){
                    System.out.println("NAME- "+objectArrayList.get(i).getFirstName()+
                                      "\nSEAT NUMBER- "+objectArrayList.get(i).getSeatNumber());
                }
            }
            catch (ClassNotFoundException | EOFException e){
                System.out.println("COMPLETED READING OBJECTS!");
                break;
            }
        }
    }

    private void reportAndSimulation() throws IOException {
        Passenger[] boardedPassengersArray = new Passenger[MAXIMUM_SEATS]; //used to store passenger objects removed from train queue
        ArrayList<Passenger> boardedArrayList = new ArrayList<>(); //used to store passenger objects removed from train queue

        for(int i=0; i<trainQueue.getQueueArray().length; i++){
            if(trainQueue.getQueueArray()[i] != null){
                int minimumDiceNumber = 1;
                int maximumDiceNumber = 6;

                int randomCount_one = (int) (Math.random()*(maximumDiceNumber - minimumDiceNumber+1)+minimumDiceNumber);
                int randomCount_two = (int) (Math.random()*(maximumDiceNumber - minimumDiceNumber+1)+minimumDiceNumber);
                int randomCount_three = (int) (Math.random()*(maximumDiceNumber - minimumDiceNumber+1)+minimumDiceNumber);

                trainQueue.getQueueArray()[i].setSecondsInQueue(randomCount_one,randomCount_two,randomCount_three); //setting waiting time for a passenger
                trainQueue.setMaxStayInQueue(trainQueue.getQueueArray()[i].getSecondsInQueue()); //setting the total waiting time of all passengers
                boardedPassengersArray[i] = trainQueue.getQueueArray()[i];
                boardedArrayList.add(trainQueue.getQueueArray()[i]);
                trainQueue.getQueueArray()[i] = null; //removing the boarded passenger from train queue
            }
        }
        trainQueue.setMaxLength(boardedArrayList.size()); //setting the maximum length of the array

        Label headLabel = new Label("- BOARDED PASSENGER DETAIL REPORT -");
        headLabel.setStyle("-fx-font-weight: bold; -fx-font-size:34; -fx-font-family:Calibri; -fx-text-fill: white");
        headLabel.setLayoutX(665);
        headLabel.setLayoutY(50);

        Label maxLengthLabel = new Label("MAXIMUM LENGTH OF THE QUEUE- "+trainQueue.getMaxLength()+" passengers");
        maxLengthLabel.setStyle("-fx-font-weight: bold; -fx-font-size:26; -fx-font-family:Calibri; -fx-text-fill: black");
        maxLengthLabel.setLayoutX(150);
        maxLengthLabel.setLayoutY(175);

        Label maxTime = new Label("MAXIMUM WAITING TIME- "+trainQueue.getMaxStayInQueue()+"s");
        maxTime.setStyle("-fx-font-weight: bold; -fx-font-size:26; -fx-font-family:Calibri; -fx-text-fill: black");
        maxTime.setLayoutX(770);
        maxTime.setLayoutY(175);

        Label averageTime = new Label("AVERAGE WAITING TIME- "+(trainQueue.getMaxStayInQueue()/trainQueue.getMaxLength())+"s");
        averageTime.setStyle("-fx-font-weight: bold; -fx-font-size:26; -fx-font-family:Calibri; -fx-text-fill: black");
        averageTime.setLayoutX(1360);
        averageTime.setLayoutY(175);

        HBox queueRow1 = new HBox(20);
        queueRow1.setLayoutX(75);
        queueRow1.setLayoutY(230);
        for (int i = 0; i<=5; i++) {
            Button buttonSet1 = new Button();
            buttonSet1.setPrefSize(275, 100);
            queueRow1.getChildren().addAll(buttonSet1);
            if (boardedPassengersArray[i] != null) {
                buttonSet1.setText("NAME - "+boardedPassengersArray[i].getFirstName()+"\nSEAT NO - "+boardedPassengersArray[i].getSeatNumber()+
                                   "\nWAITING TIME - "+boardedPassengersArray[i].getSecondsInQueue()+"s");
                buttonSet1.setStyle("-fx-font-weight: bold; -fx-font-size:18; -fx-text-fill: white; -fx-background-color:#00b3b3");
            }
            else {
                buttonSet1.setText("EMPTY");
                buttonSet1.setStyle("-fx-font-weight: bold; -fx-font-size:18; -fx-text-fill: red; -fx-background-color:#ffa463");
            }
        }
        HBox queueRow2 = new HBox(20);
        queueRow2.setLayoutX(75);
        queueRow2.setLayoutY(340);
        for (int i = 6; i <= 11; i++) {
            Button buttonSet2 = new Button();
            buttonSet2.setPrefSize(275, 100);
            queueRow2.getChildren().addAll(buttonSet2);
            if (boardedPassengersArray[i] != null) {
                buttonSet2.setText("NAME - "+boardedPassengersArray[i].getFirstName()+"\nSEAT NO - "+boardedPassengersArray[i].getSeatNumber()+
                                   "\nWAITING TIME - "+boardedPassengersArray[i].getSecondsInQueue()+"s");
                buttonSet2.setStyle("-fx-font-weight: bold; -fx-font-size:18; -fx-text-fill: white; -fx-background-color:#00b3b3");
            }
            else {
                buttonSet2.setText("EMPTY");
                buttonSet2.setStyle("-fx-font-weight: bold; -fx-font-size:18; -fx-text-fill: red; -fx-background-color:#ffa463");
            }
        }
        HBox queueRow3 = new HBox(20);
        queueRow3.setLayoutX(75);
        queueRow3.setLayoutY(450);
        for (int i = 12; i <= 17; i++) {
            Button buttonSet3 = new Button();
            buttonSet3.setPrefSize(275, 100);
            queueRow3.getChildren().addAll(buttonSet3);
            if (boardedPassengersArray[i] != null) {
                buttonSet3.setText("NAME - "+boardedPassengersArray[i].getFirstName()+"\nSEAT NO - "+boardedPassengersArray[i].getSeatNumber()+
                                   "\nWAITING TIME - "+boardedPassengersArray[i].getSecondsInQueue()+"s");
                buttonSet3.setStyle("-fx-font-weight: bold; -fx-font-size:18; -fx-text-fill: white; -fx-background-color:#00b3b3");
            }
            else {
                buttonSet3.setText("EMPTY");
                buttonSet3.setStyle("-fx-font-weight: bold; -fx-font-size:18; -fx-text-fill: red; -fx-background-color:#ffa463");
            }
        }
        HBox queueRow4 = new HBox(20);
        queueRow4.setLayoutX(75);
        queueRow4.setLayoutY(560);
        for (int i = 18; i <= 23; i++) {
            Button buttonSet4 = new Button();
            buttonSet4.setPrefSize(275, 100);
            queueRow4.getChildren().addAll(buttonSet4);
            if (boardedPassengersArray[i] != null) {
                buttonSet4.setText("NAME - "+boardedPassengersArray[i].getFirstName()+"\nSEAT NO - "+boardedPassengersArray[i].getSeatNumber()+
                                   "\nWAITING TIME - "+boardedPassengersArray[i].getSecondsInQueue()+"s");
                buttonSet4.setStyle("-fx-font-weight: bold; -fx-font-size:18; -fx-text-fill: white; -fx-background-color:#00b3b3");
            }
            else {
                buttonSet4.setText("EMPTY");
                buttonSet4.setStyle("-fx-font-weight: bold; -fx-font-size:18; -fx-text-fill: red; -fx-background-color:#ffa463");
            }
        }
        HBox queueRow5 = new HBox(20);
        queueRow5.setLayoutX(75);
        queueRow5.setLayoutY(670);
        for (int i = 24; i <= 29; i++) {
            Button buttonSet5 = new Button();
            buttonSet5.setPrefSize(275, 100);
            queueRow5.getChildren().addAll(buttonSet5);
            if (boardedPassengersArray[i] != null) {
                buttonSet5.setText("NAME - "+boardedPassengersArray[i].getFirstName()+"\nSEAT NO - "+boardedPassengersArray[i].getSeatNumber()+
                                   "\nWAITING TIME - "+boardedPassengersArray[i].getSecondsInQueue()+"s");
                buttonSet5.setStyle("-fx-font-weight: bold; -fx-font-size:18; -fx-text-fill: white; -fx-background-color:#00b3b3");
            }
            else {
                buttonSet5.setText("EMPTY");
                buttonSet5.setStyle("-fx-font-weight: bold; -fx-font-size:18; -fx-text-fill: red; -fx-background-color:#ffa463");
            }
        }
        HBox queueRow6 = new HBox(20);
        queueRow6.setLayoutX(75);
        queueRow6.setLayoutY(780);
        for (int i = 30; i <= 35; i++) {
            Button buttonSet6 = new Button();
            buttonSet6.setPrefSize(275, 100);
            queueRow6.getChildren().addAll(buttonSet6);
            if (boardedPassengersArray[i] != null) {
                buttonSet6.setText("NAME - "+boardedPassengersArray[i].getFirstName()+"\nSEAT NO - "+boardedPassengersArray[i].getSeatNumber()+
                                   "\nWAITING TIME - "+boardedPassengersArray[i].getSecondsInQueue()+"s");
                buttonSet6.setStyle("-fx-font-weight: bold; -fx-font-size:18; -fx-text-fill: white; -fx-background-color:#00b3b3");
            }
            else {
                buttonSet6.setText("EMPTY");
                buttonSet6.setStyle("-fx-font-weight: bold; -fx-font-size:18; -fx-text-fill: red; -fx-background-color:#ffa463");
            }
        }
        HBox queueRow7 = new HBox(20);
        queueRow7.setLayoutX(75);
        queueRow7.setLayoutY(890);
        for (int i = 36; i <= 41; i++) {
            Button buttonSet7 = new Button();
            buttonSet7.setPrefSize(275, 100);
            queueRow7.getChildren().addAll(buttonSet7);
            if (boardedPassengersArray[i] != null) {
                buttonSet7.setText("NAME - "+boardedPassengersArray[i].getFirstName()+"\nSEAT NO - "+boardedPassengersArray[i].getSeatNumber()+
                                   "\nWAITING TIME - "+boardedPassengersArray[i].getSecondsInQueue()+"s");
                buttonSet7.setStyle("-fx-font-weight: bold; -fx-font-size:18; -fx-text-fill: white; -fx-background-color:#00b3b3");
            }
            else {
                buttonSet7.setText("EMPTY");
                buttonSet7.setStyle("-fx-font-weight: bold; -fx-font-size:18; -fx-text-fill: red; -fx-background-color:#ffa463");
            }
        }
        Stage reportStage = new Stage();
        reportStage.setTitle("D E N U W A R A   M A N I K E - BOARDED PASSENGER DETAILS");
        AnchorPane layoutReport = new AnchorPane();
        layoutReport.getChildren().addAll(queueRow1,queueRow2,queueRow3,queueRow4,queueRow5,queueRow6,queueRow7,maxLengthLabel,maxTime,averageTime,headLabel);
        layoutReport.setStyle("-fx-background-color:#999999;");
        Scene reportScene = new Scene(layoutReport, 1900, 1000);
        reportStage.setScene(reportScene);
        reportStage.setMaximized(true);
        reportStage.showAndWait();

        //------writing a summary of the train queue details to a text file------//
        File reportSummary = new File("Report_Summary.txt");
        FileWriter fileWriterSummary = null;
        PrintWriter printWriterSummary = null;
        try{
            fileWriterSummary = new FileWriter(reportSummary,true);
            printWriterSummary = new PrintWriter(fileWriterSummary,true);
            printWriterSummary.println();

            for(int i=0; i<boardedArrayList.size(); i++){
                printWriterSummary.println("Passenger Name: "+boardedArrayList.get(i).getFirstName());
                printWriterSummary.println("Seat Number: "+boardedArrayList.get(i).getSeatNumber());
                printWriterSummary.println("Waiting Time: "+boardedArrayList.get(i).getSecondsInQueue());

            }
            printWriterSummary.println();
            printWriterSummary.println("Maximum length of train queue: "+trainQueue.getMaxLength());
            printWriterSummary.println("Maximum waiting time: "+trainQueue.getMaxStayInQueue());
            printWriterSummary.println("Average waiting time: "+(trainQueue.getMaxStayInQueue()/trainQueue.getMaxLength()));
        }catch (Exception e){
            System.out.println("SOMETHING WENT WRONG!");
        }

    }
}

