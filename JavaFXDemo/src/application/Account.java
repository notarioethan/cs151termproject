package application;

import java.time.*;
import java.util.*;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Account {
	
	private String accountName;
	private Date openingDate;
	private double balance;
	
	//public static Set<String> allAccNames = new HashSet<String>();
	
	//SQLite database stuff
	private String url = "jdbc:sqlite:budgetease.db";
	
	
	public Account(String name, LocalDate date, double bal) {
		accountName = name;
		openingDate = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());;
		balance = bal;
		//allAccNames.add(name);
		
		String inserter = "INSERT INTO accounts(accountName,openingDate,balance) VALUES(?,?,?)";
		//String sorter = "SELECT openingDate FROM accounts ORDER BY openingDate DESC;";
		
		try (var conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                //var meta = conn.getMetaData();
                System.out.println("Inserting values");//makes sure connects to db
                var pstmt = conn.prepareStatement(inserter);
                pstmt.setString(1, accountName);
                pstmt.setDate(2, new java.sql.Date(openingDate.getTime()));
                pstmt.setDouble(3, balance);
                pstmt.executeUpdate();
                /*
                pstmt = conn.prepareStatement(sorter);
                pstmt.executeUpdate();
                */
                
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
	}
	
}
