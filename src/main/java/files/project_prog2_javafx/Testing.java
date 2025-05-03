package files.project_prog2_javafx;

import files.project_prog2_javafx.Exceptions.InvalidAmountException;
import files.project_prog2_javafx.Exceptions.SpendingLimitExceededException;
import files.project_prog2_javafx.Exceptions.SpendingLimitNotFoundException;
import files.project_prog2_javafx.Exceptions.TransactionNotFoundException;

import java.io.*;
import java.time.*;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;
public class Testing {
    private static FinanceManager manager = new FinanceManager();
    private static HandleFileImplement file = new HandleFileImplement();
    private static Scanner input = new Scanner(System.in);

    //used to verify if the user enters acceptable characters for the description
    private static String verifyDescription(Scanner input) {
        while (true) {
            String description = input.nextLine().trim();//remove beginning and ending whitespaces
            if (description.isEmpty()) {
                System.err.println("Description cannot be empty.");
                continue;
            } else if (description.length() > 50) {
                System.err.println("Description is too long (Max: 50 characters)");
                continue;
            } else if (!description.matches("^[a-zA-Z0-9$#\\s]+$")) {
                System.err.println("Description can only contain letters, numbers, spaces, $, and #");
                continue;
            } else if (!description.matches(".*[a-zA-Z].*")) {
                System.err.println("Description must include at least one letter.");
                continue;
            }
            return description;
        }
    }

    //used to verify if the user enters a legal number for their salary/expense
    private static double verifyAmount(Scanner input){
        while(true) {
            try {
                double amount = input.nextDouble();
                if (amount <= 0) {
                    System.out.println("Amount cannot be negative");
                    continue;
                }
                input.nextLine();//clear the buffer
                return amount;
            }catch (InputMismatchException iME){
                System.err.println("Cannot contain characters. It must only be numbers.");
                input.nextLine();//clear the input
            }
        }
    }

    //used to check if the date is valid
    private static LocalDate verifyDate(Scanner input){
        while (true) {
            System.out.println("Enter date (YYYY-MM-DD):");
            try {
                String dateString = input.nextLine().trim();
                LocalDate date = LocalDate.parse(dateString);
                if(date.getYear() <= 0){
                    throw new DateTimeException("Cannot add a negative year");
                }
                return date;
            } catch (DateTimeException e) {
                System.err.println("Invalid date format. Use YYYY-MM-DD.");
            }
        }
    }

    //used to allow the user to choose his/her category
    private static Expense.ExpenseCategory chooseCategory(Scanner input){
        Expense.ExpenseCategory[] categories = Expense.ExpenseCategory.values();//store the enums values into an array

        //display the category options
        for (int i = 0; i < categories.length; i++) {
            System.out.println((i + 1) + ". " + categories[i]);
        }
        int categoryChoice;
        while (true) {
            try {
                System.out.println("Please choose your category");
                categoryChoice = input.nextInt();

                // Validate category choice
                if (categoryChoice < 1 || categoryChoice > categories.length) {
                    throw new InputMismatchException();
                }
                input.nextLine();//clear the buffer
                break;
            } catch (InputMismatchException iME) {
                System.err.println("Invalid choice. Please try again");
                input.nextLine();
            }
        }
        //pick the correct category
        return categories[categoryChoice - 1];
    }

    //make sure the user chooses the available options
    private static int verifyChoice(Scanner input, int min, int max){
        while(true){
            try{
                int choice = input.nextInt();
                if (choice < min || choice > max) {
                    throw new InputMismatchException();
                }
                input.nextLine();//clear the buffer
                return choice;
            }catch(InputMismatchException iME){
                System.err.println("Invalid input. Please enter the available options");
                input.nextLine();//clear the previous input
            }
        }
    }


