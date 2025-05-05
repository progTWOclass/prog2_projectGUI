// -------------------------------------------------------
// Final Project
// Written by: Steve Banh 1971537
// For “Programming 2” Section 02 – Winter 2025
// --------------------------------------------------------
/**
 * This is a custom class that will print a message informing the user that the transaction
 * they are looking for does not exist
 * */
package files.project_prog2_javafx.Exceptions;

public class TransactionNotFoundException extends Exception{

    //CONSTRUCTOR
    //default
    public TransactionNotFoundException(){
        super();
    }

    //parameterized
    public TransactionNotFoundException(String message){
        super(message);
    }
}
