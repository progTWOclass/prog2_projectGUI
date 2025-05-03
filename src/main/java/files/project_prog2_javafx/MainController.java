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
 * We have an initialize() method that contains helper methods. It is used to set up our controller such as the
 * column values for the table view, update the balance to show our current balance based on income or expense,
 * update the pie chart to show our expenses, update the spending limit chart when the user adds a spending limit,
 * and show how many categories have exceeded the spending limit.
 *
 * We have a searchTran() method that shows the transaction in the table view based on the description keyword
 * the user has entered.
 *
 * We have a saveTran() method that allows the user to save their transaction history and their spending limit
 * history into a text file.
 *
 * We have a loadTran() method that allows the user to load their previous transaction and spending limit text
 * file into our finance tracker.
 *
 * 
 **/

package files.project_prog2_javafx;

import files.project_prog2_javafx.Exceptions.InvalidAmountException;
import files.project_prog2_javafx.Exceptions.SpendingLimitExceededException;
import files.project_prog2_javafx.Exceptions.TransactionNotFoundException;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;


import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class MainController {

    //INSTANCES
    FinanceManager manager = new FinanceManager();
    HandleFileImplement handleFile = new HandleFileImplement();

    //PIECHART
    @FXML private PieChart pieChart;

    //BARCHART
    @FXML private BarChart<String, Number> spLimitGraph;
    @FXML private CategoryAxis exVsSp;
    @FXML private NumberAxis amountAxis;

    //LABELS
    @FXML private Label displayBalance;
    @FXML private Label displayName;
    @FXML private Label balance;

    //TEXTFIELD
    @FXML private TextField searchText;

    //TABLES
    @FXML private TableView<Transaction> tranTable;
    @FXML private TableColumn<Transaction, String> dateColumn;
    @FXML private TableColumn<Transaction, String> descColumn;
    @FXML private TableColumn<Transaction, Double> amountColumn;
    @FXML private TableColumn<Transaction, String> typeColumn;
    @FXML private TableColumn<Transaction, Expense.ExpenseCategory> categoryColumn;

    //LISTS
    private ArrayList<Transaction> transactionList = new ArrayList<>();

    //OBSERVABLE LIST
    //wraps arraylist with observablelist to sync both lists, so if I modify
    //either one, the other will get notified that a modification has been done
    @FXML private ObservableList<Transaction> tranList = FXCollections.observableList(transactionList);


    //FXML METHODS
    @FXML protected void initialize() {
        setupTableView();
        updateBalance();
        updateSpendingChart();
        setupPieChart();
        updateSpendingLimitStatus();
    }

    @FXML protected void searchTran() {
        String desc = searchText.getText().toLowerCase();

        //if there is no search show all data
        if(desc.isEmpty()){
            tranTable.setItems(tranList);
            return;
        }
        try {
            //returns an instance of transaction after finding the transaction by
            //using the recursive method in FinanceManager
            Transaction found = manager.searchTransactionByDescription(desc);

            //create a new observalelist to only show the transaction associated with the
            //specified word
            ObservableList<Transaction> result = FXCollections.observableArrayList(found);
            tranTable.setItems(result);
        }catch (TransactionNotFoundException tNFE){
            showError("No transactions found containing: '" + desc + "'");
        }
    }

    @FXML protected void saveTran() {
        //Check if there are transactions to save
        if (tranTable.getItems().isEmpty()) {
            showInfo("No transactions to save");
            return;
        }

        //class to save or load a file
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Transaction File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text files (*.txt)", "*.txt"));

        //get the window/stage where my tableview is
        File file = fileChooser.showSaveDialog(tranTable.getScene().getWindow());

        //if the user cancels the window
        if(file == null){
            return;
        }
        //checks if the file already exists, and ask the user if they want to override the
        //previous saved file
        if (file.exists()) {
            Optional<ButtonType> result = showConfirmation("File already exists. Overwrite?");
            //if the user clicks on the exit (X) button cancel button, or closes the dialog,
            //the result will be empty or not OK. So, we cancel the operation
            if (result.isEmpty() || result.get() != ButtonType.OK) {
                return;
            }
        }

        File limitFile = null;
        if(!manager.getSpendingLimit().isEmpty()){
            fileChooser.setTitle("Save Spending Limit File");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text files (*.txt)", "*.txt"));
            limitFile = fileChooser.showSaveDialog(tranTable.getScene().getWindow());
            if(limitFile == null){
                return;
            }
        }
        try{
            //Creates a temporary copy of the TableView's data as an ArrayList
            //Passes this copy to your file saver as to not affect the original data
            handleFile.saveToFile(file.getAbsolutePath(),  new ArrayList<>(tranTable.getItems()));

            //Save limits only if they exist and user selected a file
            if (!manager.getSpendingLimit().isEmpty() && limitFile != null) {
                handleFile.saveLimits(limitFile.getAbsolutePath(), new ArrayList<>(manager.getSpendingLimit()));
            }

            showInfo("Transactions saved successfully to:\n" + file.getAbsolutePath());
        }catch (IOException iOE){
            showError("Cannot save file. Something went wrong");
        }
    }

    @FXML protected void loadTran() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Load Transaction File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text files (*.txt)", "*.txt"));

        //get the window/stage where my tableview is
        File transactionFile = fileChooser.showOpenDialog(tranTable.getScene().getWindow());
        if (transactionFile == null) return;

        //if the user cancels the window
        try {
            //returns ArrayList<Transaction> as per our method in HandleFileImplement
            ArrayList<Transaction> loadList = handleFile.loadFromFile(transactionFile.getAbsolutePath());

            //add all loaded transactions to the ObservableList wrapper
            //to automatically notify the TableView to update
            tranList.setAll(loadList);
            manager.setTransactions(new ArrayList<>(loadList));

            Optional<ButtonType> result = showConfirmation("Would you like to load spending limits file?");
            if (result.isPresent() && result.get() == ButtonType.OK) {
                fileChooser.setTitle("Load Spending Limits File");
                File limitsFile = fileChooser.showOpenDialog(tranTable.getScene().getWindow());
                if (limitsFile != null) {
                    ArrayList<SpendingLimit> limits = handleFile.loadLimits(limitsFile.getAbsolutePath());
                    manager.setSpendingLimit(limits);
                }
            }

            updateBalance();
            updateSpendingLimitStatus();
            updateSpendingChart();
            setupPieChart();
            showInfo("File loaded successfully");
        } catch (IOException iOE) {
            showError("Cannot save file. Something went wrong");
        }
    }

    @FXML protected void addTran() throws IOException {
        //find and load the fxml file
        FXMLLoader loader = new FXMLLoader(getClass().getResource("addTransaction-view.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);

        //create an instance of AddController to get the controller
        //associated with the fxml file
        AddController addController = loader.getController();

        //takes the new object created of AddController and adds it
        //to the tableview
        addController.setAddNewTransaction(transaction -> {

            addController.loadCurrentInfo(null);

            try {
                //we add the transaction in TableView and let
                //FinanceManager also know about the update
                tranTable.getItems().add(transaction);

                //update the financeManger internal arraylist (different from arraylist<Transaction> transactionList)
                //to check for spending limit
                manager.addTransaction(transaction);
                updateBalance();
                updateSpendingLimitStatus();
            } catch (SpendingLimitExceededException e) {
                tranTable.getItems().remove(transaction);
                showWarning("You have exceeded you spending limit");
            }
        });

        //create a new window for addTransaction
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Add transaction");
        stage.setResizable(false);//not allowed to resize the window
        stage.initModality(Modality.APPLICATION_MODAL); //block the main window
        stage.showAndWait();
        updateSpendingChart();
        setupPieChart();
    }

    @FXML protected void deleteTran() {
        Transaction selectedTran = tranTable.getSelectionModel().getSelectedItem();

        if (selectedTran == null) {
            showInfo("Please select a transaction to delete");
            return;
        }
        Optional<ButtonType> confirm = showConfirmation(
                "Are you sure you want to delete transactions??\n" +
                        "Description: " + selectedTran.getDescription() + "\n" +
                        "Amount: $" + String.format("%.2f", selectedTran.getAmount())
        );
        if(confirm.get() == ButtonType.OK){
            boolean removeTran = manager.removeTransaction(selectedTran.getDescription());

            if(removeTran) {
                tranList.remove(selectedTran);
                updateBalance();
                updateSpendingLimitStatus();
                updateSpendingChart();
                setupPieChart();
            }
        }
    }

    @FXML protected void editTran() throws IOException {
        Transaction selectedTran = tranTable.getSelectionModel().getSelectedItem();

        if (selectedTran == null) {
            showInfo("Please select a transaction to edit");
            return;
        }
        //find and load the fxml file
        FXMLLoader loader = new FXMLLoader(getClass().getResource("addTransaction-view.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);

        //create an instance of AddController to get the controller
        //associated with the fxml file
        AddController addController = loader.getController();
        addController.loadCurrentInfo(selectedTran);

        addController.setAddNewTransaction(updateTransaction -> {
            try{

                int index = tranList.indexOf(selectedTran);
                if(index != -1){
                    tranList.set(index, updateTransaction);
                    manager.modifyTransaction(selectedTran, updateTransaction);

                    updateBalance();
                    updateSpendingLimitStatus();
                }
            } catch (SpendingLimitExceededException e) {
                    showWarning("You will exceed your spending limit");
            }
        });

        //create a new window for addTransaction
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Edit Transaction");
        stage.setResizable(false);//not allowed to resize the window
        stage.initModality(Modality.APPLICATION_MODAL); //block the main window
        stage.showAndWait();
        updateSpendingChart();
        setupPieChart();
    }

    @FXML protected void limitCategory() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("spendingLimit-view.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);

        SpendLimController limController = loader.getController();
        limController.setFinanceManager(manager);

        limController.setAddNewLimit(spendingLimit -> {
            try {
                manager.addSpendingLimit(spendingLimit.getCategory(), spendingLimit.getLimit());
                updateSpendingChart(); // Add this method to update the chart
                updateSpendingLimitStatus();

                showInfo("Spending limit set for " + spendingLimit.getCategory() + ": $" +
                        String.format("%.2f", spendingLimit.getLimit()));
            } catch (InvalidAmountException e) {
                showError(e.getMessage());
            }
        });

        limController.setOnLimitDeleted(category -> {
            updateSpendingChart();
            updateSpendingLimitStatus();
            showInfo("Spending limit for " + category + " has been removed");
        });

        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Set a Spending Limit");
        stage.setResizable(false);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }


    //HELPER METHODS
    private void setupTableView(){
        /**
         * Initializes the TableView columns:
         * connects each column to get the getters of the transaction class
         */
        dateColumn.setCellValueFactory(cellData -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy");//e.g. April 25, 2025
            LocalDate date = cellData.getValue().getDate();
            String formatDate = date.format(formatter);
            return new SimpleStringProperty(formatDate);
        });
        descColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        categoryColumn.setCellValueFactory(cellData -> {
            Transaction type = cellData.getValue();
            if(type instanceof Expense){
                return new SimpleObjectProperty<>(((Expense) type).getCategory());
            }
            return null;
        });

        //return the data type of the wrapped value <String>
        typeColumn.setCellValueFactory(cellData -> {
            Transaction type = cellData.getValue();
            return new SimpleStringProperty(type.getClass().getSimpleName());
        });
        tranTable.setItems(tranList);
    }

    private void setupPieChart() {
        // Clear existing data
        pieChart.getData().clear();
        ArrayList<Expense.ExpenseCategory> categories = new ArrayList<>();
        ArrayList<Double> amounts = new ArrayList<>();
        double totalExpense = 0;

        tranList.clear();
        tranList.addAll(manager.getTransactions());
        System.out.println("Transactions loaded: " + tranList.size()); // << debug

        for(Transaction typeTran : tranList){
            //System.out.println("Transaction: " + t); // << debug
            if(typeTran instanceof Expense){
                Expense expense = (Expense) typeTran;
                Expense.ExpenseCategory category = expense.getCategory();
                double amount = Math.abs(expense.getAmount());
                totalExpense += amount;

                int findIndex = categories.indexOf(category);
                if(findIndex >= 0){
                    amounts.set(findIndex, amounts.get(findIndex) + amount);
                }else {
                    categories.add(category);
                    amounts.add(amount);
                }
            }
        }

        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        for(int i = 0; i < categories.size(); i++){
            Expense.ExpenseCategory getCategory = categories.get(i);
            String categoryTitle = String.valueOf(getCategory);
            double getAmount = amounts.get(i);
            double findPercent = (getAmount/totalExpense) * 100;

            pieChartData.add(new PieChart.Data(categoryTitle, getAmount));
        }

        if (pieChartData.isEmpty()) {
            pieChartData.add(new PieChart.Data("No Data", 1)); // << Add dummy slice
        }

        pieChart.setData(pieChartData);
        pieChart.setLegendVisible(false);
        pieChart.setLabelsVisible(true);
    }

    private void updateBalance() {
        balance.setText(String.format("$%.2f", manager.getBalance()));
    }

    private void updateSpendingChart() {
        // Clear any existing data from the chart
        spLimitGraph.getData().clear();

        // Create two data series - one for actual expenses and one for spending limits
        XYChart.Series<String, Number> expenseSeries = new XYChart.Series<>();
        expenseSeries.setName("Expenses");

        XYChart.Series<String, Number> limitSeries = new XYChart.Series<>();
        limitSeries.setName("Limits");

        // These lists will track our expense categories and their values
        ArrayList<Expense.ExpenseCategory> categories = new ArrayList<>();
        ArrayList<Double> limits = new ArrayList<>();
        ArrayList<Double> expenses = new ArrayList<>();

        // Step 1: Get all categories that have spending limits
        for (SpendingLimit limit : manager.getSpendingLimit()) {
            categories.add(limit.getCategory());  // Add the category
            limits.add(limit.getLimit());         // Add the limit amount
            expenses.add(0.0);                    // Initialize expense amount to zero
        }

        // Step 2: Calculate how much has been spent in each category
        for (Transaction transaction : manager.getTransactions()) {
            // Only process expense transactions
            if (transaction instanceof Expense) {
                Expense expense = (Expense) transaction;

                // Find this expense's category in our list
                int position = categories.indexOf(expense.getCategory());

                // If we found the category (it has a spending limit)
                if (position >= 0) {
                    // Get the current amount for this category
                    double currentAmount = expenses.get(position);

                    // Add the transaction amount to the total
                    // (using absolute value since expenses are negative)
                    expenses.set(position, currentAmount + Math.abs(expense.getAmount()));
                }
            }
        }

        // Step 3: Add all data to the chart
        boolean hasData = false;

        for (int i = 0; i < categories.size(); i++) {
            String categoryName = categories.get(i).toString().toLowerCase();
            double limitAmount = limits.get(i);
            double expenseAmount = expenses.get(i);

            // Add this category's data to both series
            limitSeries.getData().add(new XYChart.Data<>(categoryName, limitAmount));
            expenseSeries.getData().add(new XYChart.Data<>(categoryName, expenseAmount));
            hasData = true;
        }

        // Step 4: Only adjust the chart if we have data to show
        if (hasData) {
            // Find the maximum value for scaling the chart
            double maxLimit = 0.0;
            for (Double limitValue : limits) {
                if (limitValue > maxLimit) {
                    maxLimit = limitValue;
                }
            }

            double maxExpense = 0.0;
            for (Double expenseValue : expenses) {
                if (expenseValue > maxExpense) {
                    maxExpense = expenseValue;
                }
            }

            // Use the larger of the two maximums
            double chartMax = Math.max(maxLimit, maxExpense);

            // Add 10% padding to make the chart look nicer
            if (amountAxis != null && chartMax > 0) {
                amountAxis.setUpperBound(chartMax * 1.1);
                amountAxis.setTickUnit(chartMax / 5); // Create 5 tick marks
            }

            // Add both series to the chart
            spLimitGraph.getData().add(expenseSeries);
            spLimitGraph.getData().add(limitSeries);
        }

        // Step 5: Make the chart look nice
        spLimitGraph.setCategoryGap(40);
        spLimitGraph.setBarGap(3);
        spLimitGraph.setAnimated(false);
        spLimitGraph.setLegendVisible(true);
    }

    private void updateSpendingLimitStatus() {
        int totalCategories = manager.getSpendingLimit().size();
        int overLimit = 0;

        // Calculate how many categories are over their limit
        for (SpendingLimit limit : manager.getSpendingLimit()) {
            double spent = manager.getCurrentSpendingLimit(limit.getCategory());

            if (spent > limit.getLimit()) {
                overLimit++;
            }
        }

        // Update the label text
        if (totalCategories > 0) {
            displayName.setText(
                    String.format("%d/%d categories over spending limit", overLimit, totalCategories)
            );
        } else {
            displayName.setText("No spending limits set");
        }
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
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

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}