    public static void addNewTransaction(){
        System.out.print("""
                Please select a new transaction
                [1] Income
                [2] Expense
                [3] Return to menu
                """);
        try {
            int choice = verifyChoice(input, 1,3);

            if(choice == 1){
                System.out.println("Please enter a description");
                String description = verifyDescription(input);

                System.out.println("Please enter a salary");
                double salary = verifyAmount(input);

                System.out.println("Please enter a date");
                LocalDate date = verifyDate(input);

                Income income = new Income(description, salary, date);
                manager.addTransaction(income);

            }
            else if (choice == 2) {
                System.out.println("Please enter a description");
                String description = verifyDescription(input);

                System.out.println("Please enter expense amount");
                double expense = verifyAmount(input);

                System.out.println("Please enter a year");
                LocalDate date = verifyDate(input);

                System.out.println("Please select expense category:");
                Expense.ExpenseCategory category = chooseCategory(input);

                Expense expenses = new Expense(description, expense, date, category);
                manager.addTransaction(expenses);
            }
            else {
                System.out.println("Exiting...");
            }
        } catch (SpendingLimitExceededException sLEE) {
            System.out.println("Failed to add transaction: " + sLEE.getMessage());
        }
    }

    public static void findTransactionDescription(){
        System.out.println("Please enter a keyword in your description");
        try {
            String keyword = verifyDescription(input);
            Transaction object = manager.searchTransactionByDescription(keyword);

            System.out.println("Here is your transaction summary\n" + object.getSummary());
            System.out.print("""
                    Please enter an action you wish to perform
                    [1] modify the transaction
                    [2] delete the transaction
                    [3] return to main menu
                    """);
            int choiceAction = verifyChoice(input, 1,3);

            if(choiceAction == 1){
                if(object instanceof Income){
                    System.out.print("""
                    Which information do you wish to update
                    [1] description
                    [2] salary
                    [3] date
                    """);
                    int choiceModify;
                    try{
                        choiceModify = verifyChoice(input, 1,3);
                        if(choiceModify == 1){
                            System.out.println("Please enter your new description");
                            String newDescription = verifyDescription(input);
                            object.setDescription(newDescription);
                            System.out.println("Description updated successfully");

                        } else if (choiceModify == 2) {
                            System.out.println("Please enter your new salary");
                            double updateSalary = verifyAmount(input);
                            object.setAmount(updateSalary);
                            System.out.println("Salary updated successfully");

                        }else{
                            System.out.println("Please enter a new year");
                            LocalDate updateDate = verifyDate(input);
                            object.setDate(updateDate);
                            System.out.println("Date updated successfully");

                        }
                    }catch (InputMismatchException iME){
                        System.out.println("Invalid option. Please try again");
                    }
                } else if (object instanceof Expense) {
                    System.out.print("""
                    Which information do you wish to update
                    [1] description
                    [2] expense
                    [3] date
                    [4] category
                    [5] modify/delete spending limit
                    """);
                    int choiceModify;
                    try{
                        choiceModify = verifyChoice(input, 1,5);
                        if(choiceModify == 1){
                            System.out.println("Please enter your new description");

                            String newDescription = verifyDescription(input);
                            object.setDescription(newDescription);
                            System.out.println("Description updated successfully");

                        } else if (choiceModify == 2) {
                            System.out.println("Please enter your new expense");
                            double updateSalary = verifyAmount(input);
                            object.setAmount(updateSalary);
                            System.out.println("Expense updated successfully");

                        }else if (choiceModify == 3){
                            System.out.println("Please enter a new year");
                            LocalDate updateDate = verifyDate(input);
                            object.setDate(updateDate);
                            System.out.println("Date updated successfully");

                        }else if (choiceModify == 4){
                            System.out.println("Please select a new category");
                            Expense.ExpenseCategory updateCategory = chooseCategory(input);
                            //downcast to get the setCategory method from expense class
                            //because it is only available in that class
                            ((Expense) object).setCategory(updateCategory);
                            System.out.println("Category updated successfully");

                        }else{
                            System.out.println("""
                            which action you want to perform
                            [1] modify spending limit
                            [2] delete spending limit
                            """);
                            Expense.ExpenseCategory getCurrentCategory = ((Expense) object).getCategory();
                            int choice = verifyChoice(input, 1,2);
                            if(choice==1) {
                                System.out.println("Please enter your new spending limit");
                                double newLimit = verifyAmount(input);

                                try{
                                    double currentSpending = manager.getCurrentSpendingLimit(getCurrentCategory);
                                    if(newLimit < currentSpending){
                                        System.out.printf("your current spending (%.2f) exceeded your new spending limit.(%.2f)" +
                                                "do you still wish to continue? (1 = yes | 2 = no)\n", currentSpending, newLimit);
                                        int decision = verifyChoice(input, 1,2);
                                        if(decision == 2){
                                            System.out.println("Operation canceled");
                                        }
                                    }
                                    manager.addSpendingLimit(getCurrentCategory, newLimit);
                                    System.out.println("Spending limit modified");
                                    System.out.printf( "%s limit set to $%.2f%n", getCurrentCategory, newLimit);
                                }catch(InvalidAmountException iAE){
                                    System.out.println("Invalid amount");
                                }
                            }else{
                                System.out.println("Are you sure you want to delete your spending limit? (1 = yes | 2 = no)");
                                try {
                                    int deleteChoice = verifyChoice(input, 1, 2);
                                    if (deleteChoice == 2) {
                                        System.out.println("Operation canceled");
                                    }
                                    manager.removeSpendingLimit(getCurrentCategory);
                                    System.out.println("Spending limit deleted successfully");
                                }catch(SpendingLimitNotFoundException sLNFE){
                                    System.out.println("spending limit not found for this category");
                                }
                            }
                        }
                    }catch (InputMismatchException iME){
                        System.err.println("Invalid option. Please try again");
                    }
                }
            }
            else if (choiceAction == 2) {
                System.out.println("Are you sure you want to delete this transaction? (1 = yes | 2 = no)");
                int choice = verifyChoice(input, 1, 2);
                if(choice == 1) {
                    boolean checkRemove = manager.removeTransaction(keyword);
                    if (checkRemove) {
                        System.out.println("Transaction deleted successfully");
                    }
                } else {
                    System.out.println("Operation canceled.");
                }
            }
        }catch (TransactionNotFoundException tNFE) {
            System.err.println("Transaction does not exist");
        }
    }

