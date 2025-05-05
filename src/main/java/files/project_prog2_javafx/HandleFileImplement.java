// -------------------------------------------------------
// Final Project
// Written by: Steve Banh 1971537
// For “Programming 2” Section 02 – Winter 2025
// --------------------------------------------------------
/**
 * This class is implementing FileHandler file. This class must have all the methods
 * in the interface class with implementations/logic.
 * */
package files.project_prog2_javafx;

import java.time.LocalDate;
import java.util.ArrayList;
import java.io.*;
public class HandleFileImplement implements FileHandler{

    //creating the text file to store the user's transactions
    @Override
    public void saveToFile(String file, ArrayList<Transaction> transactionList) throws IOException{
        try{
            PrintWriter printNumber = new PrintWriter(new FileOutputStream(file));

            for(Transaction t : transactionList){
                if(t instanceof Expense){
                    Expense expense = (Expense) t;
                    printNumber.println("EXPENSE:" + expense.getDescription() + ", " +
                            expense.getAmount() + ", " + expense.getDate() + ", " +
                            expense.getCategory());
                }else{
                    printNumber.println("INCOME:" + t.getDescription() + ", " +
                            t.getAmount() + ", " + t.getDate());
                }
            }
            System.out.println("File created successfully");
            printNumber.close();
        }catch (IOException iOE){
            System.err.println("Transaction file not created. something went wrong");
        }
    }

    //retrieve transaction data from the text file
    @Override
    public ArrayList<Transaction> loadFromFile(String file) throws IOException{
        ArrayList<Transaction> transactions = new ArrayList<>();
        try{
            BufferedReader readFile = new BufferedReader(new FileReader(file));
            String fileContent;
            while ((fileContent = readFile.readLine()) != null){

                // Split by comma followed by optional whitespace
                String[] fileData = fileContent.split(",\\s*");

                //Remove the prefix (INCOME: or EXPENSE:) from the first element
                //the program recognizes filedata[0] as [INCOME:Salary]. So, we
                //split the first element by the colon, and we get [INCOME, Salary] -> [0,1],
                //we then remove "INCOME" by overriding the filedata[0] by taking the [1] in the second line
                String type = fileData[0].split(":")[0].trim();
                fileData[0] = fileData[0].split(":")[1].trim();


                if(type.equals("INCOME")) {
                    String fileDescription = fileData[0];
                    double fileSalary = Double.parseDouble(fileData[1]);
                    LocalDate fileDate = LocalDate.parse(fileData[2]);
                    transactions.add(new Income(fileDescription, fileSalary, fileDate));
                }else {
                    String fileDescription = fileData[0];
                    double fileExpense = Double.parseDouble(fileData[1]);
                    LocalDate fileDate = LocalDate.parse(fileData[2]);
                    Expense.ExpenseCategory fileCategory = Expense.ExpenseCategory.valueOf(fileData[3]);
                    transactions.add(new Expense(fileDescription, fileExpense, fileDate, fileCategory));
                }
            }
            readFile.close();
        }catch (IOException iOE){
            System.err.println("transaction file not found.");
        }
        return transactions;
    }

    //save spending limit to a separate text file
    public void saveLimits(String limitsFile, ArrayList<SpendingLimit> limits) throws IOException {
        try{
            PrintWriter writer = new PrintWriter(new FileOutputStream(limitsFile));
            for (SpendingLimit limit : limits) {
                writer.println("SPENDING LIMIT:" + limit.getCategory() + ", " +
                        limit.getLimit());
            }
            writer.close();
        }catch (IOException iOE){
            System.err.println("Transaction file not created. something went wrong");
        }
    }

    //retrieve spending limit data from the saved spending limit text file
    public ArrayList<SpendingLimit> loadLimits(String limitsFile) throws IOException {
        ArrayList<SpendingLimit> limits = new ArrayList<>();
        try{
            BufferedReader reader = new BufferedReader(new FileReader(limitsFile));
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    String[] fileData = line.split(",\\s*");

                    //Remove (SPENDING LIMIT:RENT) from the first element
                    //the program recognizes filedata[0] as [SPENDING LIMIT:RENT].
                    //After splitting by the colon, and we get [SPENDING LIMIT, RENT],
                    //we then remove "SPENDING LIMIT" by overriding the filedata[0] with [1]
                    fileData[0] = fileData[0].split(":")[1].trim();

                    Expense.ExpenseCategory limitCategory = Expense.ExpenseCategory.valueOf(fileData[0]);
                    double limit = Double.parseDouble(fileData[1]);
                    limits.add(new SpendingLimit(limitCategory,limit));
                }
            }
            reader.close();
        }catch (IOException iOE){
            System.err.println("spending limit file not found.");
        }
        return limits;
    }
}

