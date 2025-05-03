// -------------------------------------------------------
// Final Project
// Written by: (include your name and student id)
// For “Programming 2” Section (include number)– Winter 2025
// --------------------------------------------------------
/**
 * This is a spending limit controller class that has a separate window from the main one. This class allows the
 * user to set a limit for a particular category for themselves.
 *
 * We have an initialize() method. It is just used to set up available category choices for the user to pick
 * for setting their limit expenses. We used a helper method called setupChoiceBoxCategory() for setting up the
 * category choices.
 *
 * We have sendSPLimit() method. This method will create a new spending limit object based on the input of
 * the category and the amount. When the user clicks on submit, the object is sent back to the MainController
 * via setAddNewLimit(). It will also check if the user has set a limit that is under lower than their expenses
 * for the specified category and asks if they still want to proceed.
 *
 * We have a cancelTransaction() method that will close the window for adding a transaction and clears all filled
 * fields.
 *
 * We have a deleteLimit() method which is for deleting a transaction based on the category that the user has
 * selected. When the user clicks on delete, it will ask the user to confirm the deletion of the limit
 * before proceeding.
 *
 * We have a couple helper methods such as setupChoiceBoxCategory(), showInfo(), showError(), and showConfirmation().
 *
 * setupChoiceBoxCategory() is used to set up available category choices for the user to pick.
 * showInfo() is a method that will have an information window pop up to inform the user of something.
 * showError() will have an error window pop up to show the user that there is an error has occurred.
 * showConfirmation() will have a confirmation window pop up asking the user if they will to proceed regardless
 * of something.
 **/

package files.project_prog2_javafx;

import files.project_prog2_javafx.Exceptions.InvalidAmountException;
import files.project_prog2_javafx.Exceptions.SpendingLimitNotFoundException;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.Optional;
import java.util.function.Consumer;

public class SpendLimController {

    //TEXT
    @FXML private TextField spendAmountText;

    //SELECTIONS
    @FXML private ChoiceBox<Expense.ExpenseCategory> spendCategorySelect;

    //BUTTONS
    @FXML private Button submitLimit;
    @FXML private Button deleteLim;
    @FXML private Button cancelLimit;

    //CLASS VARIABLE
    private FinanceManager manageSPLimit = new FinanceManager();

    //A functional interface that is in charge of delivering the new spending limit object created to MainController
    //class. Then, it follows instructions on how to deliver the object through setAddNewLimit()
    private Consumer<SpendingLimit> addSPLimit;

    //this sets instructions on how the delivery of the new spending limit object should be handled
    public void setAddNewLimit(Consumer<SpendingLimit> callback) {
        this.addSPLimit = callback;
    }


    public void setFinanceManager(FinanceManager manager) {
        this.manageSPLimit = manager;
    }

    private Consumer<Expense.ExpenseCategory> onLimitDeleted;

    public void setOnLimitDeleted(Consumer<Expense.ExpenseCategory> callback) {
        this.onLimitDeleted = callback;
    }

    //METHODS
    //initialize method to set everything up
    @FXML public void initialize(){
        setupChoiceBoxCategory();
    }

    @FXML protected void sendSPLimit(){
        double limitAmount;
        try {
            String format = spendAmountText.getText().trim();
            limitAmount = Double.parseDouble(format);
            if (limitAmount <= 0) {
                throw new InvalidAmountException();
            }
            limitAmount = (Math.round(limitAmount * 100)) / 100.0;
        } catch (NumberFormatException e) {
            showError("Please enter a valid number for amount");
            return;
        } catch (InvalidAmountException iAE){
            showError("Cannot enter a negative number or zero");
            return;
        }

        Expense.ExpenseCategory limitCategory = spendCategorySelect.getValue();
        if (limitCategory == null) {
            showError("Please select a category");
            return;
        }

        double currentSpending = manageSPLimit.getCurrentSpendingLimit(limitCategory);
        if(limitAmount < currentSpending){
            Optional<ButtonType> result = showConfirmation(String.format(
                    "Your current expense for %s ($%.2f) already exceeds the new limit ($%.2f).\n" +
                    "Continue?",
                    limitCategory, currentSpending, limitAmount));
            //if the user clicks on the exit (X) button cancel button, or closes the dialog,
            //the result will be empty or not OK. So, we cancel the operation
            if (result.isEmpty() || result.get() != ButtonType.OK) {
                return;
            }
        }
        SpendingLimit limit = new SpendingLimit(limitCategory, limitAmount);
        if(addSPLimit != null){
            addSPLimit.accept(limit);
        }
        // Close the window after setting limit
        spendAmountText.getScene().getWindow().hide();
    }
    @FXML protected void cancelSPLimit(){
        Stage stage = (Stage) cancelLimit.getScene().getWindow();
        stage.close();
    }

    @FXML protected void deleteLimit(){
        Expense.ExpenseCategory selectedCategory = spendCategorySelect.getValue();
        if (selectedCategory == null) {
            showError("Please select a category to delete");
            return;
        }

        try {
            boolean limitExist = false;
            for(SpendingLimit limit : manageSPLimit.getSpendingLimit()){
                if(limit.getCategory() == selectedCategory){
                    limitExist = true;
                    break;
                }
            }
            if(!limitExist){
                throw new SpendingLimitNotFoundException();
            }

            Optional<ButtonType> result = showConfirmation(String.format(
                    "Are you sure you want to delete spending limit for %s",
                    selectedCategory));
            if (result.isEmpty() || result.get() != ButtonType.OK) {
                return;
            }
            manageSPLimit.removeSpendingLimit(selectedCategory);

            if (onLimitDeleted != null) {
                onLimitDeleted.accept(selectedCategory);
            }
            showInfo("Spending limit for " + selectedCategory + " has been removed");
            spendAmountText.clear();
        } catch (SpendingLimitNotFoundException e) {
            showError("No spending limit exists for " + selectedCategory);
        }
    }


    //HELPER METHOD
    private void setupChoiceBoxCategory(){
        Expense.ExpenseCategory[] categories = Expense.ExpenseCategory.values();
        spendCategorySelect.getItems().addAll(categories);
        spendCategorySelect.setValue(null);//default
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private Optional<ButtonType> showConfirmation(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText(null);
        alert.setContentText(message);
        return alert.showAndWait();
    }
}