    public static void displayTransaction(){
        for(Transaction t : manager.getTransactions()){
            System.out.println(t.getSummary());
        }
    }

    public static void calculateMoney(){
        System.out.println("Your total income");
        System.out.printf("$%.2f\n", manager.getTotalIncome());
        System.out.println("Your total expense");
        System.out.printf("$%.2f\n", manager.getTotalExpense());
        System.out.println("Your current balance");
        System.out.printf("$%.2f\n", manager.getBalance());
    }

    public static void saveTransactionFile() {
        while(true) {
            try {
                System.out.println("Please enter a name for your file");
                String fileName = input.nextLine();

                if (fileName.isEmpty()) {
                    System.out.println("Cannot have an empty file name");
                    continue;
                }
                if (!fileName.endsWith(".txt")) {
                    fileName += ".txt";
                }
                if (manager.getTransactions().isEmpty()) {
                    System.out.println("No transactions to save. Please add transactions first.");
                    return;
                }
                System.out.println("Do you wish to save your transaction to " + fileName + "(1 = yes | 2 = no)");
                int choice = verifyChoice(input, 1, 2);
                if (choice == 1) {
                    file.saveToFile(fileName, manager.getTransactions());
                    System.out.println("Your transaction has been successfully saved ");
                    return;
                } else {
                    System.out.println("you have cancelled your transaction");
                    return;
                }
            }catch (IOException ioE){
                System.err.println("Your transaction has not been saved because something is wrong with the file");
            }
        }
    }

