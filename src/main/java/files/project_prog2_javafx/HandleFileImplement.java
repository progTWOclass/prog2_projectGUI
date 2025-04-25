package files.project_prog2_javafx;

// -------------------------------------------------------
// Final Project
// Written by: (include your name and student id)
// For “Programming 2” Section (include number)– Winter 2025
// --------------------------------------------------------
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
            System.err.println("file not created. something went wrong");
        }
    }

    //retrieve the data from the text file
    @Override
    public ArrayList<Transaction> loadFromFile(String file) throws IOException{
        ArrayList<Transaction> transactions = new ArrayList<>();
        try{
            BufferedReader readFile = new BufferedReader(new FileReader(file));
            String fileContent;
            while ((fileContent = readFile.readLine()) != null){

                // Split by comma followed by optional whitespace
                String[] fileData = fileContent.split(",\\s*");

                // Remove the prefix (INCOME: or EXPENSE:) from the first element
                //because now the program recognizes ex: "INCOME:Salary" as [0]
                //split the array, [INCOME, Salary] -> [0],[1], remove "INCOME" by
                //forcing [0] to be "salary" instead
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
            System.err.println("file not found.");
        }
        return transactions;
    }
}

