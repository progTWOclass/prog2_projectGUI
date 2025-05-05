// -------------------------------------------------------
// Final Project
// Written by: Steve Banh 1971537
// For “Programming 2” Section 02 – Winter 2025
// --------------------------------------------------------
/**
 * This is a custom class that will print a message informing the user that they have exceeded
 * their spending limit
 * */
package files.project_prog2_javafx.Exceptions;

public class SpendingLimitExceededException extends Exception{

    //CONSTRUCTOR
    //default
    public SpendingLimitExceededException(){
        super();
    }
    //parameterized
    public SpendingLimitExceededException(String message){
        super(message);
    }
}