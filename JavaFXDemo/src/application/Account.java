package application;

import java.time.*;
import java.util.*;

public class Account {
	
	private String accountName;
	private LocalDate openingDate;
	private double balance;
	
	public static Set<String> allAccNames = new HashSet<String>();
	
	public Account(String name, LocalDate date, double bal) {
		accountName = name;
		openingDate = date;
		balance = bal;
		allAccNames.add(name);
	}
	
}
