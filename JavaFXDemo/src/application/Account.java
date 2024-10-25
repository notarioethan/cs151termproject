package application;

import java.time.*;

public class Account {
	
	private String accountName;
	private LocalDate openingDate;
	private double balance;
	
	public Account(String name, LocalDate date, double bal) {
		accountName = name;
		openingDate = date;
		balance = bal;
	}
}
