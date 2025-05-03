// -------------------------------------------------------
// Final Project
// Written by: (include your name and student id)
// For “Programming 2” Section (include number)– Winter 2025
// --------------------------------------------------------
/**
 * This is an add transaction controller class that has a separate window from the main one. This window allows
 * the user to enter information about their transaction and add it to the table view in the main controller. It
 * also acts as an edit mode for transactions that the user wants to modify.
 *
 * We have an initialize() method. It is just used to set up available choices for the user to pick such as
 * the types of categories for expense, the type of transaction (income or expense), and set the default value
 * of the date to be the current day.
 *
 * We have a submitTransaction() method. This method will create a new transaction object based on the input of
 * the user or allows the user to modify their previous transaction. When the user clicks on submit, the object
 * is sent back to the MainController for it to be placed in the table view.
 *
 * We have a cancelTransaction() method that will close the window for adding a transaction and clears all filled
 * fields.
 *
 * We have a loadCurrentInfo() that takes a transaction as a parameter. This method checks if the transaction is
 * prefilled or not. This will allow the program to know if it should modify a transaction or create a new one.
 *
 * We then have a couple of helper methods that compliment the logic of a method and makes the code less crowded
 *
 * We have setupChoiceBoxCategory(), setupChoiceBoxTranType(), setDefaultDate(). setupChoiceBoxCategory() and
 * setupChoiceBoxTranType() are methods in charge of setting everything up such as available values for the user
 * to choose. setDefaultDate() is just a method that will automatically prefill the date to the current day.
 *
 * We have a verifyDescription() that verified if the description entered by the user is valid or not.
 *
 * Lastly, we have a showError() method that will have an error window pop up to inform the user that an
 * error has occurred.
 * **/
package files.project_prog2_javafx;

import files.project_prog2_javafx.Exceptions.InvalidAmountException;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.function.Consumer;

public class AddController {
    //TEXT
    @FXML private TextField descText;
    @FXML private TextField amountText;

    //SELECTIONS
    @FXML private DatePicker dateSelection;
    @FXML private ChoiceBox<Expense.ExpenseCategory> categorySelection;
    @FXML private ChoiceBox<String> tranType;

    //BUTTONS
    @FXML private Button submit;
    @FXML private Button cancel;

    //LABELS
    @FXML private Label message;

    //CLASS VARIABLES
    private Transaction editTransaction;//hold the original transaction to be modified

    //A functional interface that is in charge of delivering the new transaction object created to MainController
    //class. Then, it follows instructions on how to deliver the object through setAddNewTransaction()
    private Consumer<Transaction> addNewTransaction;

    //this sets instructions on how the delivery of the new transaction object should be handled
    public void setAddNewTransaction(Consumer<Transaction> callback) {
        this.addNewTransaction = callback;
    }


    //METHODS
    //initialize method to set everything up
    @FXML public void initialize(){
        setupChoiceBoxCategory();
        setupChoiceBoxTranType();
        setDefaultDate();
    }

    //when the user clicks on the "submit" button, the information entered by the user will register in
    //the observable list and appear on the table view
    @FXML protected void submitTransaction(){
        //checks if the transaction already has values in the field or not.
        //we override the new value entered by the user
        if(editTransaction != null){
            editTransaction.setDescription(descText.getText());
            editTransaction.setAmount(Double.parseDouble(amountText.getText()));
            editTransaction.setDate(dateSelection.getValue());

            if(editTransaction instanceof Expense){
                Expense expense = (Expense) editTransaction;
                expense.setCategory(categorySelection.getValue());
            }
            Stage stage = (Stage) descText.getScene().getWindow();
            stage.close();
        }

        String desc = descText.getText().trim();
        if(!verifyDescription(desc)){
            return;//stops from continuing with invalid data
        }

        double amount;
        try {
            String format = amountText.getText().trim();
            amount = Double.parseDouble(format);
            if (amount <= 0) {
                throw new InvalidAmountException();
            }
            amount = Math.round(amount * 100) / 100.0;
        } catch (NumberFormatException e) {
            showError("Please enter a valid number for amount");
            return;
        } catch (InvalidAmountException iAE){
            showError("Cannot enter a negative number or zero");
            return;
        }

        LocalDate date = dateSelection.getValue();
        if(date == null) {
            showError("Please enter a date");
            return;
        }

        String type = tranType.getValue();
        if(type == null) {
            showError("Please select a transaction type");
            return;
        }

        Expense.ExpenseCategory category = categorySelection.getValue();
        if (type.equalsIgnoreCase("Expense")) {
            if(category == null){
                showError("Category cannot be empty.");
                return;
            }
        }

        Transaction transaction;
        if (type.equalsIgnoreCase("Income")) {
            transaction = new Income(desc, amount, date);
        } else {
            transaction = new Expense(desc, amount, date, category);
        }

        //for safety measures, checks if there are instructions in
        //setAddNewTransaction() before sending the transaction object, or else
        //the program will crash
        if (addNewTransaction != null) {
            addNewTransaction.accept(transaction);
        }

        //clear the input
        descText.clear();
        amountText.clear();
        dateSelection.setValue(LocalDate.now());
        tranType.setValue("Income");
        categorySelection.setValue(null);
    }

    //close the window when the user clicks on the cancel button
    @FXML protected void cancelTransaction(){
        Stage stage = (Stage) cancel.getScene().getWindow();
        stage.close();
    }

    //this method determines if the form should be empty with default values
    //or have prefilled values from an initial transaction
    public void loadCurrentInfo(Transaction transaction){
        //Stores the transaction reference for later editing in submitTransaction.
        //If non-null, we're in edit mode; if null, we're creating a new transaction.
        this.editTransaction = transaction;

        //check if the transaction contains data. if it does, we prefill the
        //fields with the initial data
        if(transaction != null){
            descText.setText(transaction.getDescription());
            amountText.setText(String.valueOf(transaction.getAmount()));
            dateSelection.setValue(transaction.getDate());
            tranType.setValue(transaction.getClass().getSimpleName());

            if(transaction instanceof Expense){
                Expense expense = (Expense) transaction;
                categorySelection.setValue(expense.getCategory());
                tranType.setValue(transaction.getClass().getSimpleName());
            }
        }
    }


    //HELPER METHODS
    private void setupChoiceBoxCategory(){
        Expense.ExpenseCategory[] categories = Expense.ExpenseCategory.values();
        categorySelection.getItems().addAll(categories);
        categorySelection.setValue(null);//default
    }

    private void setupChoiceBoxTranType(){
        tranType.getItems().addAll("Income", "Expense");
        tranType.setValue("Income"); //default
    }

    private void setDefaultDate(){
        dateSelection.setValue(LocalDate.now());
    }

    //checks if the user has entered a valid description
    private boolean verifyDescription(String description) {
        if (description.isEmpty()) {
            showError("Description cannot be empty.");
            return false;
        } else if (description.length() > 50) {
            showError("Description is too long (Max: 50 characters)");
            return false;
        } else if (!description.matches("^[a-zA-Z0-9$#\\s]+$")) {
            showError("Description can only contain letters, numbers, spaces, $, and #");
            return false;
        } else if (!description.matches(".*[a-zA-Z].*")) {
            showError("Description must include at least one letter.");
            return false;
        }
        return true;
    }

    //create a window to alert the user about something went wrong
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Input Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
