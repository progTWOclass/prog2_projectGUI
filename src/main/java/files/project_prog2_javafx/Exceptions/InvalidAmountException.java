// -------------------------------------------------------
// Final Project
// Written by: Steve Banh 1971537
// For “Programming 2” Section 02 – Winter 2025
// --------------------------------------------------------
/**
 * This is a custom class that will print a message informing the user that they have entered
 * an invalid number for their transactions (income amount or expense amount)
 * */
package files.project_prog2_javafx.Exceptions;

public class InvalidAmountException extends Exception{

    //CONSTRUCTOR
    //default
    public InvalidAmountException(){
        super();
    }

    //parameterized
    public InvalidAmountException(String message){
        super(message);
    }
}
