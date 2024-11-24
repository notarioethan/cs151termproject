package application;

import java.time.*;
import java.util.*;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Account {
	
	private String accountName;
	private Date openingDate;
	private double balance;
	
	public static Map<String, Account> allAccNames = new HashMap<String, Account>();
	
	//SQLite database stuff
	private static String url = "jdbc:sqlite:budgetease.db";
	
	
	public Account(String name, Date date, double bal) {
		accountName = name;
		openingDate = date;
		balance = bal;
		
		
		
	}
	public static boolean create(String name, LocalDate date, double bal) {
		String accN = name;
		Date openD = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
		double balan = bal;
		
		String inserter = "INSERT INTO accountsV2(accountName,openingDate,balance) VALUES(?,?,?)";
		//String sorter = "SELECT openingDate FROM accounts ORDER BY openingDate DESC;";
		
		try (var conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                //var meta = conn.getMetaData();
                //System.out.println("Inserting values");//makes sure connects to db
                var pstmt = conn.prepareStatement(inserter);
                pstmt.setString(1, accN);
                pstmt.setDate(2, new java.sql.Date(openD.getTime()));
                pstmt.setDouble(3, balan);
                pstmt.executeUpdate();
                Account newAcc = new Account(accN, openD, balan);
                allAccNames.put(accN, newAcc);
                return true;
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
		return false;
	}
	
	public boolean editBalance(double amt) {
		balance += amt;
		String updater = "UPDATE accountsV2 SET balance = ? WHERE accountName = ?";// + accountName;
		try (var conn = DriverManager.getConnection(url);
	            var pstmt = conn.prepareStatement(updater)) {
	            // set the parameters
	            pstmt.setString(2, accountName);
	            pstmt.setDouble(1, balance);
	            
	            // update
	            pstmt.executeUpdate();
	            pstmt.close();
	            return true;
	        } catch (SQLException e) {
	            System.err.println(e.getMessage());
	        }
		return false;
	}
	
}