    public static void loadTransactionFile() {
        while (true) {
            try {
                System.out.println("\nEnter file name to load (or 'cancel' to abort):");
                String loadFile = input.nextLine().trim();

                // Allow user to cancel
                if (loadFile.equalsIgnoreCase("cancel")) {
                    System.out.println("Load operation cancelled.");
                    return;
                }

                // Validate filename
                if (loadFile.isEmpty()) {
                    System.err.println("Error: File name cannot be empty");
                    continue;
                }

                // Ensure .txt extension
                if (!loadFile.endsWith(".txt")) {
                    loadFile += ".txt";
                }

                // Check if a file exists
                File fileToLoad = new File(loadFile);
                if (!fileToLoad.exists()) {
                    System.err.println("Error: File '" + loadFile + "' does not exist.");
                    continue;
                }

                // Check if a file is empty
                if (fileToLoad.length() == 0) {
                    System.err.println("Warning: The file is empty. No transactions loaded.");
                    return;
                }

                // Confirm before loading
                System.out.printf("Load transactions from '%s'? (1 = yes | 2 = no): ", loadFile);
                int choice = verifyChoice(input, 1, 2);

                if (choice == 2) {
                    System.out.println("Load operation cancelled.");
                    return;
                }

                // Load transactions
                ArrayList<Transaction> loadedTransactions = file.loadFromFile(loadFile);
                manager.setTransactions(loadedTransactions);
                System.out.printf("Successfully loaded %d transactions.\n", loadedTransactions.size());
                break;
            } catch (IOException e) {
                System.err.println("Error reading file: " + e.getMessage());
            } catch (Exception e) {
                System.err.println("Unexpected error: " + e.getMessage());
                return; // Exit on critical errors
            }
        }
    }

    public static void setSpendingLimit(){
        try {
            System.out.println("Please add a limit for your spending limit: ");
            double limit = verifyAmount(input);

            System.out.println("Please choose the expense category you wish to add the limit to: ");
            Expense.ExpenseCategory category = chooseCategory(input);

            double currentSpending = manager.getCurrentSpendingLimit(category);
            if (limit < currentSpending) {
                System.out.printf(
                        "Warning: Your current spending for %s ($%.2f) already exceeds the new limit ($%.2f).%n",
                        category, currentSpending, limit
                );
                System.out.println("Do you still want to set limit? (1 = yes | 2 = no)");
                int choice = verifyChoice(input, 1, 2);
                if (choice == 2) {
                    System.out.println("Limit update cancelled.");
                    return;
                }
            }
            manager.addSpendingLimit(category, limit);
            System.out.println("New limit updated successfully");
            System.out.printf("New limit for %s set to $%.2f%n", category, limit);
        }catch(InvalidAmountException iAE) {
            System.out.println("Error: " + iAE.getMessage());
        } catch (Exception e) {
            System.out.println("An unexpected error occurred: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        boolean continueLoop = true;
        while(continueLoop) {
            System.out.println("""
                    \n===== Online Banking System =====
                    1. Add New Transaction
                    2. Search Transaction by Description
                    3. Display All Transactions
                    4. Calculate Total Income / Expense / Balance
                    5. Save Transactions to File
                    6. Load Transactions from File
                    7. Set Spending Limit
                    8. Exit
                    """);
            System.out.println("please enter your choice:");
            try {
                int commandChoice = input.nextInt();
                input.nextLine();//clears the buffer
                switch (commandChoice) {
                    case 1:
                        addNewTransaction();
                        break;//exit the switch statement
                    case 2:
                        findTransactionDescription();
                        break;
                    case 3:
                        displayTransaction();
                        break;
                    case 4:
                        calculateMoney();
                        break;
                    case 5:
                        saveTransactionFile();
                        break;
                    case 6:
                        loadTransactionFile();
                        break;
                    case 7:
                        setSpendingLimit();
                        break;
                    case 8:
                        System.out.println("Thank you for using our banking system \nGoodbye");
                        continueLoop = false;//stops the while loop
                        break;
                    default:
                        System.err.println("Invalid command");
                }
            }catch (InputMismatchException iME){
                System.err.println("Invalid command. PLease try again");
                input.next();
            }
        }
    }
}

