// -------------------------------------------------------
// Final Project
// Written by: Steve Banh 1971537
// For “Programming 2” Section 02 – Winter 2025
// --------------------------------------------------------
/**
 * This class is an abstract class that only serves as a blueprint for other classes
 * */
package files.project_prog2_javafx;

import java.time.LocalDate;
abstract class Transaction {

    //CLASS VARIABLES
    private String description;
    private double amount;
    private LocalDate date;


    //CONSTRUCTOR
    public Transaction (String description, double amount, LocalDate date){
        this.description = description;
        this.amount = amount;
        this.date = date;
    }


    //GETTERS AND SETTERS
    public String getDescription() {
        return description;
    }
    public double getAmount() {
        return amount;
    }
    public LocalDate getDate() {
        return date;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public void setAmount(double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Amount cannot be negative.");
        }
        this.amount = amount;
    }
    public void setDate(LocalDate date) {
        this.date = date;
    }


    //METHODS
    public abstract String getSummary();

    @Override
    public boolean equals(Object obj) {

        //checks if both transaction and the object we want to compare
        //reference the same memory location
        if(this == obj){
            return true;
        }

        //Checks if the object is null. If it is, then nothing to compare
        if(obj == null){
            return false;
        }

        //checks if both are the exact same class
        if(this.getClass() != obj.getClass()){
            return false;
        }

        //comparing transaction fields to obj fields
        Transaction otherTransaction = (Transaction) obj;
        return this.description.equals(otherTransaction.description) &&
                Double.compare(this.amount, otherTransaction.amount) == 0 &&
                this.date.isEqual(otherTransaction.date);
    }

    public String toString(){
        //getSimpleName() turns the class name into a string
        return getClass().getSimpleName().toUpperCase() + "\n" +
                "Description: " + getDescription() + "\n" +
                "Amount: " + getAmount() + "\n" +
                "Date: " + getDate();
    }
}

