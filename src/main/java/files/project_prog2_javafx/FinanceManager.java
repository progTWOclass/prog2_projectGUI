// -------------------------------------------------------
// Final Project
// Written by: (include your name and student id)
// For “Programming 2” Section (include number)– Winter 2025
// --------------------------------------------------------
/**
 * This is a class that manages transactions and spending limit set by the user
 *
 * we have an addTransaction() method in charge of adding a transaction (either income and expense)
 * into our arraylist. It also keeps track of spending limits by using a method called checkSpendingLimit().
 * So, if a user has set a spending limit for a specific category, it will let the user know that they have
 * exceeded their limit.
 *
 * we have a removeTransaction() method in charge of deleting a transaction based on the specified keyword
 * given by the user. It uses another method called searchTransactionByDescription() to find the transaction
 * that is associated with the keyword. It also checks if the transaction exists or not.
 *
 * we have a searchTransactionByDescription() method in charge of looking through the transaction arraylist to
 * find the transaction associated with the description word given by the user. It uses another method called
 * recursiveSearchDescription() which uses recursion to find a transaction.
 *
 * we have getTotalIncome(), getTotalExpense(), and getBalance(). These 3 methods are used to let the user get
 * their total income, total expense, and current balance.
 *
 * we have an addSpendingLimit() method that takes an expense category and a limit set by the user. It allows the
 * user to set a spending limit for an expense category of their choice. It also checks if the amount entered by
 * the user is a valid amount or not.
 *
 * we have a removeSpendingLimit() method that removes a spending limit based on the category specified by the user.
 * The method checks if a spending limit has been set for the specified category before proceeding to delete it.
 *
 * we have a getCurrentSpendingLimit() method which takes a category as a parameter and shows the spending limit
 * the user has set for themselves.
 *
 * we have a modifyTransaction() which takes an old transaction and a new transaction as parameters. It is allowing
 * the user to do some modification to one of their transactions. If the modification was done on an expense
 * transaction, the program also checks if the spending limit has been exceeded or not (if there is a spending limit
 * set for that particular expense)
 *
 * we have 2 helper methods. These are methods that are only used to help compliment other methods logic
 * and make the code less crowded.
 *
 * we have a helper method called recursiveSearchDescription(), and it takes a description word and an index as
 * parameters. The index is set by the programmer, and the method uses recursion to loop through the arraylist
 * to find the transaction that matches the description specified by the user.
 *
 * we have a second helper method called checkSpendingLimit(). It takes an expense as a parameter, and it is used
 * in the add transaction method. It keeps track of the expense transaction amount to check if the total expense
 * exceeds the spending limit or not.
 * */
package files.project_prog2_javafx;

import files.project_prog2_javafx.Exceptions.InvalidAmountException;
import files.project_prog2_javafx.Exceptions.SpendingLimitExceededException;
import files.project_prog2_javafx.Exceptions.SpendingLimitNotFoundException;
import files.project_prog2_javafx.Exceptions.TransactionNotFoundException;

import java.util.ArrayList;
public class FinanceManager {

    //CLASS VARIABLES
    private ArrayList<Transaction> transactions;
    private ArrayList<SpendingLimit> spendingLimit;


    //CONSTRUCTORS
    public FinanceManager(){
        this.transactions = new ArrayList<>();
        this.spendingLimit = new ArrayList<>();
    }

    //GETTERS AND SETTERS
    public ArrayList<Transaction> getTransactions(){
        return transactions;
    }
    public ArrayList<SpendingLimit> getSpendingLimit() {
        return spendingLimit;
    }

    public void setTransactions(ArrayList<Transaction> transactions){
        this.transactions = transactions;
    }
    public void setSpendingLimit(ArrayList<SpendingLimit> spendingLimit) {
        this.spendingLimit = spendingLimit;
    }


    //METHODS
    //add a transaction either income or expense
    public void addTransaction(Transaction tranAdd) throws SpendingLimitExceededException {
        //check if adding another expense will exceed our spending limit or not
        if (tranAdd instanceof Expense) {
            Expense addExpense = (Expense) tranAdd;
            checkSpendingLimit(addExpense);
        }
        transactions.add(tranAdd);
        System.out.println("Transaction registered successfully");
    }

    //remove a transaction based on the keyword given by the user
    public boolean removeTransaction(String description){
        try {
            Transaction transactionFound = searchTransactionByDescription(description);
            return transactions.remove(transactionFound);//remove transaction successful
        }catch(TransactionNotFoundException tNFE){
            System.out.println(tNFE.getMessage());
            return false;
        }
    }

    //find the transaction associated with the keyword given by the user
    public Transaction searchTransactionByDescription(String description) throws TransactionNotFoundException {
        Transaction findTransaction = recursiveSearchDescription(description, 0);
        if(findTransaction == null){
            throw new TransactionNotFoundException("error transaction not found");
        }
        return findTransaction;
    }

    //method for finding the total income of the user
    public double getTotalIncome(){
        double income = 0.0;
        for(Transaction findIncome : transactions){
            if(findIncome instanceof Income){
                income += findIncome.getAmount();
            }
        }
        return Double.parseDouble(String.format("%.2f", income));
    }

