package application;

import java.util.*;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.*;

public class Transaction {
	private Account account;
	private String transactionType;
	private Date date;
	private String description;
	private double amount;//-for payment; +for deposit; use deposit - payment
	
	private static String url = "jdbc:sqlite:budgetease.db";
	
	public Transaction(Account a, String ttype, Date d8, String desc, double amt) {
		account = a;
		transactionType = ttype;
		date = d8;
		description = desc;
		amount = amt;
	}
	public static boolean enterNewTransaction(String accName, String transType, LocalDate dat, String desc, double amt) {
		Date realD = Date.from(dat.atStartOfDay(ZoneId.systemDefault()).toInstant());
		String tableCreator = "CREATE TABLE IF NOT EXISTS transactionsTable(" + 
				  "id INTEGER PRIMARY KEY," +
				  "accName text NOT NULL," +
				  "transactionType text NOT NULL," +
				  "transactionDate date NOT NULL," +
				  "description text NOT NULL," +
				  "money double NOT NULL" +
				  ")";

		String accGetter = "SELECT accountName,openingDate,balance FROM accountsV2 WHERE accountName = ?";
		
		String transInserter = "INSERT INTO transactionsTable(accName,transactionType,transactionDate,description,money) VALUES(?,?,?,?,?)";
		
		try (var conn = DriverManager.getConnection(url)) {
			if (conn != null) {
				var stmt = conn.createStatement();
                stmt.execute(tableCreator);
                stmt.close();
				var pstmt = conn.prepareStatement(transInserter);
				pstmt.setString(1, accName);
				pstmt.setString(2, transType);
				pstmt.setDate(3, new java.sql.Date(realD.getTime()));
				pstmt.setString(4, desc);
				pstmt.setDouble(5, amt);
				pstmt.executeUpdate();
				
				
				
				Account acc;
				
				if (Account.allAccNames.containsKey(accName)) {
					acc = Account.allAccNames.get(accName);
				}
				else {
					//accGetter += accName;
					pstmt = conn.prepareStatement(accGetter);
					pstmt.setString(1, accName);
					var results = pstmt.executeQuery();
					String an = results.getString(1);
	            	java.sql.Date od = results.getDate(2);
	            	double b = results.getDouble(3);
	            	acc = new Account(an, (Date)od, b);
	            	
				}
				pstmt.close();
				return acc.editBalance(amt);
				
				//return true;
			}
		} catch (SQLException e) {
			System.err.println(e.getMessage());
		}
		
		
		
		return false;
	}
}
