// -------------------------------------------------------
// Final Project
// Written by: (include your name and student id)
// For “Programming 2” Section (include number)– Winter 2025
// --------------------------------------------------------
/**
 * This class is a child class of Transaction. It has an extra variable category of a type ExpenseCategory,
 * which is an enum class. The enum class is inside the Expense class.
 * */

package files.project_prog2_javafx;

import java.time.LocalDate;
public class Expense extends Transaction{

    //An enum class to only allow the values below
    public enum ExpenseCategory{
        RENT, FOOD, TRAVEL, UTILITY, ENTERTAINMENT,
        HEALTHCARE, EDUCATION, INSURANCE, OTHER,
    }

    //CLASS VARIABLES
    private ExpenseCategory category;


    //CONSTRUCTOR
    public Expense(String description, double amount, LocalDate date, ExpenseCategory category) {
        super(description, amount, date);
        this.category = category;

    }

    //GETTERS AND SETTERS
    public ExpenseCategory getCategory(){
        return category;
    }
    public void setCategory(ExpenseCategory category){
        this.category = category;
    }


    //METHODS
    @Override
    public String getSummary() {
        return "EXPENSE\n" +
                "Description: " + getDescription() + "\n" +
                "Amount: $" + String.format("%.2f", getAmount()) + "\n" +
                "Date: " + getDate() + "\n" +
                "Category: " + getCategory() + "\n";
    }
}