    //method for finding the total expense of the user
    public double getTotalExpense(){
        double expense = 0.0;
        for (Transaction findExpense : transactions) {
            if (findExpense instanceof Expense) {
                expense += findExpense.getAmount();
            }
        }
        return Double.parseDouble(String.format("%.2f", expense));
    }

    //method for finding the user's current balance
    public double getBalance() {
        return Math.round((getTotalIncome() - getTotalExpense()) * 100.0) / 100.0;
    }

    //allow the user to add a spending limit to a specific category
    public void addSpendingLimit(Expense.ExpenseCategory categoryLimit, double limit) throws InvalidAmountException {
        if(limit < 0){
            throw new InvalidAmountException("Limit must be positive");
        }

        //check if the user wants to update their spending limit
        for(SpendingLimit exist : spendingLimit){
            if(exist.getCategory() == categoryLimit){
                exist.setLimit(limit);
                return;
            }
        }
        spendingLimit.add(new SpendingLimit(categoryLimit, limit));
    }

    //allow the user to remove their spending limit of a specific category
    public void removeSpendingLimit(Expense.ExpenseCategory categoryLimit) throws SpendingLimitNotFoundException {

        if (categoryLimit == null) {
            throw new SpendingLimitNotFoundException("No spending limit set for category: " + categoryLimit);
        }
        for(int i = 0; i< spendingLimit.size(); i++){
            if (spendingLimit.get(i).getCategory() == categoryLimit) {
                spendingLimit.remove(i);
                break;
            }
        }
    }

    //this method is used to look for the current expense total of a specific category
    public double getCurrentSpendingLimit (Expense.ExpenseCategory category){
        double categoryTotal = 0.0;
        for(Transaction spending : transactions){
            if(spending instanceof Expense){
                //type cast to get getCategory method from expense class
                //since it is the only class that has this method
                Expense expense = (Expense) spending;
                if(expense.getCategory() == category){
                    categoryTotal += Math.abs(expense.getAmount());
                }
            }
        }
        return categoryTotal;
    }

    //this method is for editing a transaction
    public void modifyTransaction(Transaction oldTran, Transaction newTran) throws SpendingLimitExceededException{
        //find the index of the old transaction
        int index = transactions.indexOf(oldTran);

        //checks if the transaction exists. index starts at 0 in an arraylist
        if(index != -1){
            //if a transaction is an expense, we check for the spending limit to see
            //if the limit has been exceeded or not
            if(newTran instanceof Expense) {
                Expense newExpense = (Expense) newTran;
                checkSpendingLimit(newExpense);
            }
            //replace the old transaction with the new transaction at the same index
            transactions.set(index, newTran);
        }
    }

//    public void modifyTransaction(Transaction oldTran, Transaction newTran)
//            throws SpendingLimitExceededException {
//
//        int index = transactions.indexOf(oldTran);
//        if(index == -1) return;
//
//        // Only check spending limit if:
//        // 1. Both are expenses
//        // 2. The amount has actually changed
//        if(oldTran instanceof Expense && newTran instanceof Expense) {
//            Expense oldExpense = (Expense) oldTran;
//            Expense newExpense = (Expense) newTran;
//
//            // Only check if amount changed AND it's an increase
//            if(newExpense.getAmount() != oldExpense.getAmount() &&
//                    newExpense.getAmount() > oldExpense.getAmount()) {
//                checkSpendingLimit(newExpense);
//            }
//        }
//        transactions.set(index, newTran);
//    }

    //HELPER METHODS
    //searchTransactionByDescription() will use this method to find a transaction by specific keyword
    //given by the user using recursion
    private Transaction recursiveSearchDescription(String description, int index){

        //once the index is bigger than the size of transaction, we know that
        //we have reached the end of the arraylist, and we didn't find our
        //description keyword
        if(index >= transactions.size()){
            return null;
        }
        //get the current index, and from there we find the description we are looking for
        Transaction findDescription = transactions.get(index);
        //find partial description as well (ex: find "shopping" in "grocery shopping")
        if(findDescription.getDescription().toLowerCase().contains(description.toLowerCase())){
            return findDescription;
        }
        //we increment the index to satisfy the base case
        return recursiveSearchDescription(description, index + 1);
    }

    //addTransaction() will use this method to check if adding another expense
    //will exceed the spending limit for the specified category
    private boolean checkSpendingLimit (Expense expense) throws SpendingLimitExceededException{
        for(SpendingLimit limits : spendingLimit){
            if(limits.getCategory() == expense.getCategory()){
                double currentSpendingLimit = getCurrentSpendingLimit(expense.getCategory());
                if(currentSpendingLimit + Math.abs(expense.getAmount()) > limits.getLimit()){
                    throw new SpendingLimitExceededException(
                            String.format("You will exceed %s limit of $%.2f (current: $%.2f)",
                                    limits.getCategory(), limits.getLimit(), currentSpendingLimit));
                }
            }
        }
        return true;
    }
}
