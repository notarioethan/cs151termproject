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
    static String url = "jdbc:sqlite:budgetease.db";

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
        Button aboutButton = new Button("About");//not currently functional
        Button transactionsButton = new Button("Transactions");//user's transactions
        Button newAccountButton = new Button("New Account");//account creation
        Button myAccountsButton = new Button("My Accounts");//all of user's accounts

        // Set button styles
        String buttonStyle = "-fx-background-color: #87CEFA; -fx-text-fill: black;";
        aboutButton.setStyle(buttonStyle);
        transactionsButton.setStyle("-fx-background-color: #90EE90; -fx-text-fill: black;");
        newAccountButton.setStyle("-fx-background-color: #90EE90; -fx-text-fill: black;");
        myAccountsButton.setStyle("-fx-background-color: #90EE90; -fx-text-fill: black;");

        // Set action for buttons
        newAccountButton.setOnAction(e -> showCreateAccountScene());
        myAccountsButton.setOnAction(e -> showMyAccountsScene());
        transactionsButton.setOnAction(e -> showTransactionsScene());

        HBox.setMargin(logoView, new Insets(0, 0, 0, 10)); // Add left margin to logo
        topBar.getChildren().addAll(logoView, aboutButton, transactionsButton, myAccountsButton, newAccountButton);

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
    
    private void showTransTableScene() {
    	BorderPane layout = new BorderPane();
    	layout.setStyle("-fx-background-color: #E6FFE6;"); // Light green background

        // Add the top bar
        layout.setTop(createTopBar());
        
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));
        
        Label myTransHead = new Label("MY TRANSACTIONS");
        grid.add(myTransHead, 2, 0);
        
        String tableQuery = "SELECT accName,transactionType,transactionDate,description,money FROM transactionsTable ORDER BY transactionDate DESC";
        try (var conn = DriverManager.getConnection(url)){
        	if (conn != null) {
        		var pstmt = conn.prepareStatement(tableQuery);
                var results = pstmt.executeQuery();
                int i = 1;
                while (results.next()) {
                	String an = results.getString(1);
                	String tt = results.getString(2);
                	java.sql.Date td = results.getDate(3);
                	String de = results.getString(4);
                	double m = results.getDouble(5);
                	
                	Label anLabel = new Label(an);
                	Label ttLabel = new Label(tt);
                	Label tdLabel = new Label("" + td);
                	Label deLabel = new Label(de);
                	Label mLabel = new Label(String.format("$%.2f", m));
                	
                	grid.add(anLabel, 0, i);
                	grid.add(ttLabel, 1, i);
                	grid.add(tdLabel, 2, i);
                	grid.add(deLabel, 3, i);
                	grid.add(mLabel, 4, i++);
                }
        	}
        } catch (SQLException e) {
        	System.err.println(e.getMessage());
        }
        
        layout.setCenter(grid);

        Scene transactionsScene = new Scene(layout, 800, 600);
        primaryStage.setScene(transactionsScene);
    }
    private void showSchedTransEntryScene() {
    	BorderPane layout = new BorderPane();
    	layout.setStyle("-fx-background-color: #E6FFE6;"); // Light green background

        // Add the top bar
        layout.setTop(createTopBar());
        
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));
        
        Label schedNameLabel = new Label("Schedule Name:");
        TextField schedNameField = new TextField();
        
        Label accNameLabel = new Label("Account Label:");
        ComboBox<String> accNameSelection = new ComboBox<String>();
        //populate list
        String accNameQuery = "SELECT accountName FROM accountsV2";
        try (var conn = DriverManager.getConnection(url)) {
            if (conn != null) {
            	var pstmt = conn.prepareStatement(accNameQuery);
                var results = pstmt.executeQuery();
                
                while (results.next()) {
                	String accN = results.getString(1);
                	accNameSelection.getItems().add(accN);
                }
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        
        Label transTypeLabel = new Label("Transaction Type:");
        ComboBox<String> transTypeSelection = new ComboBox<String>();
        //populate list
        String transTypeQuery = "SELECT tname FROM transactionTypeTable";
        try (var conn = DriverManager.getConnection(url)) {
            if (conn != null) {
            	var pstmt = conn.prepareStatement(transTypeQuery);
                var results = pstmt.executeQuery();
                while (results.next()) {
                	String ttype = results.getString(1);
                	transTypeSelection.getItems().add(ttype);
                }
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        
        Label frequencyLabel = new Label("Frequency:");
        ComboBox<String> frequencySelect = new ComboBox<String>();
        frequencySelect.getItems().add("Monthly");
        
        Label dueDateLabel = new Label("Due Date:");
        TextField dueDateField = new TextField();
        
        Label payAmtLabel = new Label("Payment Amount:");
        TextField payAmtField = new TextField();
        
        Button entryButton = new Button("Schedule Transaction");
        entryButton.setOnAction(e -> {
        	String schName = schedNameField.getText();
        	String accName = accNameSelection.getValue();
        	String tType = transTypeSelection.getValue();
        	String freq = frequencySelect.getValue();
        	String due = dueDateField.getText();
        	String payAmt = payAmtField.getText();
        	
        	if (schName.equals("") || accName == null || tType == null || freq == null || due.equals("") || payAmt.equals("")) {
        		showAlert("Error", "Please fill all required fields.");
        	} else if (!due.matches("\\d+")) {
        		showAlert("Error", "Due date must be a number.");
        	} else if (!payAmt.matches("-?\\d+(\\.\\d+)?")) {
        		showAlert("Error", "Paymnet amount must be a number.");
        	}
        	else {
        		int dueInt = Integer.parseInt(due);
        		if (dueInt < 1 || dueInt > 31) {
        			showAlert("Error", "Invalid date.");
        			return;
        		}
        		double amt = Double.parseDouble(payAmt);
        		if (ScheduledTransaction.enterScheduledTransaction(schName, accName, tType, freq, dueInt, amt)) {
        			showAlert("Success!", "Transaction scheduled.");
        		} else showAlert("Error", "Something went wrong.");
        	}
        });
        
        grid.add(schedNameLabel,0,0);
        grid.add(schedNameField,1,0);
        grid.add(accNameLabel,0,1);
        grid.add(accNameSelection,1,1);
        grid.add(transTypeLabel,0,2);
        grid.add(transTypeSelection,1,2);
        grid.add(frequencyLabel,0,3);
        grid.add(frequencySelect,1,3);
        grid.add(dueDateLabel,0,4);
        grid.add(dueDateField,1,4);
        grid.add(payAmtLabel,0,5);
        grid.add(payAmtField,1,5);
        grid.add(entryButton,0,6);
        
        layout.setCenter(grid);

        Scene transactionsScene = new Scene(layout, 800, 600);
        primaryStage.setScene(transactionsScene);
    }
    private void showSchedTransTableScene() {
    	BorderPane layout = new BorderPane();
    	layout.setStyle("-fx-background-color: #E6FFE6;"); // Light green background

        // Add the top bar
        layout.setTop(createTopBar());
        
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));
        
        Label title = new Label("SCHEDULED TRANSACTIONS");
        grid.add(title, 3, 0);
        
        String tableQuery = "SELECT scheduleName,accountName,transactionType,frequency,dueDate,payAmount FROM schedTransactionTable ORDER BY dueDate ASC;";
        try (var conn = DriverManager.getConnection(url)){
        	if (conn != null) {
        		var pstmt = conn.prepareStatement(tableQuery);
                var results = pstmt.executeQuery();
                int i = 1;
                while (results.next()) {
                	String sn = results.getString(1);
                	String an = results.getString(2);
                	String tt = results.getString(3);
                	String fq = results.getString(4);
                	int dd = results.getInt(5);
                	double pa = results.getDouble(6);
                	
                	Label snL = new Label(sn);
                	Label anL = new Label(an);
                	Label ttL = new Label(tt);
                	Label fqL = new Label(fq);
                	Label ddL = new Label("" + dd);
                	Label paL = new Label(String.format("$%.2f", pa));
                	
                	grid.add(snL, 0, i);
                	grid.add(anL, 1, i);
                	grid.add(ttL, 2, i);
                	grid.add(fqL, 4, i);
                	grid.add(ddL, 5, i);
                	grid.add(paL, 6, i++);
                }
        	}
        } catch (SQLException e) {
        	System.err.println(e.getMessage());
        }
        
        layout.setCenter(grid);

        Scene transactionsScene = new Scene(layout, 800, 600);
        primaryStage.setScene(transactionsScene);
    }
    
    private void showTransactionsScene() {
    	BorderPane layout = new BorderPane();
    	layout.setStyle("-fx-background-color: #E6FFE6;"); // Light green background

        // Add the top bar
        layout.setTop(createTopBar());
        
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));
        
        Button addTransactionType = new Button("Add New Transaction Type");
        Button enterTransactions = new Button("Enter Transactions");
        Button viewTransactions = new Button("View Transactions");
        Button scheduleTransactions = new Button("Schedule Transactions");
        Button viewScheduled = new Button("View Scheduled Transactions");
        
        addTransactionType.setOnAction(e -> showAddTransactionsScene());
        enterTransactions.setOnAction(e -> showEnterTransactionsScene());
        viewTransactions.setOnAction(e -> showTransTableScene());
        scheduleTransactions.setOnAction(e -> showSchedTransEntryScene());
        viewScheduled.setOnAction(e -> showSchedTransTableScene());
        
        grid.add(addTransactionType, 0, 0);
        grid.add(enterTransactions, 0, 1);
        grid.add(viewTransactions, 0, 2);
        grid.add(scheduleTransactions, 0, 3);
        grid.add(viewScheduled, 0, 4);
        
        layout.setCenter(grid);

        Scene transactionsScene = new Scene(layout, 800, 600);
        primaryStage.setScene(transactionsScene);
    }
    private void showAddTransactionsScene() {
    	BorderPane layout = new BorderPane();
    	layout.setStyle("-fx-background-color: #E6FFE6;"); // Light green background

        // Add the top bar
        layout.setTop(createTopBar());
        
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));
        
        String transactionTable = "CREATE TABLE IF NOT EXISTS transactionTypeTable(tname text PRIMARY KEY);";
        
        try (var conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                
                var stmt = conn.createStatement();
                stmt.execute(transactionTable);
            }
        } catch (SQLException erm) {
            System.err.println(erm.getMessage());
        }
        
        Label newTransactionTypeLabel = new Label("Add a new transaction type:");
        TextField newTransactionTypeField = new TextField();
        Button addButton = new Button("Add");
        String inserter = "INSERT INTO transactionTypeTable(tname) VALUES(?)";
        addButton.setOnAction(e -> {
        	String newTypeName = newTransactionTypeField.getText();
        	if (newTypeName.equals("")) {
        		showAlert("Error", "Please fill all required fields.");
        	}
        	else {
        		try (var conn = DriverManager.getConnection(url)) {
            		if (conn != null) {
            			var pstmt = conn.prepareStatement(inserter);
            			pstmt.setString(1, newTypeName);
            			pstmt.executeUpdate();
            			showAlert("Success!", "New transaction type created.");
            		}
            	}
            	catch(SQLException erm) {
            		//System.err.println(erm.getMessage());
            		showAlert("Error", "Transaction type already exists.");
            	}
        	}
        	newTransactionTypeField.clear();
        });
        
        grid.add(newTransactionTypeLabel, 0, 0);
        grid.add(newTransactionTypeField, 1, 0);
        grid.add(addButton, 1, 1);
        
        layout.setCenter(grid);

        Scene addTransactionsScene = new Scene(layout, 800, 600);
        primaryStage.setScene(addTransactionsScene);
    }
    private void showEnterTransactionsScene() {
    	BorderPane layout = new BorderPane();
    	layout.setStyle("-fx-background-color: #E6FFE6;"); // Light green background

        // Add the top bar
        layout.setTop(createTopBar());
        
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));
        
        
        Label accNameLabel = new Label("Account Name:");
        ComboBox<String> accNameSelection = new ComboBox<String>();
        //populate list
        String accNameQuery = "SELECT accountName FROM accountsV2";
        try (var conn = DriverManager.getConnection(url)) {
            if (conn != null) {
            	var pstmt = conn.prepareStatement(accNameQuery);
                var results = pstmt.executeQuery();
                
                while (results.next()) {
                	String accN = results.getString(1);
                	accNameSelection.getItems().add(accN);
                }
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        
        
        Label transactionTypeLabel = new Label("Transaction Type:");
        ComboBox<String> transTypeSelection = new ComboBox<String>();
        //populate list
        String transTypeQuery = "SELECT tname FROM transactionTypeTable";
        try (var conn = DriverManager.getConnection(url)) {
            if (conn != null) {
            	var pstmt = conn.prepareStatement(transTypeQuery);
                var results = pstmt.executeQuery();
                while (results.next()) {
                	String ttype = results.getString(1);
                	transTypeSelection.getItems().add(ttype);
                }
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        
        
        Label transactionDateLabel = new Label("Date:");
        DatePicker transDatePicker = new DatePicker(LocalDate.now());
        
        Label descriptionLabel = new Label("Description");
        TextField descriptionField = new TextField();
        
        Label paymentAmountLabel = new Label("Payment Amount:");
        TextField paymentAmountField = new TextField();
        
        Label depositAmountLabel = new Label("Deposit Amount:");
        TextField depositAmountField = new TextField();
        
        /*
        Label payDepLabel = new Label("Payment or Deposit?");
        ComboBox<String> payOrDep = new ComboBox<String>();
        payOrDep.getItems().add("Payment");
        payOrDep.getItems().add("Deposit");
        */
        
        Button entryButton = new Button("Enter Transaction");
        entryButton.setOnAction(e -> {
        	String nam = accNameSelection.getValue();
        	String typ = transTypeSelection.getValue();
        	LocalDate tDate = transDatePicker.getValue();
        	String des = descriptionField.getText();
        	String payAmt = paymentAmountField.getText();
        	String depAmt = depositAmountField.getText();
        	//String payDep = payOrDep.getValue();
        	
        	if (nam == null || typ == null || des.equals("") || (depAmt.equals("") && payAmt.equals(""))) {
        		showAlert("Error", "Please fill all required fields.");
        	}
        	else if (!depAmt.matches("-?\\d+(\\.\\d+)?") && payAmt.equals("")) {
        		showAlert("Error", "Amount must be a number");
        	}
        	else if (!payAmt.matches("-?\\d+(\\.\\d+)?") && depAmt.equals("")) {
        		showAlert("Error", "Amount must be a number");
        	}
        	else if (!depAmt.matches("-?\\d+(\\.\\d+)?") && !payAmt.matches("-?\\d+(\\.\\d+)?")) {
        		showAlert("Error", "Amount must be a number");
        	}
        	else {
        		double amt;
        		double pamt;
        		if (payAmt.equals("")) pamt = 0;
        		else pamt = Double.parseDouble(payAmt);
        		if (depAmt.equals("")) amt = 0;
        		else amt = Double.parseDouble(depAmt);
        		amt -= pamt;
        		//if (payDep.equals("Payment")) amt *= -1;
        		if (Transaction.enterNewTransaction(nam, typ, tDate, des, amt)) showAlert("Success!", "Transaction confirmed.");
        		else showAlert("Error", "oops");
        	}
        	
        	accNameSelection.valueProperty().set(null);
        	transTypeSelection.valueProperty().set(null);
        	transDatePicker.setValue(LocalDate.now());
        	descriptionField.clear();
        	depositAmountField.clear();
        });
        
        grid.add(accNameLabel, 0, 0);
        grid.add(accNameSelection, 1, 0);
        grid.add(transactionTypeLabel, 0, 1);
        grid.add(transTypeSelection, 1, 1);
        grid.add(transactionDateLabel, 0, 2);
        grid.add(transDatePicker, 1, 2);
        grid.add(descriptionLabel, 0, 3);
        grid.add(descriptionField, 1, 3);
        //grid.add(payDepLabel, 0, 4);
        //grid.add(payOrDep, 1, 4);
        grid.add(paymentAmountLabel, 0, 4);
        grid.add(paymentAmountField, 1, 4);
        grid.add(depositAmountLabel, 0, 5);
        grid.add(depositAmountField, 1, 5);
        grid.add(entryButton, 1, 6);
        
        
        layout.setCenter(grid);

        Scene enterTransactionsScene = new Scene(layout, 800, 600);
        primaryStage.setScene(enterTransactionsScene);
    }

    private void showMyAccountsScene() {
    	BorderPane layout = new BorderPane();
    	layout.setStyle("-fx-background-color: #E6FFE6;"); // Light green background

        // Add the top bar
        layout.setTop(createTopBar());
        
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));
        
        Label myAccountsHead = new Label("ACCOUNTS");
        grid.add(myAccountsHead, 2, 0);
        
        String tableQuery = "SELECT accountName,openingDate,balance FROM accountsV2 ORDER BY openingDate DESC;";
    	try (var conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                //var meta = conn.getMetaData();
                
                
                
                var pstmt = conn.prepareStatement(tableQuery);
                var results = pstmt.executeQuery();
                int i = 1;
                while (results.next()) {
                	String an = results.getString(1);
                	java.sql.Date od = results.getDate(2);
                	double b = results.getDouble(3);
                	/*
                	System.out.printf("%-32s | ", an);
                	System.out.print(od + "| ");
                	System.out.printf("$%.2f", b);
                	System.out.println();
                	
                	String accountRow = "%-32s | %tF | $%.2f";
                	accountRow = String.format(accountRow, an, od, b);
                	*/
                	//System.out.println(accountRow);
                	Label accNameLabel = new Label(an);
                	Label openDateLabel = new Label("" + od);
                	Label balanceLabel = new Label(String.format("$%.2f", b));
                	grid.add(accNameLabel, 0, i);
                	grid.add(openDateLabel, 2, i);
                	grid.add(balanceLabel, 4, i++);
                }
                
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        
        layout.setCenter(grid);

        Scene myAccountsScene = new Scene(layout, 800, 600);
        primaryStage.setScene(myAccountsScene);
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
            	showAlert("Error", "Please fill all required fields.");
            	
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

                
                
            }
            accountNameField.clear();
            openingDatePicker.setValue(LocalDate.now());
            openingBalanceField.clear();
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
    	
    	
    	//sqlite connection
    	
    	//creates table; not necessary anymore for now
    	
    	var tbl = "CREATE TABLE IF NOT EXISTS accountsV2 (" +
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
    	
    	
    	
    }
}