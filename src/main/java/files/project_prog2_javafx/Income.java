// -------------------------------------------------------
// Final Project
// Written by: Steve Banh 1971537
// For “Programming 2” Section 02 – Winter 2025
// --------------------------------------------------------
/**
 * This class is a child class of transaction.
 * */
package files.project_prog2_javafx;

import java.time.LocalDate;
public class Income extends Transaction {

    //CONSTRUCTOR
    public Income(String description, double amount, LocalDate date) {
        super(description, amount, date);
    }

    //METHODS
    @Override
    public String getSummary() {
        return "INCOME\n" +
                "Description: " + getDescription() + "\n" +
                "Amount: $" + String.format("%.2f", getAmount()) + "\n" +
                "Date: " + getDate() + "\n";
    }
}

