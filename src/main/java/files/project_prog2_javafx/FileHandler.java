// -------------------------------------------------------
// Final Project
// Written by: (include your name and student id)
// For “Programming 2” Section (include number)– Winter 2025
// --------------------------------------------------------
/**
 * This file is an interface, and it only contains method signatures (does not have a body).
 * The method signatures tell other class that implements this type (HandleFileImplement)
 * what methods they must have.
 * */
package files.project_prog2_javafx;

import java.io.IOException;
import java.util.ArrayList;
interface FileHandler {

    //METHODS
    public void saveToFile(String file, ArrayList<Transaction> transactionList) throws IOException;

    public ArrayList<Transaction> loadFromFile(String fileName) throws IOException;

    public void saveLimits(String limitsFile, ArrayList<SpendingLimit> limits) throws IOException;

    public ArrayList<SpendingLimit> loadLimits(String limitsFile) throws IOException;
}

