package application;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.*;
import java.util.*;

public class ScheduledTransaction {
	private String scheduleName;
	private Account account;
	private String transactionType;
	private String frequency;
	private int dueDate;
	private double payAmount;
	
	private static String url = "jdbc:sqlite:budgetease.db";
	
	public ScheduledTransaction(String sName, Account acc, String tType, String freq, int ddate, double amt) {
		scheduleName = sName;
		account = acc;
		transactionType = tType;
		frequency = freq;
		dueDate = ddate;
		payAmount = amt;
	}
	
	public static boolean editScheduledTransaction(String oldName, String sName, String accName, String tType, String freq, int ddate, double amt) {
		String updater = "UPDATE schedTransactionTableV2 SET scheduleName = ?,accountName = ?,transactionType = ?,frequency = ?,dueDate = ?,payAmount = ? WHERE scheduleName = '" + oldName + "'";
		try (var conn = DriverManager.getConnection(url)){
			if (conn != null) {
				
				var pstmt = conn.prepareStatement(updater);
				pstmt.setString(1, sName);
				pstmt.setString(2, accName);
				pstmt.setString(3, tType);
				pstmt.setString(4, freq);
				pstmt.setInt(5, ddate);
				pstmt.setDouble(6, amt);
				pstmt.executeUpdate();
				
				return true;
				
			}
		} catch (SQLException e) {
			System.err.println(e.getMessage());
		}
		return false;
	}
	
	public static boolean enterScheduledTransaction(String sName, String accName, String tType, String freq, int ddate, double amt) {
		//if (ddate < 1 || ddate > 31) return false;
		
		String tableCreator = "CREATE TABLE IF NOT EXISTS schedTransactionTableV2(" + 
								"scheduleName text PRIMARY KEY," +
								"accountName text NOT NULL," +
								"transactionType text NOT NULL," +
								"frequency text NOT NULL," +
								"dueDate int NOT NULL," +
								"payAmount double NOT NULL" +
								")";
		//String accGetter = "SELECT accountName,openingDate,balance FROM accountsV2 WHERE accountName = ?";
		String inserter = "INSERT INTO schedTransactionTableV2(scheduleName,accountName,transactionType,frequency,dueDate,payAmount) VALUES(?,?,?,?,?,?)";
		
		try (var conn = DriverManager.getConnection(url)){
			if (conn != null) {
				var stmt = conn.createStatement();
				stmt.execute(tableCreator);
				stmt.close();
				var pstmt = conn.prepareStatement(inserter);
				pstmt.setString(1, sName);
				pstmt.setString(2, accName);
				pstmt.setString(3, tType);
				pstmt.setString(4, freq);
				pstmt.setInt(5, ddate);
				pstmt.setDouble(6, amt);
				pstmt.executeUpdate();
				/*
				Account acc;
				
				if (Account.allAccNames.containsKey(accName)) {
					acc = Account.allAccNames.get(accName);
				} else {
					pstmt = conn.prepareStatement(accGetter);
					pstmt.setString(1, accName);
					var results = pstmt.executeQuery();
					String an = results.getString(1);
					java.sql.Date od = results.getDate(2);
	            	double b = results.getDouble(3);
	            	acc = new Account(an, (Date)od, b);
				}
				pstmt.close();
				//return acc.editBalance(amt);
				*/
				return true;
				
			}
		} catch (SQLException e) {
			System.err.println(e.getMessage());
		}
		
		return false;
	}
}
