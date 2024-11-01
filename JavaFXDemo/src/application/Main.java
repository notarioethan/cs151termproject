package application;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.util.*;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main extends Application {

    private Stage primaryStage;
    private Scene homeScene;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("BudgetEase");

        // Create and set the home scene
        homeScene = createHomeScene();
        primaryStage.setScene(homeScene);
        primaryStage.show();
    }

    private Scene createHomeScene() {
        // Main layout
        BorderPane layout = new BorderPane();
        layout.setStyle("-fx-background-color: #E6FFE6;"); // Light green background

        // Top bar with buttons
        HBox topBar = createTopBar();
        layout.setTop(topBar);

        // Center content
        VBox centerContent = createCenterContent();
        layout.setCenter(centerContent);

        // Create the scene
        return new Scene(layout, 800, 600);
    }

    private HBox createTopBar() {
        HBox topBar = new HBox(10);
        topBar.setPadding(new Insets(10));
        topBar.setAlignment(Pos.CENTER_RIGHT);
        topBar.setStyle("-fx-background-color: #CCCCCC;"); // Gray background for top bar

        // Logo on the left
        ImageView logoView = new ImageView(new Image("file:path/to/your/logo.png")); // Replace with actual path
        logoView.setFitHeight(30);
        logoView.setPreserveRatio(true);

        // Buttons on the right
        Button aboutButton = new Button("About");
        Button viewDataButton = new Button("View Data");
        Button newAccountButton = new Button("New Account");
        Button myAccountsButton = new Button("My Accounts");

        // Set button styles
        String buttonStyle = "-fx-background-color: #87CEFA; -fx-text-fill: black;";
        aboutButton.setStyle(buttonStyle);
        viewDataButton.setStyle(buttonStyle);
        newAccountButton.setStyle("-fx-background-color: #90EE90; -fx-text-fill: black;");
        myAccountsButton.setStyle("-fx-background-color: #90EE90; -fx-text-fill: black;");

        // Set action for New Account button
        newAccountButton.setOnAction(e -> showCreateAccountScene());
        //myAccountsButton.setOnAction(e -> );

        HBox.setMargin(logoView, new Insets(0, 0, 0, 10)); // Add left margin to logo
        topBar.getChildren().addAll(logoView, aboutButton, viewDataButton, newAccountButton);

        return topBar;
    }

    private VBox createCenterContent() {
        VBox centerContent = new VBox(20);
        centerContent.setAlignment(Pos.CENTER);

        // Welcome text
        Label welcomeLabel = new Label("Welcome to");
        welcomeLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        // Logo
        ImageView logoView = new ImageView(new Image("file:path/to/your/logo.png")); // Replace with actual path
        logoView.setFitWidth(200);
        logoView.setPreserveRatio(true);

        // BudgetEase text
        Label budgetEaseLabel = new Label("BUDGETEASE");
        budgetEaseLabel.setStyle("-fx-font-size: 36px; -fx-font-weight: bold;");

        // Tagline
        Label taglineLabel = new Label("Budgeting made easy.");
        taglineLabel.setStyle("-fx-font-size: 18px;");

        centerContent.getChildren().addAll(welcomeLabel, logoView, budgetEaseLabel, taglineLabel);

        return centerContent;
    }

    private void showCreateAccountScene() {
        BorderPane layout = new BorderPane();
        layout.setStyle("-fx-background-color: #E6FFE6;"); // Light green background

        // Add the top bar
        layout.setTop(createTopBar());

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        // Create Account form
        Label accountNameLabel = new Label("Account Name:");
        TextField accountNameField = new TextField();

        Label openingDateLabel = new Label("Opening Date:");
        DatePicker openingDatePicker = new DatePicker(LocalDate.now());

        Label openingBalanceLabel = new Label("Opening Balance:");
        TextField openingBalanceField = new TextField();

        Button submitButton = new Button("Create Account");
        submitButton.setStyle("-fx-background-color: #90EE90; -fx-text-fill: black;");
        submitButton.setOnAction(e -> {
            String accountName = accountNameField.getText();
            LocalDate openingDate = openingDatePicker.getValue();
            String openingBalance = openingBalanceField.getText();
            //
            
            if (accountName.equals("") || openingBalance.equals("")) {
            	showAlert("Error", "Please fill in all required fields.");
            	accountNameField.clear();
                openingDatePicker.setValue(LocalDate.now());
                openingBalanceField.clear();
            }
            //else if (accountName is a duplicate)
            /*else if (Account.allAccNames.contains(accountName)) {
            	showAlert("Error", "Account name already in use.");
            }*/
            //else if (openingBalance is not a number)
            else if (!openingBalance.matches("-?\\d+(\\.\\d+)?")) {
            	showAlert("Error", "Please enter a number as your balance.");
            }
            else {
            	//convert opening balance to double
                //throws error when letters entered - fix later so that only numbers can be entered
            	double opBal = Double.parseDouble(openingBalance);
                //System.out.print(opBal + " ");
                if (Account.create(accountName, openingDate, opBal)) showAlert("Account Created", "Account has been successfully created.");
                else showAlert("Error", "Account name already in use.");
                // Here you would typically save this data to your persistence layer
                //System.out.println("Account Created: " + accountName + ", " + openingDate + ", $" + openingBalance);

                // Clear fields after submission
                accountNameField.clear();
                openingDatePicker.setValue(LocalDate.now());
                openingBalanceField.clear();

                
                
            }
        });

        grid.add(accountNameLabel, 0, 0);
        grid.add(accountNameField, 1, 0);
        grid.add(openingDateLabel, 0, 1);
        grid.add(openingDatePicker, 1, 1);
        grid.add(openingBalanceLabel, 0, 2);
        grid.add(openingBalanceField, 1, 2);
        grid.add(submitButton, 1, 3);

        layout.setCenter(grid);

        Scene createAccountScene = new Scene(layout, 800, 600);
        primaryStage.setScene(createAccountScene);
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Main method to launch the application
    public static void main(String[] args) {
    	
    	String url = "jdbc:sqlite:budgetease.db";
    	//sqlite connection
    	
    	//creates table; not necessary anymore for now
    	
    	var tbl = "CREATE TABLE IF NOT EXISTS dupeTester (" +
    			  "	accountName text PRIMARY KEY," +
    			  "	openingDate date NOT NULL," +
    			  "	balance double NOT NULL" +
    			  ");";
        
    	try (var conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                //var meta = conn.getMetaData();
                //System.out.println(meta.getDriverName());//makes sure JDBC driver works
                //System.out.println("The driver name is " + meta.getDriverName());
                //System.out.println("A new database has been created.");
                
                //create or access table from db
                var stmt = conn.createStatement();
                stmt.execute(tbl);
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    	
    	launch(args);
    	//output table after closing application
    	String sorter = "SELECT accountName,openingDate,balance FROM accounts ORDER BY openingDate DESC;";
    	try (var conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                //var meta = conn.getMetaData();
                
                
                
                var pstmt = conn.prepareStatement(sorter);
                var results = pstmt.executeQuery();
                while (results.next()) {
                	String an = results.getString(1);
                	java.sql.Date od = results.getDate(2);
                	double b = results.getDouble(3);
                	//System.out.println(an + " | " + od + " | " + b);
                	System.out.printf("%-32s | ", an);
                	System.out.print(od + "| ");
                	System.out.printf("$%.2f", b);
                	System.out.println();
                }
                
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    	
    }
}