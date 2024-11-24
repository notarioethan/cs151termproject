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
	
	public static boolean editTransaction(int id, String accName, String transType, LocalDate dat, String desc, double amt) {
		Date realD = Date.from(dat.atStartOfDay(ZoneId.systemDefault()).toInstant());
		
		String accGetter = "SELECT accountName,openingDate,balance FROM accountsV2 WHERE accountName = ?";
		String updater = "UPDATE transactionsTableV2 SET accName = ?,transactionType = ?,transactionDate = ?,description = ?,money = ? WHERE id = " + id;
		String amtGetter = "SELECT money,accName FROM transactionsTableV2 WHERE id = " + id;
		try (var conn = DriverManager.getConnection(url)) {
			if (conn != null) {
				var stmt = conn.prepareStatement(amtGetter);
				//stmt.execute(amtGetter);
				var r = stmt.executeQuery();
				double amti = r.getDouble(1);
				String nami = r.getString(2);
				stmt.close();
				var pstmt = conn.prepareStatement(updater);
				pstmt.setString(1, accName);
				pstmt.setString(2, transType);
				pstmt.setDate(3, new java.sql.Date(realD.getTime()));
				pstmt.setString(4, desc);
				pstmt.setDouble(5, amt);
				pstmt.executeUpdate();
				
				pstmt.close();
				
				Account acc;
				Account acci;
				
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
	            	pstmt.close();
	            	Account.allAccNames.put(an, acc);
				}
				
				if (Account.allAccNames.containsKey(nami)) {
					acci = Account.allAccNames.get(nami);
				}
				else {
					//accGetter += accName;
					pstmt = conn.prepareStatement(accGetter);
					pstmt.setString(1, nami);
					var results = pstmt.executeQuery();
					String an = results.getString(1);
	            	java.sql.Date od = results.getDate(2);
	            	double b = results.getDouble(3);
	            	acci = new Account(an, (Date)od, b);
	            	pstmt.close();
	            	Account.allAccNames.put(an, acci);
				}
				pstmt.close();
				
				//boolean cd = acci.editBalance(-amti);
				//boolean ab = acc.editBalance(amt);
				
				if (acc == acci) return acci.editBalance(amt - amti);
				return acc.editBalance(amt) && acci.editBalance(-amti);
				
				//return true;
			}
		} catch (SQLException e) {
			System.err.println(e.getMessage());
		}
		
		return false;
	}
	
	public static boolean enterNewTransaction(String accName, String transType, LocalDate dat, String desc, double amt) {
		Date realD = Date.from(dat.atStartOfDay(ZoneId.systemDefault()).toInstant());
		String tableCreator = "CREATE TABLE IF NOT EXISTS transactionsTableV2(" + 
				  "id INTEGER PRIMARY KEY," +
				  "accName text NOT NULL," +
				  "transactionType text NOT NULL," +
				  "transactionDate date NOT NULL," +
				  "description text NOT NULL," +
				  "money double NOT NULL" +
				  ")";

		String accGetter = "SELECT accountName,openingDate,balance FROM accountsV2 WHERE accountName = ?";
		
		String transInserter = "INSERT INTO transactionsTableV2(accName,transactionType,transactionDate,description,money) VALUES(?,?,?,?,?)";
		
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
